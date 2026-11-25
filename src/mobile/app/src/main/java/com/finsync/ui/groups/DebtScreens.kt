package com.finsync.ui.groups

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.finsync.domain.*
import com.finsync.ui.FinanceState
import com.finsync.ui.components.*
import com.finsync.ui.dashboard.formatVnd
import com.finsync.ui.theme.LocalFinanceColors
import com.finsync.ui.theme.Space
import kotlinx.coroutines.launch

@Composable
fun DebtScreen(group: ExpenseGroup, state: FinanceState, onBack: () -> Unit, onOpenDebt: (Debt) -> Unit, onSection: ((String) -> Unit)? = null, onPayment: ((Settlement) -> Unit)? = null, onRecord: ((Debt) -> Unit)? = null) {
    val debts = currentGroupDebts(group, state)
    var explain by rememberSaveable { mutableStateOf(false) }
    FeaturePage("Who owes whom", onBack) {
        item { GroupStorageNotice(); Text("Suggested transfers between group members") }
        if (onSection != null) item { GroupSectionTabs("Balances", onSection) }
        item { TextButton(onClick = { explain = !explain }) { Text(if (explain) "Hide explanation" else "How debt suggestions work") } }
        if (explain) item { FinanceCard {
            Text("Fewer transfers, same balances", style = MaterialTheme.typography.titleMedium)
            Text("These suggestions combine all group expenses and payments. You may repay someone who did not pay your original bill. Each person's total stays the same.", style = MaterialTheme.typography.bodySmall)
        } }
        item { FinanceCard(emphasized = true) {
            Text("Your group balance", style = MaterialTheme.typography.titleLarge)
            Text("You owe: ${formatVnd(debts.filter { it.from == "me" }.sumOf { it.amount })}", color = LocalFinanceColors.current.expense)
            Text("Owed to you: ${formatVnd(debts.filter { it.to == "me" }.sumOf { it.amount })}", color = LocalFinanceColors.current.income)
        } }
        if (debts.isEmpty()) item { FinanceCard(emphasized = true) { Text("All settled", style = MaterialTheme.typography.headlineMedium); Text("No outstanding group debts.") } }
        items(debts) { debt ->
            FinanceCard {
                Row(horizontalArrangement = Arrangement.spacedBy(Space.small), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    MemberAvatar(group.memberName(debt.from))
                    Text("→", style = MaterialTheme.typography.titleLarge)
                    MemberAvatar(group.memberName(debt.to))
                }
                Text("Outstanding · Not settled")
                Text("${group.memberName(debt.from)} owes ${group.memberName(debt.to)}", style = MaterialTheme.typography.titleMedium)
                Text(formatVnd(debt.amount), style = MaterialTheme.typography.headlineSmall)
                if (onRecord != null) {
                    MetalButton(onClick = { onRecord(debt) }) { Text("Record payment") }
                    TextButton(onClick = { onOpenDebt(debt) }) { Text("View debt details") }
                } else MetalButton(onClick = { onOpenDebt(debt) }) { Text("View debt details") }
            }
        }
        item { Text("Pay outside FinSync, then record the completed transfer here. No money is sent by this app.") }
        val confirmed = state.settlements.filter { it.groupId == group.id }.asReversed()
        item { SectionHeading("Confirmed records") }
        if (confirmed.isEmpty()) item { Text("No confirmed settlement records yet.") }
        items(confirmed, key = { it.id }) { settlement ->
            LedgerRow("${group.memberName(settlement.from)} → ${group.memberName(settlement.to)}",
                "Confirmed record · ${settlement.date} · Paid outside FinSync", formatVnd(settlement.amount),
                onClick = onPayment?.let { open -> { open(settlement) } })
        }
    }
}
@Composable
fun DebtDetailsScreen(group: ExpenseGroup, state: FinanceState, from: String, to: String,
                      onBack: () -> Unit, onMarkSettled: () -> Unit, onPayment: ((Settlement) -> Unit)? = null) {
    val debts = currentGroupDebts(group, state)
    val debt = debts.find { it.from == from && it.to == to }
    val suggestionChanged = debt == null && debts.isNotEmpty()
    val history = state.settlements.filter { it.groupId == group.id && it.from == from && it.to == to }
        .sortedByDescending { it.date }
    FeaturePage("Debt details", onBack, primaryAction = if (debt != null) ({
        MetalButton(onClick = onMarkSettled, modifier = Modifier.fillMaxWidth()) { Text("Mark as settled") }
    }) else null) {
        item { GroupStorageNotice(); Text("${group.memberName(from)} → ${group.memberName(to)}", style = MaterialTheme.typography.titleLarge) }
        item { FinanceCard(emphasized = debt != null) {
            Text(when {
                suggestionChanged -> "Transfer suggestion updated"
                debt == null -> "All settled"
                else -> "Outstanding · Not settled"
            }, style = MaterialTheme.typography.titleMedium)
            if (suggestionChanged) {
                Text("Group balances changed. Check the current debts before recording another payment.")
                MetalOutlineButton(onClick = onBack) { Text("View current debts") }
            } else Text(formatVnd(debt?.amount ?: 0L), style = MaterialTheme.typography.headlineMedium,
                color = if (debt == null) MaterialTheme.colorScheme.primary else LocalFinanceColors.current.expense)
            Text("Payments are made outside FinSync.")
        } }
        item { SectionHeading("Settlement history") }
        if (history.isEmpty()) item { Text("No payments recorded for this debt yet.") }
        items(history, key = { it.id }) { settlement ->
            LedgerRow("${group.memberName(settlement.from)} paid ${group.memberName(settlement.to)}",
                "Confirmed · ${settlement.date} · Recorded only", formatVnd(settlement.amount),
                onClick = onPayment?.let { open -> { open(settlement) } })
        }
    }
}
@Composable
fun SettlementScreen(group: ExpenseGroup, from: String, to: String, state: FinanceState, onBack: () -> Unit,
    onSave: suspend (String, String, String, String) -> String?) {
    val today = LocalGroupToday.current
    val scope = rememberCoroutineScope()
    val debt = currentGroupDebts(group, state).find { it.from == from && it.to == to }
    var amount by rememberSaveable { mutableStateOf(debt?.amount?.toString().orEmpty()) }
    var date by rememberSaveable { mutableStateOf(today.toString()) }
    var confirm by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }
    val enteredAmount = parseVnd(amount)
    val fullSettlement = debt != null && enteredAmount == debt.amount
    val remainingAfter = (debt?.amount ?: 0L) - (enteredAmount ?: 0L)
    FeaturePage("Record settlement", { if (!saving) onBack() }, amount != debt?.amount?.toString() || date != today.toString(), primaryAction = {
        ErrorText(error)
        MetalButton(onClick = {
            error = settlementValidation(debt, amount, date, today)
            if (error == null) confirm = true
        }, modifier = Modifier.fillMaxWidth(), enabled = debt != null && !saving) { Text("Review settlement") }
    }) {
        item { GroupStorageNotice(); Text("${group.memberName(from)} → ${group.memberName(to)}", style = MaterialTheme.typography.titleLarge) }
        item {
            Text("Outstanding: ${formatVnd(debt?.amount ?: 0)}. Record an external payment only; FinSync never transfers money.")
            Text(when {
                debt == null -> "Debt suggestions changed. Return to the debt summary."
                enteredAmount == null -> "Enter a whole VND amount."
                enteredAmount > debt.amount -> "Amount exceeds the outstanding debt."
                remainingAfter == 0L -> "This payment clears the full outstanding debt."
                else -> "Remaining after this payment: ${formatVnd(remainingAfter)}"
            }, color = if (debt != null && enteredAmount != null && enteredAmount in 1..debt.amount)
                MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error)
        }
        if (debt != null) item { ChoiceChips(listOf("Full amount", "Partial payment"),
            if (enteredAmount == debt.amount) "Full amount" else "Partial payment",
            { choice -> amount = if (choice == "Full amount") debt.amount.toString() else "" }) }
        item { AmountField(amount, { amount = it }) }
        item { DateField(date, { date = it }) }
        if (debt == null) item { MetalOutlineButton(onClick = onBack) { Text("View current debts") } }
        if (saving) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
    }
    if (confirm) MetalDialog(onDismissRequest = { confirm = false }, title = {
        Text(if (fullSettlement) "Mark this debt as settled?" else "Confirm partial payment?")
    }, text = {
        Text(if (fullSettlement) "${group.memberName(from)} paid ${group.memberName(to)} ${formatVnd(enteredAmount ?: 0)} on $date outside FinSync. No money is transferred by FinSync."
        else "${group.memberName(from)} paid ${group.memberName(to)} ${formatVnd(enteredAmount ?: 0)} on $date outside FinSync. ${formatVnd(remainingAfter)} will remain outstanding.")
    },
        confirmButton = { TextButton(onClick = {
            if (!saving) {
                saving = true; confirm = false
                scope.launch {
                    error = onSave(from, to, amount, date)
                    if (error != null) saving = false
                }
            }
        }, enabled = !saving) { Text(if (fullSettlement) "Confirm settlement" else "Confirm partial payment") } },
        dismissButton = { TextButton(onClick = { confirm = false }) { Text("Cancel") } })
}
