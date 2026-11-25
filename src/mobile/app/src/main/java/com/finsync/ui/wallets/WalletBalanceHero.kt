package com.finsync.ui.wallets

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.finsync.R
import com.finsync.ui.components.*
import com.finsync.ui.theme.*

/** Give the amount its own full-width line; content determines the hero's height. */
@Composable
internal fun WalletBalanceHero(amount: Long, hidden: Boolean, onPrivacy: () -> Unit) {
    val amountStyle = (if (LocalDensity.current.fontScale > LiquidTokens.largeFont)
        MaterialTheme.typography.headlineMedium else MaterialTheme.typography.displaySmall)
        .copy(fontWeight = FontWeight.Medium, textAlign = TextAlign.Start)
    LiquidMetalSurface(Modifier.fillMaxWidth(), refined = true) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Space.small)) {
            SculptedIcon(R.drawable.ic_wallet)
            Text("Personal net worth", Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
            IconButton(modifier = Modifier.clip(CircleShape)
                .border(DockTokens.edge, LiquidTokens.pearl, CircleShape)
                .pastelMetal(LiquidTokens.pearl), onClick = onPrivacy) {
                Icon(painterResource(R.drawable.ic_visibility), if (hidden) "Show amounts" else "Hide amounts")
            }
        }
        if (hidden) Text("Hidden", Modifier.fillMaxWidth(), style = amountStyle)
        else MoneyText(amount, Modifier.fillMaxWidth(), style = amountStyle)
    }
}
