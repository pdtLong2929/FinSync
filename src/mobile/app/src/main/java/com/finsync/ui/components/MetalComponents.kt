package com.finsync.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import com.finsync.ui.theme.MetalTokens as M
import com.finsync.ui.theme.Space

/** Shared bright material; emphasized surfaces use flowing silver, dense surfaces stay calm. */
fun Modifier.metalFinish(hero: Boolean = false, base: Color = com.finsync.ui.theme.LiquidTokens.mistBlue,
    glints: Boolean = false): Modifier = if (hero) pearlChrome() else drawWithCache {
    val calm = Brush.linearGradient(listOf(base, base, com.finsync.ui.theme.LiquidTokens.mistLilac))
    onDrawBehind { drawRect(calm) }
}

@Composable
fun MetalButton(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true,
                content: @Composable RowScope.() -> Unit) {
    ChromeButton(onClick, modifier, enabled, content)
}

@Composable
fun MetalOutlineButton(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true,
                       content: @Composable RowScope.() -> Unit) {
    OutlinedButton(onClick, modifier.heightIn(min = com.finsync.ui.theme.DockTokens.actionHeight).liquidGlass(MaterialTheme.shapes.medium, androidx.compose.ui.unit.Dp(2f)), enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent), content = content)
}

@Composable
fun MetalDialog(onDismissRequest: () -> Unit, title: @Composable () -> Unit,
                text: @Composable () -> Unit, confirmButton: @Composable () -> Unit,
                dismissButton: @Composable () -> Unit) {
    AlertDialog(onDismissRequest, confirmButton,
        modifier = Modifier.liquidGlass(MaterialTheme.shapes.extraLarge),
        dismissButton = dismissButton, title = title, text = text,
        containerColor = Color.Transparent,
        shape = MaterialTheme.shapes.extraLarge)
}

@Composable
fun MetalProgress(progress: () -> Float, modifier: Modifier = Modifier, accent: Color = MaterialTheme.colorScheme.primary) {
    val value = progress().coerceIn(0f, 1f)
    Box(modifier.fillMaxWidth().height(M.chartHeight).clip(MaterialTheme.shapes.small)
        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        .semantics { progressBarRangeInfo = ProgressBarRangeInfo(value, 0f..1f) }) {
        Box(Modifier.fillMaxHeight().fillMaxWidth(value).clip(MaterialTheme.shapes.small)
            .background(Brush.horizontalGradient(listOf(accent, androidx.compose.ui.graphics.lerp(accent, com.finsync.ui.theme.LiquidTokens.cyanTile, .25f), accent)))
            .border(M.rim, accent, MaterialTheme.shapes.small))
    }
}

@Composable
fun MetalEyebrow(text: String) {
    Text(text, style = MaterialTheme.typography.labelSmall.copy(letterSpacing = M.tracking))
}
