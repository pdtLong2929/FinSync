package com.finsync.bank

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.finsync.domain.*
import com.finsync.ui.components.*
import com.finsync.ui.dashboard.formatVnd
import com.finsync.ui.theme.Space
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

internal data class BankQuickDraft(val amount: String, val walletId: String, val purpose: String, val kind: String)

/** Common path: amount → wallet → purpose → Save. Advanced bookkeeping remains available. */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable internal fun BankQuickSheet(candidate: BankCandidate, controller: BankController, onDismiss: () -> Unit,
    onMore: (BankQuickDraft) -> Unit, onSaved: (String) -> Unit) {
    val state by controller.state.collectAsState()
    val finance by controller.financeState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val settings = remember(context) { BankCaptureSettings(context) }
    var defaultWallet by remember(candidate.id, controller.workspace) {
        mutableStateOf(settings.defaultWallet(controller.workspace, candidate.sourcePackage))
    }
    var remembering by remember { mutableStateOf(false) }
    var amount by rememberSaveable(candidate.id) { mutableStateOf(candidate.amount?.toString().orEmpty()) }
    var editingAmount by rememberSaveable(candidate.id) { mutableStateOf(candidate.amount == null) }
    var kind by rememberSaveable(candidate.id) { mutableStateOf(when (candidate.direction) {
        BankDirection.Outgoing -> EntryType.EXPENSE.name; BankDirection.Incoming -> EntryType.INCOME.name; else -> ""
    }) }
    var walletId by rememberSaveable(candidate.id) { mutableStateOf(defaultWallet?.takeIf { id -> finance.wallets.any { it.id == id && !it.archived } }.orEmpty()) }
    var purpose by rememberSaveable(candidate.id) { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }
    var otherPurpose by rememberSaveable(candidate.id) { mutableStateOf(false) }
    val type = EntryType.entries.find { it.name == kind }
    val wallets = finance.wallets.filter { !it.archived }
    val categories = finance.categories.filter { it.type == type }
    val day = Instant.ofEpochMilli(candidate.observedAt).atZone(ZoneId.systemDefault()).toLocalDate()
    val categoryLabels = categories.map { it.name }
    ModalBottomSheet(onDismissRequest = { if (!saving) onDismiss() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = LiquidSurfaceTokens.frost) {
        Column(Modifier.fillMaxWidth().heightIn(max = LocalConfiguration.current.screenHeightDp.dp * .85f)
            .imePadding().verticalScroll(rememberScrollState()).padding(horizontal = Space.large)
            .padding(bottom = Space.large), verticalArrangement = Arrangement.spacedBy(Space.small)) {
            Text("What was this for?", style = MaterialTheme.typography.titleLarge)
            Text("${bankAppName(context, candidate.sourcePackage)} · $day", style = MaterialTheme.typography.bodySmall)
            if (editingAmount) ChromeTextField(amount, { amount = it; error = null }, Modifier.fillMaxWidth(),
                label = { Text("Amount (VND)") }, singleLine = true, enabled = !saving,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            else if (LocalDensity.current.fontScale > com.finsync.ui.theme.LiquidTokens.largeFont) Column {
                Text(parseVnd(amount)?.let(::formatVnd).orEmpty(), style = MaterialTheme.typography.headlineMedium)
                TextButton(onClick = { editingAmount = true }, enabled = !saving) { Text("Edit amount") }
            } else Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(parseVnd(amount)?.let(::formatVnd).orEmpty(), Modifier.weight(1f), style = MaterialTheme.typography.headlineMedium)
                TextButton(onClick = { editingAmount = true }, enabled = !saving) { Text("Edit amount") }
            }
            if (candidate.direction == BankDirection.Unknown) FlowRow(horizontalArrangement = Arrangement.spacedBy(Space.small)) {
                listOf(EntryType.EXPENSE, EntryType.INCOME).forEach { option ->
                    FilterChip(selected = type == option, onClick = { if (!saving) { kind = option.name; purpose = ""; error = null } },
                        label = { Text(if (option == EntryType.EXPENSE) "Money out" else "Money in") })
                }
            } else Text(if (type == EntryType.INCOME) "Money in" else "Money out", style = MaterialTheme.typography.labelMedium)
            OptionPicker("Wallet", wallets.find { it.id == walletId }?.name.orEmpty(), wallets.map { it.name }) { name ->
                if (!saving) { walletId = wallets.first { it.name == name }.id; error = null }
            }
            if (wallets.isEmpty()) Text("Create an active wallet in Wallets first.", style = MaterialTheme.typography.bodySmall)
            if (wallets.any { it.id == walletId }) {
                TextButton(onClick = {
                    val chosen = if (defaultWallet == walletId) null else walletId
                    remembering = true
                    scope.launch {
                        val result = withContext(Dispatchers.IO) { runCatching { settings.setDefaultWallet(controller.workspace, candidate.sourcePackage, chosen) } }
                        if (result.isSuccess) defaultWallet = chosen else error = "Couldn't remember this wallet. Please try again."
                        remembering = false
                    }
                }, enabled = !saving && !remembering) {
                    Text(if (remembering) "Remembering…" else if (defaultWallet == walletId) "Forget default wallet" else "Use this wallet next time")
                }
                if (defaultWallet == walletId) Text("Suggested for this bank app. Change it for another account.", style = MaterialTheme.typography.bodySmall)
            }
            if (type != null) {
                Text("Purpose", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(Space.small), verticalArrangement = Arrangement.spacedBy(Space.small)) {
                    categories.take(4).forEach { category -> FilterChip(selected = purpose == category.name,
                        onClick = { if (!saving) { purpose = category.name; error = null } }, label = { Text(category.name) }) }
                }
                if (otherPurpose)
                    OptionPicker("Other purpose", if (purpose in categoryLabels) purpose else "", categoryLabels) { if (!saving) { purpose = it; error = null } }
                else if (categories.size > 4) TextButton(onClick = { otherPurpose = true }, enabled = !saving) { Text("Other purpose") }
            }
            if (candidate.possibleDuplicate) Text("Similar alert detected. Use More options if already recorded.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            if (state.data.personal == null) Text("Your existing app entries, including samples, are kept. This does not sync your bank balance.", style = MaterialTheme.typography.bodySmall)
            ErrorText(error)
            MetalButton(onClick = {
                if (!saving) {
                    if (type == null) error = "Choose money in or money out."
                    else {
                        val review = BankReview(if (type == EntryType.INCOME) BankRecordKind.Income else BankRecordKind.Expense,
                            amount, walletId, "", purpose, day.toString(), "")
                        error = runCatching { recordCandidate(candidate, finance.personalLedger(), review, LocalDate.now()) }.exceptionOrNull()?.message
                        if (error == null) {
                            saving = true
                            scope.launch {
                                error = controller.confirm(candidate.id, review)
                                if (error == null) { BankReminder.cancel(context, candidate.id); onSaved("Transaction saved") }
                                else saving = false
                            }
                        }
                    }
                }
            }, enabled = !saving && !state.busy, modifier = Modifier.fillMaxWidth()) { Text(if (saving) "Saving…" else "Save transaction") }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = onDismiss, enabled = !saving) { Text("Later") }
                TextButton(onClick = { onMore(BankQuickDraft(amount, walletId, purpose, when (type) { EntryType.EXPENSE -> BankRecordKind.Expense.name; EntryType.INCOME -> BankRecordKind.Income.name; else -> "" })) }, enabled = !saving) { Text("More options") }
            }
        }
    }
}
