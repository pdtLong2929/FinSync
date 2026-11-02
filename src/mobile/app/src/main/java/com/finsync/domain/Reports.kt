package com.finsync.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters

enum class BudgetCycle { Weekly, Monthly }
data class Budget(val id: String, val category: String, val limit: Long, val cycle: BudgetCycle)
data class SavingsGoal(val id: String, val title: String, val target: Long, val saved: Long, val deadline: LocalDate)
data class MonthlyReport(val income: Long, val expenses: Long, val categories: Map<String, Long>)

fun monthlyReport(entries: List<LedgerEntry>, month: YearMonth): MonthlyReport {
    return periodReport(entries, month.atDay(1), month.atEndOfMonth())
}

fun periodEntries(entries: List<LedgerEntry>, start: LocalDate, end: LocalDate): List<LedgerEntry> =
    entries.filter { it.type.isTransaction && it.date in start..end }.sortedByDescending { it.date }

fun periodReport(entries: List<LedgerEntry>, start: LocalDate, end: LocalDate): MonthlyReport {
    require(!end.isBefore(start))
    val selected = entries.filter { it.type.isTransaction && it.date in start..end }
    val expenses = selected.filter { it.type == EntryType.EXPENSE }
    return MonthlyReport(selected.filter { it.type == EntryType.INCOME }.sumOf { it.amount },
        expenses.sumOf { it.amount }, expenses.groupBy { it.category }.mapValues { it.value.sumOf { entry -> entry.amount } })
}

fun reportRangeError(start: String, end: String, today: LocalDate): String? {
    val first = runCatching { LocalDate.parse(start) }.getOrNull() ?: return "Enter a valid start date."
    val last = runCatching { LocalDate.parse(end) }.getOrNull() ?: return "Enter a valid end date."
    if (last.isBefore(first)) return "End date must be on or after start date."
    if (last > today) return "End date cannot be in the future."
    return null
}

fun spendingTrend(entries: List<LedgerEntry>, endMonth: YearMonth): List<Pair<YearMonth, Long>> =
    (5L downTo 0L).map { offset -> endMonth.minusMonths(offset).let { it to monthlyReport(entries, it).expenses } }

fun budgetSpent(budget: Budget, entries: List<LedgerEntry>, today: LocalDate): Long {
    val start = if (budget.cycle == BudgetCycle.Monthly) today.withDayOfMonth(1)
        else today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    return entries.filter { it.type == EntryType.EXPENSE && it.category == budget.category && it.date in start..today }.sumOf { it.amount }
}

fun budgetStatus(spent: Long, limit: Long): String = when {
    spent >= limit -> "Limit reached · 100% or more"
    spent * 100 >= limit * 80 -> "Warning · 80% or more"
    else -> "Within budget"
}
