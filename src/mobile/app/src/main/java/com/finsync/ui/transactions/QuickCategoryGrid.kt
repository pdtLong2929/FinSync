package com.finsync.ui.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.finsync.domain.Category
import com.finsync.ui.components.CategoryBadge
import com.finsync.ui.components.liquidGlass
import com.finsync.ui.components.LiquidSurfaceTokens
import androidx.compose.ui.graphics.Color
import com.finsync.ui.theme.LiquidTokens
import com.finsync.ui.theme.Space
import com.finsync.ui.theme.SoftFinanceTokens

@Composable
internal fun QuickCategoryGrid(categories: List<Category>, selected: String, onSelect: (String) -> Unit) {
    val initial = categories.take(6)
    val chosen = categories.find { it.name == selected }
    val visible = if (chosen != null && chosen !in initial) initial.dropLast(1) + chosen else initial
    BoxWithConstraints {
        val columns = if (maxWidth < LiquidTokens.compact || LocalDensity.current.fontScale > LiquidTokens.largeFont) 2 else 3
        Column(verticalArrangement = Arrangement.spacedBy(Space.small)) {
            visible.chunked(columns).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(Space.small)) {
                    row.forEach { category ->
                        val active = selected == category.name
                        Surface(onClick = { onSelect(category.name) },
                            modifier = Modifier.weight(1f).heightIn(min = LiquidTokens.minTarget)
                                .liquidGlass(MaterialTheme.shapes.large,if(active) 4.dp else 1.dp,if(active) LiquidTokens.lilacTile else LiquidSurfaceTokens.frost)
                                .border(if(active) 2.dp else 0.dp,if(active) LiquidTokens.violet else Color.Transparent,MaterialTheme.shapes.large)
                                .semantics { this.selected = active; role = Role.RadioButton },
                            shape = MaterialTheme.shapes.large,
                            color = Color.Transparent) {
                            Column(Modifier.padding(Space.small), horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(Space.small)) {
                                Box(Modifier.border(if (active) SoftFinanceTokens.selectionStroke else com.finsync.ui.theme.DockTokens.edge,
                                    Brush.linearGradient(if (active) listOf(LiquidTokens.violet, LiquidTokens.pearl, LiquidTokens.violet)
                                        else listOf(LiquidTokens.pearl, LiquidTokens.pearl)), CircleShape).padding(Space.small)) {
                                    CategoryBadge(category.name, category.id)
                                }
                                Text(category.name, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                    repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}
