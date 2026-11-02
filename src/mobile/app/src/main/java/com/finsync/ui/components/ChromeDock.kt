package com.finsync.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.finsync.R
import com.finsync.ui.theme.Space
import com.finsync.ui.theme.DockTokens

internal fun Modifier.chromeDockBackdrop(): Modifier = drawWithCache {
    val mist = Brush.linearGradient(listOf(DockTokens.mist, DockTokens.lilac, DockTokens.mistBase),
        start = Offset.Zero, end = Offset(size.width, size.height))
    val crest = Path().apply {
        moveTo(-size.width * .1f, size.height * .16f)
        cubicTo(size.width * .32f, -size.height * .16f, size.width * .55f,
            size.height * .30f, size.width * 1.1f, size.height * .02f)
    }
    onDrawBehind {
        drawRect(mist)
        drawPath(crest, DockTokens.reflection.copy(alpha = .50f), style = Stroke(Space.small.toPx()))
    }
}

/** Gloss is confined to the upper edge, away from the action's label. */
internal fun Modifier.chromeSelectedFinish(): Modifier = drawWithCache {
    val violet = Brush.linearGradient(listOf(DockTokens.violet, DockTokens.violetLight, DockTokens.cobalt),
        end = Offset(size.width, size.height))
    val lip = Path().apply {
        moveTo(-size.width * .05f, size.height * .20f)
        cubicTo(size.width * .25f, -size.height * .02f, size.width * .70f,
            size.height * .30f, size.width * 1.05f, size.height * .10f)
    }
    onDrawBehind {
        drawRect(violet)
        drawPath(lip, DockTokens.reflection.copy(alpha = .16f), style = Stroke(DockTokens.edge.toPx()))
    }
}

internal fun Modifier.chromeNavigationSurface(shape: Shape = DockTokens.shape): Modifier = this
    .shadow(DockTokens.elevation, shape, ambientColor = DockTokens.shadow.copy(alpha = .12f),
        spotColor = DockTokens.shadow.copy(alpha = .16f))
    .clip(shape)
    .drawWithCache {
        val metal = Brush.linearGradient(
            0f to DockTokens.pearl, .27f to DockTokens.silver,
            .47f to DockTokens.pearl, .72f to DockTokens.lilac, 1f to DockTokens.silver,
            end = Offset(size.width, size.height))
        onDrawBehind { drawRect(metal) }
    }
    .border(DockTokens.edge, Brush.linearGradient(listOf(DockTokens.reflection,
        DockTokens.silver, DockTokens.reflection)), shape)

@Composable
internal fun ChromeButton(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit) {
    val finish = if (enabled) Modifier.liquidSelected(action=true) else Modifier.background(DockTokens.silver)
    Button(onClick = onClick,
        modifier = modifier.heightIn(min = DockTokens.actionHeight)
            .shadow(DockTokens.elevation, CircleShape, ambientColor = DockTokens.shadow.copy(alpha = .18f),
                spotColor = DockTokens.shadow.copy(alpha = .24f))
            .clip(CircleShape).then(finish)
            .border(BorderStroke(DockTokens.edge, Brush.linearGradient(listOf(DockTokens.reflection,
                DockTokens.violetLight, DockTokens.lilac))), CircleShape),
        enabled = enabled, shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = DockTokens.onAction,
            disabledContainerColor = Color.Transparent, disabledContentColor = DockTokens.text),
        content = content,
    )
}

@Composable
internal fun ChromeExpenseAction(onClick: () -> Unit, label: String = "Add expense") {
    Box(Modifier.fillMaxWidth().padding(horizontal = Space.medium, vertical = Space.small),
        contentAlignment = androidx.compose.ui.Alignment.Center) {
        Button(onClick = onClick,
            modifier = Modifier.heightIn(min = DockTokens.quickActionHeight).clip(CircleShape)
                .background(Brush.linearGradient(listOf(DockTokens.silver, DockTokens.reflection, DockTokens.lilac)))
                .border(DockTokens.edge, DockTokens.reflection, CircleShape)
                .semantics { contentDescription = label },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = DockTokens.selectedText),
            contentPadding = PaddingValues(horizontal = Space.medium, vertical = Space.small)) {
            Icon(painterResource(R.drawable.ic_add), null, Modifier.size(DockTokens.quickActionIcon))
            Text(label, Modifier.padding(start = Space.small), style = MaterialTheme.typography.labelLarge)
        }
    }
}
