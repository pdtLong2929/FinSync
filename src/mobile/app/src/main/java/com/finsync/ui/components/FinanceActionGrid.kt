package com.finsync.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import com.finsync.ui.theme.*

internal data class FinanceAction(val label: String, @param:DrawableRes val icon: Int, val onClick: () -> Unit)

/** Responsive action rows: whole card is a target, text may wrap without clipping. */
@Composable
internal fun FinanceActionGrid(actions: List<FinanceAction>) {
    BoxWithConstraints {
        val columns = if (maxWidth < LiquidTokens.compact || LocalDensity.current.fontScale > LiquidTokens.largeFont) 1 else 2
        Column(verticalArrangement = Arrangement.spacedBy(Space.small)) {
            actions.chunked(columns).forEach { row ->
                Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(Space.small)) {
                    row.forEach { action ->
                        Surface(onClick = action.onClick, shape = LiquidTokens.rowShape,
                            color = androidx.compose.ui.graphics.Color.Transparent,
                            modifier = Modifier.weight(1f).fillMaxHeight().liquidGlass(LiquidTokens.rowShape)) {
                            Row(Modifier.heightIn(min = LiquidTokens.minTarget).padding(Space.medium),
                                horizontalArrangement = Arrangement.spacedBy(Space.small), verticalAlignment = Alignment.CenterVertically) {
                                SculptedIcon(action.icon, Modifier.size(DockTokens.quickActionIcon))
                                Text(action.label, Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                    if(row.size < columns) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}
