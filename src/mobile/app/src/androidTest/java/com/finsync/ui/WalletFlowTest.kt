package com.finsync.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.finsync.ui.dashboard.*
import com.finsync.ui.theme.FinSyncTheme
import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.semantics.SemanticsProperties
import com.finsync.domain.WalletType
import org.junit.Assert.assertEquals

class WalletFlowTest {
    @get:Rule val compose = createComposeRule()
    private val keyboard = TestKeyboard()
    @Test fun createEditAndSearchWallet() {
        compose.setContent { keyboard.Observe(); FinSyncTheme { FinSyncApp(DashboardUiState.Content(DemoDashboard.data)) } }
        compose.onNodeWithText("Wallets").performClick()
        compose.onNodeWithText("Create wallet").performClick()
        compose.onNodeWithText("Wallet name").performTextInput("Travel fund")
        compose.onNodeWithText("Opening balance (VND)").performTextReplacement("1500000")
        keyboard.close(compose)
        compose.onNodeWithText("Save wallet").performClick()
        compose.onNode(hasText("Wallets") and !hasClickAction()).assertIsDisplayed()
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Search wallets"))
        compose.onNodeWithText("Search wallets").performTextInput("Travel")
        keyboard.close(compose)
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Travel fund"))
        compose.onNodeWithText("Travel fund").performClick()
        compose.waitForIdle()
        captureScreen("wallet-before-edit", compose)
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Edit wallet"))
        compose.onNodeWithText("Edit wallet").performClick()
        compose.onNodeWithText("Wallet name").performTextReplacement("Trip cash")
        keyboard.close(compose)
        compose.onNodeWithText("Save wallet").performClick()
        compose.waitForIdle(); captureScreen("wallet-after-save", compose)
        compose.onNode(hasText("Wallets") and !hasClickAction()).assertIsDisplayed()
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Search wallets"))
        compose.onNodeWithText("Search wallets").performTextReplacement("Trip")
        keyboard.close(compose)
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Trip cash"))
        compose.onNodeWithText("Trip cash").assertIsDisplayed()
        compose.onNodeWithText("Trip cash").performClick()
        compose.onNodeWithText("Wallet balance").assertIsDisplayed()
        compose.waitForIdle()
        captureScreen("wallet-detail", compose)
    }

    @Test fun detailsBackRestoresScrollSearchAndFilter() {
        val finance = FinanceViewModel()
        (1..12).forEach { finance.saveWallet(null, "Travel %02d".format(it), WalletType.CASH, "1000") }
        compose.setContent { keyboard.Observe(); FinSyncTheme { FinSyncApp(DashboardUiState.Content(DemoDashboard.data), finance = finance) } }
        compose.onNodeWithText("Wallets").performClick()
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Search wallets"))
        compose.onNodeWithText("Search wallets").performTextInput("Travel")
        keyboard.close(compose)
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("All"))
        compose.onNodeWithText("All").performClick()
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Travel 10"))
        val before = compose.onNodeWithText("Travel 10").fetchSemanticsNode().boundsInRoot.top
        compose.onNodeWithText("Travel 10").performClick()
        compose.onNodeWithText("Wallet balance").assertIsDisplayed()
        compose.onNodeWithText("Back").performClick()
        compose.onNode(hasText("Wallets") and !hasClickAction()).assertIsDisplayed()
        compose.waitUntil(5_000) {
            runCatching { kotlin.math.abs(compose.onNodeWithText("Travel 10").fetchSemanticsNode().boundsInRoot.top - before) <= 1f }.getOrDefault(false)
        }
        val after = compose.onNodeWithText("Travel 10").fetchSemanticsNode().boundsInRoot.top
        assertEquals(before, after, 1f)
        compose.onNodeWithText("Travel 10").assertIsDisplayed()
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Search wallets"))
        compose.onNodeWithText("Search wallets").assertTextContains("Travel")
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("All"))
        compose.onNodeWithText("All").assertIsSelected()
    }
}
