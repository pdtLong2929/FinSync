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
import org.junit.Rule
import org.junit.Test

class LiquidMetalOverviewWalletsTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private fun host(large: Boolean = false) {
        compose.activityRule.scenario.onActivity { activity -> activity.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, if(large) 1.5f else 1f)) {
                FinSyncTheme { Box(Modifier.widthIn(max = if(large) 320.dp else 500.dp).fillMaxHeight()) {
                    FinSyncApp(DashboardUiState.Content(FinanceState().toDashboardData()))
                } }
            }
        } }
    }
    private fun click(text: String) {
        val node = hasText(text) and hasClickAction()
        if(runCatching { compose.onNode(node).assertIsDisplayed() }.isFailure)
            compose.onNode(hasScrollToIndexAction()).performScrollToNode(node)
        compose.onNode(node).performClick()
    }
    @Test fun realBalancesPrivacySearchAndWalletReturn() {
        host()
        compose.onNodeWithText("12.450.000 ₫").assertIsDisplayed()
        captureScreen("liquid-metal-c-overview", compose, nativeWindow = true)
        compose.onNodeWithContentDescription("Hide amounts").performClick()
        compose.onNodeWithText("12.450.000 ₫").assertDoesNotExist()
        compose.onNodeWithContentDescription("Show amounts").performClick()
        click("Wallets")
        compose.onNodeWithText("Everyday cash").assertIsDisplayed()
        compose.onNodeWithText("700.000 ₫").assertIsDisplayed()
        captureScreen("liquid-metal-c-wallets", compose, nativeWindow = true)
        click("Search wallets")
        compose.onNode(hasSetTextAction()).performTextInput("no such wallet")
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("No wallets found. Create a wallet or change your search."))
        compose.onNodeWithText("No wallets found. Create a wallet or change your search.").assertIsDisplayed()
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasSetTextAction())
        compose.onNode(hasSetTextAction()).performTextReplacement("Everyday")
        click("Everyday cash")
        compose.onNodeWithText("Wallet balance").assertIsDisplayed()
        click("Back")
        compose.onNode(hasSetTextAction()).assertTextContains("Everyday")
        captureScreen("liquid-metal-c-wallet-search", compose, nativeWindow = true)
        click("Close search")
        compose.onNode(hasSetTextAction()).assertDoesNotExist()
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Personal bank ledger"))
        compose.onNodeWithText("Personal bank ledger").assertIsDisplayed()
        click("All personal transactions")
        compose.onNodeWithText("Personal transactions").assertIsDisplayed()
    }
    @Test fun compactLargeTextStillOffersPrivacyAndAccounts() {
        host(true)
        captureScreen("liquid-metal-c-overview-large", compose, nativeWindow = true)
        compose.onNodeWithContentDescription("Hide amounts").assertIsDisplayed().performClick()
        compose.onNodeWithText("12.450.000 ₫").assertDoesNotExist()
        compose.onNodeWithContentDescription("Show amounts").performClick()
        click("Wallets")
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Everyday cash"))
        captureScreen("liquid-metal-c-wallets-large", compose, nativeWindow = true)
        click("Everyday cash")
        compose.onNodeWithText("Wallet balance").assertIsDisplayed()
    }
}
