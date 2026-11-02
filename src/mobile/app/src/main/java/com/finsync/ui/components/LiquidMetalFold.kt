package com.finsync.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.finsync.ui.theme.*

/** Size-cached folded foil at the right edge; the reading plane stays unobstructed. */
internal fun Modifier.liquidMetalFold(): Modifier = drawWithCache {
    val w = size.width
    val h = size.height
    val fold = Path().apply {
        moveTo(w * .60f, -h * .10f)
        cubicTo(w * .69f, h * .24f, w * 1.10f, -h * .02f, w * .97f, h * .55f)
        cubicTo(w * .91f, h * .86f, w * .84f, h * .91f, w * .76f, h * 1.12f)
    }
    val mirror = Brush.linearGradient(
        0f to LiquidTokens.pearl, .18f to LiquidTokens.silver,
        .32f to LiquidTokens.chromeShadow, .43f to LiquidTokens.pearl,
        .53f to LiquidTokens.silver, .61f to LiquidTokens.chromeShadow,
        .73f to LiquidTokens.ice, .85f to LiquidTokens.lavender, 1f to LiquidTokens.pearl,
        start = Offset(w * .66f, 0f), end = Offset(w * 1.07f, h * .72f))
    onDrawBehind {
        drawPath(fold, mirror, style = Stroke(w * .11f))
        drawPath(fold, LiquidTokens.pearl, style = Stroke(DockTokens.edge.toPx()))
    }
}
