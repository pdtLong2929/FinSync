package com.finsync.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.finsync.ui.dashboard.*
import com.finsync.ui.theme.FinSyncTheme
import org.junit.Rule
import org.junit.Test

class SoftLiquidScreensTest {
    @get:Rule val compose = createComposeRule()
    private fun capture(name: String) = captureScreen(name, compose, nativeWindow = true)
    private fun click(label: String) {
        if (runCatching { compose.onNodeWithText(label).assertIsDisplayed() }.isFailure)
            compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(label))
        compose.onNodeWithText(label).performClick()
    }
    @Test fun captureMainScreensAndQuickTransactionCategories() {
        compose.setContent { FinSyncTheme { FinSyncApp(DashboardUiState.Content(DemoDashboard.data)) } }
        capture("soft-liquid-overview-top")
        click("Wallets")
        capture("soft-liquid-wallets")
        click("All personal transactions")
        capture("soft-liquid-history")
        click("Back")
        click("Groups")
        capture("soft-liquid-groups")
        click("Reports")
        capture("soft-liquid-reports")
        compose.onNodeWithContentDescription("Add expense").performClick()
        capture("soft-liquid-entry-top")
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("All categories"))
        capture("soft-liquid-expense")
        click("Food")
        click("All categories")
        compose.onNodeWithText("Choose category").assertIsDisplayed()
        click("Back")
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("+  Income"))
        click("+  Income")
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Salary"))
        compose.onNodeWithText("Food").assertDoesNotExist()
        capture("soft-liquid-income")
    }
}
