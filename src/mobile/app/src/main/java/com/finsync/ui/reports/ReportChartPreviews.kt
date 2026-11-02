package com.finsync.ui.reports

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.finsync.data.DemoFixtures
import com.finsync.domain.*
import com.finsync.ui.FinanceState
import com.finsync.ui.components.LoadState
import com.finsync.ui.dashboard.formatVnd
import com.finsync.ui.theme.*
import java.time.LocalDate
import java.time.YearMonth

private object PreviewTokens {
    val chartHeight = 104.dp
    val ring = 14.dp
    val line = 3.dp
    val point = 4.dp
}

/** Preview and detail routes share the applied report period. */
@Composable internal fun ReportChartPreviews(state: FinanceState, mode: LoadState, onOpen: (String) -> Unit,
    start: LocalDate = DemoFixtures.today.withDayOfMonth(1), end: LocalDate = DemoFixtures.today) {
    val today = DemoFixtures.today
    val categories = remember(state.entries, state.categories, start, end) {
        analyticsResult(state.entries, state.categories, state.budgets, start, end,
            null, null, AnalyticsQuestion.Categories, today)
    }
    val trend = remember(state.entries, state.categories, start, end) {
        analyticsResult(state.entries, state.categories, state.budgets, start, end,
            null, null, AnalyticsQuestion.Trend, today)
    }
    val ready = mode == LoadState.Ready || mode == LoadState.Offline
    Column(verticalArrangement = Arrangement.spacedBy(Space.small)) {
        Text("${if (YearMonth.from(start) == YearMonth.from(end)) friendlyMonth(YearMonth.from(start)) else "Selected dates"} · Tap a chart to explore", style = MaterialTheme.typography.labelLarge)
        BoxWithConstraints {
            val stack = maxWidth < LiquidTokens.compact || LocalDensity.current.fontScale > LiquidTokens.largeFont
            val first: @Composable (Modifier) -> Unit = { modifier ->
                ChartPreviewCard("Spending by category", "category-chart-preview", categories, true, ready, modifier) { onOpen("charts") }
            }
            val second: @Composable (Modifier) -> Unit = { modifier ->
                ChartPreviewCard("Spending trend", "trend-chart-preview", trend, false, ready, modifier) { onOpen("charts/trend") }
            }
            if(stack) Column(verticalArrangement = Arrangement.spacedBy(Space.small)) { first(Modifier.fillMaxWidth()); second(Modifier.fillMaxWidth()) }
            else Row(horizontalArrangement = Arrangement.spacedBy(Space.small)) { first(Modifier.weight(1f)); second(Modifier.weight(1f)) }
        }
    }
}

@Composable private fun ChartPreviewCard(title: String, tag: String, data: AnalyticsResult, donut: Boolean,
    ready: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val total = data.points.sumOf { it.values.firstOrNull() ?: 0L }
    val palette = listOf(LiquidTokens.violet, LiquidTokens.expenseLight, LiquidTokens.incomeLight, LiquidTokens.cobalt)
    val emptyColor = MaterialTheme.colorScheme.outlineVariant
    Column(modifier.clip(LiquidTokens.panelShape).background(MaterialTheme.colorScheme.surfaceContainerLow)
        .border(MetalTokens.rim, MaterialTheme.colorScheme.outlineVariant, LiquidTokens.panelShape)
        .testTag(tag).clickable(role = Role.Button, onClickLabel = "Explore $title", onClick = onClick)
        .padding(Space.medium), verticalArrangement = Arrangement.spacedBy(Space.small)) {
        Text(title, style = MaterialTheme.typography.titleSmall)
        Canvas(Modifier.fillMaxWidth().height(PreviewTokens.chartHeight).clearAndSetSemantics {}) {
            if(!ready || total == 0L) {
                if(donut) drawCircle(emptyColor, size.minDimension / 2 - PreviewTokens.ring.toPx(), style = Stroke(PreviewTokens.ring.toPx()))
                else drawLine(emptyColor, Offset(0f, size.height / 2), Offset(size.width, size.height / 2), PreviewTokens.line.toPx())
            } else if(donut) {
                val diameter = size.minDimension - PreviewTokens.ring.toPx()
                var angle = -90f
                data.points.forEachIndexed { index, point ->
                    val sweep = (point.values.first() * 360.0 / total).toFloat()
                    drawArc(palette[index % palette.size], angle, sweep, false,
                        Offset((size.width - diameter) / 2, (size.height - diameter) / 2), Size(diameter, diameter), style = Stroke(PreviewTokens.ring.toPx()))
                    angle += sweep
                }
            } else {
                val maximum = data.points.maxOf { it.values.first() }.coerceAtLeast(1)
                val inset = PreviewTokens.point.toPx()
                fun point(index: Int) = Offset(inset + index * (size.width - inset*2) / (data.points.size - 1).coerceAtLeast(1),
                    size.height - inset - (data.points[index].values.first().toDouble() / maximum * (size.height-inset*2)).toFloat())
                data.points.indices.forEach { index ->
                    if(index > 0) drawLine(palette.first(), point(index-1), point(index), PreviewTokens.line.toPx())
                    drawCircle(palette.first(), inset, point(index))
                }
            }
        }
        Text(if(ready) formatVnd(total) else "Data unavailable", style = MaterialTheme.typography.titleMedium)
        Text(if(!ready) "Open to view status" else if(total == 0L) "No recorded expenses" else if(donut)
            "Largest: ${data.points.first().label}" else "Expenses in selected period", style = MaterialTheme.typography.bodySmall)
        Text("Explore & customize", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
    }
}
