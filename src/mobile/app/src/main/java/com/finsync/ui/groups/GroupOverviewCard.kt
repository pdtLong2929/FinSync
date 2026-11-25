package com.finsync.ui.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import com.finsync.R
import com.finsync.domain.groupBalances
import com.finsync.domain.simplifiedDebts
import com.finsync.ui.FinanceState
import com.finsync.ui.components.*
import com.finsync.ui.dashboard.formatVnd
import com.finsync.ui.theme.*

/** Group balances stay separate from personal wallets; never net debt across groups. */
@Composable
internal fun GroupOverviewCard(state: FinanceState) {
    val groups = state.groups.filter { it.joined }
    val debts = groups.flatMap { currentGroupDebts(it, state) }
    val owed = debts.filter { it.to == "me" }.sumOf { it.amount }
    val owing = debts.filter { it.from == "me" }.sumOf { it.amount }
    LiquidCard(Modifier.fillMaxWidth(), foil = true) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.small)) {
            SculptedIcon(R.drawable.ic_groups)
            Column {
                Text("Shared balance", style = MaterialTheme.typography.titleMedium)
                Text(when(groups.size) { 0 -> "No groups yet"; 1 -> "1 active group"; else -> "${groups.size} active groups" },
                    style = MaterialTheme.typography.labelSmall, color = LiquidTokens.mutedLight)
            }
        }
        val metric: @Composable (String, Long, Boolean, Modifier) -> Unit = { label, amount, receivable, modifier ->
            Column(modifier.clip(LiquidSurfaceTokens.insetShape)
                .background(if (receivable) LiquidTokens.cyanTile else LiquidTokens.lilacTile)
                .padding(Space.medium), verticalArrangement = Arrangement.spacedBy(Space.small)) {
                Text(label, style = MaterialTheme.typography.labelMedium,
                    color = if (receivable) LiquidTokens.incomeLight else LiquidTokens.heroDeep)
                Text(formatVnd(amount), style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum"))
            }
        }
        BoxWithConstraints {
            if (maxWidth < LiquidTokens.transactionCompact || LocalDensity.current.fontScale > LiquidTokens.largeFont) {
                Column(verticalArrangement = Arrangement.spacedBy(Space.small)) {
                    metric("Owed to you", owed, true, Modifier.fillMaxWidth())
                    metric("You owe", owing, false, Modifier.fillMaxWidth())
                }
            } else Row(horizontalArrangement = Arrangement.spacedBy(Space.small)) {
                metric("Owed to you", owed, true, Modifier.weight(1f))
                metric("You owe", owing, false, Modifier.weight(1f))
            }
        }
    }
}
