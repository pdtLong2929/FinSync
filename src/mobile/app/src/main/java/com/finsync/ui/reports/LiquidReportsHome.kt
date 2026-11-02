package com.finsync.ui.reports

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.res.painterResource
import com.finsync.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.finsync.data.DemoFixtures
import com.finsync.domain.*
import com.finsync.ui.FinanceState
import com.finsync.ui.components.*
import com.finsync.ui.dashboard.formatVnd
import com.finsync.ui.theme.*
import java.time.YearMonth

/** Reports landing: one report period, one dominant plot and genuine ledger drilldowns. */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable internal fun LiquidReportsHome(state: FinanceState, onOpen: (String) -> Unit,
    mode: LoadState, onTransaction: (String) -> Unit) {
    val today = DemoFixtures.today
    var monthText by rememberSaveable { mutableStateOf(YearMonth.from(today).toString()) }
    val month = YearMonth.parse(monthText)
    var monthMenu by remember { mutableStateOf(false) }
    var chart by rememberSaveable { mutableStateOf("Compare") }
    val chartMode = chart
    var selected by rememberSaveable { mutableStateOf(-1) }
    var showDetails by rememberSaveable { mutableStateOf(false) }
    val comparison = remember(state.entries, month, today) { expenseMonthComparison(state.entries, month, today) }
    val start = month.atDay(1)
    val end = comparison.current.end
    val report = remember(state.entries, start, end) { periodReport(state.entries, start, end) }
    val question = if(chartMode == "Trend") AnalyticsQuestion.Trend else AnalyticsQuestion.Categories
    val plot = remember(state.entries, state.categories, state.budgets, start, end, question) {
        analyticsResult(state.entries, state.categories, state.budgets, start, end, null, null, question, today)
    }
    val months = (state.entries.map { YearMonth.from(it.date) } + (0L..11L).map { YearMonth.from(today).minusMonths(it) }).distinct().sortedDescending()
    val ready = mode == LoadState.Ready || mode == LoadState.Offline
    val chosenPeriod = if(selected == 0) comparison.previous else comparison.current
    val chosenPoint = plot.points.getOrNull(selected)
    val selectedEntries = if(chartMode == "Compare") chosenPeriod.entries else chosenPoint?.entries.orEmpty()
    FeaturePage("Reports", compact = true) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Box {
                    TextButton(onClick = { monthMenu = true }, modifier = Modifier.testTag("report-period")) {
                        Text("${friendlyMonth(month)} ▾", style = MaterialTheme.typography.labelLarge)
                    }
                    DropdownMenu(expanded = monthMenu, onDismissRequest = { monthMenu = false }) {
                        months.forEach { value -> DropdownMenuItem(text = { Text(friendlyMonth(value)) }, onClick = {
                            monthText = value.toString(); selected = -1; showDetails = false; monthMenu = false
                        }) }
                    }
                }
                TextButton(onClick = { onOpen("${if(chartMode == "Trend") "charts/trend" else "charts"}?start=$start&end=$end") }) { Icon(painterResource(R.drawable.ic_filter), "Filters") }
            }
        }
        if(ready) item {
            ReportSignatureHero(report.expenses, report.income)
        }
        item { ReportSegments(listOf("Compare", "Category", "Trend"), chart) { chart = it; selected = -1; showDetails = false } }

        if(mode != LoadState.Ready) item { StateNotice(mode, empty = false, emptyText = "No transactions in this period") {} }
        if(ready) {
            item {
                Surface(modifier=Modifier.liquidGlass(LiquidTokens.panelShape),shape = LiquidTokens.panelShape, color = androidx.compose.ui.graphics.Color.Transparent) {
                Column(Modifier.fillMaxWidth().padding(Space.medium)) {
                    if(chartMode == "Compare") MonthComparisonChart(comparison, selected.takeIf { it >= 0 }) { selected = it; showDetails = true }
                    else if(plot.points.isEmpty()) Text("No expenses in this period")
                    else {
                        Text(if(chartMode == "Trend") "Spending over time" else "Where your money went", style = MaterialTheme.typography.titleMedium)
                        AnalyticsPlot(plot, if(chartMode == "Trend") ChartKind.Line else ChartKind.Donut, selected, showLegend = false,
                            plotHeight = ReportMetalTokens.plotHeight) { selected = it; showDetails = true }
                        if(chartMode == "Trend") Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            listOf(0, plot.points.lastIndex / 2, plot.points.lastIndex).distinct().forEach { index ->
                                val date = java.time.LocalDate.parse(plot.points[index].label.take(10))
                                Text(date.format(java.time.format.DateTimeFormatter.ofPattern("d MMM", java.util.Locale.ENGLISH)),
                                    style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        OptionPicker("Inspect ${if(chartMode == "Trend") "interval" else "category"}", chosenPoint?.label ?: "Choose data", plot.points.map { it.label }, { label ->
                            selected = plot.points.indexOfFirst { it.label == label }; showDetails = true
                        })
                    }
                }
                }
            }
            item { Text("Spending by Category", style = MaterialTheme.typography.titleMedium) }
            if(chartMode == "Compare" && plot.points.isNotEmpty()) item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.small)) {
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        AnalyticsPlot(plot, ChartKind.Donut, -1, showLegend = false, plotHeight = SoftFinanceTokens.reportDonutHeight) { selected = it; chart = "Category"; showDetails = true }
                        Text(compactVnd(report.expenses), style = MaterialTheme.typography.labelLarge)
                    }
                    Column(Modifier.weight(1f)) {
                        plot.points.take(4).forEachIndexed { index, point ->
                            val tint = when(state.categories.find { it.name == point.category }?.color) {
                                CategoryColor.Cyan -> LiquidTokens.chartAqua
                                CategoryColor.Rose -> LiquidTokens.chartRose
                                else -> LiquidTokens.chartBlue
                            }
                            Row(Modifier.fillMaxWidth().heightIn(min = LiquidTokens.minTarget).clickable {
                                selected = index; chart = "Category"; showDetails = true
                            }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.small)) {
                                Surface(Modifier.size(Space.small), shape = androidx.compose.foundation.shape.CircleShape, color = tint) {}
                                Text(point.label, Modifier.weight(1f), style = MaterialTheme.typography.labelSmall)
                                Text("${point.values.sum() * 100 / report.expenses.coerceAtLeast(1)}%", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
            if(report.categories.isEmpty()) item { Text("No expenses yet. Choose another month.") }
            items(report.categories.entries.sortedByDescending { it.value }, key = { it.key }) { (category, amount) ->
                Row(Modifier.fillMaxWidth().heightIn(min = LiquidTokens.minTarget).clickable {
                    chart = "Category"
                    // Category chart ordering follows the same aggregation as its drilldown.
                    val categories = analyticsResult(state.entries, state.categories, state.budgets, start, end, null, null, AnalyticsQuestion.Categories, today)
                    selected = categories.points.indexOfFirst { it.category == category || it.label == category }
                    showDetails = true
                }.padding(vertical = Space.small), horizontalArrangement = Arrangement.spacedBy(Space.medium), verticalAlignment = Alignment.CenterVertically) {
                    CategoryBadge(category)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Space.small)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(category, Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                            Text(formatVnd(amount), style = MaterialTheme.typography.labelLarge)
                        }
                        MetalProgress(progress = { (amount.toDouble()/report.expenses.coerceAtLeast(1)).toFloat() }, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(Space.small)) {
                TextButton(onClick = { onOpen("${if(chartMode == "Trend") "charts/trend" else "charts"}?start=$start&end=$end") }) { Text("See analytics") }
                TextButton(onClick = { onOpen("analytics") }) { Text("Income & dates") }
                TextButton(onClick = { onOpen("budgets") }) { Text("Budgets") }
                TextButton(onClick = { onOpen("goals") }) { Text("Savings goals") }
                TextButton(onClick = { onOpen("insights") }) { Text("Spending guide") }
            }
        }
    }
    if(showDetails && selected >= 0) ModalBottomSheet(onDismissRequest = { showDetails = false }, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        androidx.compose.foundation.lazy.LazyColumn(Modifier.fillMaxWidth().fillMaxHeight(.85f).testTag("report-detail-list"), contentPadding = PaddingValues(Space.large), verticalArrangement = Arrangement.spacedBy(Space.medium)) {
            item {
                TextButton(onClick = { showDetails = false }) { Text("Close") }
                Text(if(chartMode == "Compare") friendlyMonth(chosenPeriod.month) else chosenPoint?.label.orEmpty(), style = MaterialTheme.typography.titleLarge)
                Text(formatVnd(selectedEntries.sumOf { it.amount }), Modifier.testTag("report-detail-amount"), style = MaterialTheme.typography.headlineMedium)
                Text(if(chartMode == "Compare") "${chosenPeriod.month.atDay(1)} – ${chosenPeriod.end}" else "$start – $end", style = MaterialTheme.typography.bodySmall)
            }
            if(selectedEntries.isEmpty()) item { Text("No expenses recorded in this period") }
            items(selectedEntries, key = { it.id }) { entry ->
                LedgerRow(entry.note.ifBlank { entry.category }, "${entry.date} · ${state.wallets.find { it.id == entry.walletId }?.name.orEmpty()}",
                    formatVnd(entry.amount), { onTransaction(entry.id) }, category = entry.category, categoryId = entry.categoryId)
            }

        }
    }
}




