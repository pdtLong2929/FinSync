package com.finsync.bank

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import com.finsync.domain.*
import com.finsync.ui.components.*
import com.finsync.ui.dashboard.formatVnd
import com.finsync.ui.theme.Space
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalLayoutApi::class)
@Composable fun BankReviewScreen(candidate: BankCandidate, controller: BankController, onBack: () -> Unit, onHandled: (String) -> Unit) = BankReviewScreen(candidate, controller, null, onBack, onHandled)

@OptIn(ExperimentalLayoutApi::class)
@Composable internal fun BankReviewScreen(candidate: BankCandidate, controller: BankController, initial: BankQuickDraft?, onBack: () -> Unit, onHandled: (String) -> Unit) {
    val state by controller.state.collectAsState()
    val finance by controller.financeState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var amount by rememberSaveable(candidate.id) { mutableStateOf(initial?.amount ?: candidate.amount?.toString().orEmpty()) }
    var kind by rememberSaveable(candidate.id) { mutableStateOf(initial?.kind ?: when (candidate.direction) { BankDirection.Incoming -> BankRecordKind.Income.name; BankDirection.Outgoing -> BankRecordKind.Expense.name; else -> "" }) }
    var walletId by rememberSaveable(candidate.id) { mutableStateOf(initial?.walletId.orEmpty()) }
    var destinationId by rememberSaveable(candidate.id) { mutableStateOf("") }
    var category by rememberSaveable(candidate.id) { mutableStateOf(initial?.purpose.orEmpty()) }
    var date by rememberSaveable(candidate.id) { mutableStateOf(Instant.ofEpochMilli(candidate.observedAt).atZone(ZoneId.systemDefault()).toLocalDate().toString()) }
    var note by rememberSaveable(candidate.id) { mutableStateOf("") }
    var linkedId by rememberSaveable(candidate.id) { mutableStateOf("") }
    var error by rememberSaveable(candidate.id) { mutableStateOf<String?>(null) }
    var confirming by rememberSaveable(candidate.id) { mutableStateOf(false) }
    var ignoring by rememberSaveable(candidate.id) { mutableStateOf(false) }
    var submitting by remember { mutableStateOf(false) }
    val mode = BankRecordKind.entries.find { it.name == kind }
    val review = BankReview(mode ?: BankRecordKind.Expense, amount, walletId, destinationId, category, date, note, linkedId)
    val current = state.data.candidates.find { it.id == candidate.id } ?: candidate
    if (current.status != CaptureStatus.Pending) {
        FeaturePage("Handled bank alert", onBack) {
            item { FinanceCard {
                Text(current.status.name, style = MaterialTheme.typography.titleMedium)
                Text(bankAppName(context, current.sourcePackage))
                Text("This notification is not a bank statement. No new amount will be recorded again.", style = MaterialTheme.typography.bodySmall)
                val entries = finance.entries.filter { it.id in current.entryIds }
                entries.forEach { Text("${it.category} · ${formatVnd(it.amount)} · ${it.date}") }
                if (current.entryIds.isNotEmpty() && entries.isEmpty()) Text("The linked ledger entry was removed. This alert remains handled to avoid recording it twice.")
                if (current.status == CaptureStatus.Ignored) TextButton(onClick = { scope.launch {
                    error = controller.reopenIgnored(current.id)
                    if (error == null) onHandled("Alert returned to Inbox")
                } }) { Text("Return to Inbox") }
                ErrorText(error)
            } }
        }
        return
    }
    FeaturePage("Review bank alert", { if (!submitting) onBack() }, dirty = note.isNotBlank() || walletId.isNotBlank() || amount != candidate.amount?.toString().orEmpty(), primaryAction = {
        ErrorText(error)
        MetalButton(onClick = {
            error = if (mode == null) "Choose what this transaction was for." else runCatching { recordCandidate(current, finance.personalLedger(), review, LocalDate.now()) }
                .exceptionOrNull()?.let { it.message ?: "Check the transaction details." }
            if (error == null) confirming = true
        }, enabled = !submitting && !state.busy, modifier = Modifier.fillMaxWidth()) { Text("Review bookkeeping entry") }
    }) {
        item { FinanceCard {
            Text(bankAppName(context, candidate.sourcePackage), style = MaterialTheme.typography.titleMedium)
            Text("Detected: ${candidate.direction.name} · ${candidate.amount?.let(::formatVnd) ?: "Amount unclear"}")
            Text("Date below is when the alert arrived. Check it against your actual transaction.", style = MaterialTheme.typography.bodySmall)
            if (candidate.possibleDuplicate) ErrorText("Possible duplicate: a similar alert arrived recently. Link an existing entry or ignore this alert if it was already recorded.")
            if (candidate.amount == null || candidate.direction == BankDirection.Unknown) Text("We couldn't confidently read this alert. Check your bank app before entering details.", style = MaterialTheme.typography.bodySmall)
        } }
        item { Text("What was this for?", style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(Space.small)) {
                BankRecordKind.entries.forEach { option -> FilterChip(selected = mode == option, onClick = { if (!submitting) { kind = option.name; category = ""; error = null } }, label = { Text(if (option == BankRecordKind.Link) "Already recorded" else option.name) }) }
            }
            Text("Transfers between your own wallets are not expenses. A group settlement is not recorded automatically.", style = MaterialTheme.typography.bodySmall)
        }
        item { ChromeTextField(amount, { amount = it; error = null }, Modifier.fillMaxWidth(), enabled = !submitting,
            label = { Text("Amount (VND)") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            supportingText = { parseVnd(amount)?.let { Text(formatVnd(it)) } }) }
        val wallets = finance.wallets.filter { !it.archived }
        val labels = wallets.associate { it.id to "${it.name} · ${formatVnd(walletBalance(it, finance.entries))}" }
        item { OptionPicker(if (mode == BankRecordKind.Transfer) "From wallet" else "Wallet", labels[walletId].orEmpty(), labels.values.toList()) { label -> walletId = labels.entries.first { it.value == label }.key; linkedId = ""; error = null }
            Text("These are your app ledger balances, not balances read from the notification.", style = MaterialTheme.typography.bodySmall)
        }
        if (wallets.isEmpty()) item { ErrorText("Create an active wallet in Wallets first.") }
        if (mode == BankRecordKind.Transfer) item { OptionPicker("To wallet", labels[destinationId].orEmpty(), labels.filterKeys { it != walletId }.values.toList()) { label -> destinationId = labels.entries.first { it.value == label }.key; error = null } }
        if (mode == BankRecordKind.Expense || mode == BankRecordKind.Income) item {
            val type = if (mode == BankRecordKind.Expense) EntryType.EXPENSE else EntryType.INCOME
            OptionPicker("Category", category, finance.categories.filter { it.type == type }.map { it.name }) { category = it; error = null }
        }
        if (mode == BankRecordKind.Link) item {
            val eligible = finance.entries.filter { it.walletId == walletId && it.amount == parseVnd(amount) && it.type != EntryType.OPENING }
            val options = eligible.mapIndexed { index, entry -> "${entry.category} · ${entry.date} · ${formatVnd(entry.amount)} · ${index + 1}" }
            OptionPicker("Existing entry", options.getOrNull(eligible.indexOfFirst { it.id == linkedId }).orEmpty(), options) { label ->
                eligible[options.indexOf(label)].let { linkedId = it.id; date = it.date.toString(); error = null }
            }
            if (eligible.isEmpty()) Text("No matching entry in this wallet. Check the amount or wallet, or choose Ignore.", style = MaterialTheme.typography.bodySmall)
        }
        item { DateField(date, { date = it; error = null }); ChromeTextField(note, { note = it; error = null }, Modifier.fillMaxWidth(), label = { Text("Purpose / note (optional)") }, enabled = !submitting) }
        item { TextButton(onClick = { ignoring = true }, enabled = !submitting) { Text("Ignore this alert") } }
    }
    if (confirming) MetalDialog(onDismissRequest = { if (!submitting) confirming = false },
        title = { Text(if (mode == BankRecordKind.Link) "Link without recording again?" else "Confirm bookkeeping entry?") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(Space.small)) {
            Text("${parseVnd(amount)?.let(::formatVnd)} · ${mode?.name.orEmpty()}")
            Text("Wallet: ${finance.wallets.find { it.id == walletId }?.name.orEmpty()}")
            if (mode == BankRecordKind.Transfer) Text("To: ${finance.wallets.find { it.id == destinationId }?.name.orEmpty()}")
            Text("This changes your app ledger only. Your current wallets and transactions will be saved locally; they may include sample data. No money is moved and no bank balance is synchronized.", style = MaterialTheme.typography.bodySmall)
            ErrorText(error)
        } }, confirmButton = { TextButton(onClick = { if (!submitting) {
            submitting = true
            scope.launch {
                error = controller.confirm(candidate.id, review)
                if (error == null) { BankReminder.cancel(context, candidate.id); onHandled(if (mode == BankRecordKind.Link) "Linked · No duplicate entry created" else "Recorded · Wallets and Reports updated") }
                else submitting = false
            }
        } }, enabled = !submitting) { Text(if (submitting) "Saving…" else "Confirm entry") } },
        dismissButton = { TextButton(onClick = { confirming = false }, enabled = !submitting) { Text("Cancel") } })
    if (ignoring) MetalDialog(onDismissRequest = { if (!submitting) ignoring = false }, title = { Text("Ignore bank alert?") },
        text = { Text("No transaction will be created or deleted. The alert remains in History to avoid capturing it twice.") },
        confirmButton = { TextButton(onClick = { if (!submitting) { submitting = true; scope.launch {
            error = controller.ignore(candidate.id)
            if (error == null) { BankReminder.cancel(context, candidate.id); onHandled("Alert ignored · Ledger unchanged") }
            else { submitting = false; ignoring = false }
        } } }, enabled = !submitting) { Text("Confirm ignore") } },
        dismissButton = { TextButton(onClick = { ignoring = false }, enabled = !submitting) { Text("Cancel") } })
}
