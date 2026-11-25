package com.finsync.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.finsync.R
import com.finsync.ui.components.*
import com.finsync.ui.theme.*

internal object AuthVisualTokens {
    val compactEmblem = 52.dp
    val fieldOutline = DockTokens.text.copy(alpha = .72f)
    val emblemShape = RoundedCornerShape(18.dp)
}

/** One level, compact brand row; chrome is confined to the monogram. */
@Composable
internal fun AuthSculpture(modifier: Modifier = Modifier, small: Boolean = false) {
    Row(modifier.fillMaxWidth().spatialGlass().padding(Space.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Space.medium)) {
        Box(Modifier.size(if (small) LiquidTokens.iconSize else AuthVisualTokens.compactEmblem)
            .liquidLens(AuthVisualTokens.emblemShape), contentAlignment = Alignment.Center) {
            Icon(painterResource(R.drawable.ic_brand_monogram), null,
                Modifier.size(DockTokens.quickActionIcon), tint = LiquidTokens.heroDeep)
        }
        Column(Modifier.weight(1f)) {
            Text("FinSync", style = MaterialTheme.typography.headlineSmall)
            Text("Money, in sync.", style = MaterialTheme.typography.bodySmall, color = LiquidTokens.mutedLight)
        }
    }
}

@Composable
internal fun AuthHeading(title: String, subtitle: String, showcase: Boolean = false, compact: Boolean = false) {
    Column(verticalArrangement = Arrangement.spacedBy(Space.small)) {
        if(!compact) AuthSculpture(small = !showcase)
        Row(horizontalArrangement = Arrangement.spacedBy(Space.small), verticalAlignment = Alignment.CenterVertically) {
            if(compact) Box(Modifier.size(LiquidTokens.iconSize).liquidLens(), contentAlignment = Alignment.Center) {
                Icon(painterResource(R.drawable.ic_brand_monogram), null, Modifier.size(DockTokens.quickActionIcon), tint = LiquidTokens.heroDeep)
            }
            Column(verticalArrangement = Arrangement.spacedBy(Space.small)) {
                Text(title, style = if(showcase) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.headlineMedium)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = LiquidTokens.mutedLight)
            }
        }
    }
}

@Composable internal fun AuthFormPlane(content: @Composable ColumnScope.() -> Unit) {
    Box(Modifier.fillMaxWidth().spatialGlass()) {
        Column(Modifier.fillMaxWidth().padding(Space.medium), verticalArrangement = Arrangement.spacedBy(Space.small), content = content)
    }
}

@Composable internal fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedContainerColor = LiquidTokens.pearl,
    focusedContainerColor = LiquidSurfaceTokens.rim,
    unfocusedBorderColor = AuthVisualTokens.fieldOutline,
    focusedBorderColor = LiquidTokens.violet,
)

@Composable internal fun AuthFieldIcon(icon: Int) {
    Icon(painterResource(icon), null, Modifier.size(DockTokens.quickActionIcon), tint = LiquidTokens.violet)
}
