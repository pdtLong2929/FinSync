package com.finsync.ui.groups

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import com.finsync.domain.*
import com.finsync.ui.components.*
import com.finsync.ui.dashboard.formatVnd
import com.finsync.ui.theme.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

@Composable internal fun MemberAllocationRow(member: GroupMember, payer: Boolean, selected: Boolean,
    mode: SplitMode, input: String, share: Long?, onToggle: () -> Unit, onInput: (String) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(vertical = Space.small), verticalArrangement = Arrangement.spacedBy(Space.small)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.small)) {
            FilterChip(selected, onToggle, leadingIcon = { MemberAvatar(member.name) },
                label = { Text(member.name + if(payer) " · Payer" else "") }, enabled = !payer,
                modifier = Modifier.weight(1f).heightIn(min=LiquidTokens.minTarget).clip(MaterialTheme.shapes.medium)
                    .then(if(selected) Modifier.liquidSelected() else Modifier.liquidGlass(MaterialTheme.shapes.medium)),
                shape=MaterialTheme.shapes.medium,border=null,
                colors=FilterChipDefaults.filterChipColors(containerColor=Color.Transparent,selectedContainerColor=Color.Transparent,
                    disabledSelectedContainerColor=LiquidTokens.lilacTile,selectedLabelColor=DockTokens.onAction))
            if(mode == SplitMode.Equal && share != null && LocalDensity.current.fontScale <= LiquidTokens.largeFont)
                Text(formatVnd(share), style = MaterialTheme.typography.titleMedium)
        }
        if(selected && mode != SplitMode.Equal) {
            if(mode == SplitMode.Shares) ChromeTextField(input, { onInput(it.take(13)) }, Modifier.fillMaxWidth(),
                label = { Text("${member.name} (shares)") }, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            else AmountField(input, onInput, if(mode == SplitMode.Percentage) "${member.name} (%)" else "${member.name} (VND)", percentage = mode == SplitMode.Percentage)
        }
        if(share != null && (mode != SplitMode.Equal || LocalDensity.current.fontScale > LiquidTokens.largeFont))
            Text("Share: ${formatVnd(share)}", style = MaterialTheme.typography.labelLarge)
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = .35f))
    }
}
