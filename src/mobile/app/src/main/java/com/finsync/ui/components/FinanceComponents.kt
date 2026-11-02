package com.finsync.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.finsync.ui.theme.MetalTokens
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.semantics.semantics
import com.finsync.ui.dashboard.formatVnd
import com.finsync.ui.theme.Space
import com.finsync.ui.theme.LayoutTokens

@Composable
fun MoneyText(
    amount: Long,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleLarge,
    signed: Boolean = false,
) {
    Text(
        text = if (signed && amount > 0) "+${formatVnd(amount)}" else formatVnd(amount),
        modifier = modifier,
        style = style.copy(fontFeatureSettings = "tnum"),
    )
}

@Composable
fun FinanceCard(
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth().liquidGlass(MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Column(Modifier.fillMaxWidth().padding(Space.large), verticalArrangement = Arrangement.spacedBy(Space.medium)) {
            content()
        }
    }
}

@Composable
fun LabeledAmount(label: String, amount: Long, hidden: Boolean, hiddenLabel: String) {
    Column(Modifier.semantics(mergeDescendants = true) {}) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        if (hidden) Text(hiddenLabel) else MoneyText(amount)
    }
}
