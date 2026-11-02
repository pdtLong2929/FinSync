package com.finsync.domain

import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

enum class AnalyticsPeriod(val label: String) {
    ThisWeek("This week"), ThisMonth("This month"), ThreeMonths("3 months"),
    SixMonths("6 months"), ThisYear("This year"), Custom("Custom")
}
data class AnalyticsDateRange(val start: LocalDate, val end: LocalDate)
fun analyticsDateRange(period: AnalyticsPeriod, today: LocalDate,
                       customStart: LocalDate? = null, customEnd: LocalDate? = null): AnalyticsDateRange {
    val start = when (period) {
        AnalyticsPeriod.ThisWeek -> today.minusDays(today.dayOfWeek.value.toLong() - 1)
        AnalyticsPeriod.ThisMonth -> YearMonth.from(today).atDay(1)
        AnalyticsPeriod.ThreeMonths -> YearMonth.from(today).minusMonths(2).atDay(1)
        AnalyticsPeriod.SixMonths -> YearMonth.from(today).minusMonths(5).atDay(1)
        AnalyticsPeriod.ThisYear -> today.withDayOfYear(1)
        AnalyticsPeriod.Custom -> requireNotNull(customStart) { "Choose a custom start date." }
    }
    val end = if (period == AnalyticsPeriod.Custom) requireNotNull(customEnd) { "Choose a custom end date." } else today
    require(!end.isBefore(start)) { "End date must be on or after start date." }
    return AnalyticsDateRange(start, end)
}

enum class AnalyticsQuestion(val label: String, val advanced: Boolean = false) {
    Categories("Where did my money go?"), IncomeExpense("Income vs expenses"),
    Trend("Spending trend"), Top("Top spending categories"), Budgets("Budget progress"),
    Calendar("Calendar heatmap"),
    Composition("Spending composition over time", true), CashFlow("Cumulative net cash flow", true),
    Comparison("Previous period comparison", true), CategoryTrends("Category trend comparison", true)
}
enum class ChartKind(val label: String) {
    Donut("Donut"), Columns("Columns"), Line("Line"), Bars("Bar chart"),
    Progress("Progress"), Stacked("Stacked bar"), Heatmap("Heatmap")
}
fun compatibleCharts(question: AnalyticsQuestion): List<ChartKind> = when(question) {
    AnalyticsQuestion.Categories -> listOf(ChartKind.Donut, ChartKind.Bars)
    AnalyticsQuestion.IncomeExpense, AnalyticsQuestion.Comparison -> listOf(ChartKind.Columns, ChartKind.Bars)
    AnalyticsQuestion.Trend, AnalyticsQuestion.CashFlow, AnalyticsQuestion.CategoryTrends -> listOf(ChartKind.Line, ChartKind.Columns)
    AnalyticsQuestion.Top -> listOf(ChartKind.Bars, ChartKind.Donut)
    AnalyticsQuestion.Budgets -> listOf(ChartKind.Progress)
    AnalyticsQuestion.Calendar -> listOf(ChartKind.Heatmap)
    AnalyticsQuestion.Composition -> listOf(ChartKind.Stacked)
}
data class ChartDatum(val label: String, val values: List<Long>, val entries: List<LedgerEntry>, val category: String? = null)
data class AnalyticsResult(val series: List<String>, val points: List<ChartDatum>, val summary: String)
data class AnalyticsSummary(
    val income: Long, val expenses: Long, val net: Long,
    val largestCategory: String?, val largestCategoryAmount: Long,
    val comparisonLabel: String, val currentComparison: Long, val previousComparison: Long,
    val comparisonChange: Long, val previousStart: LocalDate, val previousEnd: LocalDate,
)
fun analyticsEntries(entries: List<LedgerEntry>, categories: List<Category>, start: LocalDate, end: LocalDate,
                     walletId: String?, categoryId: String?, transactionType: EntryType? = null): List<LedgerEntry> = entries.filter {
    it.type.isTransaction && it.date in start..end && (walletId == null || it.walletId == walletId) &&
        (categoryId == null || resolveCategory(categories, it)?.id == categoryId) &&
        (transactionType == null || it.type == transactionType)
}

/** Consistent filtered totals and an equal-length comparison window for every report area. */
fun analyticsSummary(all: List<LedgerEntry>, categories: List<Category>, start: LocalDate, end: LocalDate,
                     walletId: String?, categoryId: String?, transactionType: EntryType?): AnalyticsSummary {
    require(end >= start)
    val current = analyticsEntries(all, categories, start, end, walletId, categoryId, transactionType)
    val days = ChronoUnit.DAYS.between(start, end) + 1
    val previousEnd = start.minusDays(1)
    val previousStart = previousEnd.minusDays(days - 1)
    val previous = analyticsEntries(all, categories, previousStart, previousEnd, walletId, categoryId, transactionType)
    fun total(items: List<LedgerEntry>, type: EntryType) = items.filter { it.type == type }.sumOf { it.amount }
    val currentIncome = total(current, EntryType.INCOME)
    val currentExpenses = total(current, EntryType.EXPENSE)
    val comparisonType = if (transactionType == EntryType.INCOME) EntryType.INCOME else EntryType.EXPENSE
    val comparisonCurrent = if (comparisonType == EntryType.INCOME) currentIncome else currentExpenses
    val comparisonPrevious = total(previous, comparisonType)
    val largest = current.filter { it.type == EntryType.EXPENSE }
        .groupBy { resolveCategory(categories, it)?.id ?: "legacy:${it.category}" }
        .maxByOrNull { (_, entries) -> entries.sumOf { it.amount } }
    val largestEntry = largest?.value?.firstOrNull()
    return AnalyticsSummary(
        income = currentIncome, expenses = currentExpenses, net = currentIncome - currentExpenses,
        largestCategory = largestEntry?.let { resolveCategory(categories, it)?.name ?: it.category },
        largestCategoryAmount = largest?.value?.sumOf { it.amount } ?: 0L,
        comparisonLabel = if (comparisonType == EntryType.INCOME) "Income" else "Expenses",
        currentComparison = comparisonCurrent, previousComparison = comparisonPrevious,
        comparisonChange = comparisonCurrent - comparisonPrevious,
        previousStart = previousStart, previousEnd = previousEnd,
    )
}

fun analyticsResult(all: List<LedgerEntry>, categories: List<Category>, budgets: List<Budget>,
                    start: LocalDate, end: LocalDate, walletId: String?, categoryId: String?,
                    question: AnalyticsQuestion, today: LocalDate,
                    transactionType: EntryType? = null): AnalyticsResult {
    require(end >= start)
    val entries = analyticsEntries(all, categories, start, end, walletId, categoryId, transactionType)
    val expenses = entries.filter { it.type == EntryType.EXPENSE }
    fun total(items: List<LedgerEntry>, type: EntryType) = items.filter { it.type == type }.sumOf { it.amount }
    val groups = expenses.groupBy { resolveCategory(categories, it)?.id ?: "legacy:${it.category}" }
        .values.sortedByDescending { it.sumOf { e -> e.amount } }
    if (question == AnalyticsQuestion.Categories || question == AnalyticsQuestion.Top) {
        return AnalyticsResult(listOf("Expenses"), groups.map { group ->
            val name = resolveCategory(categories, group.first())?.name ?: group.first().category
            ChartDatum(name, listOf(group.sumOf { it.amount }), group, name)
        }, "All expense categories in the selected period. Percentages use the full filtered expense total.")
    }
    if (question == AnalyticsQuestion.IncomeExpense) return AnalyticsResult(listOf("Income", "Expenses"),
        listOf(ChartDatum("Selected period", listOf(total(entries, EntryType.INCOME), total(entries, EntryType.EXPENSE)), entries)),
        "Recorded income and expenses; opening balances and transfers excluded.")
    if (question == AnalyticsQuestion.Calendar) {
        val days = generateSequence(start) { it.plusDays(1).takeIf { next -> next <= end } }.toList()
        val byDate = entries.groupBy { it.date }
        val types = when (transactionType) {
            EntryType.INCOME -> listOf(EntryType.INCOME)
            EntryType.EXPENSE -> listOf(EntryType.EXPENSE)
            else -> listOf(EntryType.INCOME, EntryType.EXPENSE)
        }
        val series = types.map { if (it == EntryType.INCOME) "Income" else "Expenses" }
        return AnalyticsResult(series, days.map { date ->
            val linked = byDate[date].orEmpty()
            ChartDatum(date.toString(), types.map { type -> total(linked, type) }, linked)
        }, "Daily transaction totals. Tap a date to inspect its recorded amounts and related transactions.")
    }
    if (question == AnalyticsQuestion.Comparison) {
        val days = ChronoUnit.DAYS.between(start, end) + 1
        val previous = analyticsEntries(all, categories, start.minusDays(days), start.minusDays(1), walletId, categoryId, transactionType)
        val compareType = if (transactionType == EntryType.INCOME) EntryType.INCOME else EntryType.EXPENSE
        val compareLabel = if (compareType == EntryType.INCOME) "income" else "expenses"
        val previousTotal = total(previous, compareType)
        val selectedTotal = total(entries, compareType)
        return AnalyticsResult(listOf("Previous $compareLabel", "Selected $compareLabel"), listOf(ChartDatum(
            "Equal-length periods", listOf(previousTotal, selectedTotal),
            (previous + entries).filter { it.type == compareType })),
            "Previous: ${start.minusDays(days)} to ${start.minusDays(1)}. Selected: $start to $end. Recorded totals only.")
    }
    if (question == AnalyticsQuestion.Budgets) {
        val scoped = all.filter { (walletId == null || it.walletId == walletId) &&
            (categoryId == null || resolveCategory(categories, it)?.id == categoryId) }
        return AnalyticsResult(listOf("Spent", "Limit"), budgets.filter { budget ->
            categoryId == null || categories.find { it.id == categoryId }?.name == budget.category
        }.map { budget ->
            val matching = scoped.filter { budgetSpent(budget, listOf(it), today) > 0 }
            ChartDatum("${budget.category} · ${budget.cycle}", listOf(budgetSpent(budget, scoped, today), budget.limit), matching, budget.category)
        }, "Current budget cycles through $today; date filter does not apply. Wallet/category filters affect spending, not limits.")
    }
    val days = ChronoUnit.DAYS.between(start, end) + 1
    val step = ((days + 5) / 6).coerceAtLeast(1)
    val buckets = generateSequence(start) { it.plusDays(step).takeIf { next -> next <= end } }.toList()
    val leaders = groups.take(3).map { resolveCategory(categories, it.first())?.id ?: "legacy:${it.first().category}" }
    val names = leaders.map { id -> categories.find { it.id == id }?.name ?: id.removePrefix("legacy:") }
    var cumulative = 0L
    val multi = question == AnalyticsQuestion.Composition || question == AnalyticsQuestion.CategoryTrends
    val points = buckets.map { first ->
        val last = minOf(first.plusDays(step - 1), end)
        val period = entries.filter { it.date in first..last }
        val spent = total(period, EntryType.EXPENSE)
        cumulative += total(period, EntryType.INCOME) - spent
        val values = if (multi) leaders.map { id -> period.filter { it.type == EntryType.EXPENSE &&
            (resolveCategory(categories, it)?.id ?: "legacy:${it.category}") == id }.sumOf { it.amount } } +
            if (question == AnalyticsQuestion.Composition) listOf(period.filter { it.type == EntryType.EXPENSE &&
                (resolveCategory(categories, it)?.id ?: "legacy:${it.category}") !in leaders }.sumOf { it.amount }) else emptyList()
        else listOf(if (question == AnalyticsQuestion.CashFlow) cumulative else spent)
        val linked = if (question == AnalyticsQuestion.CashFlow) entries.filter { it.date <= last }
            else period.filter { it.type == EntryType.EXPENSE && (question != AnalyticsQuestion.CategoryTrends ||
                (resolveCategory(categories, it)?.id ?: "legacy:${it.category}") in leaders) }
        ChartDatum(if (first == last) first.toString() else "$first – $last", values, linked)
    }
    return AnalyticsResult(if (multi) names + if (question == AnalyticsQuestion.Composition) listOf("Remaining categories") else emptyList()
        else listOf(if (question == AnalyticsQuestion.CashFlow) "Cumulative cash flow" else "Expenses"), points,
        if (question == AnalyticsQuestion.CashFlow) "Starts at zero for the selected period. This is cumulative net cash flow, not net worth."
        else if (multi) "Up to three largest expense categories; numbered intervals match the data table below."
        else "Expense totals by interval; numbered intervals match the data table below. Missing records do not prove zero real-world spending.")
}
