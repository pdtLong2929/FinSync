package com.finsync.ui

import androidx.activity.compose.setContent
import androidx.compose.ui.test.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.finsync.MainActivity
import com.finsync.ui.dashboard.*
import com.finsync.ui.theme.FinSyncTheme
import org.junit.Rule
import org.junit.Test

class LiquidMetalTransactionTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val keyboard = TestKeyboard()
    private fun click(text: String) {
        val target = hasText(text) and hasClickAction()
        if (runCatching { compose.onNode(target).assertIsDisplayed() }.isFailure)
            compose.onNode(hasScrollToIndexAction()).performScrollToNode(target)
        compose.onNode(target).performClick()
    }
    @Test fun amountTypeWalletAndHistoryUseActualData() {
        compose.activityRule.scenario.onActivity { it.setContent {
            keyboard.Observe(); FinSyncTheme { FinSyncApp(DashboardUiState.Content(DemoDashboard.data)) }
        } }
        compose.onNodeWithContentDescription("Add expense").performClick()
        compose.onNodeWithText("Amount (VND)").performTextInput("125000")
        keyboard.close(compose)
        captureScreen("liquid-metal-d-expense", compose, nativeWindow = true)
        click("Clear")
        click("+  Income")
        compose.onNodeWithText("Add income").assertIsDisplayed()
        compose.onNodeWithText("Amount (VND)").performTextInput("300000")
        keyboard.close(compose)
        captureScreen("liquid-metal-d-income", compose, nativeWindow = true)
        click("Everyday cash")
        captureScreen("liquid-metal-d-wallet-picker", compose, nativeWindow = true)
        click("Personal bank ledger")
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Notes (optional)"))
        compose.onNodeWithText("Notes (optional)").performTextInput("Design income")
        keyboard.close(compose)
        click("Save transaction")
        compose.onNodeWithText("Personal transactions").assertIsDisplayed()
        captureScreen("liquid-metal-d-history", compose, nativeWindow = true)
        click("Design income")
        compose.onNodeWithText("300.000 ₫").assertIsDisplayed()
        compose.onNodeWithText("Personal bank ledger").assertIsDisplayed()
        captureScreen("liquid-metal-d-detail", compose, nativeWindow = true)
    }
    @Test fun largeTextSaveRemainsVisibleWithRealKeyboard() {
        compose.activityRule.scenario.onActivity { it.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 1.5f)) {
                keyboard.Observe()
                FinSyncTheme { Box(Modifier.width(320.dp).fillMaxHeight()) {
                    FinSyncApp(DashboardUiState.Content(DemoDashboard.data))
                } }
            }
        } }
        compose.onNodeWithContentDescription("Add expense").performClick()
        compose.onNodeWithText("Amount (VND)").performClick()
        keyboard.awaitOpen(compose)
        compose.onNodeWithText("Amount (VND)").performTextInput("50000")
        compose.onNodeWithText("Save transaction").assertIsDisplayed()
        captureScreen("liquid-metal-d-keyboard-large", compose, nativeWindow = true)
        compose.onNodeWithText("Save transaction").performClick()
        compose.onNodeWithText("Personal transactions").assertIsDisplayed()
    }
}
