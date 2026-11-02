package com.finsync.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.finsync.ui.dashboard.*
import com.finsync.ui.theme.FinSyncTheme
import org.junit.Rule
import org.junit.Test
import com.finsync.data.DemoFixtures
import java.time.YearMonth

class ReportsFlowTest {
    @get:Rule val compose = createComposeRule()
    private val keyboard = TestKeyboard()
    private fun click(text: String) {
        if (runCatching { compose.onNodeWithText(text).assertIsDisplayed() }.isFailure)
            compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(text))
        compose.onNodeWithText(text).performClick()
    }
    @Test fun editBudgetAndShowThresholdAndMockInsights() {
        compose.setContent { keyboard.Observe(); FinSyncTheme { FinSyncApp(DashboardUiState.Content(DemoDashboard.data)) } }
        compose.onNodeWithText("Reports").performClick()
        click("Budgets")
        click("Edit Food budget")
        compose.onNodeWithText("Budget limit (VND)").performTextReplacement("1000000")
        keyboard.close(compose)
        click("Save budget")
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Limit reached · 100% or more"))
        compose.onNodeWithText("Limit reached · 100% or more").assertIsDisplayed()
        captureScreen("budget-warning", compose)
        click("Explain Food spending")
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Lunch"))
        compose.onNodeWithText("Lunch").assertIsDisplayed()
        compose.onNodeWithText("Back").performClick()
        click("Spending guide")
        compose.waitForIdle()
        compose.onNodeWithText("Small steps, clearer goals").assertIsDisplayed()
        compose.waitForIdle(); captureScreen("ai-insights", compose)
    }
    @Test fun darkReportsFilterEmptyMonth() {
        compose.setContent { FinSyncTheme(darkTheme = true) { FinSyncApp(DashboardUiState.Content(DemoDashboard.data)) } }
        compose.onNodeWithText("Reports").performClick()
        compose.onNodeWithTag("report-period").assertIsDisplayed()
        compose.waitForIdle()
        captureScreen("reports-dark", compose)
        val month = YearMonth.from(DemoFixtures.today)
        compose.onNodeWithTag("report-period").performClick()
        compose.onNodeWithText(com.finsync.ui.reports.friendlyMonth(month.minusMonths(1))).performClick()
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("No expenses yet. Choose another month."))
        compose.onNodeWithText("No expenses yet. Choose another month.").assertIsDisplayed()
        click("See analytics")
        click("Report filters")
        val expected = "Showing ${month.minusMonths(1).atDay(1)} to ${month.minusMonths(1).atEndOfMonth()} · current periods are partial"
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(expected))
        compose.onNodeWithText(expected).assertIsDisplayed()
    }
    @Test fun savingsGoalCanBeCreatedWithLargeText() {
        compose.setContent {
            keyboard.Observe()
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 1.5f)) {
                FinSyncTheme { FinSyncApp(DashboardUiState.Content(DemoDashboard.data)) }
            }
        }
        compose.onNodeWithText("Reports").performClick()
        click("Savings goals")
        click("Create savings goal")
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Goal title"))
        compose.onNodeWithText("Goal title").performTextInput("Demo course")
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Target (VND)"))
        compose.onNodeWithText("Target (VND)").performTextInput("1000000")
        keyboard.close(compose)
        click("Save goal")
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Demo course"))
        compose.onNodeWithText("Demo course").assertIsDisplayed()
        captureScreen("goal-large-text", compose)
    }
}

