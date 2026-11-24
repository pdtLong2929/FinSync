package com.finsync.ui.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import com.finsync.domain.*
import com.finsync.ui.FinanceState
import com.finsync.ui.components.*
import com.finsync.ui.dashboard.formatVnd
import com.finsync.ui.theme.*

internal val groupTypes = linkedMapOf("HOME" to "Shared home", "TRIP" to "Trip", "COUPLE" to "Couple", "PROJECT" to "Project", "OTHER" to "Other")
internal fun groupTypeLabel(type: String) = groupTypes[type] ?: type

@Composable internal fun GroupSectionTabs(selected: String, onSelect: (String) -> Unit) {
    val largeText = LocalDensity.current.fontScale > 1.2f
    val inset = if (largeText) Space.small / 4 else Space.small
    val gap = if (largeText) Space.small * 0 else Space.small
    Row(Modifier.fillMaxWidth().clip(LiquidTokens.rowShape).background(LiquidTokens.mistLilac)
        .padding(inset).selectableGroup(), horizontalArrangement = Arrangement.spacedBy(gap)) {
        listOf("Expenses", "Balances", "Members").forEach { label ->
            Box(Modifier.weight(1f).clip(LiquidSurfaceTokens.insetShape)
                .then(if (selected == label) Modifier.liquidSelected(action = true) else Modifier)
                .selectable(selected == label, role = Role.Tab, onClick = { if (label != selected) onSelect(label) })
                .heightIn(min = LiquidTokens.minTarget).padding(horizontal = if (largeText) Space.small * 0 else Space.small / 2, vertical = Space.small), contentAlignment = Alignment.Center) {
                Text(label, modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center,
                    color = if (selected == label) DockTokens.onAction else LiquidTokens.ink)
            }
        }
    }
}

@Composable internal fun GroupSummary(group: ExpenseGroup, state: FinanceState) {
    val balance = groupBalances(group, state.groupExpenses, state.settlements)["me"] ?: 0L
    LiquidMetalSurface {
        Text(groupTypeLabel(group.type), style = MaterialTheme.typography.labelMedium)
        Text(formatVnd(state.groupExpenses.filter { it.groupId == group.id }.sumOf { it.amount }), style = MaterialTheme.typography.headlineMedium)
        Text("Group spending · ${group.members.count { it.active }} members", style = MaterialTheme.typography.bodySmall)
        Text(group.members.filter { it.active }.joinToString(" · ") { it.name }, style = MaterialTheme.typography.bodySmall)
        Text(when {
            balance > 0 -> "Owed to you ${formatVnd(balance)}"
            balance < 0 -> "You owe ${formatVnd(-balance)}"
            else -> "Your balance is settled"
        }, color = if (balance < 0) LocalFinanceColors.current.expense else LocalFinanceColors.current.income)
        if (group.description.isNotBlank()) Text(group.description, style = MaterialTheme.typography.bodySmall)
    }
}




