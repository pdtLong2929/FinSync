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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
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
    val porcelain = Color(0xFFFDFBFF)
    val porcelainEdge = Color(0xFFE4DFF1)
    val readingElevation = 3.dp
    val heroElevation = 12.dp
    val insetShape = RoundedCornerShape(18.dp)
    val inset = Color(0xFFE5E1F2)
    val bevel = 1.dp
    val contour = Color(0xFFB7ABD9)
    val backingOffset = 5.dp
    const val backingScale = .965f
    const val pressScale = .985f
    const val pressDuration = 120
}

/** A translucent reading plane with directional reflection and an illuminated rim. */
internal fun Modifier.liquidGlass(shape: Shape = LiquidSurfaceTokens.radius, elevation: androidx.compose.ui.unit.Dp = LiquidSurfaceTokens.readingElevation, tint: Color = LiquidSurfaceTokens.porcelain): Modifier =
    shadow(elevation, shape, clip = false, ambientColor = LiquidSurfaceTokens.shade.copy(alpha = .25f),
        spotColor = LiquidSurfaceTokens.shade.copy(alpha = .4f))
        .clip(shape).drawWithCache {
            // Opaque base avoids Android elevation occlusion rectangles beneath text render nodes.
            val plane = Brush.linearGradient(listOf(LiquidSurfaceTokens.rim,
                tint, lerp(tint, LiquidTokens.lavender, .055f)),
                end = Offset(size.width*.75f, size.height))
            val light = Brush.radialGradient(listOf(LiquidSurfaceTokens.rim.copy(alpha = .85f), Color.Transparent),
                center = Offset(size.width*.12f, -size.height*.15f), radius = size.width*.65f)
            val innerLip = Path().apply {
                moveTo(size.width*.06f,size.height*.97f)
                cubicTo(size.width*.32f,size.height*.99f,size.width*.73f,size.height*.99f,size.width*.94f,size.height*.97f)
            }
            onDrawBehind {
                drawRect(plane); drawRect(light)
                drawPath(innerLip,LiquidSurfaceTokens.contour.copy(alpha=.18f),style=Stroke(LiquidSurfaceTokens.bevel.toPx()))
            }
        }.border(1.dp, Brush.linearGradient(listOf(LiquidSurfaceTokens.rim, LiquidSurfaceTokens.porcelainEdge.copy(alpha = .65f),
            LiquidSurfaceTokens.rim)), shape)

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
    val body=if(action) Brush.linearGradient(listOf(LiquidTokens.heroDeep, LiquidTokens.violet, LiquidTokens.heroDeep), end=Offset(size.width,size.height)) else Brush.linearGradient(0f to Color(0xFFB5A2EC), .35f to Color(0xFF7960CE),
        .72f to Color(0xFF7852D7), 1f to Color(0xFFB38AFF),end=Offset(size.width*.4f,size.height))
    val gloss=Brush.radialGradient(listOf(LiquidSurfaceTokens.rim.copy(alpha=if(action) .22f else .6f),Color.Transparent),
        Offset(size.width*.25f,0f),if(action) size.height*.9f else size.width*.7f)
    val reflection=Path().apply {
        moveTo(0f,size.height*.2f)
        cubicTo(size.width*.25f,-size.height*.05f,size.width*.68f,size.height*.28f,size.width,size.height*.08f)
    }
    val edge=Path().apply {
        moveTo(size.width*.08f,size.height*.94f)
        cubicTo(size.width*.32f,size.height*.99f,size.width*.7f,size.height*.99f,size.width*.92f,size.height*.94f)
    }
    onDrawBehind {
        drawRect(body); drawRect(gloss)
        drawPath(reflection,LiquidSurfaceTokens.rim.copy(alpha=if(action) .24f else .38f),style=Stroke(LiquidSurfaceTokens.bevel.toPx()))
        drawPath(edge,LiquidTokens.ice.copy(alpha=.42f),style=Stroke(LiquidSurfaceTokens.bevel.toPx()))
    }
}

@Composable internal fun LiquidCard(modifier: Modifier=Modifier, foil: Boolean=false, content: @Composable ColumnScope.()->Unit) {
    Box(modifier, propagateMinConstraints = true) {
    if(foil) Box(Modifier.matchParentSize().graphicsLayer {
        translationY = LiquidSurfaceTokens.backingOffset.toPx()
        scaleX = LiquidSurfaceTokens.backingScale
    }.drawWithCache {
        // Only the lower metal lip is opaque, so the glass can reveal the scene.
        val thickness = LiquidSurfaceTokens.backingOffset.toPx()*1.6f
        val lip = Brush.verticalGradient(listOf(LiquidTokens.silver, LiquidTokens.ice, LiquidTokens.silver),
            startY = size.height-thickness, endY = size.height)
        onDrawBehind {
            drawRoundRect(lip, Offset(0f,size.height-thickness), Size(size.width,thickness), CornerRadius(thickness/2))
        }
    })
    Box(Modifier.fillMaxWidth().then(if (foil) Modifier.spatialGlass(prominent = true) else Modifier.liquidGlass())) {
        if(foil) {
            Box(Modifier.matchParentSize().drawWithCache {
                val reflectedLight=Brush.radialGradient(listOf(LiquidTokens.lavender.copy(alpha=.35f),Color.Transparent),
                    Offset(size.width*.83f,size.height*.78f),size.width*.6f)
                onDrawBehind { drawRect(reflectedLight) }
            })
            Image(painterResource(R.drawable.overview_liquid_silver),null,Modifier.matchParentSize().graphicsLayer { alpha = .45f },contentScale=ContentScale.FillBounds)
        }
        Column(Modifier.fillMaxWidth().padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp),content=content)
    }
    }
}

