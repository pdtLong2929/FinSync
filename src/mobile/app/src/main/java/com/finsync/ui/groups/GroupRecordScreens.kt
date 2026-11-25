package com.finsync.ui.groups

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.finsync.domain.*
import com.finsync.ui.*
import com.finsync.ui.components.*
import com.finsync.ui.dashboard.formatVnd
import com.finsync.ui.theme.*
import kotlinx.coroutines.launch

@Composable internal fun MissingGroupRecord(onBack: () -> Unit, saving: Boolean) {
    FeaturePage("Group record", { if (!saving) onBack() }) {
        item { if (saving) LoadingState() else EmptyState("This record is no longer available. Return to the group.") }
    }
}

@Composable
internal fun SharedExpenseDetailsScreen(group: ExpenseGroup, expense: GroupExpense, state: FinanceState,
    onBack: () -> Unit, onEdit: () -> Unit, onBalances: () -> Unit, onRemove: () -> Unit,
    busy: Boolean, error: String?) {
    var confirm by rememberSaveable { mutableStateOf(false) }
    val server = com.finsync.groups.LocalServerGroups.current
    val editable = server == null && canEditExpense(group, expense)
    FeaturePage("Shared expense", { if (!busy) onBack() }) {
        item { GroupStorageNotice() }
        item { LiquidCard {
            CategoryBadge(expense.category)
            Text(expense.title, style = MaterialTheme.typography.titleLarge)
            MoneyText(expense.amount, style = MaterialTheme.typography.headlineMedium)
            Text("Paid by ${group.memberName(expense.payer)} · ${expense.date}")
            Text("${expense.category} · ${expense.splitMode?.name ?: "Recorded amounts"}", style = MaterialTheme.typography.bodySmall)
        } }
        if (server != null) item { Text("Server record · Corrections are not supported by the current API.", style = MaterialTheme.typography.bodySmall) }
        item { Text("Original split", style = MaterialTheme.typography.titleMedium) }
        items(expense.shares.entries.toList(), key = { it.key }) { (id, amount) ->
            LedgerRow(group.memberName(id), if (id == expense.payer) "Payer's own share" else "Participant share", formatVnd(amount))
        }
        item { Text("These are the original shares. Current balances include all expenses and recorded payments.", style = MaterialTheme.typography.bodySmall) }
        if (state.settlements.any { it.groupId == group.id }) item {
            Text("Recorded payments are kept if you edit this expense.", style = MaterialTheme.typography.bodySmall)
        }
        item { MetalOutlineButton(onClick = onBalances) { Text("View group balances") } }
        if (editable) item { Row(horizontalArrangement = Arrangement.spacedBy(Space.small)) {
            MetalButton(onClick = onEdit, enabled = !busy) { Text("Edit expense") }
            TextButton(onClick = { confirm = true }, enabled = !busy) { Text("Remove expense") }
        } }
        item { ErrorText(error); if (busy) LinearProgressIndicator(Modifier.fillMaxWidth()) }
    }
    if (confirm) MetalDialog(onDismissRequest = { confirm = false }, title = { Text("Remove shared expense?") },
        text = { Text("Group balances will be recalculated. Existing payment records are retained. You can Undo this removal.") },
        confirmButton = { TextButton(onClick = { confirm = false; onRemove() }, enabled = !busy) { Text("Confirm removal") } },
        dismissButton = { TextButton(onClick = { confirm = false }) { Text("Cancel") } })
}

@Composable
internal fun PaymentRecordScreen(group: ExpenseGroup, payment: Settlement, onBack: () -> Unit,
    onSave: suspend (String, String) -> String?, onRemove: () -> Unit, busy: Boolean, removeError: String?) {
    val today = LocalGroupToday.current
    var amount by rememberSaveable { mutableStateOf(payment.amount.toString()) }
    var date by rememberSaveable { mutableStateOf(payment.date.toString()) }
    var confirm by rememberSaveable { mutableStateOf(false) }
    var remove by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val editable = canEditPayment(group, payment) && !group.hasFormerParticipant(listOf(payment.from, payment.to))
    val dirty = amount != payment.amount.toString() || date != payment.date.toString()
    FeaturePage("Payment record", { if (!saving && !busy) onBack() }, dirty, primaryAction = if (editable) ({
        ErrorText(error ?: removeError)
        MetalButton(onClick = {
            error = when {
                (parseVnd(amount) ?: 0) <= 0 -> "Enter a positive whole VND amount."
                runCatching { java.time.LocalDate.parse(date) }.getOrNull()?.let { it <= today } != true -> "Enter a valid date, not in the future."
                else -> null
            }
            if (error == null) confirm = true
        }, enabled = !saving && !busy && dirty, modifier = Modifier.fillMaxWidth()) { Text(if (saving) "Saving…" else "Review correction") }
    }) else null) {
        item { GroupStorageNotice() }
        item { LiquidCard {
            Text("${group.memberName(payment.from)} → ${group.memberName(payment.to)}", style = MaterialTheme.typography.titleLarge)
            MoneyText(payment.amount, style = MaterialTheme.typography.headlineMedium)
            Text("Recorded payment · ${payment.date}", style = MaterialTheme.typography.bodySmall)
        } }
        item { Text("Correcting or removing this entry changes the group ledger only. It never moves or refunds money.") }
        if (editable) {
            item { AmountField(amount, { amount = it; error = null }) }
            item { DateField(date, { date = it; error = null }) }
            item { TextButton(onClick = { remove = true }, enabled = !saving && !busy) { Text("Remove payment record") } }
        } else item { Text("This historical record is read-only.") }
        if (saving || busy) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
        if (!editable) item { ErrorText(removeError) }
    }
    if (confirm) MetalDialog(onDismissRequest = { confirm = false }, title = { Text("Correct payment record?") },
        text = { Text("Record ${formatVnd(parseVnd(amount) ?: 0)} on $date. Group balances will update; no money is transferred.") },
        confirmButton = { TextButton(onClick = {
            if (!saving && !busy) { saving = true; confirm = false; scope.launch {
                error = onSave(amount, date)
                if (error != null) saving = false
            } }
        }) { Text("Confirm correction") } },
        dismissButton = { TextButton(onClick = { confirm = false }) { Text("Cancel") } })
    if (remove) MetalDialog(onDismissRequest = { remove = false }, title = { Text("Remove payment record?") },
        text = { Text("This removes a bookkeeping entry and recalculates balances. It does not reverse an actual payment. You can Undo.") },
        confirmButton = { TextButton(onClick = { remove = false; onRemove() }, enabled = !busy) { Text("Confirm removal") } },
        dismissButton = { TextButton(onClick = { remove = false }) { Text("Cancel") } })
}
