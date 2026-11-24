package com.finsync.ui.components

import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.finsync.R
import com.finsync.ui.theme.LiquidTokens
import kotlin.math.max
import kotlin.math.roundToInt

/** Main tabs share the full-window scene drawn by their parent scaffold. */
internal val LocalAtmosphereProvided = compositionLocalOf { false }

/** Single 720x1280 local decorative image; cached resource, no network or runtime blur. */
@Composable
internal fun Modifier.liquidAtmosphere(): Modifier {
    if (LocalAtmosphereProvided.current) return this
    val image = ImageBitmap.imageResource(R.drawable.finsync_spatial_atmosphere)
    return drawWithCache {
        // Omit the quiet tail so floor reflections remain visible above the dock.
        val scene = IntSize(image.width, (image.height * .86f).roundToInt())
        val scale = max(size.width / scene.width, size.height / scene.height)
        val drawn = IntSize((scene.width * scale).roundToInt(), (scene.height * scale).roundToInt())
        val offset = IntOffset(((size.width - drawn.width) / 2).roundToInt(), ((size.height - drawn.height) / 2).roundToInt())
        val veil = Brush.verticalGradient(
            0f to LiquidTokens.pearl.copy(alpha = .12f),
            .45f to LiquidTokens.day.copy(alpha = .12f),
            1f to LiquidTokens.day.copy(alpha = .08f))
        onDrawBehind {
            clipRect {
                drawImage(image, srcSize = scene, dstOffset = offset, dstSize = drawn, filterQuality = FilterQuality.Low)
                drawRect(veil)
            }
        }
    }
}

/** Translucent glass over the soft local environment. No elevated transparent render node. */
internal fun Modifier.spatialGlass(shape: Shape = LiquidSurfaceTokens.radius, prominent: Boolean = false): Modifier = clip(shape)
    .drawWithCache {
        val body = Brush.linearGradient(
            0f to LiquidSurfaceTokens.rim.copy(alpha = if(prominent) .78f else .94f),
            .42f to LiquidTokens.pearl.copy(alpha = if(prominent) .68f else .84f),
            1f to LiquidTokens.mistLilac.copy(alpha = if(prominent) .64f else .80f), end = Offset(size.width, size.height))
        val edgeLight = Brush.radialGradient(listOf(LiquidSurfaceTokens.rim.copy(alpha = .55f), Color.Transparent),
            Offset(size.width*.1f, 0f), size.width*.65f)
        onDrawBehind { drawRect(body); drawRect(edgeLight) }
    }.border(LiquidSurfaceTokens.bevel,
        Brush.linearGradient(listOf(LiquidSurfaceTokens.rim, LiquidTokens.silver.copy(alpha = .5f), LiquidSurfaceTokens.rim.copy(alpha = .85f))), shape)
