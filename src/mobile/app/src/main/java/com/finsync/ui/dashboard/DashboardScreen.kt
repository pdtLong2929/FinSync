package com.finsync.ui.dashboard

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import com.finsync.R
import com.finsync.ui.components.*
import com.finsync.ui.theme.LiquidTokens
import com.finsync.ui.components.LabeledAmount
import com.finsync.ui.components.MoneyText
import com.finsync.ui.theme.FinSyncTheme
import com.finsync.ui.theme.Space

@Composable
fun DashboardScreen(
    state: DashboardUiState,
    amountsHidden: Boolean,
    onToggleAmounts: () -> Unit,
    onGroups: () -> Unit,
    onCreateTransaction: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    onTransaction: (String) -> Unit = {},
    onHistory: () -> Unit = {},
    onBudgets: () -> Unit = {},
) {
    val palette = chromePalette()
    MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(
        primary = palette.accent, onPrimary = if (palette.dark) LiquidTokens.ink else LiquidTokens.white,
        surface = palette.background, onSurface = palette.text,
        onSurfaceVariant = palette.muted,
    )) {
        Surface(modifier.fillMaxSize(), color = Color.Transparent, contentColor = palette.text) {
            when (state) {
                DashboardUiState.Loading -> Box(Modifier.fillMaxSize().padding(Space.large), contentAlignment = Alignment.TopCenter) {
                    LoadingState(dashboard = true)
                }
                DashboardUiState.Empty -> StatusContent(
                    stringResource(R.string.empty_title), stringResource(R.string.empty_body),
                    stringResource(R.string.create_transaction), onCreateTransaction,
                )
                DashboardUiState.Error -> StatusContent(
                    stringResource(R.string.error_title), stringResource(R.string.error_body),
                    stringResource(R.string.retry), onRetry,
                )
                is DashboardUiState.Content -> DashboardContent(
                    state, amountsHidden, onToggleAmounts, onGroups, onTransaction, onHistory, onBudgets,
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    state: DashboardUiState.Content,
    hidden: Boolean,
    onToggleAmounts: () -> Unit,
    onGroups: () -> Unit,
    onTransaction: (String) -> Unit,
    onHistory: () -> Unit,
    onBudgets: () -> Unit,
) {
    val data = state.data
    val palette = chromePalette()
    val hiddenLabel = stringResource(R.string.balance_hidden)
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = Space.medium, end = Space.medium, top = Space.large, bottom = Space.fabClearance),
        verticalArrangement = Arrangement.spacedBy(Space.small),
    ) {
        if (state.isOffline) item("offline") {
            Text(stringResource(R.string.offline_notice),
                Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                style = MaterialTheme.typography.bodyMedium, color = palette.muted)
        }
        item("personal") { LiquidBalance(data.personal, hidden, hiddenLabel, onToggleAmounts) }
        item("trend") { OverviewTrend(data.spendingTrend, hidden, onHistory) }
        item("metrics") { DashboardAdaptiveRow(breakpoint = LiquidTokens.transactionCompact,
            first = { HeroMetric(R.string.income, data.personal.income, hidden, hiddenLabel, true) },
            second = { HeroMetric(R.string.expense, data.personal.expenses, hidden, hiddenLabel, false) }) }
        item("budget") { DashboardBudget(data, hidden, hiddenLabel, onBudgets) }
        item("transactions_heading") {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.recent_transactions), Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = onHistory) { Text(stringResource(R.string.view_all_transactions), style = MaterialTheme.typography.labelSmall) }
            }
        }
        if (data.transactions.isEmpty()) item("transactions_empty") {
            ChromePanel { Text(stringResource(R.string.chrome_no_transactions), color = palette.muted) }
        }
        if (data.transactions.isNotEmpty()) item("transactions") {
            Surface(modifier = Modifier.liquidGlass(), shape = LiquidSurfaceTokens.radius, color = Color.Transparent, contentColor = palette.text) {
                Column(Modifier.fillMaxWidth().padding(horizontal = Space.medium)) {
                    data.transactions.forEachIndexed { index, transaction ->
                        Surface(
                            Modifier.fillMaxWidth().clip(LiquidTokens.rowShape)
                                .clickable(role = Role.Button) { onTransaction(transaction.id) },
                            color = Color.Transparent, contentColor = palette.text,
                        ) {
                            Row(Modifier.padding(vertical = Space.small), horizontalArrangement = Arrangement.spacedBy(Space.medium), verticalAlignment = Alignment.CenterVertically) {
                                ChromeCategoryBadge(transaction.category, transaction.signedAmount > 0)
                                Box(Modifier.weight(1f)) {
                                    DashboardAdaptiveRow(breakpoint = LiquidTokens.transactionCompact,
                                        first = {
                                            Column(verticalArrangement = Arrangement.spacedBy(Space.small)) {
                                                Text(transaction.displayTitle ?: stringResource(transaction.title), style = MaterialTheme.typography.labelLarge)
                                                Text(transaction.category.orEmpty() + " · " + transaction.displayDetail.orEmpty().substringAfterLast(" · "), style = MaterialTheme.typography.labelSmall, color = palette.muted)
                                            }
                                        },
                                        second = {
                                            if (hidden) Text(hiddenLabel, style = MaterialTheme.typography.titleMedium)
                                            else MoneyText(transaction.signedAmount, signed = true, style = MaterialTheme.typography.titleMedium)
                                        },
                                    )
                                }
                            }
                        }
                        if (index < data.transactions.lastIndex) HorizontalDivider(color = palette.line)
                    }
                }
            }
        }
        item("quick_tools") {
            DashboardQuickTools(onHistory, onBudgets, onGroups)
        }
        item("group") {
            ChromePanel {
                Row(horizontalArrangement = Arrangement.spacedBy(Space.medium), verticalAlignment = Alignment.CenterVertically) {
                    ChromeBadge("◇", palette.accent)
                    Column(Modifier.weight(1f)) {
                        SectionTitle(stringResource(R.string.group_finance))
                        Text(data.group.displayName ?: stringResource(data.group.name), style = MaterialTheme.typography.bodyMedium, color = palette.muted)
                    }
                }
                DashboardAdaptiveRow(breakpoint = LiquidTokens.transactionCompact,
                    first = { LabeledAmount(stringResource(R.string.owed_to_you), data.group.receivable, hidden, hiddenLabel) },
                    second = { LabeledAmount(stringResource(R.string.you_owe), data.group.payable, hidden, hiddenLabel) },
                )
                TextButton(onClick = onGroups) { Text(stringResource(R.string.view_groups)) }
            }
        }
        item("insight") {
            ChromePanel {
                Text(stringResource(R.string.ai_title), style = MaterialTheme.typography.labelLarge, color = palette.accent)
                Text(stringResource(R.string.ai_body), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun LiquidBalance(personal: PersonalSummary, hidden: Boolean, hiddenLabel: String, onToggle: () -> Unit) {
    LiquidCard(Modifier.fillMaxWidth().heightIn(min = LiquidSurfaceTokens.heroHeight), foil = true) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.personal_balance), Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
            IconToggleButton(modifier = Modifier.background(LiquidTokens.pearl.copy(alpha = .92f), androidx.compose.foundation.shape.CircleShape), checked = hidden, onCheckedChange = { onToggle() }) {
                Icon(painterResource(R.drawable.ic_visibility), stringResource(if(hidden) R.string.show_balance else R.string.hide_balance), tint = LiquidTokens.violet)
            }
        }
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val style = if(LocalDensity.current.fontScale > LiquidTokens.largeFont || maxWidth < LiquidTokens.transactionCompact)
                MaterialTheme.typography.headlineMedium else MaterialTheme.typography.headlineLarge.copy(fontSize = LiquidSurfaceTokens.amount)
            if(hidden) Text(hiddenLabel, style = style)
            else MoneyText(personal.balance, style = style.copy(fontWeight = FontWeight.SemiBold))
        }

    }
}

@Composable
private fun HeroMetric(@StringRes label: Int, amount: Long, hidden: Boolean, hiddenLabel: String, income: Boolean) {
    LiquidCard(Modifier.fillMaxWidth().heightIn(min = LiquidSurfaceTokens.metricHeight)) {
        Row(horizontalArrangement = Arrangement.spacedBy(Space.small), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(24.dp).clip(CircleShape).pastelMetal(if(income) LiquidTokens.cyanTile else LiquidTokens.pinkTile), contentAlignment = Alignment.Center) {
                Canvas(Modifier.size(14.dp)) {
                    val tint=if(income) LiquidTokens.incomeLight else LiquidTokens.expenseLight
                    val end=if(income) size.height*.8f else size.height*.2f
                    val shoulder=if(income) size.height*.52f else size.height*.48f
                    drawLine(tint,Offset(size.width*.5f,size.height-end),Offset(size.width*.5f,end),2.dp.toPx(),StrokeCap.Round)
                    drawPath(Path().apply {
                        moveTo(size.width*.2f,shoulder); lineTo(size.width*.5f,end); lineTo(size.width*.8f,shoulder)
                    },tint,style=Stroke(2.dp.toPx(),cap=StrokeCap.Round,join=StrokeJoin.Round))
                }
            }
            Text(stringResource(label), style=MaterialTheme.typography.labelLarge)
        }
        if(hidden) Text(hiddenLabel) else MoneyText(amount,style=MaterialTheme.typography.titleMedium.copy(fontSize=LiquidSurfaceTokens.metricAmount))
    }
}

@Composable
private fun DashboardQuickTools(onHistory: () -> Unit, onBudgets: () -> Unit, onGroups: () -> Unit) {
    val labels = listOf(R.string.concept_activity, R.string.concept_budgets, R.string.concept_shared)
    val icons = listOf(R.drawable.ic_wallet, R.drawable.ic_reports, R.drawable.ic_groups)
    val tints = listOf(LiquidTokens.lilacTile, LiquidTokens.cyanTile, LiquidTokens.pinkTile)
    val actions = listOf(onHistory, onBudgets, onGroups)
    BoxWithConstraints {
        val stacked = maxWidth < LiquidTokens.compact || LocalDensity.current.fontScale > LiquidTokens.largeFont
        val tile: @Composable (Int, Modifier) -> Unit = { index, modifier ->
            TextButton(onClick = actions[index], modifier = modifier.heightIn(min = if (stacked) LiquidTokens.minTarget else LiquidTokens.tileHeight).clip(LiquidTokens.rowShape).pastelMetal(tints[index]),
                shape = LiquidTokens.rowShape,
                colors = ButtonDefaults.textButtonColors(containerColor = Color.Transparent, contentColor = LiquidTokens.ink)) {
                if (stacked) {
                    Icon(painterResource(icons[index]), null)
                    Text(stringResource(labels[index]), Modifier.padding(start = Space.medium))
                } else Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Space.small)) {
                    Icon(painterResource(icons[index]), null)
                    Text(stringResource(labels[index]), style = MaterialTheme.typography.labelLarge)
                }
            }
        }
        if (stacked) Column(verticalArrangement = Arrangement.spacedBy(Space.small)) {
            labels.indices.forEach { tile(it, Modifier.fillMaxWidth()) }
        } else Row(horizontalArrangement = Arrangement.spacedBy(Space.small)) {
            labels.indices.forEach { tile(it, Modifier.weight(1f)) }
        }
    }
}

@Composable
private fun DashboardBudget(data: DashboardData, hidden: Boolean, hiddenLabel: String, onBudgets: () -> Unit) {
    val palette = chromePalette()
    val progress = budgetProgress(data.diningSpent, data.diningLimit)
    val remaining = budgetRemaining(data.diningSpent, data.diningLimit)
    val percent = if (data.diningLimit > 0) data.diningSpent * 100 / data.diningLimit else 0
    val status = when {
        data.diningLimit <= 0 -> R.string.budget_no_limit
        data.diningSpent >= data.diningLimit -> R.string.budget_reached
        percent >= 80 -> R.string.budget_close
        else -> R.string.budget_within
    }
    val statusLabel = stringResource(status)
    Surface(onClick = onBudgets, modifier = Modifier.liquidGlass(), shape = LiquidSurfaceTokens.radius, color = Color.Transparent, contentColor = LiquidTokens.ink) {
        Row(Modifier.fillMaxWidth().padding(Space.medium).semantics { contentDescription = "Review budgets and spending"; stateDescription = "$percent% · $statusLabel" },
            horizontalArrangement = Arrangement.spacedBy(Space.medium), verticalAlignment = Alignment.CenterVertically) {
            if(LocalDensity.current.fontScale <= LiquidTokens.largeFont) OverviewBudgetRing(progress, "$percent%")
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.budget_title), style = MaterialTheme.typography.labelLarge)
                if(data.diningLimit > 0) Text(if(hidden) hiddenLabel else stringResource(
                    if(remaining < 0) R.string.chrome_over_budget else R.string.chrome_budget_remaining,
                    formatVnd(kotlin.math.abs(remaining))), style = MaterialTheme.typography.titleMedium)
                Text(if(hidden) hiddenLabel else stringResource(R.string.budget_amount, formatVnd(data.diningSpent), formatVnd(data.diningLimit)), style = MaterialTheme.typography.labelSmall)

            }
            Text("›", style = MaterialTheme.typography.titleLarge)
        }
    }
}

/** Allow long labels and full monetary values to wrap; never ellipsize financial data. */
@Composable
private fun DashboardAdaptiveRow(
    breakpoint: Dp = LiquidTokens.compact,
    first: @Composable () -> Unit,
    second: @Composable () -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        if (maxWidth < breakpoint || LocalDensity.current.fontScale > LiquidTokens.largeFont) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Space.small)) { first(); second() }
        } else {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.small), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f)) { first() }
                Box(Modifier.weight(1f)) { second() }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, Modifier.semantics { heading() }, style = MaterialTheme.typography.titleLarge)
}

@Composable
private fun StatusContent(title: String, body: String, action: String, onAction: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(Space.large), contentAlignment = Alignment.Center) {
        ChromePanel {
            SectionTitle(title)
            Text(body)
            Button(onClick = onAction, modifier = Modifier.heightIn(min = LiquidTokens.minTarget)) { Text(action) }
        }
    }
}

@Preview(name = "Dashboard light", showBackground = true, widthDp = 393, heightDp = 852)
@Preview(name = "Dashboard dark", showBackground = true, widthDp = 393, heightDp = 852, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DashboardPreview() {
    FinSyncTheme { Surface { DashboardScreen(DashboardUiState.Content(DemoDashboard.data), false, {}, {}, {}, {}) } }
}

@Preview(name = "Large text", fontScale = 1.5f, widthDp = 393, heightDp = 852)
@Composable
private fun DashboardLargeTextPreview() {
    DashboardPreview()
}

@Preview(name = "Empty")
@Composable
private fun DashboardEmptyPreview() {
    FinSyncTheme { DashboardScreen(DashboardUiState.Empty, false, {}, {}, {}, {}) }
}

@Preview(name = "Error")
@Composable
private fun DashboardErrorPreview() {
    FinSyncTheme { DashboardScreen(DashboardUiState.Error, false, {}, {}, {}, {}) }
}

@Preview(name = "Loading")
@Composable
private fun DashboardLoadingPreview() {
    FinSyncTheme { DashboardScreen(DashboardUiState.Loading, false, {}, {}, {}, {}) }
}

@Preview(name = "Offline")
@Composable
private fun DashboardOfflinePreview() {
    FinSyncTheme { DashboardScreen(DashboardUiState.Content(DemoDashboard.data, isOffline = true), false, {}, {}, {}, {}) }
}
