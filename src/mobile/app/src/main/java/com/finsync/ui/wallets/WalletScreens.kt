package com.finsync.ui.wallets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Brush
import com.finsync.ui.theme.DockTokens
import androidx.compose.foundation.clickable
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.finsync.domain.*
import com.finsync.ui.FinanceState
import com.finsync.ui.components.*
import com.finsync.ui.dashboard.formatVnd
import com.finsync.ui.theme.FinSyncTheme
import com.finsync.ui.theme.Space
import com.finsync.ui.theme.LiquidTokens
import com.finsync.ui.theme.LocalFinanceColors
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import com.finsync.R
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WalletListScreen(state: FinanceState, hidden: Boolean, onPrivacy: () -> Unit, onOpen: (String) -> Unit,
                     onCreate: () -> Unit, mode: LoadState = LoadState.Ready, onRetry: () -> Unit = {}, onHistory: () -> Unit = {}, onTransfer: () -> Unit = {}, onTransferHistory: () -> Unit = {}) {
    var search by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf("Active") }
    var searchExpanded by rememberSaveable { mutableStateOf(false) }
    val wallets = state.wallets.filter { it.name.contains(search, true) && (filter == "All" || !it.archived) }
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    var returnWalletId by rememberSaveable { mutableStateOf<String?>(null) }
    var returnOffset by rememberSaveable { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        val index = wallets.indexOfFirst { it.id == returnWalletId }
        if(index >= 0) listState.requestScrollToItem(index + 2 + if(searchExpanded || state.wallets.size > 4) 1 else 0, returnOffset)
        returnWalletId = null
    }
    FeaturePage("Wallets", compact = true, listState = listState, headerAction = {
        IconButton(onClick = onCreate, modifier=Modifier.liquidGlass(MaterialTheme.shapes.medium,4.dp)) { Icon(painterResource(R.drawable.ic_add), "Create wallet") }
    }) {
        item { LiquidMetalSurface(Modifier.fillMaxWidth().heightIn(min = com.finsync.ui.theme.SoftFinanceTokens.walletHeroHeight), refined = true) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.small)) {
                Icon(painterResource(R.drawable.ic_wallet), null, tint = LiquidTokens.violet)
                Column(Modifier.weight(1f)) {
                    Text("Personal net worth", style = MaterialTheme.typography.labelSmall)
                    Text(if(hidden) "Hidden" else formatVnd(personalNetWorth(state.wallets, state.entries)), style = MaterialTheme.typography.headlineSmall)
                }
                IconButton(modifier = Modifier.clip(androidx.compose.foundation.shape.CircleShape).border(DockTokens.edge, LiquidTokens.pearl, androidx.compose.foundation.shape.CircleShape).pastelMetal(LiquidTokens.pearl), onClick = onPrivacy) { Icon(painterResource(R.drawable.ic_visibility), if(hidden) "Show amounts" else "Hide amounts") }
            }
        } }
        item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            ChoiceChips(listOf("Active", "All"), filter, { filter = it })
            if(state.wallets.size <= 4) TextButton(onClick = {
                if (searchExpanded) search = ""
                searchExpanded = !searchExpanded
            }) {
                Text(if(searchExpanded) "Close search" else "Search wallets")
            }
        } }
        if(searchExpanded || state.wallets.size > 4) item {
            ChromeTextField(search, { search = it }, Modifier.fillMaxWidth(), label = { Text("Search wallets") }, singleLine = true)
        }
        if(mode != LoadState.Ready || wallets.isEmpty()) item { StateNotice(mode, wallets.isEmpty(), "No wallets found. Create a wallet or change your search.", onRetry) {} }
        if (mode in listOf(LoadState.Ready, LoadState.Offline)) items(wallets, key = { it.id }) { wallet ->
            WalletProductCard(wallet, if (hidden) "Hidden" else formatVnd(walletBalance(wallet, state.entries)), {
                returnWalletId = wallet.id
                returnOffset = -(listState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == wallet.id }?.offset ?: 0)
                onOpen(wallet.id)
            })
        }
        item { Text("Quick actions", style = MaterialTheme.typography.titleMedium) }
        item { Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(Space.small)) {
            val labels = listOf("Transfer between wallets", "Create wallet", "Manage wallets")
            val symbols = listOf("↔", "+", "✎")
            val actions = listOf(onTransfer, onCreate, { state.wallets.firstOrNull()?.let { onOpen(it.id) }; Unit })
            labels.indices.forEach { index ->
                TextButton(onClick = actions[index], enabled = index != 2 || state.wallets.isNotEmpty(),
                    modifier = Modifier.weight(1f).fillMaxHeight().heightIn(min = LiquidTokens.tileHeight).liquidGlass(LiquidTokens.rowShape,4.dp),
                    shape = LiquidTokens.rowShape,
                    colors = ButtonDefaults.textButtonColors(containerColor = androidx.compose.ui.graphics.Color.Transparent, contentColor = LiquidTokens.violet)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Space.small)) {
                        Text(symbols[index], style = MaterialTheme.typography.titleLarge)
                        Text(labels[index], style = MaterialTheme.typography.labelSmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
            }
        } }
        item { FlowRow(horizontalArrangement = Arrangement.spacedBy(Space.small)) {
            TextButton(onClick = onHistory) { Text("All personal transactions") }
            TextButton(onClick = onTransferHistory) { Text("Transfer history") }
        } }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WalletDetailsScreen(wallet: Wallet, entries: List<LedgerEntry>, hidden: Boolean, onPrivacy: () -> Unit,
                        onBack: () -> Unit, onEdit: () -> Unit, onHistory: () -> Unit, onArchive: () -> Unit) {
    var confirmArchive by remember { mutableStateOf(false) }
    FeaturePage(wallet.name, onBack) {
        item { LiquidMetalSurface(Modifier.fillMaxWidth(), refined = true) {
            Icon(painterResource(R.drawable.ic_wallet), null, tint = LiquidTokens.violet)
            Text("${wallet.type.label} · VND")
            Text(if (wallet.type == WalletType.CREDIT_CARD) "Outstanding debt" else "Wallet balance")
            Text(if (hidden) "Hidden" else formatVnd(walletBalance(wallet, entries)), style = MaterialTheme.typography.headlineMedium)
            PrivacyButton(hidden, onPrivacy)
        } }
        item { FlowRow(horizontalArrangement = Arrangement.spacedBy(Space.small), verticalArrangement = Arrangement.spacedBy(Space.small)) {
            MetalOutlineButton(onClick = onEdit) { Text("Edit wallet") }
            TextButton(onClick = { confirmArchive = true }) { Text(if (wallet.archived) "Restore" else "Archive") }
        } }
        item { SectionHeading("Recent wallet activity") }
        val recent = entries.filter { it.walletId == wallet.id }.sortedByDescending { it.date }.take(4)
        if (recent.isEmpty()) item { Text("No activity yet.") }
        items(recent, key = { it.id }) { EntryRow(it, hidden) }
        item { TextButton(onClick = onHistory) { Text("View wallet history") } }
    }
    if (confirmArchive) MetalDialog(onDismissRequest = { confirmArchive = false }, title = { Text(if (wallet.archived) "Restore wallet?" else "Archive wallet?") },
        text = { Text(if (wallet.archived)
            "History and net worth are preserved. This wallet will be available for new entries again."
            else "History and net worth are preserved. Archived wallets are unavailable for new entries.") },
        confirmButton = { TextButton(onClick = { confirmArchive = false; onArchive() }) { Text("Confirm") } },
        dismissButton = { TextButton(onClick = { confirmArchive = false }) { Text("Cancel") } })
}

@Composable
fun WalletFormScreen(wallet: Wallet?, onBack: () -> Unit, onSave: (String, WalletType, String) -> String?) {
    var name by rememberSaveable { mutableStateOf(wallet?.name ?: "") }
    var type by rememberSaveable { mutableStateOf(wallet?.type?.name ?: WalletType.CASH.name) }
    var opening by rememberSaveable { mutableStateOf("0") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    val dirty = name != (wallet?.name ?: "") || opening != "0" || type != (wallet?.type?.name ?: WalletType.CASH.name)
    FeaturePage(if (wallet == null) "Create wallet" else "Edit wallet", onBack, dirty, primaryAction = {
        ErrorText(error)
        MetalButton(onClick = { error = onSave(name, WalletType.valueOf(type), opening) }, modifier = Modifier.fillMaxWidth()) { Text("Save wallet") }
    }) {
        item { DemoNotice() }
        item { ChromeTextField(name, { name = it; error = null }, Modifier.fillMaxWidth(), label = { Text("Wallet name") }, singleLine = true) }
        item { Text("Wallet type", style = MaterialTheme.typography.titleMedium) }
        item { if (wallet == null) ChoiceChips(WalletType.entries.map { it.label }, WalletType.valueOf(type).label,
            { label -> type = WalletType.entries.first { it.label == label }.name }) else Text(wallet.type.label) }
        if (wallet == null) item { AmountField(opening, { opening = it; error = null }, if (type == WalletType.CREDIT_CARD.name) "Opening debt (VND)" else "Opening balance (VND)") }
        item { Text("VND · Manual bookkeeping only. No bank connection.", style = MaterialTheme.typography.bodyMedium) }
    }
}

@Composable
fun WalletHistoryScreen(wallet: Wallet, entries: List<LedgerEntry>, hidden: Boolean, onBack: () -> Unit, onOpen: (String) -> Unit = {}) {
    var search by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf("All") }
    val filtered = entries.filter { it.walletId == wallet.id &&
        (filter == "All" || it.type.name.equals(filter, true)) && (it.category + it.note).contains(search, true) }.sortedByDescending { it.date }
    FeaturePage("${wallet.name} history", onBack) {
        item { DemoNotice() }
        item { ChromeTextField(search, { search = it }, Modifier.fillMaxWidth(), label = { Text("Search activity") }) }
        item { ChoiceChips(listOf("All", "Income", "Expense", "Opening"), filter, { filter = it }) }
        if (filtered.isEmpty()) item { Text("No transactions match these filters.") }
        items(filtered, key = { it.id }) { EntryRow(it, hidden, if (!it.type.isTransaction) null else ({ onOpen(it.id) })) }
    }
}

@Composable
fun EntryRow(entry: LedgerEntry, hidden: Boolean, onClick: (() -> Unit)? = null) {
    val direction = when (entry.type) { EntryType.INCOME -> "Income +"; EntryType.EXPENSE -> "Expense −"; EntryType.OPENING -> "Opening "; EntryType.TRANSFER_IN -> "Transfer in +"; EntryType.TRANSFER_OUT -> "Transfer out -" }
    LedgerRow(entry.note.ifBlank { entry.category }, "${entry.category} · ${entry.date}",
        if (hidden) "Hidden" else direction + formatVnd(entry.amount), onClick,
        category = entry.category, categoryId = entry.categoryId, tone = when (entry.type) {
            EntryType.INCOME -> LocalFinanceColors.current.income
            EntryType.EXPENSE -> LocalFinanceColors.current.expense
            EntryType.OPENING, EntryType.TRANSFER_IN, EntryType.TRANSFER_OUT -> MaterialTheme.colorScheme.primary
        })
}

@Composable
private fun WalletProductCard(wallet: Wallet, amount: String, onOpen: () -> Unit) {
    val tint = when (wallet.type) {
        WalletType.CASH -> LiquidTokens.cyanTile
        WalletType.BANK_ACCOUNT -> LiquidTokens.lilacTile
        WalletType.CREDIT_CARD -> LiquidTokens.pinkTile
        WalletType.E_WALLET -> LiquidTokens.mistBlue
    }
    Surface(Modifier.fillMaxWidth().liquidGlass(MaterialTheme.shapes.large,4.dp,tint).clickable(role = Role.Button, onClick = onOpen),
        color = androidx.compose.ui.graphics.Color.Transparent, contentColor = MaterialTheme.colorScheme.onSurface,
        shape = MaterialTheme.shapes.large) {
        BoxWithConstraints(Modifier.fillMaxWidth().padding(Space.medium)) {
            val stacked = androidx.compose.ui.platform.LocalDensity.current.fontScale > LiquidTokens.largeFont || maxWidth < LiquidTokens.transactionCompact
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.small)) {
                Surface(shape = MaterialTheme.shapes.small, color = tint) {
                    Icon(painterResource(R.drawable.ic_wallet), null, Modifier.padding(Space.small), tint = if(wallet.type == WalletType.CASH || wallet.type == WalletType.E_WALLET) LiquidTokens.incomeLight else LiquidTokens.violet)
                }
                Column(Modifier.weight(1f)) {
                    Text(wallet.name, style = MaterialTheme.typography.titleSmall)
                    Text(wallet.type.label + if(wallet.archived) " · Archived" else " · VND", style = MaterialTheme.typography.labelSmall)
                    if(wallet.type == WalletType.CREDIT_CARD) Text("Outstanding debt", style = MaterialTheme.typography.labelSmall)
                    if(stacked) Text(amount, style = MaterialTheme.typography.titleMedium)
                }
                if(!stacked) Text(amount, style = MaterialTheme.typography.titleMedium)
                Text("›", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Preview(name = "Wallet list", showBackground = true)
@Composable private fun WalletPreview() { FinSyncTheme { WalletListScreen(FinanceState(), false, {}, {}, {}) } }
@Preview(name = "Create wallet", showBackground = true)
@Composable private fun WalletFormPreview() { FinSyncTheme { WalletFormScreen(null, {}) { _, _, _ -> null } } }
@Preview(name = "Wallet offline", showBackground = true)
@Composable private fun WalletOfflinePreview() { FinSyncTheme { WalletListScreen(FinanceState(), false, {}, {}, {}, LoadState.Offline) } }
