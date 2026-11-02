package com.finsync.ui.dashboard

import com.finsync.ui.components.LiquidSurfaceTokens
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.finsync.ui.theme.LiquidTokens
import com.finsync.ui.theme.SoftFinanceTokens

@Composable
internal fun OverviewBudgetRing(value: Float, label: String) {
    val progress = value.coerceIn(0f, 1f)
    Box(Modifier.size(SoftFinanceTokens.conceptBudgetRing).drawWithCache {
        val width = 8.dp.toPx()
        val inset = width / 2
        val arcSize = Size(size.width-width, size.height-width)
        val brush = Brush.sweepGradient(listOf(LiquidSurfaceTokens.blue, LiquidSurfaceTokens.violet,
            LiquidTokens.lavender, LiquidSurfaceTokens.violet, LiquidSurfaceTokens.blue))
        val core = Brush.radialGradient(listOf(LiquidSurfaceTokens.rim, LiquidSurfaceTokens.frost))
        onDrawBehind {
            drawCircle(core, radius=size.minDimension*.39f)
            drawArc(LiquidTokens.lavender.copy(alpha=.42f),0f,360f,false,Offset(inset,inset),arcSize,style=Stroke(width))
            if(progress>0f) {
                drawArc(brush,-90f,360f*progress,false,Offset(inset,inset),arcSize,
                    alpha=.12f,style=Stroke(width+4.dp.toPx(),cap=StrokeCap.Round))
                drawArc(brush,-90f,360f*progress,false,Offset(inset,inset),arcSize,
                    style=Stroke(width,cap=StrokeCap.Round))
                drawArc(Color.White.copy(alpha=.25f),-87f,(360f*progress-6f).coerceAtLeast(0f),false,
                    Offset(inset,inset),arcSize,style=Stroke(1.dp.toPx(),cap=StrokeCap.Round))
            }
        }
    }.semantics { progressBarRangeInfo=ProgressBarRangeInfo(progress,0f..1f) },contentAlignment=Alignment.Center) {
        Text(label,style=MaterialTheme.typography.titleLarge,color=LiquidTokens.ink)
    }
}
