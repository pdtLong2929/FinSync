package com.finsync.ui.reports

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.finsync.domain.*
import com.finsync.data.DemoFixtures
import com.finsync.ui.FinanceState
import com.finsync.ui.components.*
import com.finsync.ui.dashboard.formatVnd
import com.finsync.ui.theme.*
import com.finsync.ui.wallets.EntryRow
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.*

private object ChartTokens {
    val height = 224.dp
    val stroke = 3.dp
    val ring = 28.dp
    val point = 5.dp
    val calendarWidth = 364.dp
    val calendarCell = 52.dp
}

@Composable fun AnalyticsScreen(state: FinanceState, onBack: () -> Unit, onTransaction: (String) -> Unit,
                               mode: LoadState = LoadState.Ready, onRetry: () -> Unit = {},
                               initialQuestion: AnalyticsQuestion = AnalyticsQuestion.Categories,
                               initialStart: LocalDate? = null, initialEnd: LocalDate? = null) {
    val today = DemoFixtures.today
    var advanced by rememberSaveable { mutableStateOf(false) }
    var filtersExpanded by rememberSaveable { mutableStateOf(false) }
    var questionName by rememberSaveable { mutableStateOf(initialQuestion.name) }
    var kindName by rememberSaveable { mutableStateOf(compatibleCharts(initialQuestion).first().name) }
    var periodName by rememberSaveable { mutableStateOf(if (initialStart != null && initialEnd != null) AnalyticsPeriod.Custom.name else AnalyticsPeriod.ThisMonth.name) }
    var transactionTypeName by rememberSaveable { mutableStateOf("All transactions") }
    var walletId by rememberSaveable { mutableStateOf<String?>(null) }
    var categoryId by rememberSaveable { mutableStateOf<String?>(null) }
    var startDraft by rememberSaveable { mutableStateOf((initialStart ?: today.withDayOfMonth(1)).toString()) }
    var endDraft by rememberSaveable { mutableStateOf((initialEnd ?: today).toString()) }
    var appliedStart by rememberSaveable { mutableStateOf(startDraft) }
    var appliedEnd by rememberSaveable { mutableStateOf(endDraft) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var selected by rememberSaveable { mutableStateOf(-1) }
    var drillDown by rememberSaveable { mutableStateOf(false) }
    val question = AnalyticsQuestion.valueOf(questionName)
    val period = AnalyticsPeriod.valueOf(periodName)
    val selectedType = when (transactionTypeName) {
        "Income" -> EntryType.INCOME
        "Expense" -> EntryType.EXPENSE
        else -> null
    }
    // Budgets always describe expense-only cycles, even if the last report area used income.
    val transactionType = if (question == AnalyticsQuestion.Budgets) EntryType.EXPENSE else selectedType
    val compatible = compatibleCharts(question)
    val kind = ChartKind.valueOf(kindName).takeIf { it in compatible } ?: compatible.first()
    val range = analyticsDateRange(period, today,
        runCatching { LocalDate.parse(appliedStart) }.getOrNull(), runCatching { LocalDate.parse(appliedEnd) }.getOrNull())
    val start = range.start
    val end = range.end
    val calendarTooLong = question == AnalyticsQuestion.Calendar && end.toEpochDay() - start.toEpochDay() + 1 > 366
    val result = remember(state.entries, state.categories, state.budgets, start, end, walletId, categoryId, question, transactionType, calendarTooLong) {
        if (calendarTooLong) AnalyticsResult(emptyList(), emptyList(), "Calendar heatmap supports up to 366 days.")
        else analyticsResult(state.entries, state.categories, state.budgets, start, end, walletId, categoryId, question, today, transactionType)
    }
    val summary = remember(state.entries, state.categories, start, end, walletId, categoryId, transactionType) {
        analyticsSummary(state.entries, state.categories, start, end, walletId, categoryId, transactionType)
    }
    val resetSelection = { selected = -1; drillDown = false }
    val monthStarts = remember(start, end, question, calendarTooLong) {
        if (question != AnalyticsQuestion.Calendar || calendarTooLong) emptyList()
        else buildList {
                var month = YearMonth.from(start)
                val lastMonth = YearMonth.from(end)
                while (!month.isAfter(lastMonth)) { add(month); month = month.plusMonths(1) }
            }
    }
    val pointsByDate = remember(result.points) {
        result.points.mapNotNull { point -> runCatching { LocalDate.parse(point.label) }.getOrNull()?.let { it to point } }.toMap()
    }
    val indexByDate = remember(result.points) { result.points.mapIndexedNotNull { index, point ->
        runCatching { LocalDate.parse(point.label) }.getOrNull()?.let { it to index }
    }.toMap() }
    FeaturePage("Charts & Analytics", onBack) {
        item { Text("Personal activity · ${period.label}", style = MaterialTheme.typography.labelLarge) }
        item { ChoiceChips(listOf("Simple", "Advanced"), if(advanced) "Advanced" else "Simple", {
            advanced = it == "Advanced"
            if(!advanced && question.advanced) { questionName = AnalyticsQuestion.Categories.name; kindName = ChartKind.Donut.name }
            resetSelection()
        }) }
        item { OptionPicker("Report area", question.label, AnalyticsQuestion.entries.filter { advanced || !it.advanced }.map { it.label }, { label ->
            val next = AnalyticsQuestion.entries.first { it.label == label }; questionName = next.name
            kindName = compatibleCharts(next).first().name; resetSelection()
            if (next == AnalyticsQuestion.Budgets && state.categories.find { it.id == categoryId }?.type != EntryType.EXPENSE) categoryId = null
        }) }
        item { if(compatible.size > 1) ChoiceChips(compatible.map { it.label }, kind.label, { label ->
            kindName = compatible.first { it.label == label }.name; resetSelection()
        }) }
        item { TextButton(onClick = { filtersExpanded = !filtersExpanded }) { Text(if (filtersExpanded) "Hide report filters" else "Report filters") } }
        if (filtersExpanded) {
        item { OptionPicker("Period", period.label, AnalyticsPeriod.entries.map { it.label }, { label ->
            periodName = AnalyticsPeriod.entries.first { it.label == label }.name; resetSelection()
        }) }
        if(period == AnalyticsPeriod.Custom) {
            item { DateField(startDraft, { startDraft = it; error = null }, "Analytics start date") }
            item { DateField(endDraft, { endDraft = it; error = null }, "Analytics end date") }
            item { ErrorText(error); MetalButton(onClick = {
                error = reportRangeError(startDraft, endDraft, today)
                if(error == null) { appliedStart = startDraft; appliedEnd = endDraft; resetSelection() }
            }) { Text("Apply analytics dates") } }
        }
        item { Text("Showing $start to $end · current periods are partial") }
        if (question == AnalyticsQuestion.Budgets) item { Text("Budget progress uses expense records in the current budget cycle; dates do not change its limit.") }
        else item { OptionPicker("Transaction type", transactionTypeName,
            listOf("All transactions", "Income", "Expense"), { value ->
                transactionTypeName = value
                val nextType = when (value) { "Income" -> EntryType.INCOME; "Expense" -> EntryType.EXPENSE; else -> null }
                if (nextType != null && state.categories.find { it.id == categoryId }?.type != nextType) categoryId = null
                resetSelection()
            }) }
        item { OptionPicker("Analytics wallet", state.wallets.find { it.id == walletId }?.name ?: "All wallets",
            listOf("All wallets") + state.wallets.map { it.name }, { name -> walletId = state.wallets.find { it.name == name }?.id; resetSelection() }) }
        val availableCategories = if (question == AnalyticsQuestion.Budgets) state.categories.filter { it.type == EntryType.EXPENSE }
            else state.categories.filter { selectedType == null || it.type == selectedType }
        item { OptionPicker("Analytics category", state.categories.find { it.id == categoryId }?.name ?: "All categories",
            listOf("All categories") + availableCategories.map { it.name }, { name -> categoryId = availableCategories.find { it.name == name }?.id; resetSelection() }) }
        item { TextButton(onClick = { periodName = AnalyticsPeriod.ThisMonth.name; transactionTypeName = "All transactions"; walletId = null; categoryId = null; error = null
            startDraft = today.withDayOfMonth(1).toString(); endDraft = today.toString(); appliedStart = startDraft; appliedEnd = endDraft; resetSelection()
        }) { Text("Reset filters") } }
        }
        item {
            if (mode in listOf(LoadState.Loading, LoadState.Error)) StateNotice(mode, false, "No values for these filters.", onRetry) {}
            else if (calendarTooLong) ErrorText("Calendar heatmap is limited to 366 days. Choose a shorter period.")
            else StateNotice(mode, result.points.isEmpty() || result.points.all { p -> p.values.all { it == 0L } },
                if (question == AnalyticsQuestion.Budgets) "No budget records match these filters." else "No values for these filters. Try another period or reset filters.", onRetry) {}
        }
        if(mode in listOf(LoadState.Ready, LoadState.Offline)) {
            item { FinanceCard(emphasized = true) {
                SectionHeading("Period summary")
                Text("Income: ${formatVnd(summary.income)}")
                Text("Expenses: ${formatVnd(summary.expenses)}")
                Text("Net cash flow: ${formatVnd(summary.net)}", style = MaterialTheme.typography.titleMedium)
                Text(summary.largestCategory?.let { "Largest spending category: $it · ${formatVnd(summary.largestCategoryAmount)}" }
                    ?: "Largest spending category: none recorded")
                Text(when {
                    summary.previousComparison == 0L -> "No ${summary.comparisonLabel.lowercase()} recorded in previous period"
                    summary.comparisonChange > 0 -> "${summary.comparisonLabel} increased by ${formatVnd(summary.comparisonChange)}"
                    summary.comparisonChange < 0 -> "${summary.comparisonLabel} decreased by ${formatVnd(-summary.comparisonChange)}"
                    else -> "${summary.comparisonLabel} unchanged vs previous period"
                })
                Text("Previous equal-length period (${summary.previousStart} to ${summary.previousEnd}): ${formatVnd(summary.previousComparison)}")
            } }
            item { FinanceCard {
                SectionHeading(question.label)
                Text(result.summary)
                val hasValues = result.points.any { point -> point.values.any { it != 0L } }
                if ((result.points.isNotEmpty() && hasValues) || (kind == ChartKind.Heatmap && !calendarTooLong)) {
                    if (kind == ChartKind.Heatmap) {
                        Text("Lighter = less activity · darker = more. Cells contain exact daily VND values for assistive reading.")
                        HeatmapLegend()
                    } else AnalyticsPlot(result, kind, selected) { selected = it; drillDown = false }
                    if(kind in listOf(ChartKind.Line, ChartKind.Columns, ChartKind.Stacked)) Row(Modifier.fillMaxWidth()) {
                        result.points.indices.forEach { index -> Text("${index + 1}", Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center) }
                    }
                    Text(if (kind == ChartKind.Heatmap) "Tap a date cell to see its totals and transactions."
                        else "Tap a chart element or select a numbered data row below.")
                }
                result.series.forEachIndexed { index, name -> Text("${index + 1}. $name" +
                    if(kind == ChartKind.Line) " · ${listOf("solid", "dashed", "dotted", "long dash")[index % 4]}" else "") }
                if(question in listOf(AnalyticsQuestion.Categories, AnalyticsQuestion.Top)) {
                    Text("Total expenses: ${formatVnd(result.points.sumOf { it.values.first() })}")
                    result.points.firstOrNull()?.let { Text("Largest category: ${it.label}") }
                }
            } }
            if (kind == ChartKind.Heatmap && !calendarTooLong) {
                items(monthStarts, key = { "heatmap-month-$it" }) { month ->
                    CalendarHeatmapMonth(month, start, end, pointsByDate, pointsByDate.values.maxOfOrNull { it.values.sum() } ?: 0L,
                        result.series, selected.takeIf { it in result.points.indices }?.let { result.points[it].label },
                        onSelect = { date -> indexByDate[date]?.let { selected = it; drillDown = false } })
                }
            } else {
                val denominator = result.points.sumOf { it.values.firstOrNull() ?: 0L }
                items(result.points.size, key = { "chart-data-$questionName-$it" }) { index ->
                    val point = result.points[index]
                    Column(Modifier.fillMaxWidth().heightIn(min = LiquidTokens.minTarget)
                        .testTag("analytics-point-$index")
                        .border(MetalTokens.rim, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium)
                        .clickable(role = Role.Button) { selected = index; drillDown = false }
                        .semantics { this.selected = selected == index }) {
                        Column(Modifier.padding(Space.medium), verticalArrangement = Arrangement.spacedBy(Space.small)) {
                            point.category?.let { CategoryBadge(it) }
                            Text("${index + 1}. ${point.label}", style = MaterialTheme.typography.titleMedium)
                            point.values.forEachIndexed { series, value -> Text("${result.series.getOrElse(series) { "Value" }}: ${formatVnd(value)}") }
                            if(question in listOf(AnalyticsQuestion.Categories, AnalyticsQuestion.Top)) {
                                Text(if(denominator > 0) "${"%.1f".format(java.util.Locale.ROOT, (point.values.first() * 100.0 / denominator))}% of filtered expenses" else "0% of filtered expenses")
                            }
                            if(selected == index) Text("Selected", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
            result.points.getOrNull(selected)?.let { point ->
                item { FinanceCard {
                    Text("Selected: ${point.label}", style = MaterialTheme.typography.titleLarge)
                    point.values.forEachIndexed { index, value -> Text("${result.series.getOrElse(index) { "Value" }}: ${formatVnd(value)}") }
                    if(point.entries.isEmpty()) Text("No related transactions.") else MetalButton(onClick = { drillDown = !drillDown }) {
                        Text(if(drillDown) "Hide related transactions" else "View related transactions")
                    }
                } }
                if(drillDown) items(point.entries.sortedByDescending { it.date }, key = { "analytics-entry-${it.id}" }) {
                    EntryRow(it, false, { onTransaction(it.id) })
                }
            }
        }
    }
}

@Composable private fun HeatmapLegend() {
    val scheme = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.small), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Text("Less", style = MaterialTheme.typography.labelSmall)
        listOf(scheme.secondaryContainer, scheme.tertiaryContainer, scheme.primaryContainer).forEach { color ->
            Surface(Modifier.size(20.dp), shape = MaterialTheme.shapes.extraSmall, color = color) {}
        }
        Text("More", style = MaterialTheme.typography.labelSmall)
    }
}

@Composable private fun CalendarHeatmapMonth(
    month: YearMonth, start: LocalDate, end: LocalDate, points: Map<LocalDate, ChartDatum>, maximum: Long,
    series: List<String>, selectedDate: String?, onSelect: (LocalDate) -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val offset = month.atDay(1).dayOfWeek.value - 1
    val weeks = (offset + month.lengthOfMonth() + 6) / 7
    FinanceCard {
        SectionHeading(month.toString())
        Box(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
            Column(Modifier.width(ChartTokens.calendarWidth), verticalArrangement = Arrangement.spacedBy(Space.small)) {
                Row(Modifier.fillMaxWidth()) {
                    listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su").forEach { label ->
                        Text(label, Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall, color = scheme.onSurfaceVariant)
                    }
                }
                repeat(weeks) { week ->
                    Row(Modifier.fillMaxWidth()) {
                        repeat(7) { weekday ->
                            val dayNumber = week * 7 + weekday - offset + 1
                            val date = dayNumber.takeIf { it in 1..month.lengthOfMonth() }?.let(month::atDay)
                            val inRange = date != null && date in start..end
                            val point = date?.let(points::get)
                            val total = point?.values?.sum() ?: 0L
                            val ratio = if (maximum > 0) total.toDouble() / maximum else 0.0
                            val background = when {
                                !inRange || total == 0L -> scheme.surfaceContainerLow
                                ratio < .34 -> scheme.secondaryContainer
                                ratio < .67 -> scheme.tertiaryContainer
                                else -> scheme.primaryContainer
                            }
                            val foreground = when {
                                !inRange || total == 0L -> scheme.onSurfaceVariant
                                ratio < .34 -> scheme.onSecondaryContainer
                                ratio < .67 -> scheme.onTertiaryContainer
                                else -> scheme.onPrimaryContainer
                            }
                            if (date == null) Spacer(Modifier.weight(1f).height(ChartTokens.calendarCell))
                            else Surface(
                                onClick = { if (inRange) onSelect(date) }, enabled = inRange,
                                modifier = Modifier.weight(1f).height(ChartTokens.calendarCell).padding(2.dp)
                                    .testTag("calendar-cell-$date")
                                    .then(if (selectedDate == date.toString()) Modifier.border(MetalTokens.rim, scheme.primary, MaterialTheme.shapes.small) else Modifier)
                                    .semantics {
                                        contentDescription = "$date · ${point?.values?.mapIndexed { index, value -> "${series.getOrElse(index) { "Total" }} ${formatVnd(value)}" }?.joinToString() ?: "No data"}"
                                        stateDescription = if (selectedDate == date.toString()) "Selected" else "Not selected"
                                        selected = selectedDate == date.toString()
                                    },
                                shape = MaterialTheme.shapes.small, color = background,
                            ) {
                                Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                                    Text(dayNumber.toString(), style = MaterialTheme.typography.labelMedium, color = foreground, maxLines = 1)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Visual layer only. Every point also has a focusable, labelled 48dp+ data row. */
@Composable internal fun AnalyticsPlot(data: AnalyticsResult, kind: ChartKind, selected: Int, showLegend: Boolean = true, plotHeight: androidx.compose.ui.unit.Dp = ChartTokens.height, onSelect: (Int) -> Unit) {
    val colors = listOf(LiquidTokens.violet, LiquidTokens.incomeLight, LiquidTokens.expenseLight, LiquidTokens.cobalt)
    val axis = MaterialTheme.colorScheme.outline.copy(alpha=.3f)
    val points = data.points
    val categories = LocalCategories.current
    val categoryColors = points.map { point -> when(categories.find { it.name == point.category }?.color) {
        CategoryColor.Cyan -> LiquidTokens.chartAqua
        CategoryColor.Rose -> LiquidTokens.chartRose
        else -> LiquidTokens.chartBlue
    } }
    if(kind == ChartKind.Bars || kind == ChartKind.Progress) {
        val maximum = points.maxOf { it.values.maxOrNull() ?: 0 }.coerceAtLeast(1)
        Column(verticalArrangement = Arrangement.spacedBy(Space.small)) {
            points.forEachIndexed { index, point ->
                MetalOutlineButton(onClick = { onSelect(index) }, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Space.small)) {
                        Text("${index + 1}. ${point.label}" + if(selected == index) " · Selected" else "")
                        val values = if(kind == ChartKind.Progress) point.values.take(1) else point.values
                        values.forEachIndexed { series, value ->
                            Text("${data.series[series]}: ${formatVnd(value)}")
                            val limit = if(kind == ChartKind.Progress) point.values.getOrElse(1) { 1L } else maximum
                            MetalProgress(progress = { (value.toDouble() / limit.coerceAtLeast(1)).coerceIn(0.0, 1.0).toFloat() },
                                accent = if(point.category != null) categoryColors[index] else colors[series % colors.size], modifier = Modifier.fillMaxWidth())
                            if(kind == ChartKind.Progress) Text("Limit: ${formatVnd(limit)} · ${budgetStatus(value, limit)}")
                        }
                    }
                }
            }
        }
        return
    }
    if(kind != ChartKind.Donut) Text("Scale (VND): ${formatVnd(minOf(0L, points.minOf { it.values.minOrNull() ?: 0L }))} to ${formatVnd(points.maxOf { if(kind == ChartKind.Stacked) it.values.sum() else it.values.maxOrNull() ?: 0L })}")
    Canvas(Modifier.fillMaxWidth().height(plotHeight).semantics {
        contentDescription = "${kind.name} chart. Exact values and accessible selection in numbered rows below."
    }.pointerInput(data, kind) {
        detectTapGestures { offset ->
            val index = if(kind == ChartKind.Donut) {
                val angle = ((atan2(offset.y - size.height / 2f, offset.x - size.width / 2f) * 180 / PI + 450) % 360).toFloat()
                val total = points.sumOf { it.values.firstOrNull() ?: 0L }.coerceAtLeast(1)
                var sum = 0.0
                points.indexOfFirst { sum += (it.values.firstOrNull() ?: 0L) * 360.0 / total; angle <= sum }.coerceAtLeast(0)
            } else if(kind == ChartKind.Bars || kind == ChartKind.Progress) (offset.y / size.height * points.size).toInt()
            else (offset.x / size.width * points.size).toInt()
            onSelect(index.coerceIn(points.indices))
        }
    }) {
        val width = size.width; val height = size.height
        val maximum = points.maxOf { p -> if(kind == ChartKind.Stacked) p.values.sum() else p.values.maxOrNull() ?: 0 }.coerceAtLeast(1).toDouble()
        val minimum = minOf(0L, points.minOf { it.values.minOrNull() ?: 0L }).toDouble()
        fun y(value: Long) = (height - ChartTokens.point.toPx() - (value - minimum) / (maximum - minimum) * (height - ChartTokens.point.toPx() * 2)).toFloat()
        val slot = width / points.size
        if(kind == ChartKind.Donut) {
            val total = points.sumOf { it.values.firstOrNull() ?: 0L }.coerceAtLeast(1)
            val diameter = min(width, height) - ChartTokens.ring.toPx()
            var angle = -90f
            points.forEachIndexed { i, point ->
                val sweep = ((point.values.firstOrNull() ?: 0L) * 360.0 / total).toFloat()
                drawArc(Brush.linearGradient(listOf(categoryColors[i], androidx.compose.ui.graphics.lerp(categoryColors[i], LiquidTokens.lavender, .35f)), start = Offset.Zero, end = Offset(width, height)), angle, sweep, false,
                    Offset((width-diameter)/2, (height-diameter)/2), Size(diameter, diameter),
                    style = Stroke(ChartTokens.ring.toPx() + if(i == selected) ChartTokens.stroke.toPx() else 0f))
                angle += sweep
            }
        } else if(kind == ChartKind.Bars || kind == ChartKind.Progress) {
            val row = height / points.size
            points.forEachIndexed { i, point ->
                val values = if(kind == ChartKind.Progress) point.values.take(1) else point.values
                values.forEachIndexed { series, value ->
                    val max = if(kind == ChartKind.Progress) point.values.getOrElse(1) { 1 }.coerceAtLeast(1).toDouble() else maximum
                    val barHeight = row * .65f / values.size.coerceAtLeast(1)
                    drawRect(if(point.category != null) categoryColors[i] else colors[series % colors.size], Offset(0f, row*i + series*barHeight), Size((width*(value/max).coerceIn(0.0,1.0)).toFloat(), barHeight))
                }
            }
        } else {
            drawLine(axis, Offset(0f,y(0)), Offset(width,y(0)), ChartTokens.stroke.toPx())
            points.forEachIndexed { i, point ->
                var accumulated = 0L
                point.values.forEachIndexed { series, value ->
                    val color = colors[series % colors.size]
                    val x = slot * (i + .5f)
                    if(kind == ChartKind.Line) {
                        val unit = ChartTokens.stroke.toPx()
                        val dash = when(series % 4) {
                            1 -> androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(unit*3,unit*2))
                            2 -> androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(unit,unit*2))
                            3 -> androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(unit*5,unit*2))
                            else -> null
                        }
                        if(i > 0) {
                            val before=Offset(x-slot,y(points[i-1].values.getOrElse(series) { 0 }))
                            val path=androidx.compose.ui.graphics.Path().apply {
                                moveTo(before.x,before.y)
                                cubicTo(x-slot/2,before.y,x-slot/2,y(value),x,y(value))
                            }
                            if(series==0) {
                                val fill=androidx.compose.ui.graphics.Path().apply {
                                    addPath(path); lineTo(x,y(0)); lineTo(before.x,y(0)); close()
                                }
                                drawPath(fill,Brush.verticalGradient(listOf(color.copy(alpha=.16f),color.copy(alpha=.01f))))
                            }
                            drawPath(path,Brush.horizontalGradient(listOf(androidx.compose.ui.graphics.lerp(color,LiquidTokens.cyan,.25f),color)),
                                style=Stroke(unit,cap=androidx.compose.ui.graphics.StrokeCap.Round,pathEffect=dash))
                        }
                        if(i==selected) drawCircle(color.copy(alpha=.15f),ChartTokens.point.toPx()*2.5f,Offset(x,y(value)))
                        drawCircle(color, ChartTokens.point.toPx() + if(i == selected) ChartTokens.stroke.toPx() else 0f, Offset(x,y(value)))
                    } else {
                        val stacked = kind == ChartKind.Stacked
                        val barWidth = slot * .7f / if(stacked) 1 else point.values.size.coerceAtLeast(1)
                        val bottom = if(stacked) accumulated else 0L
                        val top = bottom + value
                        val brush=Brush.verticalGradient(listOf(androidx.compose.ui.graphics.lerp(color,LiquidTokens.cyan,.35f),color))
                        val origin=Offset(slot*i + slot*.15f + if(stacked) 0f else series*barWidth, min(y(top),y(bottom)))
                        val barSize=Size(barWidth,abs(y(top)-y(bottom)))
                        if(stacked) drawRect(brush,origin,barSize)
                        else drawRoundRect(brush,origin,barSize,androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()))
                        accumulated = top
                    }
                }
            }
        }
    }
    if(kind == ChartKind.Donut && showLegend) points.forEachIndexed { index, point ->
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.small)) {
            point.category?.let { CategoryBadge(it) }
            Text("${index + 1}. ${point.label}: ${formatVnd(point.values.firstOrNull() ?: 0L)}", Modifier.weight(1f))
        }
    }
}
