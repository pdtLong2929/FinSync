package com.finsync

import com.finsync.domain.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class AnalyticsTest {
    private val day = LocalDate.of(2026, 10, 10)
    private val food = defaultCategories.first { it.name == "Food" }
    private val entries = listOf(
        LedgerEntry("food", "a", EntryType.EXPENSE, 100, "Food", day, categoryId = food.id),
        LedgerEntry("bus", "b", EntryType.EXPENSE, 50, "Transport", day),
        LedgerEntry("salary", "a", EntryType.INCOME, 500, "Salary", day),
        LedgerEntry("opening", "a", EntryType.OPENING, 999, "Opening", day),
        LedgerEntry("transfer", "a", EntryType.TRANSFER_OUT, 999, "Transfer", day))
    private fun result(question: AnalyticsQuestion, wallet: String? = null, category: String? = null) =
        analyticsResult(entries, defaultCategories, emptyList(), day, day, wallet, category, question, day)
    @Test fun categoriesUseStableIdsAndPreserveLegacyEntries() {
        assertEquals(defaultCategories.size, defaultCategories.map { it.id }.distinct().size)
        val renamed = food.copy(name = "Meals")
        assertEquals(renamed, resolveCategory(listOf(renamed), entries.first()))
        assertEquals(CategoryIcon.Transport, resolveCategory(defaultCategories, entries[1])?.icon)
        assertEquals(food.id, food.copy(icon = CategoryIcon.Travel).id)
        assertEquals(CategoryIcon.Other, Category("Custom", EntryType.EXPENSE).icon)
    }
    @Test fun filteringAndChartsAgreeAndExcludeNonTransactions() {
        assertEquals(150L, result(AnalyticsQuestion.Categories).points.sumOf { it.values.first() })
        assertEquals(listOf(500L,150L), result(AnalyticsQuestion.IncomeExpense).points.single().values)
        assertEquals(150L, result(AnalyticsQuestion.Composition).points.sumOf { it.values.sum() })
        assertEquals(350L, result(AnalyticsQuestion.CashFlow).points.last().values.single())
        assertEquals(100L, result(AnalyticsQuestion.Categories, "a", food.id).points.single().values.single())
        assertTrue(result(AnalyticsQuestion.Categories, "missing").points.isEmpty())
        assertFalse(compatibleCharts(AnalyticsQuestion.Trend).contains(ChartKind.Donut))
    }
    @Test fun zeroAndNegativeCashFlowAreSafe() {
        val empty = analyticsResult(emptyList(), defaultCategories, emptyList(), day, day.plusDays(20), null, null, AnalyticsQuestion.Trend, day)
        assertTrue(empty.points.all { it.values.single() == 0L })
        val expensesOnly = analyticsResult(entries.take(2), defaultCategories, emptyList(), day, day, null, null, AnalyticsQuestion.CashFlow, day)
        assertEquals(-150L, expensesOnly.points.single().values.single())
    }
    @Test fun previousPeriodHasEqualLengthAndCorrectBoundaries() {
        val previous = entries.first().copy(id = "old", date = day.minusDays(1), amount = 80)
        val data = analyticsResult(entries + previous, defaultCategories, emptyList(), day, day, null, null, AnalyticsQuestion.Comparison, day)
        assertEquals(listOf(80L,150L), data.points.single().values)
    }
    @Test fun reportPeriodPresetsUseCalendarBoundariesAndCustomDates() {
        val today = LocalDate.of(2026, 10, 10)
        assertEquals(AnalyticsDateRange(LocalDate.of(2026, 10, 5), today), analyticsDateRange(AnalyticsPeriod.ThisWeek, today))
        assertEquals(AnalyticsDateRange(LocalDate.of(2026, 10, 1), today), analyticsDateRange(AnalyticsPeriod.ThisMonth, today))
        assertEquals(AnalyticsDateRange(LocalDate.of(2026, 8, 1), today), analyticsDateRange(AnalyticsPeriod.ThreeMonths, today))
        assertEquals(AnalyticsDateRange(LocalDate.of(2026, 5, 1), today), analyticsDateRange(AnalyticsPeriod.SixMonths, today))
        assertEquals(AnalyticsDateRange(LocalDate.of(2026, 1, 1), today), analyticsDateRange(AnalyticsPeriod.ThisYear, today))
        assertEquals(AnalyticsDateRange(day.minusDays(1), day.plusDays(1)),
            analyticsDateRange(AnalyticsPeriod.Custom, day, day.minusDays(1), day.plusDays(1)))
    }
    @Test fun transactionTypeFilterAndPeriodSummaryUseTheSameLedgerScope() {
        val previous = entries.first().copy(id = "old", date = day.minusDays(1), amount = 80)
        val all = entries + previous
        assertEquals(listOf("salary"), analyticsEntries(all, defaultCategories, day, day, null, null, EntryType.INCOME).map { it.id })
        assertEquals(setOf("food", "bus"), analyticsEntries(all, defaultCategories, day, day, null, null, EntryType.EXPENSE).map { it.id }.toSet())
        val summary = analyticsSummary(all, defaultCategories, day, day, null, null, null)
        assertEquals(500L, summary.income)
        assertEquals(150L, summary.expenses)
        assertEquals(350L, summary.net)
        assertEquals("Food", summary.largestCategory)
        assertEquals(100L, summary.largestCategoryAmount)
        assertEquals(80L, summary.previousComparison)
        assertEquals(70L, summary.comparisonChange)
        assertEquals(day.minusDays(1), summary.previousStart)
        val incomeOnly = analyticsSummary(all, defaultCategories, day, day, null, null, EntryType.INCOME)
        assertEquals("Income", incomeOnly.comparisonLabel)
        assertEquals(500L, incomeOnly.currentComparison)
        assertNull(incomeOnly.largestCategory)
    }
    @Test fun calendarHeatmapReturnsDailyTotalsAndOnlyMatchingTransactions() {
        val calendar = analyticsResult(entries, defaultCategories, emptyList(), day.minusDays(1), day.plusDays(1),
            null, null, AnalyticsQuestion.Calendar, day)
        assertEquals(listOf("Income", "Expenses"), calendar.series)
        assertEquals(listOf(day.minusDays(1).toString(), day.toString(), day.plusDays(1).toString()), calendar.points.map { it.label })
        assertEquals(listOf(500L, 150L), calendar.points[1].values)
        assertEquals(setOf("food", "bus", "salary"), calendar.points[1].entries.map { it.id }.toSet())
        val expenseOnly = analyticsResult(entries, defaultCategories, emptyList(), day, day, null, null,
            AnalyticsQuestion.Calendar, day, EntryType.EXPENSE)
        assertEquals(listOf("Expenses"), expenseOnly.series)
        assertEquals(listOf(150L), expenseOnly.points.single().values)
        assertEquals(listOf(ChartKind.Heatmap), compatibleCharts(AnalyticsQuestion.Calendar))
    }
}
