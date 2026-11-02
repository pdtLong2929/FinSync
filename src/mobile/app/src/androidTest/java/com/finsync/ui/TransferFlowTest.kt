package com.finsync.ui

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.finsync.MainActivity
import com.finsync.ui.dashboard.*
import com.finsync.ui.theme.FinSyncTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class TransferFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val keyboard = TestKeyboard()
    private fun open(finance: FinanceViewModel, large: Boolean = false) {
        compose.activityRule.scenario.onActivity { it.setContent {
            keyboard.Observe()
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, if (large) 1.5f else 1f)) {
                FinSyncTheme { Box(Modifier.width(if (large) 320.dp else 393.dp).fillMaxHeight()) {
                    FinSyncApp(DashboardUiState.Content(DemoDashboard.data), finance = finance)
                } }
            }
        } }
        compose.onNodeWithText("Wallets").performClick()
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Transfer between wallets"))
        compose.onNodeWithText("Transfer between wallets").performClick()
    }
    @Test fun reviewConfirmAndHistoryPreserveBalances() {
        val finance = FinanceViewModel()
        open(finance)
        compose.onNodeWithText("Amount (VND)").performTextInput("50000")
        keyboard.awaitOpen(compose)
        compose.onNodeWithText("Review transfer").assertIsDisplayed().performClick()
        keyboard.close(compose)
        compose.onNodeWithText("Confirm transfer").assertIsDisplayed()
        assertTrue(finance.uiState.value.transfers.isEmpty())
        captureScreen("transfer-review", compose, nativeWindow = true)
        compose.onNodeWithText("Confirm transfer").performTouchInput { doubleClick() }
        compose.onNodeWithText("Transfer recorded").assertIsDisplayed()
        assertEquals(1, finance.uiState.value.transfers.size)
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("View transfer history"))
        compose.onNodeWithText("View transfer history").performClick()
        compose.onNodeWithText("Transfer history").assertIsDisplayed()
        compose.onNodeWithText("Everyday cash → Personal bank ledger").assertIsDisplayed()
        captureScreen("transfer-history", compose, nativeWindow = true)
    }
    @Test fun invalidAmountAndDiscardKeepDraftSafe() {
        val finance = FinanceViewModel()
        open(finance, large = true)
        compose.onNodeWithText("Review transfer").performClick()
        compose.onNodeWithText("Enter a positive whole VND amount", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Amount (VND)").performTextInput("999999999999")
        keyboard.close(compose)
        compose.onNodeWithText("Review transfer").performClick()
        compose.onNodeWithText("Insufficient balance in the source wallet.").assertIsDisplayed()
        captureScreen("transfer-error-large", compose, nativeWindow = true)
        compose.onNodeWithText("Back").performClick()
        compose.onNodeWithText("Keep editing").performClick()
        compose.onNodeWithText("Amount (VND)").assertTextContains("999999999999")
        assertTrue(finance.uiState.value.transfers.isEmpty())
    }
}
