package com.finsync.ui.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.finsync.ui.theme.LiquidTokens
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.finsync.R
import com.finsync.data.DemoFixtures
import com.finsync.domain.*
import com.finsync.ui.FinanceState
import com.finsync.ui.components.*
import com.finsync.ui.dashboard.formatVnd
import com.finsync.ui.theme.FinSyncTheme
import com.finsync.ui.theme.LocalFinanceColors
import com.finsync.ui.theme.Space
import com.finsync.ui.theme.MetalTokens
import kotlinx.coroutines.launch
import com.finsync.ui.wallets.EntryRow

@Composable
fun TransactionFormScreen(state: FinanceState, entry: LedgerEntry?, onBack: () -> Unit,
                          onSave: (String, String, EntryType, String, String, String) -> String?,
                          onAddCategory: (String, EntryType) -> String?,
                          onVisual: (String, CategoryIcon, CategoryColor) -> Unit = { _, _, _ -> }) {
    val initialWallet = entry?.walletId ?: state.wallets.firstOrNull { it.id == state.recentWalletId && !it.archived }?.id
        ?: state.wallets.firstOrNull { !it.archived }?.id.orEmpty()
    val initialCategory = entry?.category ?: state.recentExpenseCategory
    var amount by rememberSaveable { mutableStateOf(entry?.amount?.toString() ?: "") }
    var walletId by rememberSaveable { mutableStateOf(initialWallet) }
    var type by rememberSaveable { mutableStateOf(entry?.type?.name ?: EntryType.EXPENSE.name) }
    var category by rememberSaveable { mutableStateOf(initialCategory) }
    var date by rememberSaveable { mutableStateOf(entry?.date?.toString() ?: DemoFixtures.today.toString()) }
    var note by rememberSaveable { mutableStateOf(entry?.note ?: "") }
    var selectingCategory by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var validationRequested by rememberSaveable { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val kind = EntryType.valueOf(type)
    val fieldErrors = if (validationRequested) transactionFieldErrors(amount,
        state.wallets.find { it.id == walletId }, category, kind, date, DemoFixtures.today, state.categories, note)
        else TransactionFieldErrors()
    val save: () -> Unit = {
        if (!saving) {
            validationRequested = true
            val validation = transactionFieldErrors(amount, state.wallets.find { it.id == walletId },
                category, kind, date, DemoFixtures.today, state.categories, note)
            if (validation.first != null) {
                error = null
                scope.launch { listState.scrollToItem(validation.firstFieldIndex + 1) }
            } else {
                // The local write is synchronous. Latch success until navigation disposes this form.
                saving = true
                error = onSave(amount, walletId, kind, category, date, note)
                if (error != null) saving = false
            }
        }
    }
    val dirty = amount != (entry?.amount?.toString() ?: "") || walletId != initialWallet ||
        type != (entry?.type?.name ?: EntryType.EXPENSE.name) || category != initialCategory ||
        date != (entry?.date?.toString() ?: DemoFixtures.today.toString()) || note != (entry?.note ?: "")
    if (selectingCategory) {
        CategorySelectionScreen(state.categories.filter { it.type == kind }, { selectingCategory = false },
            { category = it; selectingCategory = false; error = null }, { onAddCategory(it, kind) }, onVisual,
            selectedCategory = category)
        return
    }
    FeaturePage(if (entry != null) "Edit transaction" else if (kind == EntryType.INCOME) "Add income" else "Add expense", onBack, dirty, listState = listState, primaryAction = {
        if (fieldErrors.first != null) ErrorText("Check the highlighted fields.")
        ErrorText(error)
        MetalButton(onClick = save, modifier = Modifier.fillMaxWidth(), enabled = !saving) {
            if (saving) {
                CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(Space.small))
                Text("Saving…")
            } else Text("Save transaction")
        }
    }) {
        item { TransactionTypeToggle(kind) { selected ->
            type = selected.name
            category = if (selected == EntryType.EXPENSE) "Food" else "Salary"
            error = null
        } }
        item { TransactionAmountField(amount, { amount = it; error = null }, fieldErrors.amount) }
        item { WalletSelector(walletId, initialWallet, state.wallets.filterNot { it.archived }, state.entries,
            onSelect = { walletId = it; error = null })
            ErrorText(fieldErrors.wallet)
            if (state.wallets.none { !it.archived }) Text("Create or restore a wallet in Wallets before adding a transaction.") }
        item { Text("Category", style = MaterialTheme.typography.labelLarge)
            QuickCategoryGrid(state.categories.filter { it.type == kind }, category) { category = it; error = null }
            TextButton(onClick = { selectingCategory = true }) { Text("All categories") }
            ErrorText(fieldErrors.category) }
        item { DateField(date, { date = it; error = null }, errorMessage = fieldErrors.date) }
        item { ChromeTextField(note, { note = it; error = null }, Modifier.fillMaxWidth(), label = { Text("Notes (optional)") }, minLines = 1,
            isError = fieldErrors.note != null,
            supportingText = { ErrorText(fieldErrors.note); if (note.length > 240) Text("${note.length}/300") }) }
    }
}

@Composable
private fun TransactionTypeToggle(type: EntryType, onSelect: (EntryType) -> Unit) {
    val options = listOf("−  Expense", "+  Income")
    LiquidMetalSelector(options, if (type == EntryType.EXPENSE) options[0] else options[1]) {
        onSelect(if (it == options[0]) EntryType.EXPENSE else EntryType.INCOME)
    }
}

@Composable
private fun TransactionAmountField(value: String, onValue: (String) -> Unit, error: String?) {
    LiquidCard(Modifier.fillMaxWidth()) {
        TextField(
            value = value,
            onValueChange = { typed -> onValue(typed.filter { it in '0'..'9' }.take(12)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Amount (VND)") },
            placeholder = { Text("0", style = MaterialTheme.typography.headlineLarge) },
            singleLine = true,
            isError = error != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = MaterialTheme.typography.headlineLarge.copy(fontFeatureSettings = "tnum"),
            shape = LiquidTokens.rowShape,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = LiquidTokens.pearl,
                unfocusedContainerColor = LiquidTokens.pearl,
                errorContainerColor = LiquidTokens.pearl,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent),
            visualTransformation = VndGroupingTransformation,
        )
        if (value.isNotEmpty()) TextButton(onClick = { onValue("") }) { Text("Clear") }
        ErrorText(error)
    }
}

private object VndGroupingTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        val offsets = IntArray(raw.length + 1)
        val display = StringBuilder(raw.length + raw.length / 3)
        raw.forEachIndexed { index, digit ->
            offsets[index] = display.length
            if (index > 0 && (raw.length - index) % 3 == 0) display.append('.')
            display.append(digit)
        }
        offsets[raw.length] = display.length
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = offsets[offset.coerceIn(0, raw.length)]
            override fun transformedToOriginal(offset: Int): Int {
                val bounded = offset.coerceIn(0, display.length)
                return offsets.indexOfLast { it <= bounded }.coerceAtLeast(0)
            }
        }
        return TransformedText(AnnotatedString(display.toString()), mapping)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WalletSelector(
    walletId: String,
    defaultWalletId: String,
    wallets: List<Wallet>,
    entries: List<LedgerEntry>,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedWallet = wallets.find { it.id == walletId }
    Column {
        Text("Wallet", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(bottom = Space.small))
        MetalOutlineButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth(), enabled = wallets.isNotEmpty()) {
            if (selectedWallet == null) Text("Choose a wallet") else {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Space.small)) {
                    Icon(painterResource(R.drawable.ic_wallet), contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                        Text(selectedWallet.name, style = MaterialTheme.typography.titleMedium)
                        Text("${selectedWallet.type.label} · ${formatVnd(walletBalance(selectedWallet, entries))}" +
                            if (selectedWallet.id == defaultWalletId) " · Default" else "",
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        if (expanded) ModalBottomSheet(onDismissRequest = { expanded = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = LiquidSurfaceTokens.frost) {
            LazyColumn(Modifier.fillMaxWidth().fillMaxHeight(.7f).liquidAtmosphere(),
                contentPadding = PaddingValues(Space.medium), verticalArrangement = Arrangement.spacedBy(Space.small)) {
                item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("Choose a wallet", style = MaterialTheme.typography.titleLarge)
                    TextButton(onClick = { expanded = false }) { Text("Close") }
                } }
                items(wallets, key = { it.id }) { wallet ->
                    Surface(onClick = { onSelect(wallet.id); expanded = false },
                        modifier=Modifier.liquidGlass(LiquidTokens.rowShape,2.dp,if(wallet.id==walletId) LiquidTokens.lilacTile else LiquidSurfaceTokens.frost),
                        shape = LiquidTokens.rowShape,
                        color = Color.Transparent) {
                        Row(Modifier.fillMaxWidth().padding(Space.medium), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Space.medium)) {
                            Icon(painterResource(R.drawable.ic_wallet), null, tint = LiquidTokens.violet)
                            Column(Modifier.weight(1f)) {
                                Text(wallet.name, style = MaterialTheme.typography.titleMedium)
                                Text("${wallet.type.label} · ${formatVnd(walletBalance(wallet, entries))}", style = MaterialTheme.typography.bodySmall)
                                if (wallet.id == defaultWalletId) Text("Default", style = MaterialTheme.typography.labelSmall)
                            }
                            if (wallet.id == walletId) Text("Selected", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun CategorySelectionScreen(categories: List<Category>, onBack: () -> Unit, onSelect: (String) -> Unit,
                            onAdd: (String) -> String?, onVisual: (String, CategoryIcon, CategoryColor) -> Unit = { _, _, _ -> },
                            selectedCategory: String = "") {
    var search by rememberSaveable { mutableStateOf("") }
    var icon by rememberSaveable { mutableStateOf(CategoryIcon.Other.name) }
    var color by rememberSaveable { mutableStateOf(CategoryColor.Lavender.name) }
    var editing by rememberSaveable { mutableStateOf<String?>(null) }
    var newName by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    FeaturePage("Choose category", onBack, newName.isNotBlank()) {
        item { ChromeTextField(search, { search = it }, Modifier.fillMaxWidth(), label = { Text("Search categories") }) }
        val filtered = categories.filter { it.name.contains(search, true) }
        if (filtered.isEmpty()) item { Text("No matching categories. You can create one below.") }
        if (filtered.isNotEmpty()) item {
            FlowRow(maxItemsInEachRow = if(LocalDensity.current.fontScale > LiquidTokens.largeFont) 2 else 3, horizontalArrangement = Arrangement.spacedBy(Space.small),
                verticalArrangement = Arrangement.spacedBy(Space.small)) {
                filtered.forEach { category ->
                    val isSelected = category.name == selectedCategory
                    Surface(
                        modifier = Modifier.weight(1f).heightIn(min = MetalTokens.badge + Space.large)
                            .liquidGlass(MaterialTheme.shapes.large,2.dp)
                            .border(if (isSelected) 2.dp else 1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                MaterialTheme.shapes.large)
                            .clickable(role = Role.RadioButton, onClick = { onSelect(category.name) })
                            .semantics { selected = isSelected },
                        shape = MaterialTheme.shapes.large,
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                    ) {
                        Column(Modifier.fillMaxWidth().padding(Space.small), horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(Space.small)) {
                            CategoryBadge(category.name, category.id)
                            Text(category.name, style = MaterialTheme.typography.labelMedium, maxLines = 2,
                                minLines = 2, textAlign = TextAlign.Center)
                            if (isSelected) Text("Selected", style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
        filtered.find { it.name == selectedCategory }?.let { selected ->
            item("style-selected-category") {
                TextButton(onClick = { editing = selected.name; icon = selected.icon.name; color = selected.color.name }) {
                    Text("Style ${selected.name}")
                }
            }
        }
        item { ChromeTextField(newName, { newName = it }, Modifier.fillMaxWidth(), label = { Text("New category name") }) }
        item { Text("Category style · saved for this sample session only")
            Text(editing?.let { "Editing $it" } ?: "Style for new category")
            OptionPicker("Icon", icon, CategoryIcon.entries.map { it.name }, { icon = it })
            OptionPicker("Pastel color", color, CategoryColor.entries.map { it.name }, { color = it })
            CompositionLocalProvider(LocalCategories provides listOf(Category("Preview", EntryType.EXPENSE,
                id = "preview", icon = CategoryIcon.valueOf(icon), color = CategoryColor.valueOf(color)))) {
                CategoryBadge("Preview", "preview")
            }
            editing?.let { name -> MetalOutlineButton(onClick = { onVisual(name, CategoryIcon.valueOf(icon), CategoryColor.valueOf(color)); editing = null }) { Text("Apply category style") } }
        }
        item { ErrorText(error) }
        item { MetalButton(onClick = { error = onAdd(newName); if (error == null) { onVisual(newName.trim(), CategoryIcon.valueOf(icon), CategoryColor.valueOf(color)); onSelect(newName.trim()) } }) { Text("Create category") } }
    }
}

@Composable
fun TransactionHistoryScreen(state: FinanceState, hidden: Boolean, onPrivacy: () -> Unit, onBack: () -> Unit,
                             onOpen: (String) -> Unit, onCreate: () -> Unit,
                             mode: LoadState = LoadState.Ready, onRetry: () -> Unit = {}) {
    var search by rememberSaveable { mutableStateOf("") }
    var filters by rememberSaveable(stateSaver = TransactionFilterState.Saver) { mutableStateOf(TransactionFilterState()) }
    var showFilters by rememberSaveable { mutableStateOf(false) }
    val entries = remember(state.entries, state.wallets, search, filters) {
        filterTransactions(state.entries, state.wallets, search, filters, DemoFixtures.today)
    }
    val hasActiveCriteria = search.isNotBlank() || filters.activeCount > 0
    FeaturePage("Personal transactions", onBack, compact = true) {
        item { FlowRow(horizontalArrangement = Arrangement.spacedBy(Space.small)) {
            MetalButton(onClick = onCreate) { Text("Add transaction") }
            MetalOutlineButton(onClick = { showFilters = true }) {
                Icon(painterResource(R.drawable.ic_filter),null,Modifier.size(18.dp))
                Spacer(Modifier.width(Space.small))
                Text(if (filters.activeCount == 0) "Filters" else "Filters · ${filters.activeCount} active")
            }
            FilledIconButton(onClick = onPrivacy) {
                Icon(painterResource(R.drawable.ic_visibility), if (hidden) "Show amounts" else "Hide amounts")
            }
        } }
        item { ChromeTextField(search, { search = it }, Modifier.fillMaxWidth(), label = { Text("Search transactions") }, singleLine = true) }
        if (mode != LoadState.Ready || entries.isEmpty()) item {
            StateNotice(mode, entries.isEmpty(),
                if (hasActiveCriteria) "No transactions match these filters. Try changing or resetting the filters."
                else "No personal transactions yet. Add a transaction to see it here.", onRetry) {}
        }
        if (mode in listOf(LoadState.Ready, LoadState.Offline)) entries.groupBy { it.date }.forEach { (date, dayEntries) ->
            item("date-$date") { SectionHeading(date.toString()) }
            items(dayEntries, key = { it.id }) { entry ->
                TransactionHistoryRow(entry, state.wallets.find { it.id == entry.walletId }?.name ?: "Unavailable wallet",
                    hidden, { onOpen(entry.id) })
            }
        }
    }
    if (showFilters) TransactionFilterSheet(filters, state.categories, state.wallets,
        onApply = { filters = it }, onDismiss = { showFilters = false })
}

@Composable
private fun TransactionHistoryRow(entry: LedgerEntry, walletName: String, hidden: Boolean, onOpen: () -> Unit) {
    val isIncome = entry.type == EntryType.INCOME
    val amount = when {
        hidden -> "Hidden"
        isIncome -> "+${formatVnd(entry.amount)}"
        else -> "−${formatVnd(entry.amount)}"
    }
    val tone = if (isIncome) LocalFinanceColors.current.income else LocalFinanceColors.current.expense
    Surface(onClick = onOpen, modifier=Modifier.liquidGlass(MaterialTheme.shapes.large,1.dp),shape = MaterialTheme.shapes.large, color = Color.Transparent) {
        BoxWithConstraints(Modifier.fillMaxWidth().padding(Space.small)) {
            val stack = maxWidth < com.finsync.ui.theme.LiquidTokens.transactionCompact || LocalDensity.current.fontScale > com.finsync.ui.theme.LiquidTokens.largeFont
            val amountWidth = maxWidth / 2
            Row(horizontalArrangement = Arrangement.spacedBy(Space.medium), verticalAlignment = Alignment.CenterVertically) {
                CategoryBadge(entry.category, entry.categoryId)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Space.small)) {
                    Text(entry.note.ifBlank { entry.category }, style = MaterialTheme.typography.titleMedium)
                    Text("${entry.category} · $walletName", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (stack) Text(amount, style = MaterialTheme.typography.titleMedium, color = tone)
                }
                if (!stack) Text(amount, Modifier.widthIn(max = amountWidth), style = MaterialTheme.typography.titleMedium, color = tone)
            }
        }
    }
}

@Composable
fun TransactionDetailsScreen(entry: LedgerEntry, wallet: Wallet?, hidden: Boolean, onBack: () -> Unit,
                             onEdit: () -> Unit, onDelete: () -> Unit) {
    var confirmDelete by remember { mutableStateOf(false) }
    FeaturePage("Transaction details", onBack) {
        item { LiquidMetalSurface(Modifier.fillMaxWidth(), refined = true) {
            CategoryBadge(entry.category, entry.categoryId)
            Text(if (entry.type == EntryType.INCOME) "Personal income" else "Personal expense")
            Text(if (hidden) "Hidden" else formatVnd(entry.amount), style = MaterialTheme.typography.headlineMedium)
        } }
        item { LedgerRow("Wallet", wallet?.name ?: "Unavailable wallet", "Personal ledger") }
        item { LedgerRow(entry.category, entry.date.toString(), entry.note.ifBlank { "No notes" }, category = entry.category, categoryId = entry.categoryId) }
        item { MetalButton(onClick = onEdit) { Text("Edit transaction") } }
        item { TextButton(onClick = { confirmDelete = true }) { Text("Delete transaction") } }
    }
    if (confirmDelete) MetalDialog(onDismissRequest = { confirmDelete = false }, title = { Text("Delete this transaction?") },
        text = { Text("The wallet balance will be recalculated. You can undo using the message shown after deletion.") },
        confirmButton = { TextButton(onClick = { confirmDelete = false; onDelete() }) { Text("Delete") } },
        dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancel") } })
}

@Preview(name = "Transaction form", showBackground = true)
@Composable private fun TransactionFormPreview() { FinSyncTheme {
    TransactionFormScreen(FinanceState(), null, {}, { _, _, _, _, _, _ -> null }, { _, _ -> null })
} }
@Preview(name = "History", showBackground = true)
@Composable private fun HistoryPreview() { FinSyncTheme { TransactionHistoryScreen(FinanceState(), false, {}, {}, {}, {}) } }
