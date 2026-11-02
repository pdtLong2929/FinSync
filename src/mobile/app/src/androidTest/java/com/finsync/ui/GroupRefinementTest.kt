package com.finsync.ui

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.finsync.MainActivity
import com.finsync.domain.*
import com.finsync.ui.groups.*
import com.finsync.ui.theme.FinSyncTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class GroupRefinementTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val keyboard = TestKeyboard()
    private fun host(content: @Composable () -> Unit) {
        compose.activityRule.scenario.onActivity { it.setContent {
            keyboard.Observe()
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 1.5f)) {
                FinSyncTheme { Box(Modifier.width(320.dp).fillMaxHeight().safeDrawingPadding()) { content() } }
            }
        } }
    }
    private fun click(text: String) {
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
    @Test fun splitValidationAndDuplicateSaveAtLargeText() {
        val group = FinanceState().groups.first()
        var writes = 0
        host { GroupExpenseForm(group, {}, { _, _, _, _, _, _, _, _ -> writes++; null }) }
        click("Review split")
        compose.onNodeWithText("Add group expense").assertIsDisplayed()
        assertEquals(0, writes)
        input("Expense title", "Groceries"); input("Amount (VND)", "90000")
        click("Review split")
        click("Exact")
        input("You (VND)", "30000"); input("Linh (VND)", "30000"); input("Minh (VND)", "20000")
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Difference: 10.000 ₫ left to allocate"))
        compose.onNodeWithText("Difference: 10.000 ₫ left to allocate").assertIsDisplayed()
        compose.onNodeWithText("Save shared expense").assertIsNotEnabled()
        input("Minh (VND)", "30000")
        compose.onNodeWithText("Save shared expense").assertIsEnabled()
        click("Percentage")
        input("You (%)", "40"); input("Linh (%)", "30"); input("Minh (%)", "20")
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Difference: 9.000 ₫ left to allocate"))
        compose.onNodeWithText("Difference: 9.000 ₫ left to allocate").assertIsDisplayed()
        compose.onNodeWithText("Save shared expense").assertIsNotEnabled()
        input("Minh (%)", "30")
        input("Minh (%)", "-1")
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Use percentages from 0% to 100% with up to 2 decimal places."))
        compose.onNodeWithText("Use percentages from 0% to 100% with up to 2 decimal places.").assertIsDisplayed()
        input("You (%)", "70")
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Difference: 0 ₫ · complete all participant fields"))
        compose.onNodeWithText("Difference: 0 ₫ · complete all participant fields").assertIsDisplayed()
        input("Minh (%)", "30")
        input("You (%)", "40")
        compose.onNodeWithText("Save shared expense").assertIsEnabled()
        captureScreen("group-split-large", compose, nativeWindow = true)
        compose.onNodeWithText("Save shared expense").performTouchInput { doubleClick() }
        compose.onNodeWithText("Saving…").assertIsNotEnabled()
        assertEquals(1, writes)
    }
    @Test fun sharesSplitRejectsNegativeDataAndPreviewsPayerRounding() {
        val group = FinanceState().groups.first()
        var saved: Map<String, Long>? = null
        host { GroupExpenseForm(group, {}, { _, amount, payer, _, _, people, mode, inputs ->
            saved = splitExpense(parseVnd(amount)!!, payer, people, mode, inputs)
            null
        }) }
        input("Expense title", "Shared supplies")
        input("Amount (VND)", "90001")
        click("Review split")
        click("Shares")
        input("You (shares)", "1")
        input("Linh (shares)", "-1")
        input("Minh (shares)", "2")
        compose.onNodeWithText("Save shared expense").assertIsNotEnabled()
        compose.onNodeWithText("Enter each share count as a nonnegative whole number.").assertIsDisplayed()
        input("Linh (shares)", "2")
        compose.onNodeWithText("Save shared expense").assertIsEnabled().performClick()
        compose.runOnIdle { assertEquals(mapOf("me" to 18001L, "linh" to 36000L, "minh" to 36000L), saved) }
    }
    @Test fun settlementRejectsInvalidReviewAndSavesOnceWithKeyboard() {
        val vm = FinanceViewModel()
        val before = vm.uiState.value
        host { SettlementScreen(before.groups.first(), "linh", "me", before, {}, { from, to, amount, date ->
            vm.recordSettlement("home", from, to, amount, date)
        }) }
        input("Amount (VND)", "100001")
        click("Review settlement")
        compose.onNodeWithText("Mark this debt as settled?").assertDoesNotExist()
        compose.onNodeWithText("Amount must be positive", substring = true).assertIsDisplayed()
        input("Amount (VND)", "10000")
        compose.onNodeWithText("Amount (VND)").performTouchInput { click() }
        keyboard.awaitOpen(compose)
        compose.onNodeWithText("Review settlement").assertIsDisplayed().performClick()
        compose.onNodeWithText("Confirm partial payment?").assertIsDisplayed()
        captureScreen("group-confirm-large", compose, nativeWindow = true)
        compose.onNodeWithText("Confirm partial payment").performClick()
        compose.onNodeWithText("Review settlement").assertIsNotEnabled()
        assertEquals(1, vm.uiState.value.settlements.size)
        assertEquals(before.entries, vm.uiState.value.entries)
        assertEquals(before.wallets, vm.uiState.value.wallets)
    }
}
