package com.finsync.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.finsync.R
import com.finsync.ui.theme.Space

/** Static skeletons avoid continuous shimmer work and motion while data is loading. */
@Composable
fun LoadingState(dashboard: Boolean = false) {
    Column(Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite },
        verticalArrangement = Arrangement.spacedBy(Space.medium)) {
        LinearProgressIndicator(Modifier.fillMaxWidth())
        Text("Loading your ledger…")
        repeat(if (dashboard) 2 else 3) {
            Surface(modifier=Modifier.liquidGlass(MaterialTheme.shapes.large,2.dp),shape = MaterialTheme.shapes.large, color = androidx.compose.ui.graphics.Color.Transparent) {
                Column(Modifier.fillMaxWidth().padding(Space.medium), verticalArrangement = Arrangement.spacedBy(Space.small)) {
                    Box(Modifier.fillMaxWidth(.55f).height(16.dp).background(MaterialTheme.colorScheme.surfaceContainerHighest, MaterialTheme.shapes.small))
                    Box(Modifier.fillMaxWidth(.8f).height(if (dashboard) 56.dp else 24.dp).background(MaterialTheme.colorScheme.surfaceContainerHighest, MaterialTheme.shapes.small))
                }
            }
        }
    }
}

@Composable
fun EmptyState(message: String, actionLabel: String? = null, onAction: () -> Unit = {}) {
    FinanceCard {
        Icon(painterResource(R.drawable.ic_category_other), contentDescription = null,
            modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
        Text(message, Modifier.semantics { liveRegion = LiveRegionMode.Polite }, style = MaterialTheme.typography.bodyLarge)
        if (actionLabel != null) MetalOutlineButton(onClick = onAction) { Text(actionLabel) }
    }
}
