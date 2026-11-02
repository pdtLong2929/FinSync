package com.finsync.ui.reports

import androidx.compose.foundation.border
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.platform.testTag
import com.finsync.ui.theme.*
import com.finsync.ui.components.*
import com.finsync.ui.dashboard.formatVnd

/** Quiet porcelain reading plane; silver contour is confined to the lower edge. */
@Composable internal fun ReportSignatureHero(expenses: Long, income: Long) {
    LiquidCard(Modifier.fillMaxWidth().heightIn(min = SoftFinanceTokens.reportHeroHeight).testTag("liquid-metal-report-hero"),foil=true) {
        Text("Spending", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(formatVnd(expenses), style = MaterialTheme.typography.headlineLarge.copy(fontFeatureSettings = "tnum"))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(Space.large), verticalArrangement = Arrangement.spacedBy(Space.small)) {
            Text("Income  ${formatVnd(income)}", style = MaterialTheme.typography.labelSmall)
            Text("Net  ${formatVnd(income-expenses)}", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable internal fun ReportSegments(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    val large = androidx.compose.ui.platform.LocalDensity.current.fontScale > LiquidTokens.largeFont
    val option: @Composable (String, Modifier) -> Unit = { label, modifier ->
        val active = selected == label
        Box(modifier.clip(MaterialTheme.shapes.large)
            .then(if(active) Modifier.liquidSelected(action=true) else Modifier.liquidGlass(MaterialTheme.shapes.large, androidx.compose.ui.unit.Dp(0f)))
            .selectable(active, role = Role.Tab, onClick = { onSelect(label) })
            .heightIn(min = LiquidTokens.minTarget).padding(Space.small), contentAlignment = Alignment.Center) {
            Text(label, style = MaterialTheme.typography.labelMedium,
                color = if(active) DockTokens.onAction else MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
    if(large) Column(Modifier.fillMaxWidth().selectableGroup(), verticalArrangement = Arrangement.spacedBy(Space.small)) {
        options.forEach { option(it, Modifier.fillMaxWidth()) }
    } else Row(Modifier.fillMaxWidth().selectableGroup(), horizontalArrangement = Arrangement.spacedBy(Space.small)) {
        options.forEach { option(it, Modifier.weight(1f)) }
    }
}
