package com.finsync

import com.finsync.data.DemoFixtures
import com.finsync.domain.*
import com.finsync.ui.FinanceViewModel
import org.junit.Assert.*
import org.junit.Test
import java.time.YearMonth

class ReportsTest {
    @Test fun customRangeIncludesBoundariesAndExcludesTransferLegs() {
        val day = DemoFixtures.today
        val entries = listOf(
            LedgerEntry("a", "cash", EntryType.EXPENSE, 20, "Food", day.minusDays(1)),
            LedgerEntry("b", "cash", EntryType.INCOME, 50, "Salary", day),
            LedgerEntry("c", "cash", EntryType.EXPENSE, 100, "Food", day.minusDays(2)),
            LedgerEntry("d", "cash", EntryType.OPENING, 1000, "Opening", day),
            LedgerEntry("e", "cash", EntryType.TRANSFER_IN, 200, "Transfer", day),
            LedgerEntry("f", "cash", EntryType.TRANSFER_OUT, 300, "Transfer", day),
        )
        val report = periodReport(entries, day.minusDays(1), day)
        assertEquals(50L, report.income); assertEquals(20L, report.expenses)
        assertEquals(mapOf("Food" to 20L), report.categories)
        assertEquals(listOf("b", "a"), periodEntries(entries, day.minusDays(1), day).map { it.id })
        assertNotNull(reportRangeError(day.toString(), day.minusDays(1).toString(), day))
        assertNotNull(reportRangeError("invalid", day.toString(), day))
        assertNotNull(reportRangeError(day.toString(), day.plusDays(1).toString(), day))
        assertNull(reportRangeError(day.toString(), day.toString(), day))
    }
    @Test fun trendContainsSixOrderedMonthsIncludingZeroAndYearBoundary() {
        val month = YearMonth.of(2026, 2)
        val entries = listOf(LedgerEntry("x", "cash", EntryType.EXPENSE, 123, "Food", month.minusMonths(2).atDay(1)),
            LedgerEntry("y", "cash", EntryType.INCOME, 999, "Salary", month.atDay(1)))
        val trend = spendingTrend(entries, month)
        assertEquals(6, trend.size)
        assertEquals(YearMonth.of(2025, 9), trend.first().first)
        assertEquals(month, trend.last().first)
        assertEquals(123L, trend.sumOf { it.second })
        assertEquals(0L, trend.last().second)
    }
    @Test fun monthlyReportsExcludeOpeningAndRespectPeriod() {
        val month = YearMonth.from(DemoFixtures.today)
        val report = monthlyReport(DemoFixtures.entries, month)
        assertEquals(8_000_000L, report.income)
        assertEquals(report.expenses, report.categories.values.sum())
        assertEquals(0L, monthlyReport(DemoFixtures.entries, month.minusMonths(1)).expenses)
        assertEquals(1_200_000L, report.categories.getValue("Food"))
    }
    @Test fun thresholdsAndWeeklyBoundaryAreExact() {
        assertEquals("Within budget", budgetStatus(79, 100))
        assertEquals("Warning · 80% or more", budgetStatus(80, 100))
        assertEquals("Limit reached · 100% or more", budgetStatus(100, 100))
        val day = DemoFixtures.today
        val entries = listOf(LedgerEntry("a", "cash", EntryType.EXPENSE, 80, "Food", day),
            LedgerEntry("b", "cash", EntryType.EXPENSE, 90, "Food", day.minusDays(7)))
        assertEquals(80L, budgetSpent(Budget("x", "Food", 100, BudgetCycle.Weekly), entries, day))
    }
    @Test fun budgetAndGoalValidationPreserveLedgers() {
        val vm = FinanceViewModel()
        val before = vm.uiState.value.entries
        assertNotNull(vm.saveBudget(null, "Food", "0", BudgetCycle.Weekly))
        assertNull(vm.saveBudget(null, "Food", "500000", BudgetCycle.Weekly))
        assertNotNull(vm.saveBudget(null, "Food", "500000", BudgetCycle.Weekly))
        assertNotNull(vm.saveGoal(null, "Laptop", "100", "101", "2027-01-01"))
        assertNull(vm.saveGoal(null, "Laptop", "100", "80", "2027-01-01"))
        assertEquals(before, vm.uiState.value.entries)
    }
}
