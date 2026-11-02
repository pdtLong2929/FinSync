package com.finsync.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.finsync.ui.dashboard.*
import com.finsync.ui.theme.FinSyncTheme
import org.junit.Rule
import org.junit.Test

class SoftLiquidOverviewTest {
    @get:Rule val compose = createComposeRule()
    @Test fun overviewTrendUsesLedgerValuesAndSupportsAccessibleSelection() {
        val data = FinanceState().toDashboardData()
        compose.setContent { FinSyncTheme { FinSyncApp(DashboardUiState.Content(data)) } }
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Select interval"))
        captureScreen("soft-liquid-overview-trend", compose)
        compose.onNodeWithText("Select interval").performClick()
        compose.onNodeWithText(data.spendingTrend.first().first).performClick()
        val expected = "${data.spendingTrend.first().first} · ${formatVnd(data.spendingTrend.first().second)}"
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(expected))
        compose.onNodeWithText(expected).assertIsDisplayed()
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasContentDescription("Hide amounts"))
        compose.onNodeWithContentDescription("Hide amounts").performClick()
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Amounts hidden"))
        compose.onNodeWithText("Select interval").assertDoesNotExist()
    }
}
