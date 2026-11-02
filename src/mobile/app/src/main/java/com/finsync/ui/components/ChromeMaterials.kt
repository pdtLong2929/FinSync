package com.finsync.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.finsync.R
import com.finsync.ui.theme.Space
import com.finsync.ui.theme.LiquidTokens

internal data class ChromePalette(
    val dark: Boolean, val background: Color, val panel: Color, val text: Color,
    val muted: Color, val line: Color, val accent: Color, val income: Color, val expense: Color,
)

@Composable
internal fun chromePalette(): ChromePalette {
    // The requested pearl art direction stays luminous in either Android appearance.
    // Shared palette stays luminous under either Android appearance setting.
    return ChromePalette(
        false, LiquidTokens.day, LiquidTokens.dayPanel, LiquidTokens.ink,
        LiquidTokens.mutedLight, LiquidTokens.lineLight, LiquidTokens.violet,
        LiquidTokens.incomeLight, LiquidTokens.expenseLight,
    )
}

/** Light is reflected through broad, curved bands rather than a flat silver fill.
 * All text-bearing bands remain light; dark specular details stay in the chrome knot. */
internal fun Modifier.pearlChrome(): Modifier = drawWithCache {
    val w = size.width
    val h = size.height
    val base = Brush.linearGradient(
        0f to LiquidTokens.pearl, .22f to LiquidTokens.pearlSilver,
        .36f to LiquidTokens.white, .53f to LiquidTokens.mistLilac,
        .69f to LiquidTokens.pearl, .87f to LiquidTokens.pearlSilver, 1f to LiquidTokens.mistBlue,
        start = Offset.Zero, end = Offset(w, h),
    )
    val flowing = Path().apply {
        moveTo(-w * .12f, h * .12f)
        cubicTo(w * .18f, -h * .13f, w * .27f, h * .42f, w * .66f, h * .25f)
        cubicTo(w * .96f, h * .08f, w * 1.05f, h * .38f, w * .89f, h * .63f)
        cubicTo(w * .73f, h * .95f, w * .90f, h * 1.04f, w * 1.13f, h * 1.08f)
    }
    val mirror = Brush.linearGradient(
        0f to LiquidTokens.white, .20f to LiquidTokens.pearlSilver,
        .30f to LiquidTokens.white, .39f to LiquidTokens.pearlShade,
        .49f to LiquidTokens.mistBlue, .58f to LiquidTokens.white,
        .72f to LiquidTokens.pearlSilver, .86f to LiquidTokens.pearlRose, 1f to LiquidTokens.white,
        start = Offset(w * .1f, 0f), end = Offset(w, h),
    )
    onDrawBehind {
        drawRect(base)
        drawPath(flowing, mirror, style = Stroke(w * .27f, cap = StrokeCap.Round))
        drawPath(flowing, LiquidTokens.white.copy(alpha = .8f), style = Stroke(w * .008f))
    }
}

internal fun Modifier.pearlMist(): Modifier = drawWithCache {
    val mist = Brush.linearGradient(listOf(LiquidTokens.day, LiquidTokens.mistLilac, LiquidTokens.day), end = Offset(size.width, size.height))
    onDrawBehind { drawRect(mist) }
}

/** Quieter foil for small tiles and budget panels; dense text stays on bright material. */
internal fun Modifier.pastelMetal(tint: Color): Modifier = drawWithCache {
    val foil = Brush.linearGradient(listOf(tint, androidx.compose.ui.graphics.lerp(tint, LiquidTokens.pearl, .45f)),
        end = Offset(size.width, size.height))
    onDrawBehind { drawRect(foil) }
}

/** Static sculptural chrome knot, kept in a reserved decorative area. */
internal fun Modifier.chromeKnot(): Modifier = drawWithCache {
    val w = size.width
    val h = size.height
    val path = Path().apply {
        moveTo(w * .15f, h * .38f)
        cubicTo(w * .12f, -h * .08f, w * .99f, h * .04f, w * .76f, h * .51f)
        cubicTo(w * .49f, h * 1.05f, -w * .02f, h * .90f, w * .30f, h * .47f)
        cubicTo(w * .69f, -h * .03f, w * 1.04f, h * .65f, w * .59f, h * .83f)
    }
    val metal = Brush.linearGradient(
        0f to LiquidTokens.chromeShadow, .17f to LiquidTokens.ice,
        .27f to LiquidTokens.silver, .38f to LiquidTokens.white,
        .47f to LiquidTokens.chromeShadow, .57f to LiquidTokens.lavender,
        .66f to LiquidTokens.white, .78f to LiquidTokens.cobalt,
        .88f to LiquidTokens.silver, 1f to LiquidTokens.white,
        start = Offset.Zero, end = Offset(w, h),
    )
    onDrawBehind {
        drawPath(path, metal, style = Stroke(w * .23f, cap = StrokeCap.Round))
        drawPath(path, LiquidTokens.white.copy(alpha = .6f), style = Stroke(w * .012f))
    }
}

@Composable
internal fun BudgetOrbit(value: Float, label: String, lightSurface: Boolean = false, diameter: androidx.compose.ui.unit.Dp = LiquidTokens.ringSize) {
    val colors = chromePalette()
    Box(Modifier.size(diameter).drawWithCache {
        val stroke = Space.small.toPx()
        val inset = stroke / 2
        val diameter = size.minDimension - stroke
        val rim = Brush.sweepGradient(if (colors.dark && !lightSurface)
            listOf(LiquidTokens.lavender, LiquidTokens.cyan, LiquidTokens.lavender)
            else listOf(LiquidTokens.violet, LiquidTokens.cobalt, LiquidTokens.violet))
        onDrawBehind {
            drawCircle(if (lightSurface) LiquidTokens.ink.copy(alpha = .12f) else colors.line, style = Stroke(stroke))
            if (value > 0f) drawArc(rim, -90f, 360f * value, false,
                topLeft = Offset(inset, inset), size = androidx.compose.ui.geometry.Size(diameter, diameter),
                style = Stroke(stroke, cap = StrokeCap.Round))
        }
    }.semantics { progressBarRangeInfo = ProgressBarRangeInfo(value, 0f..1f) },
        contentAlignment = androidx.compose.ui.Alignment.Center) {
        Text(label, style = MaterialTheme.typography.titleLarge, color = if (lightSurface) LiquidTokens.ink else colors.text)
    }
}

@Composable
internal fun ChromePanel(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val colors = chromePalette()
    Surface(modifier.fillMaxWidth().liquidGlass(LiquidTokens.panelShape), shape = LiquidTokens.panelShape,
        color = Color.Transparent, contentColor = colors.text) {
        Column(Modifier.padding(Space.large), verticalArrangement = Arrangement.spacedBy(Space.medium), content = content)
    }
}

@Composable
internal fun ChromeBadge(label: String, tint: Color, modifier: Modifier = Modifier) {
    Box(modifier.size(LiquidTokens.iconSize).clip(CircleShape).background(tint.copy(alpha = 0.12f)),
        contentAlignment = androidx.compose.ui.Alignment.Center) {
        Text(label, style = MaterialTheme.typography.titleMedium, color = tint)
    }
}

@Composable
internal fun ChromeCategoryBadge(category: String?, income: Boolean) {
    CategoryBadge(category.orEmpty())
}
