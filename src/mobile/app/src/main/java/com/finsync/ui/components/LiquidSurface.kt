package com.finsync.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finsync.R
import com.finsync.ui.theme.LiquidTokens

/** Approved shared materials. Static cached lighting; no blur or animated shader. */
internal object LiquidSurfaceTokens {
    val radius = RoundedCornerShape(26.dp)
    val heroHeight = 158.dp
    val metricHeight = 88.dp
    val amount = 38.sp
    val metricAmount = 21.sp
    val plot = 100.dp
    val frost = Color(0xFFF4F1FD)
    val shade = Color(0xFFA497D3)
    val violet = Color(0xFF8953F4)
    val blue = Color(0xFF657DF4)
    val rim = Color(0xFFFEFDFF)
}

internal fun Modifier.liquidAtmosphere(): Modifier = drawWithCache {
    val w = size.width; val h = size.height
    val lights = listOf(
        Brush.radialGradient(listOf(LiquidSurfaceTokens.rim.copy(alpha = .92f), Color.Transparent), Offset(w*.12f,h*.04f), w*.86f),
        Brush.radialGradient(listOf(LiquidTokens.lavender.copy(alpha = .28f), Color.Transparent), Offset(w*.96f,h*.25f), w*.8f),
        Brush.radialGradient(listOf(LiquidTokens.ice.copy(alpha = .83f), Color.Transparent), Offset(w*.05f,h*.57f), w*.72f),
        Brush.radialGradient(listOf(LiquidTokens.lavender.copy(alpha = .18f), Color.Transparent), Offset(w*.7f,h*.9f), w*.65f),
    )
    onDrawBehind { drawRect(LiquidTokens.day); lights.forEach { drawRect(it) } }
}

/** A translucent reading plane with directional reflection and an illuminated rim. */
internal fun Modifier.liquidGlass(shape: Shape = LiquidSurfaceTokens.radius, elevation: androidx.compose.ui.unit.Dp = 8.dp, tint: Color = LiquidSurfaceTokens.frost): Modifier =
    shadow(elevation, shape, clip = false, ambientColor = LiquidSurfaceTokens.shade.copy(alpha = .6f),
        spotColor = LiquidSurfaceTokens.shade.copy(alpha = .65f))
        .clip(shape).drawWithCache {
            // Opaque base avoids Android elevation occlusion rectangles beneath text render nodes.
            val plane = Brush.linearGradient(listOf(LiquidSurfaceTokens.rim,
                tint, if(tint == LiquidSurfaceTokens.frost) Color(0xFFE6E0F8) else lerp(tint, LiquidTokens.lavender, .18f)),
                end = Offset(size.width*.75f, size.height))
            val light = Brush.radialGradient(listOf(LiquidSurfaceTokens.rim.copy(alpha = .85f), Color.Transparent),
                center = Offset(size.width*.12f, -size.height*.15f), radius = size.width*.65f)
            onDrawBehind { drawRect(plane); drawRect(light) }
        }.border(1.dp, Brush.linearGradient(listOf(LiquidSurfaceTokens.rim, LiquidSurfaceTokens.rim.copy(alpha = .35f),
            LiquidSurfaceTokens.rim.copy(alpha = .85f))), shape)

/** Circular chrome/lavender lens. The specular band stays above the central glyph. */
internal fun Modifier.liquidLens(shape: Shape = CircleShape): Modifier = shadow(8.dp, shape, clip = false,
    ambientColor = LiquidSurfaceTokens.violet.copy(alpha = .22f), spotColor = LiquidSurfaceTokens.shade.copy(alpha = .5f))
    .clip(shape).drawWithCache {
        val w=size.width; val h=size.height
        val body=Brush.linearGradient(0f to LiquidSurfaceTokens.rim, .19f to LiquidTokens.silver,
            .35f to Color(0xFFB4A7F0), .62f to Color(0xFF957ADD), .82f to LiquidTokens.lavender,
            1f to LiquidSurfaceTokens.rim, end=Offset(w*.7f,h))
        val gleam=Brush.radialGradient(listOf(LiquidSurfaceTokens.rim.copy(alpha=.95f), Color.Transparent), Offset(w*.26f,h*.15f),w*.65f)
        val lip=Path().apply { moveTo(0f,h*.24f); cubicTo(w*.3f,h*.01f,w*.7f,h*.35f,w,h*.16f) }
        onDrawBehind {
            drawRect(body); drawRect(gleam)
            drawPath(lip, LiquidSurfaceTokens.rim.copy(alpha=.6f), style=Stroke(.7.dp.toPx()))
        }
    }.border(1.2.dp, Brush.linearGradient(listOf(LiquidSurfaceTokens.rim, LiquidTokens.lavender, LiquidSurfaceTokens.rim)), shape)

internal fun Modifier.liquidSelected(action: Boolean = false): Modifier = drawWithCache {
    val body=if(action) Brush.verticalGradient(0f to Color(0xFF9E88D3),.25f to Color(0xFF7252B8),
        .7f to Color(0xFF6742AD),1f to Color(0xFF8356CE)) else Brush.linearGradient(0f to Color(0xFFB5A2EC), .35f to Color(0xFF7960CE),
        .72f to Color(0xFF7852D7), 1f to Color(0xFFB38AFF),end=Offset(size.width*.4f,size.height))
    val gloss=Brush.radialGradient(listOf(LiquidSurfaceTokens.rim.copy(alpha=if(action) .22f else .6f),Color.Transparent),
        Offset(size.width*.25f,0f),if(action) size.height*.9f else size.width*.7f)
    onDrawBehind { drawRect(body); drawRect(gloss) }
}

@Composable internal fun LiquidCard(modifier: Modifier=Modifier, foil: Boolean=false, content: @Composable ColumnScope.()->Unit) {
    Box(modifier.liquidGlass()) {
        if(foil) {
            Box(Modifier.matchParentSize().drawWithCache {
                val reflectedLight=Brush.radialGradient(listOf(LiquidTokens.lavender.copy(alpha=.35f),Color.Transparent),
                    Offset(size.width*.83f,size.height*.78f),size.width*.6f)
                onDrawBehind { drawRect(reflectedLight) }
            })
            Image(painterResource(R.drawable.overview_liquid_silver),null,Modifier.matchParentSize(),contentScale=ContentScale.FillBounds)
        }
        Column(Modifier.fillMaxWidth().padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp),content=content)
    }
}

