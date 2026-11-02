package com.finsync.domain

import java.time.LocalDate
import java.time.YearMonth

data class ExpenseMonth(val month: YearMonth, val end: LocalDate, val entries: List<LedgerEntry>) {
    val amount: Long get() = entries.sumOf { it.amount }
}
data class MonthComparison(val previous: ExpenseMonth, val current: ExpenseMonth) {
    val delta: Long get() = current.amount - previous.amount
    val percent: Double? get() = previous.amount.takeIf { it > 0 }?.let { delta.toDouble() * 100 / it }
}

/** Personal expenses only. Current month ends today; previous month is explicitly full. */
fun expenseMonthComparison(entries: List<LedgerEntry>, month: YearMonth, today: LocalDate): MonthComparison {
    fun period(value: YearMonth): ExpenseMonth {
        val end = if (value == YearMonth.from(today)) today else value.atEndOfMonth()
        return ExpenseMonth(value, end, periodEntries(entries, value.atDay(1), end).filter { it.type == EntryType.EXPENSE })
    }
    return MonthComparison(period(month.minusMonths(1)), period(month))
}
