package com.finsync.ui.groups

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import com.finsync.ui.theme.SoftFinanceTokens
import com.finsync.ui.components.liquidGlass
import com.finsync.ui.theme.LiquidTokens
import androidx.compose.ui.graphics.Color

@Composable
internal fun MemberAvatar(name: String) {
    val colors=listOf(LiquidTokens.lilacTile,LiquidTokens.cyanTile,LiquidTokens.pinkTile,LiquidTokens.mistBlue)
    val tint=colors[Math.floorMod(name.hashCode(),colors.size)]
    Surface(Modifier.size(SoftFinanceTokens.avatarSize).liquidGlass(CircleShape,2.dp,tint).clearAndSetSemantics {}, shape = CircleShape, color = Color.Transparent) {
        Box(contentAlignment = Alignment.Center) {
            Text(name.trim().split(Regex("\\s+")).take(2).mapNotNull { it.firstOrNull() }.joinToString("").uppercase(),
                style = MaterialTheme.typography.labelMedium)
        }
    }
}
