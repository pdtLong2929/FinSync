package com.finsync.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import com.finsync.ui.theme.*

/** Shared approved decorative ribbon; financial content remains native. */
@Composable
internal fun LiquidMetalSurface(modifier: Modifier = Modifier, refined: Boolean = false, content: @Composable ColumnScope.() -> Unit) {
    LiquidCard(modifier, foil = true) {
        androidx.compose.runtime.CompositionLocalProvider(androidx.compose.material3.LocalContentColor provides LiquidTokens.ink) { content() }
    }
}

/** Metal is reserved for the active option; labels and selection semantics stay explicit. */
@OptIn(ExperimentalLayoutApi::class)
@Composable internal fun LiquidMetalSelector(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    val large=androidx.compose.ui.platform.LocalDensity.current.fontScale > LiquidTokens.largeFont
    FlowRow(Modifier.fillMaxWidth().clip(LiquidTokens.rowShape).background(LiquidTokens.mistLilac)
        .padding(DockTokens.edge).selectableGroup(),maxItemsInEachRow=if(large) 2 else options.size,
        verticalArrangement=Arrangement.spacedBy(Space.small)) {
        options.forEach { label ->
            val active = selected == label
            Box(Modifier.weight(1f).clip(LiquidTokens.rowShape)
                .then(if(active) Modifier.liquidSelected(action=true) else Modifier.liquidGlass(LiquidTokens.rowShape, androidx.compose.ui.unit.Dp(0f)))
                .selectable(active, role = Role.Tab, onClick = { onSelect(label) })
                .heightIn(min = LiquidTokens.minTarget).padding(Space.small), contentAlignment = Alignment.Center) {
                Text(label, color = if(active) DockTokens.onAction else LiquidTokens.ink, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
