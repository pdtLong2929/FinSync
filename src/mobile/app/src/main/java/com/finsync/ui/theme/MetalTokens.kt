package com.finsync.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Static material optics. No animated noise, bitmap textures or frame-time random work. */
object MetalTokens {
    val graphite = Color(0xFF191C22)
    val titanium = Color(0xFF343A45)
    val reflection = Color(0xFF46515F)
    val platinum = Color(0xFFE9EDF2)
    val silver = Color(0xFFB6BEC9)
    val ink = Color(0xFF15191F)
    val white = Color(0xFFFFFFFF)
    val clear = Color.Transparent
    val radius = 24.dp
    val fieldRadius = 16.dp
    val rim = 1.dp
    val grainStep = 3.dp
    val grainWidth = 0.35.dp
    const val grainAlpha = 0.035f
    const val sheenAlpha = 0.12f
    val badge = 44.dp
    val actionHeight = 52.dp
    val chartHeight = 12.dp
    val compactMetricsBreakpoint = 360.dp
    val compactTransactionBreakpoint = 300.dp
    const val metricsFontScaleLimit = 1.2f
    val heroSize = 36.sp
    val heroLine = 44.sp
    val tracking = 2.sp
}
