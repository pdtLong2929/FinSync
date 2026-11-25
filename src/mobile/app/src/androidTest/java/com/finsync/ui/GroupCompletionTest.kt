package com.finsync.ui

import android.content.ContextWrapper
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.finsync.MainActivity
import com.finsync.data.*
import com.finsync.domain.*
import com.finsync.ui.dashboard.*
import com.finsync.ui.groups.memberName
import com.finsync.ui.theme.FinSyncTheme
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.time.LocalDate
import java.util.UUID

class GroupCompletionTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val keyboard = TestKeyboard()
    private val root by lazy { File(compose.activity.cacheDir, "group-completion-${UUID.randomUUID()}") }
    private val context by lazy { object : ContextWrapper(compose.activity.applicationContext) {
        override fun getNoBackupFilesDir() = root
    } }
    private val group = ExpenseGroup("audit", "Trip ledger", "TRIP", "", listOf(
        GroupMember("me", "You"), GroupMember("linh", "Linh"), GroupMember("minh", "Minh")))
    private val today = LocalDate.now()
    private fun store() = FileGroupLedgerStore(context, "completion")
    @After fun cleanup() { if (root.exists()) root.deleteRecursively() }

    private fun click(text: String) {
        keyboard.close(compose)
        if (runCatching { compose.onNodeWithText(text).assertIsDisplayed() }.isFailure)
            compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(text))
        compose.onNodeWithText(text).performClick()
    }
    private fun input(label: String, value: String) {
        keyboard.close(compose)
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(label))
        compose.onNodeWithText(label).performTextReplacement(value)
        keyboard.close(compose)
    }

    @Test fun threeSectionsRemainReadableAt320dpAnd150Percent() {
        compose.activityRule.scenario.onActivity { activity -> activity.setContent {
            keyboard.Observe()
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 1.5f)) {
                FinSyncTheme { Box(Modifier.width(320.dp).fillMaxHeight()) {
                    FinSyncApp(DashboardUiState.Content(DemoDashboard.data))
                } }
            }
        } }
        click("Groups"); click("Roommates")
        for (section in listOf("Balances", "Members", "Expenses")) {
            click(section)
            compose.onNodeWithText(section).assertIsSelected()
            captureScreen("pa4-large-${section.lowercase()}", compose, nativeWindow = true)
            for (label in listOf("Expenses", "Balances", "Members")) {
                compose.onNodeWithText(label).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { read ->
                    val results = mutableListOf<TextLayoutResult>()
                    read(results)
                    assertFalse("$label layout=${results.single().size} lines=${results.single().lineCount} width=${results.single().didOverflowWidth} height=${results.single().didOverflowHeight}", results.single().hasVisualOverflow)
                    assertEquals("$label should stay on one line", 1, results.single().lineCount)
                }
            }
        }
        captureScreen("pa4-workspace-large", compose, nativeWindow = true)
    }

    @Test fun expensesAndPaymentsCanBeCorrectedRemovedUndoneAndReloaded() {
        val ids = group.members.map { it.id }
        val expense = GroupExpense("shared", group.id, "Dinner", 100001, "me",
            splitExpense(100001, "me", ids, SplitMode.Equal), today, "Food", SplitMode.Equal)
        val storage = store()
        runBlocking { storage.save(GroupLedger(listOf(group), listOf(expense))) }
        lateinit var finance: FinanceViewModel
        compose.activityRule.scenario.onActivity { activity ->
            finance = FinanceViewModel()
            finance.groupStorage.bind("completion", storage)
            activity.setContent { keyboard.Observe(); FinSyncTheme {
                FinSyncApp(DashboardUiState.Content(DemoDashboard.data), finance = finance)
            } }
        }
        compose.waitUntil(5_000) { !finance.groupStorage.state.value.loading }
        val personal = finance.uiState.value.entries
        fun waitSaved(check: () -> Boolean) = compose.waitUntil(5_000) { !finance.groupStorage.state.value.saving && check() }
        click("Groups"); click(group.name)
        click("Members"); compose.onNodeWithText("Linh").assertIsDisplayed()
        click("Expenses"); click("Dinner")
        captureScreen("pa4-expense-detail", compose, nativeWindow = true)
        click("Edit expense")
        compose.onNodeWithText("Expense title").assertTextContains("Dinner")
        compose.onNodeWithText("Amount (VND)").assertTextContains("100001")
        input("Amount (VND)", "100004")
        click("Review split")
        compose.onNodeWithText("Equal").assertIsSelected()
        click("Save shared expense")
        waitSaved { finance.uiState.value.groupExpenses.single().amount == 100004L }
        click("Remove expense"); click("Cancel")
        assertEquals(1, finance.uiState.value.groupExpenses.size)
        click("Remove expense"); click("Confirm removal")
        waitSaved { finance.uiState.value.groupExpenses.isEmpty() }
        compose.onNodeWithText("Undo").assertIsDisplayed().performClick()
        waitSaved { finance.uiState.value.groupExpenses.size == 1 }
        assertEquals(SplitMode.Equal, finance.uiState.value.groupExpenses.single().splitMode)
        captureScreen("pa4-expenses-complete", compose, nativeWindow = true)
        click("Balances")
        captureScreen("pa4-balances-complete", compose, nativeWindow = true)
        keyboard.close(compose)
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Record payment"))
        compose.onAllNodesWithText("Record payment").onFirst().performClick()
        click("Partial payment"); input("Amount (VND)", "10000")
        click("Review settlement"); click("Confirm partial payment")
        waitSaved { finance.uiState.value.settlements.size == 1 }
        val paid = finance.uiState.value.settlements.single()
        val title = "${group.memberName(paid.from)} → ${group.memberName(paid.to)}"
        click(title)
        input("Amount (VND)", "4000")
        click("Review correction"); click("Confirm correction")
        waitSaved { finance.uiState.value.settlements.single().amount == 4000L }
        click(title); click("Remove payment record"); click("Confirm removal")
        waitSaved { finance.uiState.value.settlements.isEmpty() }
        compose.onNodeWithText("Undo").assertIsDisplayed().performClick()
        waitSaved { finance.uiState.value.settlements.size == 1 }
        assertEquals(4000L, finance.uiState.value.settlements.single().amount)
        assertEquals(personal, finance.uiState.value.entries)
        val disk = runBlocking { storage.load() }
        assertEquals(finance.uiState.value.groupExpenses, disk.expenses)
        assertEquals(finance.uiState.value.settlements, disk.settlements)
        assertEquals(0L, groupBalances(group, disk.expenses, disk.settlements).values.sum())
        captureScreen("pa4-payment-restored", compose, nativeWindow = true)
    }

    @Test fun splitMetadataRoundTripsAndLegacyVersionOneKeepsAmounts() = runBlocking {
        val people = group.members.map { it.id }
        val entries = SplitMode.entries.map { mode ->
            val inputs = when (mode) {
                SplitMode.Equal -> emptyMap()
                SplitMode.Exact -> mapOf("me" to "33335", "linh" to "33333", "minh" to "33333")
                SplitMode.Percentage -> mapOf("me" to "33.34", "linh" to "33.33", "minh" to "33.33")
                SplitMode.Shares -> people.associateWith { "1" }
            }
            GroupExpense(mode.name, group.id, mode.name, 100001, "me",
                splitExpense(100001, "me", people, mode, inputs), today, "Food", mode, inputs)
        }
        val ledger = GroupLedger(listOf(group), entries)
        val storage = store()
        storage.save(ledger)
        assertEquals(ledger, storage.load())
        val old = JSONObject(GroupLedgerJson.encode(ledger))
        val array = old.getJSONArray("expenses")
        for (i in 0 until array.length()) { array.getJSONObject(i).remove("splitMode"); array.getJSONObject(i).remove("splitInputs") }
        val legacy = GroupLedgerJson.decode(old.toString())
        assertTrue(legacy.expenses.all { it.splitMode == null && it.splitInputs.isEmpty() })
        assertEquals(entries.map { it.shares }, legacy.expenses.map { it.shares })
        val invalid = ledger.copy(expenses = listOf(entries.first().copy(splitMode = SplitMode.Exact, splitInputs = people.associateWith { "1" })))
        assertTrue(runCatching { storage.save(invalid) }.isFailure)
        assertEquals(ledger, storage.load())
    }
}

