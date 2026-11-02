package com.finsync.ui.dashboard

import com.finsync.ui.components.LiquidSurfaceTokens
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.finsync.R
import com.finsync.ui.theme.Space
import com.finsync.ui.theme.SoftFinanceTokens

/** Same current-month expense aggregation as Reports; no opening balances or transfers. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun OverviewTrend(points: List<Pair<String, Long>>, hidden: Boolean, onHistory: () -> Unit) {
    var selected by rememberSaveable(points) { mutableIntStateOf(points.lastIndex) }
    var menu by remember { mutableStateOf(false) }
    val color = MaterialTheme.colorScheme.primary
    val largeText = LocalDensity.current.fontScale > 1.2f
    val tooltip: @Composable () -> Unit = {
        points.getOrNull(selected)?.let {
            Surface(shape=MaterialTheme.shapes.large,color=LiquidSurfaceTokens.rim,shadowElevation=2.dp) {
                Text("${it.first} · ${formatVnd(it.second)}",Modifier.padding(horizontal=10.dp,vertical=4.dp), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
    Column(Modifier.fillMaxWidth().padding(horizontal = Space.small), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Spending", style=MaterialTheme.typography.labelLarge, color=MaterialTheme.colorScheme.onSurfaceVariant)
        if (points.isEmpty() || points.all { it.second == 0L }) Text("No expenses this month")
        else if (hidden) Text("Amounts hidden")
        else {
            Box(Modifier.fillMaxWidth().height(LiquidSurfaceTokens.plot)) {
            Canvas(Modifier.fillMaxSize().semantics {
                contentDescription = "Monthly spending trend. Select an interval below for exact values."
            }.pointerInput(points) {
                detectTapGestures { selected = (it.x / size.width * points.size).toInt().coerceIn(points.indices) }
            }) {
                val inset = Space.small.toPx()
                val maximum = points.maxOf { it.second }.coerceAtLeast(1)
                fun position(index: Int) = Offset(inset + index * (size.width - 2 * inset) / (points.size - 1).coerceAtLeast(1),
                    size.height - inset - (points[index].second.toDouble() / maximum * (size.height - inset * 2)).toFloat())
                val path = Path().apply {
                    points.indices.forEach { i ->
                        val p=position(i)
                        if(i==0) moveTo(p.x,p.y) else {
                            val before=position(i-1);val mid=(before.x+p.x)/2
                            cubicTo(mid,before.y,mid,p.y,p.x,p.y)
                        }
                    }
                }
                val fill = Path().apply {
                    addPath(path); lineTo(position(points.lastIndex).x, size.height-inset)
                    lineTo(position(0).x, size.height-inset); close()
                }
                drawPath(fill, Brush.verticalGradient(listOf(LiquidSurfaceTokens.violet.copy(alpha = .2f), LiquidSurfaceTokens.blue.copy(alpha = .01f))))
                val line = Brush.horizontalGradient(listOf(LiquidSurfaceTokens.blue,LiquidSurfaceTokens.violet))
                drawPath(path, line, alpha=.08f, style=Stroke(6.dp.toPx(),cap=StrokeCap.Round))
                drawPath(path, line, style = Stroke(2.dp.toPx(),cap=StrokeCap.Round))
                if(selected in points.indices) {
                    val p=position(selected)
                    drawLine(color.copy(alpha=.15f),p,Offset(p.x,size.height),strokeWidth=1.dp.toPx())
                    drawCircle(Brush.radialGradient(listOf(LiquidSurfaceTokens.violet.copy(alpha=.28f),Color.Transparent),p,12.dp.toPx()),12.dp.toPx(),p)
                    drawCircle(Color.White,6.dp.toPx(),p)
                    drawCircle(LiquidSurfaceTokens.violet,4.dp.toPx(),p)
                }
            }
            if(!largeText) Box(Modifier.align(Alignment.TopCenter).padding(top=2.dp)) { tooltip() }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                points.indices.toList().forEach { index ->
                    val label = runCatching { java.time.YearMonth.parse(points[index].first).atDay(1)
                        .format(java.time.format.DateTimeFormatter.ofPattern("MMM", java.util.Locale.ENGLISH)) }.getOrDefault(points[index].first)
                    Text(label, Modifier.background(if(selected==index) com.finsync.ui.theme.LiquidTokens.lavender.copy(alpha=.35f) else Color.Transparent,CircleShape)
                        .padding(horizontal=5.dp,vertical=2.dp), style = MaterialTheme.typography.labelSmall,
                        color=if(selected==index) color else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if(largeText) tooltip()
        }
        FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp), maxItemsInEachRow = if(largeText) 1 else 2) {
            if(!hidden && points.any { it.second > 0 }) {
            Box(Modifier.weight(1f)) {
                OutlinedButton(onClick = { menu = true }, modifier=Modifier.fillMaxWidth().heightIn(min=48.dp),
                    shape=RoundedCornerShape(14.dp), border=BorderStroke(1.dp,color.copy(alpha=.35f)),
                    colors=ButtonDefaults.outlinedButtonColors(containerColor=LiquidSurfaceTokens.rim.copy(alpha=.85f)),
                    contentPadding=PaddingValues(horizontal=10.dp,vertical=8.dp)) {
                    Icon(painterResource(R.drawable.ic_filter),null,Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Select interval", style=MaterialTheme.typography.labelLarge)
                }
                DropdownMenu(menu, { menu = false }) {
                    points.forEachIndexed { index, point -> DropdownMenuItem(text = { Text(point.first) }, onClick = { selected = index; menu = false }) }
                }
            }
            }
            FilledTonalButton(onClick = onHistory,modifier=Modifier.weight(1f).heightIn(min=48.dp),
                shape=RoundedCornerShape(14.dp),contentPadding=PaddingValues(horizontal=10.dp,vertical=8.dp),
                colors=ButtonDefaults.filledTonalButtonColors(containerColor=com.finsync.ui.theme.LiquidTokens.lavender.copy(alpha=.6f),contentColor=color)) {
                Icon(painterResource(R.drawable.ic_reports),null,Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("View activity",style=MaterialTheme.typography.labelLarge)
            }
        }
    }
}
