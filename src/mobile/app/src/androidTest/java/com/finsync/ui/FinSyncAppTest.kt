package com.finsync.ui

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.*
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.finsync.ui.dashboard.DashboardUiState
import com.finsync.ui.dashboard.DemoDashboard
import com.finsync.ui.theme.FinSyncTheme
import org.junit.Rule
import org.junit.Test

class FinSyncAppTest {
    @get:Rule val compose = createComposeRule()

    private fun launchApp() {
        compose.setContent { FinSyncTheme { FinSyncApp(DashboardUiState.Content(DemoDashboard.data)) } }
    }

    @Test fun tabsNavigateAndReturnToDashboard() {
        launchApp()
        compose.onNodeWithText("Wallets").performClick()
        compose.onNode(hasText("Wallets") and !hasClickAction()).assertIsDisplayed()
        compose.onNodeWithText("Overview").performClick()
        compose.onNodeWithText("Groups").performClick()
        compose.onNode(hasText("Groups") and !hasClickAction()).assertIsDisplayed()
        compose.onNodeWithText("Overview").performClick()
        compose.onNodeWithText("Reports").performClick()
        compose.onNodeWithTag("chrome-tab-Reports").assertIsSelected()
        compose.onNodeWithText("Overview").performClick()
        compose.onNodeWithText("Personal net worth").assertIsDisplayed()
    }

    @Test fun fabOpensPersonalEntryDirectly() {
        launchApp()
        compose.onNodeWithContentDescription("Add expense").performClick()
        compose.onNodeWithText("Add expense").assertIsDisplayed()
    }

    @Test fun hiddenBalanceIsRemovedFromSemantics() {
        launchApp()
        compose.onNodeWithText("12.450.000 ₫").assertIsDisplayed()
        compose.onNodeWithContentDescription("Hide amounts").performClick()
        compose.onNodeWithText("12.450.000 ₫").assertDoesNotExist()
        compose.onNodeWithContentDescription("Show amounts").performClick()
        compose.onNodeWithText("12.450.000 ₫").assertIsDisplayed()
        compose.waitForIdle(); captureScreen("dashboard-metallic-light", compose)
    }
}


