package com.finsync.ui.components

import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.finsync.ui.theme.DockTokens
import com.finsync.ui.theme.LiquidTokens

/** Static material lighting; no blur, bitmap texture or continuously animated shader. */
private object PremiumDockTokens {
    val elevation = 10.dp
    val edge = 1.dp
    val inset = 4.dp
    val coreRadius = 12.dp
}

internal fun Modifier.premiumDockSurface(): Modifier =
    shadow(PremiumDockTokens.elevation, ChromeNavigationTokens.dockShape,
        ambientColor = DockTokens.shadow.copy(alpha = .12f), spotColor = DockTokens.shadow.copy(alpha = .20f))
        .clip(ChromeNavigationTokens.dockShape).drawWithCache {
            val plane = Brush.linearGradient(
                0f to DockTokens.reflection.copy(alpha = .97f),
                .38f to DockTokens.pearl.copy(alpha = .91f),
                1f to DockTokens.silver.copy(alpha = .86f), end = Offset(size.width, size.height))
            val gleam = Brush.radialGradient(listOf(DockTokens.reflection.copy(alpha = .60f), Color.Transparent),
                Offset(size.width * .20f, 0f), size.width * .65f)
            onDrawBehind { drawRect(plane); drawRect(gleam) }
        }.border(PremiumDockTokens.edge, Brush.linearGradient(listOf(
            DockTokens.reflection, DockTokens.silver, DockTokens.reflection)), ChromeNavigationTokens.dockShape)

internal fun Modifier.premiumDockIndicator(): Modifier =
    clip(ChromeNavigationTokens.indicatorShape).drawWithCache {
        val body = Brush.linearGradient(listOf(DockTokens.violet, DockTokens.cobalt), end = Offset(size.width, size.height))
        val reflection = Path().apply {
            moveTo(0f, size.height * .18f)
            cubicTo(size.width * .32f, -size.height * .04f, size.width * .66f,
                size.height * .30f, size.width, size.height * .12f)
        }
        onDrawBehind {
            drawRect(body)
            drawPath(reflection, DockTokens.reflection.copy(alpha = .24f), style = Stroke(PremiumDockTokens.edge.toPx()))
        }
    }.border(PremiumDockTokens.edge, Brush.linearGradient(listOf(
        DockTokens.lilac, DockTokens.cobalt)), ChromeNavigationTokens.indicatorShape)

/** Silver bevel frames a deep violet core; the central plus sits on quiet contrast. */
internal fun Modifier.premiumDockAction(): Modifier =
    shadow(DockTokens.elevation, ChromeNavigationTokens.tileShape,
        ambientColor = DockTokens.shadow.copy(alpha = .14f), spotColor = DockTokens.shadow.copy(alpha = .28f))
        .clip(ChromeNavigationTokens.tileShape).drawWithCache {
            val bevel = Brush.linearGradient(
                0f to DockTokens.reflection, .23f to DockTokens.silver,
                .48f to DockTokens.reflection, .72f to DockTokens.lilac,
                1f to DockTokens.reflection, end = Offset(size.width, size.height))
            val core = Brush.linearGradient(listOf(DockTokens.violet, LiquidTokens.heroDeep),
                end = Offset(size.width * .7f, size.height))
            val inset = PremiumDockTokens.inset.toPx()
            val shine = Brush.radialGradient(listOf(DockTokens.reflection.copy(alpha = .32f), Color.Transparent),
                Offset(size.width * .22f, size.height * .12f), size.width * .7f)
            val rim = Path().apply {
                moveTo(inset * 2, size.height * .18f)
                cubicTo(size.width * .35f, size.height * .04f, size.width * .67f,
                    size.height * .30f, size.width - inset * 2, size.height * .14f)
            }
            onDrawBehind {
                drawRect(bevel)
                drawRoundRect(core, Offset(inset, inset),
                    Size(size.width - inset * 2, size.height - inset * 2),
                    CornerRadius(PremiumDockTokens.coreRadius.toPx()))
                drawRoundRect(shine, Offset(inset, inset),
                    Size(size.width - inset * 2, size.height - inset * 2),
                    CornerRadius(PremiumDockTokens.coreRadius.toPx()))
                drawPath(rim, DockTokens.reflection.copy(alpha = .40f), style = Stroke(PremiumDockTokens.edge.toPx()))
            }
        }.border(PremiumDockTokens.edge, DockTokens.reflection, ChromeNavigationTokens.tileShape)
