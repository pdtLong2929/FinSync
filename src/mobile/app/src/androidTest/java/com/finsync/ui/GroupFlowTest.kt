package com.finsync.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.finsync.MainActivity
import com.finsync.domain.*
import com.finsync.ui.dashboard.*
import com.finsync.ui.groups.SettlementScreen
import com.finsync.ui.theme.FinSyncTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class GroupFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val keyboard = TestKeyboard()
    private fun host(content: @Composable () -> Unit) {
        compose.activityRule.scenario.onActivity { it.setContent { keyboard.Observe(); content() } }
    }
    private fun click(text: String) {
        if (runCatching { compose.onNodeWithText(text).assertIsDisplayed() }.isFailure)
            compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(text))
        compose.onNodeWithText(text).performClick()
    }
    @Test fun splitExpenseAndRecordExternalSettlement() {
        host { FinSyncTheme { FinSyncApp(DashboardUiState.Content(DemoDashboard.data)) } }
        compose.onNodeWithText("Groups").performClick()
        captureScreen("liquid-metal-e-groups", compose, nativeWindow = true)
        click("Join with invitation")
        compose.onNodeWithText("Invitation code or link").performTextInput("DRAFT")
        captureScreen("liquid-metal-e-invitation", compose, nativeWindow = true)
        click("Close")
        click("Join with invitation")
        compose.onNodeWithText("Invitation code or link").assertTextContains("DRAFT")
        click("Close")
        click("Roommates")
        compose.onNodeWithText("You · Linh · Minh").assertIsDisplayed()
        captureScreen("liquid-metal-e-detail", compose, nativeWindow = true)
        click("Add group expense")
        compose.onNodeWithText("Expense title").performTextInput("Demo groceries")
        compose.onNodeWithText("Amount (VND)").performTextInput("90000")
        keyboard.close(compose)
        click("Review split")
        captureScreen("liquid-metal-e-split", compose, nativeWindow = true)
        click("Save shared expense")
        click("Who owes whom")
        captureScreen("liquid-metal-e-debts", compose, nativeWindow = true)
        compose.onAllNodesWithText("View debt details").onFirst().performClick()
        compose.onNodeWithText("Debt details").assertIsDisplayed()
        click("Mark as settled")
        click("Review settlement")
        compose.onNodeWithText("Mark this debt as settled?").assertIsDisplayed()
        compose.waitForIdle(); captureScreen("settlement-confirmation", compose)
        compose.onNodeWithText("Confirm settlement").performClick()
        compose.onNodeWithText("Who owes whom").assertIsDisplayed()
        captureScreen("group-debts", compose)
    }

    @Test fun partialSettlementExplainsRemainingBalanceBeforeConfirmation() {
        val state = FinanceState()
        val group = state.groups.first()
        val debt = simplifiedDebts(groupBalances(group, state.groupExpenses, state.settlements)).first()
        var recordedAmount: String? = null
        host { FinSyncTheme {
            SettlementScreen(group, debt.from, debt.to, state, {}, { _, _, amount, _ -> recordedAmount = amount; null })
        } }
        compose.onNodeWithText("Amount (VND)").performTextReplacement("1")
        compose.onNodeWithText("Remaining after this payment:", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Review settlement").performClick()
        compose.waitUntil(5_000) {
            runCatching { compose.onNodeWithText("Confirm partial payment?").assertIsDisplayed() }.isSuccess
        }
        compose.onNodeWithText("Confirm partial payment?").assertIsDisplayed()
        compose.onNodeWithText("will remain outstanding.", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Confirm partial payment").performClick()
        compose.runOnIdle { assertEquals("1", recordedAmount) }
    }
}
