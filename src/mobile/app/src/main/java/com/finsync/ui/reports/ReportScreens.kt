package com.finsync.ui.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import com.finsync.data.*
import com.finsync.domain.*
import com.finsync.ui.*
import com.finsync.ui.components.*
import com.finsync.ui.dashboard.budgetProgress
import com.finsync.ui.dashboard.formatVnd
import com.finsync.ui.theme.*
import java.time.YearMonth
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

internal fun friendlyMonth(month: YearMonth): String = month.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH))

@Composable
fun ReportFlow(state: FinanceState, finance: FinanceViewModel, route: String, navigate: (String) -> Unit, onFeedback: (String) -> Unit = {}, onTransaction: (String) -> Unit = {}) {
    key(route) {
        when {
            route.startsWith("budget/") -> BudgetForm(state, state.budgets.find { it.id == route.substringAfter('/') }, { navigate("budgets") }) { id, category, limit, cycle ->
                finance.saveBudget(id, category, limit, cycle).also { if (it == null) { navigate("budgets"); onFeedback("Budget saved") } }
            }
            route == "budgets" -> BudgetScreen(state, { navigate("") }, { navigate("budget/$it") })
            route.startsWith("goal/") -> GoalForm(state.goals.find { it.id == route.substringAfter('/') }, { navigate("goals") }) { id, title, target, saved, deadline ->
                finance.saveGoal(id, title, target, saved, deadline).also { if (it == null) { navigate("goals"); onFeedback("Savings progress saved") } }
            }
            route == "goals" -> GoalsScreen(state.goals, { navigate("") }, { navigate("goal/$it") })
            route.substringBefore('?') in listOf("charts", "charts/trend") -> {
                val dates = route.substringAfter('?', "").split('&').mapNotNull {
                    val pair = it.split('=', limit = 2)
                    if (pair.size == 2) pair[0] to runCatching { LocalDate.parse(pair[1]) }.getOrNull() else null
                }.toMap()
                AnalyticsScreen(state, { navigate("") }, onTransaction,
                    initialQuestion = if (route.substringBefore('?') == "charts/trend") AnalyticsQuestion.Trend else AnalyticsQuestion.Categories,
                    initialStart = dates["start"], initialEnd = dates["end"])
            }
            route == "insights" -> InsightsScreen({ navigate("") })
            else -> ReportsScreen(state, route, { navigate(it) }, onTransaction = onTransaction)
        }
    }
}

@Composable
fun ReportsScreen(state: FinanceState, page: String, onOpen: (String) -> Unit, mode: LoadState = LoadState.Ready, onTransaction: (String) -> Unit = {}) {
    if (page.isEmpty()) {
        LiquidReportsHome(state, onOpen, mode, onTransaction)
        return
    }
    var month by rememberSaveable { mutableStateOf(YearMonth.from(DemoFixtures.today).toString()) }
    var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }
    val months = (state.entries.map { YearMonth.from(it.date) } + (0L..5L).map { YearMonth.from(DemoFixtures.today).minusMonths(it) }).distinct().sortedDescending()
    var rangeMode by rememberSaveable { mutableStateOf(false) }
    var startDraft by rememberSaveable { mutableStateOf(DemoFixtures.today.withDayOfMonth(1).toString()) }
    var endDraft by rememberSaveable { mutableStateOf(DemoFixtures.today.toString()) }
    var appliedStart by rememberSaveable { mutableStateOf(startDraft) }
    var appliedEnd by rememberSaveable { mutableStateOf(endDraft) }
    var rangeError by rememberSaveable { mutableStateOf<String?>(null) }
    var showTransactions by rememberSaveable { mutableStateOf(false) }
    val selectedMonth = YearMonth.parse(month)
    val start = if (rangeMode) LocalDate.parse(appliedStart) else selectedMonth.atDay(1)
    val end = if (rangeMode) LocalDate.parse(appliedEnd) else selectedMonth.atEndOfMonth()
    val chartEnd = if (start <= DemoFixtures.today) minOf(end, DemoFixtures.today) else end
    val periodLabel = if (rangeMode) "$start to $end" else month
    val selectedEntries = remember(state.entries, start, end) { periodEntries(state.entries, start, end) }
    val report = remember(state.entries, start, end) { periodReport(state.entries, start, end) }
    val previous = remember(state.entries, selectedMonth) { monthlyReport(state.entries, selectedMonth.minusMonths(1)) }
    val trend = remember(state.entries, end) { spendingTrend(state.entries, YearMonth.from(end)) }
    val title = when (page) { "analytics" -> "Income and expenses"; "categories" -> "Category breakdown"; else -> "Financial reports" }
    FeaturePage(title, if (page.isEmpty()) null else ({ onOpen("") })) {
        item { Text("Personal activity · VND", style = MaterialTheme.typography.labelLarge) }
        if(page.isEmpty()) item { ReportChartPreviews(state, mode,
            { chart -> onOpen("$chart?start=$start&end=$chartEnd") }, start, chartEnd) }
        item { ChoiceChips(listOf("Month", "Date range"), if (rangeMode) "Date range" else "Month", { rangeMode = it == "Date range"; selectedCategory = null; rangeError = null }) }
        if (!rangeMode) item { OptionPicker("Report month", friendlyMonth(selectedMonth), months.map(::friendlyMonth), { label ->
            month = months.first { friendlyMonth(it) == label }.toString(); selectedCategory = null
        }) }
        else {
            item { DateField(startDraft, { startDraft = it; rangeError = null }, "Start date (YYYY-MM-DD)") }
            item { DateField(endDraft, { endDraft = it; rangeError = null }, "End date (YYYY-MM-DD)") }
            item {
                ErrorText(rangeError)
                MetalButton(onClick = {
                    rangeError = reportRangeError(startDraft, endDraft, DemoFixtures.today)
                    if (rangeError == null) { appliedStart = startDraft; appliedEnd = endDraft; selectedCategory = null }
                }) { Text("Apply dates") }
                Text("Showing: $appliedStart to $appliedEnd")
            }
        }
        item { StateNotice(mode, report.income == 0L && report.expenses == 0L, if (rangeMode) "No personal transactions in this date range." else "No personal transactions in this month. Choose another period.") {} }
            if (!rangeMode && mode in listOf(LoadState.Ready, LoadState.Offline)) item { FinanceCard {
                Text("${friendlyMonth(selectedMonth)} spending", style = MaterialTheme.typography.titleMedium)
                Text(formatVnd(report.expenses), style = MaterialTheme.typography.headlineLarge)
                val delta = report.expenses - previous.expenses
                if (previous.expenses == 0L) Text("No expense data for ${friendlyMonth(selectedMonth.minusMonths(1))}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                else Text("${if (delta > 0) "↑" else if (delta < 0) "↓" else "="} ${"%.0f".format(Locale.ENGLISH, kotlin.math.abs(delta.toDouble()) * 100 / previous.expenses)}% · vs ${friendlyMonth(selectedMonth.minusMonths(1))}")
                if (selectedMonth == YearMonth.from(DemoFixtures.today)) Text("Month to date · previous month in full", style = MaterialTheme.typography.labelSmall)
            } }
        if (mode in listOf(LoadState.Ready, LoadState.Offline) && (report.income != 0L || report.expenses != 0L)) {
            item { FinanceCard(emphasized = true) {
                Text("Net cash flow · $periodLabel"); Text(formatVnd(report.income - report.expenses), style = MaterialTheme.typography.headlineMedium)
                Text("Income ${formatVnd(report.income)}"); Text("Expenses ${formatVnd(report.expenses)}")
            } }
            if (page != "categories") item {
                FinanceCard {
                    SectionHeading("Income vs expenses")
                    val max = maxOf(report.income, report.expenses, 1L)
                    ChartBar("Income", report.income, max, LocalFinanceColors.current.income)
                    ChartBar("Expenses", report.expenses, max, LocalFinanceColors.current.expense)
                }
            }
            if (page != "analytics") {
                item { SectionHeading("Spending by category"); Text("Select a category to view its transactions.") }
                items(report.categories.entries.sortedByDescending { it.value }, key = { it.key }) { (category, amount) ->
                    Column {
                        FilterChip(selectedCategory == category, { selectedCategory = if (selectedCategory == category) null else category },
                            label = { Text(category) }, leadingIcon = { CategoryBadge(category) }, modifier = Modifier.minimumInteractiveComponentSize())
                        ChartBar(category, amount, report.expenses.coerceAtLeast(1))
                    }
                }
                selectedCategory?.let { category ->
                    item { SectionHeading("$category transactions") }
                    items(selectedEntries.filter { it.type == EntryType.EXPENSE && it.category == category }, key = { "category-${it.id}" }) {
                        LedgerRow(it.note.ifBlank { it.category }, it.date.toString(), formatVnd(it.amount), { onTransaction(it.id) }, category = it.category, categoryId = it.categoryId)
                    }
                }
            }
        }
        if (mode in listOf(LoadState.Ready, LoadState.Offline)) {
            if (page != "categories") item { FinanceCard {
                SectionHeading("Six-month spending trend")
                Text("Monthly expense totals; months without recorded spending show zero.")
                if (rangeMode) Text("Trend shows full calendar months ending ${YearMonth.from(end)}; totals above use your selected dates.")
                val maximum = trend.maxOf { it.second }.coerceAtLeast(1)
                trend.forEach { (period, amount) -> ChartBar(period.toString(), amount, maximum, LocalFinanceColors.current.expense) }
            } }
            if (report.categories.isNotEmpty()) item {
                val largest = report.categories.maxBy { it.value }
                Text("Largest spending category: ${largest.key} · ${formatVnd(largest.value)}")
            }
            item { TextButton(onClick = { showTransactions = !showTransactions }) {
                Text(if (showTransactions) "Hide period transactions" else "View period transactions")
            } }
            if (showTransactions) {
                item { SectionHeading("Transactions · $periodLabel") }
                if (selectedEntries.isEmpty()) item { Text("No transactions in the selected period.") }
                items(selectedEntries, key = { "period-${it.id}" }) { entry ->
                    LedgerRow(entry.note.ifBlank { entry.category }, "${entry.date} · ${if (entry.type == EntryType.INCOME) "Income" else "Expense"}",
                        formatVnd(entry.amount), { onTransaction(entry.id) }, category = entry.category, categoryId = entry.categoryId)
                }
            }
        }
        if (page.isEmpty()) {
            item { SectionHeading("Explore your finances") }
            item { MetalOutlineButton(onClick = { onOpen("analytics") }) { Text("Income and expense analytics") } }
            item { MetalOutlineButton(onClick = { onOpen("categories") }) { Text("Category spending breakdown") } }
            item { MetalButton(onClick = { onOpen("budgets") }) { Text("Manage budgets") } }
            item { MetalOutlineButton(onClick = { onOpen("goals") }) { Text("Savings goals") } }
            item { TextButton(onClick = { onOpen("insights") }) { Text("Spending guide") } }
        }
    }
}

/** Accessible horizontal chart; amounts and labels remain available without relying on color. */
@Composable
private fun ChartBar(label: String, amount: Long, maximum: Long, accent: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary) {
    Column(Modifier.fillMaxWidth().padding(vertical = Space.small), verticalArrangement = Arrangement.spacedBy(Space.small)) {
        Text("$label · ${formatVnd(amount)}", style = MaterialTheme.typography.titleMedium)
        MetalProgress(progress = { budgetProgress(amount, maximum) }, accent = accent, modifier = Modifier.fillMaxWidth().semantics {
            stateDescription = "$label: ${formatVnd(amount)}"
        })
    }
}

@Composable
fun BudgetScreen(state: FinanceState, onBack: () -> Unit, onEdit: (String) -> Unit) {
    FeaturePage("Budgets", onBack) {
        item { DemoNotice(); Text("Current date: ${DemoFixtures.today}. Weekly cycles begin Monday.") }
        item { MetalButton(onClick = { onEdit("new") }) { Text("Create budget") } }
        if (state.budgets.isEmpty()) item { Text("No budgets yet. Add a limit for an expense category.") }
        items(state.budgets, key = { it.id }) { budget ->
            var expanded by rememberSaveable { mutableStateOf(false) }
            val spent = budgetSpent(budget, state.entries, DemoFixtures.today)
            FinanceCard {
                CategoryBadge(budget.category)
                Text("${budget.category} · ${budget.cycle}", style = MaterialTheme.typography.titleLarge)
                Text("${formatVnd(spent)} of ${formatVnd(budget.limit)}")
                val color = when {
                    spent >= budget.limit -> MaterialTheme.colorScheme.error
                    spent * 100 >= budget.limit * 80 -> LocalFinanceColors.current.warning
                    else -> LocalFinanceColors.current.income
                }
                MetalProgress(progress = { budgetProgress(spent, budget.limit) }, modifier = Modifier.fillMaxWidth(), accent = color)
                Text(budgetStatus(spent, budget.limit), color = color)
                Text("Remaining: ${formatVnd((budget.limit - spent).coerceAtLeast(0))}")
                TextButton(onClick = { expanded = !expanded }) { Text(if (expanded) "Hide spending" else "Explain ${budget.category} spending") }
                if (expanded) {
                    val matching = state.entries.filter { it.type == EntryType.EXPENSE && it.category == budget.category &&
                        budgetSpent(budget, listOf(it), DemoFixtures.today) > 0 }.sortedByDescending { it.date }
                    if (matching.isEmpty()) Text("No spending in this budget cycle.")
                    matching.take(5).forEach { LedgerRow(it.note.ifBlank { it.category }, it.date.toString(), formatVnd(it.amount), category = it.category, categoryId = it.categoryId) }
                    if (matching.size > 5) Text("Showing the 5 most recent of ${matching.size} expenses. Full history is in Wallets.")
                }
                MetalOutlineButton(onClick = { onEdit(budget.id) }) { Text("Edit ${budget.category} budget") }
            }
        }
    }
}

@Composable
fun BudgetForm(state: FinanceState, budget: Budget?, onBack: () -> Unit, onSave: (String?, String, String, BudgetCycle) -> String?) {
    val categories = state.categories.filter { it.type == EntryType.EXPENSE }.map { it.name }
    var category by rememberSaveable { mutableStateOf(budget?.category ?: categories.first()) }
    var limit by rememberSaveable { mutableStateOf(budget?.limit?.toString().orEmpty()) }
    var cycle by rememberSaveable { mutableStateOf(budget?.cycle?.name ?: BudgetCycle.Monthly.name) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    FeaturePage(if (budget == null) "Create budget" else "Edit budget", onBack,
        category != (budget?.category ?: categories.first()) || limit != budget?.limit?.toString().orEmpty() || cycle != (budget?.cycle?.name ?: BudgetCycle.Monthly.name), primaryAction = {
            ErrorText(error)
            MetalButton(onClick = { error = onSave(budget?.id, category, limit, BudgetCycle.valueOf(cycle)) }, Modifier.fillMaxWidth()) { Text("Save budget") }
        }) {
        item { DemoNotice() }
        item { CategoryBadge(category); OptionPicker("Expense category", category, categories, { category = it }) }
        item { AmountField(limit, { limit = it }, "Budget limit (VND)") }
        item { ChoiceChips(BudgetCycle.entries.map { it.name }, cycle, { cycle = it }) }
        item { Text("Warnings appear at 80% and 100%. Group spending is excluded.") }
    }
}

@Composable
fun GoalsScreen(goals: List<SavingsGoal>, onBack: () -> Unit, onEdit: (String) -> Unit) {
    FeaturePage("Savings goals", onBack) {
        item { DemoNotice(); Text("Progress tracking only. Recorded savings are not added to net worth or transferred from wallets.") }
        item { MetalButton(onClick = { onEdit("new") }) { Text("Create savings goal") } }
        if (goals.isEmpty()) item { Text("Start with a goal that matters to you.") }
        items(goals, key = { it.id }) { goal -> FinanceCard {
            MetalEyebrow("SAVINGS / PERSONAL")
            Text(goal.title, style = MaterialTheme.typography.headlineSmall)
            Text("${formatVnd(goal.saved)} of ${formatVnd(goal.target)} · Due ${goal.deadline}")
            MetalProgress(progress = { budgetProgress(goal.saved, goal.target) }, modifier = Modifier.fillMaxWidth(), accent = LocalFinanceColors.current.income)
            Text(if (goal.saved >= goal.target) "Goal reached!" else "${goal.saved * 100 / goal.target}% recorded · ${formatVnd(goal.target - goal.saved)} remaining")
            MetalOutlineButton(onClick = { onEdit(goal.id) }) { Text("Update recorded progress") }
        } }
    }
}

@Composable
fun GoalForm(goal: SavingsGoal?, onBack: () -> Unit, onSave: (String?, String, String, String, String) -> String?) {
    var title by rememberSaveable { mutableStateOf(goal?.title.orEmpty()) }
    var target by rememberSaveable { mutableStateOf(goal?.target?.toString().orEmpty()) }
    var saved by rememberSaveable { mutableStateOf(goal?.saved?.toString() ?: "0") }
    var date by rememberSaveable { mutableStateOf((goal?.deadline ?: DemoFixtures.today.plusMonths(6)).toString()) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    FeaturePage(if (goal == null) "Create savings goal" else "Update savings goal", onBack,
        title != goal?.title.orEmpty() || target != goal?.target?.toString().orEmpty() || saved != (goal?.saved?.toString() ?: "0") || date != (goal?.deadline ?: DemoFixtures.today.plusMonths(6)).toString(), primaryAction = {
            ErrorText(error)
            MetalButton(onClick = { error = onSave(goal?.id, title, target, saved, date) }, Modifier.fillMaxWidth()) { Text("Save goal") }
        }) {
        item { DemoNotice(); Text("Track your progress here. No money is transferred.") }
        item { ChromeTextField(title, { title = it }, Modifier.fillMaxWidth(), label = { Text("Goal title") }) }
        item { AmountField(target, { target = it }, "Target (VND)") }
        item { AmountField(saved, { saved = it }, "Recorded savings (VND)") }
        item { DateField(date, { date = it }, "Deadline (YYYY-MM-DD)") }
    }
}

@Composable
fun InsightsScreen(onBack: () -> Unit) {
    var refreshed by rememberSaveable { mutableStateOf(false) }
    FeaturePage("Spending guide", onBack) {
        item { FinanceCard(emphasized = true) {
            MetalEyebrow("FINSYNC / PERSPECTIVE")
            Text("Small steps, clearer goals", style = MaterialTheme.typography.headlineLarge)
            Text("General guidance, not a personalized analysis of your finances.")
        } }
        items(ReportFixtures.insights) { (title, body) ->
            FinanceCard {
                MetalEyebrow("PERSPECTIVE / " + (ReportFixtures.insights.indexOfFirst { it.first == title } + 1).toString().padStart(2, '0'))
                Text(title, style = MaterialTheme.typography.headlineSmall)
                Text(body, style = MaterialTheme.typography.bodyLarge)
            }
        }
        item { TextButton(onClick = { refreshed = true }) { Text("Refresh tips") }; if (refreshed) Text("Tips are up to date.") }
    }
}

@Preview(showBackground = true)
@Composable private fun ReportsPreview() { FinSyncTheme { ReportsScreen(FinanceState(), "", {}) } }
@Preview(showBackground = true, fontScale = 1.5f)
@Composable private fun BudgetsDarkPreview() { FinSyncTheme(darkTheme = true) { BudgetScreen(FinanceState(), {}, {}) } }
@Preview(showBackground = true)
@Composable private fun ReportsEmptyPreview() { FinSyncTheme { ReportsScreen(FinanceState(entries = emptyList()), "", {}) } }
@Preview(showBackground = true)
@Composable private fun ReportsErrorPreview() { FinSyncTheme { ReportsScreen(FinanceState(), "", {}, LoadState.Error) } }
