package com.finsync.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.finsync.ui.components.*
import com.finsync.ui.dashboard.*
import com.finsync.ui.theme.FinSyncTheme
import org.junit.Rule
import org.junit.Test

class AnalyticsFlowTest {
    @get:Rule val compose = createComposeRule()
    private fun click(text: String) {
        val target = hasText(text) and hasClickAction()
        if(runCatching { compose.onNode(target).assertIsDisplayed() }.isFailure)
            compose.onNode(hasScrollToIndexAction()).performScrollToNode(target)
        compose.onNode(target).performClick()
    }
    private fun selectCalendarCell(matcher: SemanticsMatcher) {
        val list = compose.onNode(hasScrollToIndexAction())
        list.performScrollToNode(matcher)
        // The month is one lazy item: reveal its lower rows before injecting a touch.
        var attempts = 0
        while (runCatching { compose.onNode(matcher).assertIsDisplayed() }.isFailure && attempts++ < 6)
            list.performTouchInput { swipeUp() }
        compose.onNode(matcher).assertIsDisplayed().performClick().assertIsSelected()
    }
    @Test fun chartSelectionDrillDownAndReturn() {
        compose.setContent { FinSyncTheme { FinSyncApp(DashboardUiState.Content(DemoDashboard.data)) } }
        click("Reports")
        captureScreen("reports-with-analytics-entry", compose)
        click("Category"); click("See analytics")
        captureScreen("analytics-filters", compose)
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Total expenses: 2.300.000 ₫"))
        captureScreen("analytics-simple-donut", compose)
        click("Bar chart")
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasTestTag("analytics-point-0"))
        compose.onNodeWithTag("analytics-point-0").performClick()
        captureScreen("analytics-simple-bars", compose)
        click("View related transactions")
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Lunch"))
        captureScreen("analytics-transaction-history", compose)
        click("Lunch")
        compose.onNodeWithText("Transaction details").assertIsDisplayed()
        compose.onAllNodesWithContentDescription("Food category").onFirst().assertExists()
        click("Back")
        click("Hide related transactions")
        click("Advanced")
        compose.onNode(hasText("Where did my money go?") and hasClickAction()).performClick()
        compose.onNodeWithText("Cumulative net cash flow").performClick()
        compose.onNodeWithText("Donut").assertDoesNotExist()
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Starts at zero for the selected period. This is cumulative net cash flow, not net worth."))
        captureScreen("analytics-advanced-cash-flow", compose)
        click("Report filters")
        click("Reset filters")
    }
    @Test fun semanticIconsHaveStableResourceMapping() {
        val icons = com.finsync.domain.CategoryIcon.entries.map { categoryIconResource(it) }
        org.junit.Assert.assertEquals(icons.size, icons.distinct().size)
        compose.setContent { FinSyncTheme { CategoryBadge("Unknown category") } }
        compose.onNodeWithContentDescription("Unknown category category").assertExists()
    }
    @Test fun reportPreviewCardsOpenMatchingCharts() {
        compose.setContent { FinSyncTheme { FinSyncApp(DashboardUiState.Content(DemoDashboard.data)) } }
        click("Reports")
        compose.onNodeWithText("Charts & Analytics").assertDoesNotExist()
        compose.onNodeWithText("Category").assertIsDisplayed()
        compose.onNodeWithText("Trend").assertIsDisplayed()
        captureScreen("reports-chart-previews", compose)
        click("Trend"); click("See analytics")
        compose.onNode(hasText("Spending trend") and hasClickAction()).assertIsDisplayed()
        click("Columns")
        click("Back")
        click("Category"); click("See analytics")
        compose.onNodeWithText("Donut").assertIsDisplayed()
        click("Back")
        click("Trend"); click("See analytics")
        compose.onNodeWithText("Columns").assertIsSelected()
    }
    @Test fun customizedCategoryStyleMatchesFormAndPicker() {
        val finance = FinanceViewModel()
        finance.setCategoryVisual("Food", com.finsync.domain.CategoryIcon.Gift, com.finsync.domain.CategoryColor.Cyan)
        val snapshot = finance.uiState.value
        compose.setContent { FinSyncTheme {
            androidx.compose.runtime.CompositionLocalProvider(LocalCategories provides snapshot.categories) {
                com.finsync.ui.transactions.TransactionFormScreen(snapshot, null, {}, { _, _, _, _, _, _ -> null }, { _, _ -> null })
            }
        } }
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Food"))
        compose.onNodeWithContentDescription("Food category", useUnmergedTree = true)
            .assert(SemanticsMatcher.expectValue(androidx.compose.ui.semantics.SemanticsProperties.StateDescription, "Gift · Cyan"))
        click("Food")
        compose.onNodeWithContentDescription("Food category", useUnmergedTree = true)
            .assert(SemanticsMatcher.expectValue(androidx.compose.ui.semantics.SemanticsProperties.StateDescription, "Gift · Cyan"))
        captureScreen("category-picker-custom-style", compose)
    }
    @Test fun compactLargeTextEmptyAnalytics() {
        compose.setContent {
            val density = androidx.compose.ui.platform.LocalDensity.current
            androidx.compose.runtime.CompositionLocalProvider(androidx.compose.ui.platform.LocalDensity provides
                androidx.compose.ui.unit.Density(density.density, 1.5f)) {
                FinSyncTheme { Box(Modifier.width(320.dp).fillMaxHeight()) {
                    com.finsync.ui.reports.AnalyticsScreen(FinanceState(entries = emptyList()), {}, {})
                } }
            }
        }
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("No values for these filters. Try another period or reset filters."))
        compose.onNodeWithText("No values for these filters. Try another period or reset filters.").assertIsDisplayed()
        captureScreen("analytics-empty-compact-large", compose)
    }
    @Test fun advancedPlotsAreVisibleAndSelectable() {
        compose.setContent { FinSyncTheme { com.finsync.ui.reports.AnalyticsScreen(FinanceState(), {}, {}) } }
        click("Advanced")
        var current = "Where did my money go?"
        listOf("Income vs expenses" to "Columns", "Spending composition over time" to "Stacked",
            "Cumulative net cash flow" to "Line").forEach { (question, chart) ->
            val picker = hasText(current) and hasClickAction()
            compose.onNode(hasScrollToIndexAction()).performScrollToNode(picker)
            compose.onNode(picker).performClick()
            compose.onNodeWithText(question).performScrollTo().performClick()
            val label = "$chart chart. Exact values and accessible selection in numbered rows below."
            compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasContentDescription(label))
            compose.onNodeWithContentDescription(label).performScrollTo().assertIsDisplayed()
            captureScreen("analytics-plot-${chart.lowercase()}", compose)
            compose.onNodeWithContentDescription(label).performTouchInput { click(center) }
            compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Selected:", substring = true))
            compose.onNodeWithText("Selected:", substring = true).assertIsDisplayed()
            current = question
        }
    }
    @Test fun periodTypeWalletCategoryFiltersDriveInteractiveCalendar() {
        val state = FinanceState()
        val today = com.finsync.data.DemoFixtures.today
        val salaryDate = com.finsync.data.DemoFixtures.entries.first { it.id == "allowance" }.date
        val quarter = com.finsync.domain.analyticsDateRange(com.finsync.domain.AnalyticsPeriod.ThreeMonths, today)
        compose.setContent { FinSyncTheme { com.finsync.ui.reports.AnalyticsScreen(state, {}, {}) } }
        click("Report filters")
        compose.onNodeWithText("This month").performClick()
        compose.onNodeWithText("3 months").performClick()
        compose.onNodeWithText("Showing ${quarter.start} to ${quarter.end} · current periods are partial").assertIsDisplayed()
        click("Where did my money go?")
        compose.onNodeWithText("Calendar heatmap").performClick()
        val lunchDate = state.entries.first { it.note == "Lunch" }.date
        val dayExpense = state.entries.filter { it.date == lunchDate && it.type == com.finsync.domain.EntryType.EXPENSE }.sumOf { it.amount }
        val dayExpenseLabel = com.finsync.ui.dashboard.formatVnd(dayExpense)
        val expenseCell = hasContentDescription("Expenses $dayExpenseLabel", substring = true)
        selectCalendarCell(expenseCell)
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Expenses: $dayExpenseLabel", substring = true))
        compose.onNode(hasText("Expenses: $dayExpenseLabel", substring = true)).assertIsDisplayed()
        click("View related transactions")
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Lunch"))
        compose.onNodeWithText("Lunch").assertIsDisplayed()
        click("Hide related transactions")

        val incomeCell = hasContentDescription("Income 8.000.000", substring = true)
        selectCalendarCell(incomeCell)
        click("View related transactions")
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Monthly allowance"))
        compose.onNodeWithText("Monthly allowance").assertIsDisplayed()
        click("Hide related transactions")

        click("All transactions")
        compose.onNodeWithText("Income").performClick()
        click("All wallets")
        compose.onNodeWithText("Personal bank ledger").performClick()
        click("All categories")
        compose.onNodeWithText("Salary").performClick()
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasContentDescription("Income 8.000.000", substring = true))
        compose.onNode(hasContentDescription("Income 8.000.000", substring = true)).assertExists()
    }

    @Test fun budgetProgressUsesCurrentExpenseCycleAndAnalyticsStatesRetry() {
        val mode = androidx.compose.runtime.mutableStateOf(LoadState.Ready)
        var retries = 0
        compose.setContent { FinSyncTheme {
            com.finsync.ui.reports.AnalyticsScreen(FinanceState(), {}, {}, mode = mode.value, onRetry = { retries++ })
        } }
        click("Where did my money go?")
        compose.onNodeWithText("Budget progress").performClick()
        click("Report filters")
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Spent:", substring = true))
        compose.onAllNodes(hasText("Spent:", substring = true)).onFirst().assertIsDisplayed()
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Budget progress uses expense records", substring = true))
        compose.onNodeWithText("Budget progress uses expense records in the current budget cycle; dates do not change its limit.").assertIsDisplayed()

        compose.runOnIdle { mode.value = LoadState.Loading }
        compose.onNodeWithText("Loading your ledger…").assertIsDisplayed()
        compose.runOnIdle { mode.value = LoadState.Error }
        compose.onNodeWithText("Unable to load this view. Your entries are unchanged.").assertIsDisplayed()
        compose.onNodeWithText("Try again").performClick()
        compose.runOnIdle { org.junit.Assert.assertEquals(1, retries) }
    }
}

