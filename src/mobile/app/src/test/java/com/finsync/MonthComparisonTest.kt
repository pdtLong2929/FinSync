package com.finsync

import com.finsync.domain.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class MonthComparisonTest {
    private val today = LocalDate.of(2026, 10, 15)
    private val month = YearMonth.from(today)
    private fun entry(id: String, date: String, amount: Long, type: EntryType = EntryType.EXPENSE) =
        LedgerEntry(id, "cash", type, amount, "Food", LocalDate.parse(date))
    @Test fun missingAndRecordedZeroDiffer() {
        val empty = expenseMonthComparison(emptyList(), month, today)
        assertTrue(empty.previous.entries.isEmpty()); assertNull(empty.percent)
        val zero = expenseMonthComparison(listOf(entry("zero", "2026-09-30", 0)), month, today)
        assertEquals(1, zero.previous.entries.size); assertNull(zero.percent)
    }
    @Test fun denominatorAndPartialMonthBoundary() {
        val result = expenseMonthComparison(listOf(entry("prior", "2026-09-30", 100), entry("current", "2026-10-01", 150),
            entry("future", "2026-10-16", 999), entry("old", "2026-08-31", 999)), month, today)
        assertEquals(150L, result.current.amount); assertEquals(100L, result.previous.amount)
        assertEquals(50.0, result.percent!!, 0.001); assertEquals(today, result.current.end)
    }
    @Test fun excludesNonExpensesAndRecomputesEditsDeletes() {
        val entries = listOf(entry("expense", "2026-10-02", 200), entry("income", "2026-10-02", 1000, EntryType.INCOME),
            entry("opening", "2026-10-02", 1000, EntryType.OPENING), entry("out", "2026-10-02", 1000, EntryType.TRANSFER_OUT),
            entry("in", "2026-10-02", 1000, EntryType.TRANSFER_IN))
        assertEquals(200L, expenseMonthComparison(entries, month, today).current.amount)
        assertEquals(75L, expenseMonthComparison(entries.map { if(it.id == "expense") it.copy(amount = 75) else it }, month, today).current.amount)
        assertEquals(0L, expenseMonthComparison(entries.filterNot { it.id == "expense" }, month, today).current.amount)
    }
    @Test fun yearBoundaryAndPastMonth() {
        val result = expenseMonthComparison(listOf(entry("dec", "2025-12-31", 3), entry("jan", "2026-01-31", 7)), YearMonth.of(2026,1), today)
        assertEquals(3L, result.previous.amount); assertEquals(7L, result.current.amount)
        assertEquals(LocalDate.of(2026,1,31), result.current.end)
    }
    @Test fun settlementCannotChangePersonalComparison() {
        val model = com.finsync.ui.FinanceViewModel()
        val date = com.finsync.data.DemoFixtures.today
        val selected = YearMonth.from(date)
        val before = expenseMonthComparison(model.uiState.value.entries, selected, date)
        assertNull(model.recordSettlement("home", "linh", "me", "100000", date.toString()))
        assertEquals(before, expenseMonthComparison(model.uiState.value.entries, selected, date))
    }
}
