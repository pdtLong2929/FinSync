package com.finsync.ui

import android.Manifest
import android.content.ComponentName
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.finsync.MainActivity
import com.finsync.bank.*
import com.finsync.auth.*
import com.finsync.domain.*
import com.finsync.ui.dashboard.*
import com.finsync.ui.dashboard.formatVnd
import com.finsync.ui.theme.FinSyncTheme
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.io.FileInputStream
import java.time.LocalDate
import java.util.UUID

/** Actual Android service/notification test plus isolated local finance/UI tests. No real bank transfer. */
class BankCaptureUiTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val keyboard = TestKeyboard()
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val target get() = compose.activity.applicationContext
    private val root by lazy { File(compose.activity.cacheDir, "bank-ui-${UUID.randomUUID()}") }
    private val privateContext by lazy { object : ContextWrapper(target) { override fun getNoBackupFilesDir() = root } }
    private val workspace = "bank-qa-${UUID.randomUUID()}"
    private val today = LocalDate.now()
    private val ledger get() = PersonalLedger(listOf(Wallet("qa-bank", "Bank QA", WalletType.BANK_ACCOUNT), Wallet("qa-cash", "Cash QA", WalletType.CASH)),
        listOf(LedgerEntry("qa-opening", "qa-bank", EntryType.OPENING, 1_000_000, "Opening", today)), defaultCategories, emptyList(), "qa-bank", "Food")
    private val candidate get() = BankNotificationParser.parse("qa.bank.fixture", "qa-event", System.currentTimeMillis(), 1L, "GD -125.000 VND So du 9.999.999 VND")!!
    private var controller: BankController? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var originalConfig: CaptureConfig? = null
    private var previousAccess = false
    private var mainPosted = false
    private var testPosted = false
    private var systemTest = false
    private fun shell(command: String): String = instrumentation.uiAutomation.executeShellCommand(command).use { pipe -> FileInputStream(pipe.fileDescriptor).use { String(it.readBytes()) } }
    @After fun cleanup() {
        BankCaptureSettings(target).setDefaultWallet(workspace, "qa.bank.fixture", null)
        controller?.close(); scope.cancel()
        if (systemTest) {
            val settings = BankCaptureSettings(target)
            settings.save(settings.read().copy(enabled = false))
            shell("am start -W -n com.finsync.test/com.finsync.ui.BankFixtureActivity --ez clear true")
            target.getSystemService(android.app.NotificationManager::class.java).cancelAll()
            if (!previousAccess) shell("cmd notification disallow_listener ${ComponentName(target, BankNotificationListener::class.java).flattenToString()}")
            originalConfig?.let(settings::save)
            File(target.noBackupFilesDir, "bank-notifications/${digest(workspace)}.json").delete()
        }
        if (root.exists()) root.deleteRecursively()
    }
    private fun click(text: String) {
        keyboard.close(compose)
        if (runCatching { compose.onNodeWithText(text).assertIsDisplayed() }.isFailure) {
            if (runCatching { compose.onNodeWithText(text).performScrollTo() }.isFailure)
                compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(text))
        }
        compose.onNodeWithText(text).performClick()
    }
    private fun host(store: FileBankStore, finance: FinanceViewModel, large: Boolean = false, requestedId: String? = null): BankController {
        val bank = BankController(workspace, store, finance, scope); controller = bank
        runBlocking { bank.open() }
        compose.activityRule.scenario.onActivity { activity -> activity.setContent {
            keyboard.Observe()
            val density = LocalDensity.current
            LaunchedEffect(store) { store.updates.collect { bank.observeStore(it) } }
            CompositionLocalProvider(LocalBankController provides bank, LocalDensity provides Density(density.density, if (large) 1.5f else 1f)) {
                FinSyncTheme { Box(Modifier.width(if (large) 320.dp else 411.dp).fillMaxHeight().safeDrawingPadding()) {
                    FinSyncApp(DashboardUiState.Content(DemoDashboard.data), finance = finance,
                        bankOpenRequest = if (requestedId != null) 1 else 0, bankCandidateId = requestedId)
                } }
            }
        } }
        return bank
    }
    @Test fun realSystemListenerCapturesApprovedEventsAndPostsPrivateReviewReminder() {
        // Wait for MainActivity's initial device-workspace effect before opting the
        // isolated QA workspace into capture; otherwise that effect pauses it.
        compose.waitForIdle()
        val settings = BankCaptureSettings(target)
        originalConfig = settings.read().let { if (it.workspace.startsWith("bank-qa-") && !it.enabled) CaptureConfig() else it }
        previousAccess = BankCaptureSettings.hasAccess(target)
        mainPosted = target.packageManager.checkPermission(Manifest.permission.POST_NOTIFICATIONS, "com.finsync") == PackageManager.PERMISSION_GRANTED
        testPosted = target.packageManager.checkPermission(Manifest.permission.POST_NOTIFICATIONS, "com.finsync.test") == PackageManager.PERMISSION_GRANTED
        systemTest = true
        // Runtime permission revocation kills the instrumentation process. The host
        // grants before this run and restores AFTER the entire runner exits.
        if (Build.VERSION.SDK_INT >= 33) { assertTrue(mainPosted); assertTrue(testPosted) }
        settings.save(CaptureConfig(workspace, true, setOf("com.finsync.test"), System.currentTimeMillis()))
        shell("cmd notification allow_listener ${ComponentName(target, BankNotificationListener::class.java).flattenToString()}")
        compose.waitUntil(15_000) { BankNotificationListener.connected.value }
        assertTrue("Capture must remain enabled after the Activity's startup effects", settings.read().enabled)
        val eventTime = System.currentTimeMillis()
        fun post(tag: String, text: String) {
            // UiAutomation executes a tokenized command, not an interactive shell;
            // encode fixture text so whitespace cannot truncate the intent extra.
            val encoded = android.util.Base64.encodeToString(text.toByteArray(Charsets.UTF_8), android.util.Base64.NO_WRAP)
            val result = shell("am start -W -n com.finsync.test/com.finsync.ui.BankFixtureActivity --es tag $tag --el eventTime $eventTime --es text64 $encoded")
            assertFalse("QA source did not launch: $result", result.contains("Error"))
        }
        val store = FileBankStore(target, workspace)
        post("qa-one", "GD -125.000 VND So du 9.999.999 VND Ma GD QAABC12345")
        compose.waitUntil(15_000) { runBlocking { store.load().candidates.size == 1 } }
        val first = runBlocking { store.load() }.candidates.single()
        assertEquals(125000L, first.amount); assertEquals(BankDirection.Outgoing, first.direction)
        assertNull(runBlocking { store.load() }.personal)
        post("qa-one", "GD -125.000 VND So du 9.999.999 VND Ma GD QAABC12345")
        post("qa-otp", "OTP 987654 giao dich -125.000 VND")
        post("qa-two", "GD -125.000 VND So du 9.999.999 VND Ma GD QAXYZ67890")
        compose.waitUntil(15_000) { runBlocking { store.load().candidates.size == 2 } }
        val saved = runBlocking { store.load() }
        assertTrue(saved.candidates.any { it.possibleDuplicate })
        assertFalse(BankJson.encode(saved).contains("QAABC12345")); assertFalse(BankJson.encode(saved).contains("987654"))
        compose.waitUntil(5_000) { target.getSystemService(android.app.NotificationManager::class.java).activeNotifications.any { it.tag == "bank-${first.id}" } }
        val reminder = target.getSystemService(android.app.NotificationManager::class.java).activeNotifications.first { it.tag == "bank-${first.id}" }.notification
        assertEquals(android.app.Notification.VISIBILITY_PRIVATE, reminder.visibility)
        assertEquals(android.app.NotificationManager.IMPORTANCE_HIGH,
            target.getSystemService(android.app.NotificationManager::class.java).getNotificationChannel(BankReminder.CHANNEL).importance)
        assertFalse(reminder.extras.toString().contains("125.000")); assertNotNull(reminder.contentIntent)
        settings.save(settings.read().copy(enabled = false))
        post("qa-paused", "GD -999.000 VND Ma GD QAPAUSED123")
        assertEquals(2, runBlocking { store.load() }.candidates.size)
        settings.save(settings.read().copy(enabled = true))
        settings.pauseForDifferentWorkspace("another-account")
        assertFalse(settings.read().enabled)
    }
    @Test fun confirmedRecordReloadsAndNativeEditingDeletionUndoRemainDurable() {
        val store = FileBankStore(privateContext, workspace)
        runBlocking { store.edit { BankData(listOf(candidate)) } }
        val finance = FinanceViewModel(); finance.restorePersonal(ledger)
        val bank = host(store, finance)
        val groups = finance.uiState.value.groups
        compose.onNodeWithContentDescription("Bank notifications").performClick()
        click(formatVnd(125000)); click("More options"); click("Review bookkeeping entry")
        compose.onNodeWithText("Select an active personal wallet.").assertIsDisplayed()
        click("Choose Wallet"); click("Bank QA · ${formatVnd(1_000_000)}")
        click("Choose Category"); click("Food")
        compose.onNodeWithText("Select an active personal wallet.").assertDoesNotExist()
        captureScreen("bank-review-before-confirm", compose, nativeWindow = true)
        click("Review bookkeeping entry"); click("Cancel")
        assertEquals(1, finance.uiState.value.entries.size)
        click("Review bookkeeping entry"); click("Confirm entry")
        compose.waitUntil(5_000) { finance.uiState.value.entries.size == 2 }
        assertEquals(875000L, walletBalance(finance.uiState.value.wallets.first(), finance.uiState.value.entries))
        assertEquals(groups, finance.uiState.value.groups)
        val entry = finance.uiState.value.entries.last()
        compose.runOnIdle { finance.saveTransaction(entry.id, "100000", "qa-bank", EntryType.EXPENSE, "Food", today.toString(), "Corrected") }
        compose.waitUntil(5_000) { bank.state.value.data.personal?.entries?.last()?.amount == 100000L }
        compose.runOnIdle { finance.deleteTransaction(entry.id) }
        compose.waitUntil(5_000) { bank.state.value.data.personal?.entries?.size == 1 }
        compose.runOnIdle { finance.restoreTransaction(entry.copy(amount = 100000)) }
        compose.waitUntil(5_000) { bank.state.value.data.personal?.entries?.size == 2 }
        val disk = runBlocking { FileBankStore(privateContext, workspace).load() }
        assertEquals(finance.uiState.value.personalLedger(), disk.personal)
        val nextFinance = FinanceViewModel()
        val next = BankController(workspace, FileBankStore(privateContext, workspace), nextFinance, scope)
        runBlocking { next.open() }
        assertEquals(finance.uiState.value.entries, nextFinance.uiState.value.entries); next.close()
        click("History"); captureScreen("bank-handled-history", compose, nativeWindow = true)
    }
    @Test fun unknownLargeTextAlertRequiresClassificationAndCanBeIgnoredThenRestored() {
        val store = FileBankStore(privateContext, workspace)
        val uncertain = candidate.copy(amount = null, direction = BankDirection.Unknown)
        runBlocking { store.edit { BankData(listOf(uncertain)) } }
        val finance = FinanceViewModel(); finance.restorePersonal(ledger)
        host(store, finance, large = true)
        compose.onNodeWithContentDescription("Bank notifications").performClick()
        click("Amount needs review"); click("More options"); click("Review bookkeeping entry")
        compose.onNodeWithText("Choose what this transaction was for.").assertIsDisplayed()
        click("Expense"); click("Review bookkeeping entry")
        compose.onNodeWithText("Enter a positive whole-VND amount.").assertIsDisplayed()
        captureScreen("bank-uncertain-large-text", compose, nativeWindow = true)
        click("Ignore this alert"); click("Confirm ignore")
        click("History"); click("Amount needs review"); click("Return to Inbox"); click("Inbox")
        assertEquals(1, finance.uiState.value.entries.size)
        click("Set up bank alerts")
        val grant = if (BankCaptureSettings.hasAccess(target)) "Manage notification access" else "Grant notification access"
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(grant))
        compose.onNodeWithText(grant).assertIsNotEnabled()
        captureScreen("bank-permission-setup-large", compose, nativeWindow = true)
    }
    @Test fun atomicCodecAndAccountIsolationProtectExistingWalletHistory() = runBlocking {
        val store = FileBankStore(privateContext, workspace)
        val original = BankData(listOf(candidate), ledger)
        store.edit { original }
        assertEquals(original, store.load())
        assertEquals(BankData(), FileBankStore(privateContext, "$workspace-other").load())
        val invalid = ledger.copy(entries = ledger.entries.map { it.copy(walletId = "missing") })
        assertTrue(runCatching { store.edit { it.copy(personal = invalid) } }.isFailure)
        assertEquals(original, store.load())
        val path = File(root, "bank-notifications/${digest(workspace)}.json")
        val bytes = path.readBytes(); path.writeText("broken-json")
        assertTrue(runCatching { store.load() }.isFailure)
        val finance = FinanceViewModel(); val bank = BankController(workspace, store, finance, scope)
        bank.open(); assertTrue(bank.state.value.loadFailed)
        assertEquals("broken-json", path.readText())
        path.writeBytes(bytes); bank.open(); assertFalse(bank.state.value.loadFailed)
        assertEquals(ledger, finance.uiState.value.personalLedger()); bank.close()
    }
    @Test fun notificationTargetOpensQuickSheetAndSavesOnceWithoutExtraConfirmation() {
        val store = FileBankStore(privateContext, workspace)
        val targetAlert = candidate
        val other = candidate.copy(id = "f".repeat(64), amount = 200000, direction = BankDirection.Incoming)
        runBlocking { store.edit { BankData(listOf(targetAlert, other)) } }
        val finance = FinanceViewModel(); finance.restorePersonal(ledger)
        val bank = host(store, finance, requestedId = targetAlert.id)
        compose.onNodeWithText("What was this for?").assertIsDisplayed()
        compose.onNodeWithText("Confirm entry").assertDoesNotExist()
        click("Choose Wallet"); click("Bank QA"); click("Food")
        captureScreen("bank-quick-overview-popup", compose, nativeWindow = true)
        click("Later")
        assertEquals(1, finance.uiState.value.entries.size)
        assertEquals(2, bank.state.value.data.candidates.count { it.status == CaptureStatus.Pending })
        compose.onNodeWithContentDescription("Bank notifications").performClick()
        click(formatVnd(125000)); click("Choose Wallet"); click("Bank QA"); click("Food")
        captureScreen("bank-quick-popup", compose, nativeWindow = true)
        compose.onNodeWithText("Save transaction").assertIsDisplayed().performTouchInput { doubleClick() }
        compose.waitUntil(5_000) { bank.state.value.data.candidates.first().status == CaptureStatus.Recorded }
        compose.onNodeWithText("What was this for?").assertDoesNotExist()
        assertEquals(2, finance.uiState.value.entries.size)
        assertEquals(875000L, walletBalance(finance.uiState.value.wallets.first(), finance.uiState.value.entries))
        assertEquals(CaptureStatus.Pending, bank.state.value.data.candidates.last().status)
        assertEquals(finance.uiState.value.personalLedger(), runBlocking { store.load() }.personal)
    }
    @Test fun rememberedWalletIsScopedDurableAndCanBeForgottenWithoutBooking() {
        val settings = BankCaptureSettings(target)
        val store = FileBankStore(privateContext, workspace)
        val alert = candidate
        runBlocking { store.edit { BankData(listOf(alert)) } }
        val finance = FinanceViewModel(); finance.restorePersonal(ledger)
        host(store, finance, requestedId = alert.id)
        click("Choose Wallet"); click("Bank QA"); click("Use this wallet next time")
        compose.waitUntil(5_000) { settings.defaultWallet(workspace, alert.sourcePackage) == "qa-bank" }
        assertEquals("qa-bank", BankCaptureSettings(target).defaultWallet(workspace, alert.sourcePackage))
        assertNull(settings.defaultWallet("$workspace-other", alert.sourcePackage))
        assertNull(settings.defaultWallet(workspace, "qa.other.bank"))
        click("Later")
        compose.onNodeWithContentDescription("Bank notifications").performClick()
        click(formatVnd(125000))
        compose.onNodeWithText("Bank QA").assertIsDisplayed()
        click("Forget default wallet")
        compose.waitUntil(5_000) { settings.defaultWallet(workspace, alert.sourcePackage) == null }
        click("Later")
        assertEquals(ledger, finance.uiState.value.personalLedger())
        assertEquals(CaptureStatus.Pending, runBlocking { store.load() }.candidates.single().status)
    }
    @Test fun missingDefaultWalletRequiresManualChoice() {
        BankCaptureSettings(target).setDefaultWallet(workspace, "qa.bank.fixture", "deleted-wallet")
        val store = FileBankStore(privateContext, workspace)
        val alert = candidate
        runBlocking { store.edit { BankData(listOf(alert)) } }
        val finance = FinanceViewModel(); finance.restorePersonal(ledger)
        host(store, finance, requestedId = alert.id)
        compose.onNodeWithText("Choose Wallet").assertIsDisplayed()
        click("Food"); click("Save transaction")
        compose.onNodeWithText("What was this for?").assertIsDisplayed()
        assertEquals(1, finance.uiState.value.entries.size)
        assertEquals(CaptureStatus.Pending, runBlocking { store.load() }.candidates.single().status)
    }
    @Test fun moreOptionsKeepsQuickWalletPurposeAndAmount() {
        val store = FileBankStore(privateContext, workspace)
        val alert = candidate
        runBlocking { store.edit { BankData(listOf(alert)) } }
        val finance = FinanceViewModel(); finance.restorePersonal(ledger)
        host(store, finance, large = true, requestedId = alert.id)
        click("Choose Wallet"); click("Bank QA"); click("Food")
        captureScreen("bank-quick-popup-large", compose, nativeWindow = true)
        click("More options")
        compose.onNodeWithText("Amount (VND)").assertTextContains("125000")
        compose.onNode(hasScrollToIndexAction() and hasAnyAncestor(hasTestTag("bank-advanced-overlay"))).performScrollToNode(hasText("Bank QA · ${formatVnd(1_000_000)}"))
        compose.onNodeWithText("Bank QA · ${formatVnd(1_000_000)}").assertIsDisplayed()
        compose.onNode(hasScrollToIndexAction() and hasAnyAncestor(hasTestTag("bank-advanced-overlay"))).performScrollToNode(hasText("Food"))
        compose.onNodeWithText("Food").assertIsDisplayed()
        assertEquals(1, finance.uiState.value.entries.size)
    }
    @Test fun promptOverProfileDoesNotDiscardAnUnsavedProfileEdit() {
        val store = FileBankStore(privateContext, workspace)
        val alert = candidate
        runBlocking { store.edit { BankData(listOf(alert)) } }
        val finance = FinanceViewModel(); finance.restorePersonal(ledger)
        val bank = BankController(workspace, store, finance, scope); controller = bank
        runBlocking { bank.open() }
        val user = AuthUser(UUID.randomUUID().toString(), "bank-overlay@example.test", "Profile QA", "ROLE_USER")
        val repository = object : AuthRepository {
            override suspend fun signIn(email: String, password: String) = AuthResult.SignedIn(AuthSession("qa-session", user, System.currentTimeMillis() + 3_600_000))
            override suspend fun register(email: String, password: String) = AuthResult.Failed(AuthFailure.Unsupported)
            override suspend fun requestPasswordReset(email: String) = AuthResult.Failed(AuthFailure.Unsupported)
        }
        val auth = AuthController(repository, scope)
        val requests = MutableStateFlow(0)
        compose.activityRule.scenario.onActivity { activity -> activity.setContent {
            keyboard.Observe()
            val request by requests.collectAsState()
            CompositionLocalProvider(LocalBankController provides bank) { FinSyncTheme {
                AuthHost(auth, request, alert.id) { onAccount -> FinSyncApp(DashboardUiState.Content(DemoDashboard.data), finance = finance, onAccount = onAccount) }
            } }
        } }
        compose.runOnIdle { auth.open(AuthPage.Login); auth.submit(user.email, "test-password") }
        compose.waitUntil(5_000) { auth.state.value.authenticated }
        compose.onNodeWithContentDescription("Account").performClick()
        click("Edit profile")
        compose.onNodeWithText("Display name").performTextReplacement("Unsaved profile QA")
        keyboard.close(compose)
        compose.runOnIdle { requests.value = 1 }
        compose.onNodeWithText("What was this for?").assertIsDisplayed()
        click("Later")
        compose.onNodeWithText("Display name").assertTextContains("Unsaved profile QA")
        assertEquals(AuthPage.Account, auth.state.value.page)
        assertEquals(1, finance.uiState.value.entries.size)
    }
}
