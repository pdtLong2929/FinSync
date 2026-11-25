package com.finsync.ui

import android.content.ContextWrapper
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.finsync.MainActivity
import com.finsync.data.*
import com.finsync.domain.*
import com.finsync.ui.dashboard.*
import com.finsync.ui.groups.*
import com.finsync.ui.theme.FinSyncTheme
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.time.LocalDate
import java.util.UUID

class DeviceGroupLedgerTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val models = ViewModelStore()
    private val keyboard = TestKeyboard()
    private val root by lazy { File(compose.activity.cacheDir, "pa4-group-test-${UUID.randomUUID()}") }
    private val context by lazy { object : ContextWrapper(compose.activity.applicationContext) {
        override fun getNoBackupFilesDir(): File = root
    } }
    @After fun cleanup() {
        compose.runOnIdle { models.clear() }
        if (root.exists()) root.deleteRecursively()
    }

    private fun store(workspace: String) = FileGroupLedgerStore(context, workspace)
    private fun click(text: String) {
        keyboard.close(compose)
        if (runCatching { compose.onNodeWithText(text).assertIsDisplayed() }.isFailure)
            compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(text))
        compose.onNodeWithText(text).performClick()
    }
    private fun input(label: String, text: String) {
        keyboard.close(compose)
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(label))
        compose.onNodeWithText(label).performTextReplacement(text)
        keyboard.close(compose)
    }

    @Test fun emptyStartCreateMembersSplitSettleAndReloadFromDisk() {
        lateinit var finance: FinanceViewModel
        val storage = store("owner-a")
        compose.activityRule.scenario.onActivity { activity ->
            finance = ViewModelProvider(models, ViewModelProvider.NewInstanceFactory())[FinanceViewModel::class.java]
            finance.groupStorage.bind("owner-a", storage)
            activity.setContent {
                keyboard.Observe()
                FinSyncTheme { FinSyncApp(DashboardUiState.Content(DemoDashboard.data), finance = finance) }
            }
        }
        compose.waitUntil(5_000) { !finance.groupStorage.state.value.loading }
        click("Groups")
        compose.onNodeWithText("Roommates").assertDoesNotExist()
        click("Create group")
        click("Save group")
        compose.onNodeWithText("Use a name of 1–40", substring = true).assertIsDisplayed()
        input("Group name", "PA4 household")
        click("Save group")
        compose.waitUntil(5_000) { finance.uiState.value.groups.size == 1 && !finance.groupStorage.state.value.saving }
        click("PA4 household")
        click("Members")
        input("Member name", "Linh")
        click("Add member")
        compose.waitUntil(5_000) { finance.uiState.value.groups.single().members.size == 2 && !finance.groupStorage.state.value.saving }
        compose.onNodeWithText("Member added").assertIsDisplayed()
        click("Back")
        click("Edit group")
        compose.onNodeWithText("Group name").assertTextContains("PA4 household")
        input("Group name", "Household ledger")
        click("Save group")
        compose.waitUntil(5_000) { finance.uiState.value.groups.single().name == "Household ledger" && !finance.groupStorage.state.value.saving }
        click("Edit group")
        input("Group name", "Discard this name")
        click("Back")
        click("Discard")
        click("Edit group")
        compose.onNodeWithText("Group name").assertTextContains("Household ledger")
        click("Back")
        click("Add group expense")
        input("Expense title", "Groceries")
        input("Amount (VND)", "90001")
        click("Review split")
        click("Save shared expense")
        compose.waitUntil(5_000) { finance.uiState.value.groupExpenses.size == 1 && !finance.groupStorage.state.value.saving }
        val expense = finance.uiState.value.groupExpenses.single()
        assertEquals(45001L, expense.shares.getValue("me"))
        assertEquals(LocalDate.now(), expense.date)
        click("Who owes whom")
        click("View debt details")
        click("Mark as settled")
        input("Amount (VND)", "10000")
        click("Review settlement")
        compose.onNodeWithText("Confirm partial payment?").assertIsDisplayed()
        click("Confirm partial payment")
        compose.waitUntil(5_000) { finance.uiState.value.settlements.size == 1 && !finance.groupStorage.state.value.saving }
        captureScreen("pa4-device-group-debts", compose, nativeWindow = true)
        val reloaded = runBlocking { FileGroupLedgerStore(context, "owner-a").load() }
        assertEquals(finance.uiState.value.groupLedger(), reloaded)
        assertTrue(runBlocking { store("owner-b").load() }.groups.isEmpty())
        val group = reloaded.groups.single()
        val remaining = simplifiedDebts(groupBalances(group, reloaded.expenses, reloaded.settlements)).single()
        assertEquals(35000L, remaining.amount)
        assertEquals(DemoFixtures.entries, finance.uiState.value.entries)
        assertEquals(DemoFixtures.wallets, finance.uiState.value.wallets)
        // Recreate both the Activity and the financial ViewModel, then read from disk.
        compose.activityRule.scenario.recreate()
        compose.activityRule.scenario.onActivity { activity ->
            models.clear()
            finance = ViewModelProvider(models, ViewModelProvider.NewInstanceFactory())[FinanceViewModel::class.java]
            finance.groupStorage.bind("owner-a", FileGroupLedgerStore(context, "owner-a"))
            activity.setContent {
                keyboard.Observe()
                FinSyncTheme { FinSyncApp(DashboardUiState.Content(DemoDashboard.data), finance = finance) }
            }
        }
        compose.waitUntil(5_000) { !finance.groupStorage.state.value.loading }
        click("Groups")
        click("Household ledger")
        click("Who owes whom")
        compose.onNodeWithText("35.000 ₫").assertIsDisplayed()
        assertEquals(reloaded, finance.uiState.value.groupLedger())
        captureScreen("pa4-groups-restored", compose, nativeWindow = true)
    }

    @Test fun damagedLedgerIsReportedAndNeverReplacedByEmptyData() = runBlocking {
        val storage = store("damaged")
        val group = GroupFixtures.groups.first()
        val ledger = GroupLedger(listOf(group), GroupFixtures.expenses)
        storage.save(ledger)
        val file = root.resolve("group-ledgers").listFiles()!!.single { it.extension == "json" }
        file.writeText("{broken json")
        assertTrue(runCatching { storage.load() }.isFailure)
        assertEquals("{broken json", file.readText())
        lateinit var finance: FinanceViewModel
        compose.activityRule.scenario.onActivity { activity ->
            finance = ViewModelProvider(models, ViewModelProvider.NewInstanceFactory())[FinanceViewModel::class.java]
            finance.groupStorage.bind("damaged", storage)
            activity.setContent {
                keyboard.Observe()
                val state by finance.uiState.collectAsState()
                FinSyncTheme { GroupFlow(state, finance, "", {}) }
            }
        }
        compose.waitUntil(5_000) { finance.groupStorage.state.value.loadFailed }
        compose.onNodeWithText("Try again").assertIsDisplayed()
        compose.onNodeWithText("Create group").assertDoesNotExist()
        storage.save(ledger)
        click("Try again")
        compose.waitUntil(5_000) { !finance.groupStorage.state.value.loading && !finance.groupStorage.state.value.loadFailed }
        compose.onNodeWithText("Roommates").assertIsDisplayed()
        assertEquals(ledger, storage.load())
        val malformed = ledger.copy(expenses = ledger.expenses.map { it.copy(amount = it.amount + 1) })
        assertTrue(runCatching { storage.save(malformed) }.isFailure)
        assertEquals(ledger, storage.load())
    }

    @Test fun saveFailureKeepsFormAndRetryCommitsOnce() {
        root.mkdirs()
        val blocker = root.resolve("group-ledgers").apply { writeText("Test-only blocked storage directory") }
        lateinit var finance: FinanceViewModel
        compose.activityRule.scenario.onActivity { activity ->
            finance = ViewModelProvider(models, ViewModelProvider.NewInstanceFactory())[FinanceViewModel::class.java]
            finance.groupStorage.bind("write-failure", store("write-failure"))
            activity.setContent {
                keyboard.Observe()
                val state by finance.uiState.collectAsState()
                var route by remember { mutableStateOf("") }
                FinSyncTheme { GroupFlow(state, finance, route, { route = it }) }
            }
        }
        compose.waitUntil(5_000) { !finance.groupStorage.state.value.loading }
        click("Create group")
        input("Group name", "Retry household")
        click("Save group")
        compose.waitUntil(5_000) { !finance.groupStorage.state.value.saving }
        compose.onNodeWithText("Couldn't save on this device.", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Group name").assertTextContains("Retry household")
        assertTrue(finance.uiState.value.groups.isEmpty())
        assertTrue(blocker.delete())
        click("Save group")
        compose.waitUntil(5_000) { finance.uiState.value.groups.size == 1 && !finance.groupStorage.state.value.saving }
        assertEquals("Retry household", runBlocking { store("write-failure").load() }.groups.single().name)
    }

    @Test fun changedDebtSuggestionDoesNotClaimGroupIsSettled() {
        val day = LocalDate.of(2026, 11, 1)
        val group = ExpenseGroup("qa", "QA", "HOME", "", listOf("me", "b", "c", "d").map { GroupMember(it, it) })
        val expenses = listOf(
            GroupExpense("one", "qa", "One", 120, "c", mapOf("me" to 100L, "b" to 20L, "c" to 0L), day, "Food"),
            GroupExpense("two", "qa", "Two", 70, "d", mapOf("b" to 70L, "d" to 0L), day, "Food"),
        )
        val initial = FinanceState(groups = listOf(group), groupExpenses = expenses,
            settlements = listOf(Settlement("partial", "qa", "me", "c", 30, day)))
        var state by mutableStateOf(initial)
        var returned = false
        compose.activityRule.scenario.onActivity { it.setContent {
            keyboard.Observe()
            FinSyncTheme { DebtDetailsScreen(group, state, "me", "c", { returned = true }, {}) }
        } }
        compose.onNodeWithText("Transfer suggestion updated").assertIsDisplayed()
        compose.onNodeWithText("All settled").assertDoesNotExist()
        compose.onNodeWithText("Mark as settled").assertDoesNotExist()
        click("View current debts")
        assertTrue(returned)
        compose.runOnIdle { state = initial.copy(settlements = initial.settlements + listOf(
            Settlement("b-c", "qa", "b", "c", 90, day), Settlement("me-d", "qa", "me", "d", 70, day))) }
        compose.onNodeWithText("All settled").assertIsDisplayed()
        compose.onNodeWithText("View current debts").assertDoesNotExist()
    }
}
