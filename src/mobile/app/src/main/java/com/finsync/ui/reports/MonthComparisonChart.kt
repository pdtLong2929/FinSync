package com.finsync.ui.reports

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.finsync.domain.*
import com.finsync.ui.components.*
import com.finsync.ui.dashboard.formatVnd
import com.finsync.ui.theme.*
import java.time.format.DateTimeFormatter
import java.util.Locale

internal object ReportMetalTokens {
    val plotHeight = 116.dp
    val barWidth = 64.dp
    val axisWidth = 42.dp
    val largeAxisWidth = 64.dp
}

@Composable internal fun MonthComparisonChart(data: MonthComparison, selected: Int?, onSelect: (Int) -> Unit) {
    val periods = listOf(data.previous, data.current)
    val recordedMaximum = periods.maxOf { it.amount }
    val maximum = recordedMaximum.coerceAtLeast(1)
    val axis = MaterialTheme.colorScheme.outlineVariant.copy(alpha = .35f)
    val largeText = LocalDensity.current.fontScale > LiquidTokens.largeFont || recordedMaximum >= 10_000_000
    Column(Modifier.semantics { contentDescription = "Monthly comparison" }, verticalArrangement = Arrangement.spacedBy(Space.small)) {

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.small)) {
            Column(Modifier.width(if(largeText) ReportMetalTokens.largeAxisWidth else ReportMetalTokens.axisWidth).height(ReportMetalTokens.plotHeight), verticalArrangement = Arrangement.SpaceBetween) {
                Text(if(recordedMaximum > 0) compactVnd(maximum).removeSuffix(" ₫") else "", style = MaterialTheme.typography.labelSmall)
                Text(if(recordedMaximum > 1) compactVnd(maximum / 2).removeSuffix(" ₫") else "", style = MaterialTheme.typography.labelSmall)
                Text("0 ₫", style = MaterialTheme.typography.labelSmall)
            }
            periods.forEachIndexed { index, period ->
                Column(Modifier.weight(1f).selectable(selected == index, role = Role.Button, onClick = { onSelect(index) })
                    .testTag("compare-month-$index").semantics {
                        contentDescription = "${friendlyMonth(period.month)}: ${formatVnd(period.amount)}; ${period.entries.size} expenses"
                    }, horizontalAlignment = Alignment.CenterHorizontally) {
                    Canvas(Modifier.fillMaxWidth().height(ReportMetalTokens.plotHeight).clearAndSetSemantics {}) {
                        listOf(0f, .5f, 1f).forEach { fraction ->
                            val y = (size.height - DockTokens.edge.toPx()) * fraction
                            drawLine(axis, Offset(0f, y), Offset(size.width, y), DockTokens.edge.toPx())
                        }
                        val height = (period.amount.toDouble() / maximum * (size.height - DockTokens.edge.toPx())).toFloat()
                        if (period.amount > 0) {
                            val width = minOf(ReportMetalTokens.barWidth.toPx(), size.width * .65f)
                            val colors = if(index == 0) listOf(LiquidTokens.silver, LiquidTokens.pearlShade)
                                else listOf(LiquidTokens.cyan, LiquidTokens.violet, LiquidTokens.heroViolet)
                            drawRoundRect(Brush.verticalGradient(colors), Offset((size.width - width) / 2, size.height - height), Size(width, height), CornerRadius(Space.small.toPx()))
                            drawLine(LiquidTokens.pearl.copy(alpha = .8f), Offset((size.width-width)/2 + width*.15f, size.height-height),
                                Offset((size.width-width)/2 + width*.15f, size.height), DockTokens.edge.toPx())
                        } else drawCircle(LiquidTokens.silver, Space.small.toPx() / 2, Offset(size.width / 2, size.height - Space.small.toPx() / 2))
                    }
                    Text(period.month.format(DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH)), style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(top = Space.small))
                    Text(if (largeText) compactVnd(period.amount) else formatVnd(period.amount), style = MaterialTheme.typography.labelLarge)

                }
            }
        }
        Surface(shape = LiquidTokens.rowShape, color = LiquidTokens.lilacTile) {
            Column(Modifier.fillMaxWidth().padding(Space.small)) {
                if(data.previous.entries.isEmpty()) Text("${data.previous.month.format(DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH))}: no spending", style = MaterialTheme.typography.labelSmall)
                else if(data.percent != null) Text("${if(data.delta > 0) "↑" else if(data.delta < 0) "↓" else "="} ${String.format(Locale.ENGLISH, "%.1f", kotlin.math.abs(data.percent!!))}% · ${formatVnd(kotlin.math.abs(data.delta))} ${if(data.delta > 0) "more" else "less"}", style = MaterialTheme.typography.labelSmall)
                else Text("Previous recorded total: 0 ₫", style = MaterialTheme.typography.labelSmall)
                if(data.current.end < data.current.month.atEndOfMonth()) Text("To date / previous full month", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
internal fun compactVnd(value: Long): String = when {
    value >= 1_000_000_000 -> String.format(Locale.ENGLISH, "%.2fB ₫", value / 1_000_000_000.0)
    value >= 1_000_000 -> String.format(Locale.ENGLISH, "%.2f", value / 1_000_000.0).trimEnd('0').trimEnd('.') + "M ₫"
    value >= 1_000 -> String.format(Locale.ENGLISH, "%.0fk ₫", value / 1_000.0)
    else -> "$value ₫"
}
