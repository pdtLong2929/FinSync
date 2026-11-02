package com.finsync.ui.wallets

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
import java.util.UUID

@Composable
fun TransferScreen(state: FinanceState, onBack: () -> Unit, onHistory: () -> Unit,
                   onConfirm: (String, String, String, String, String) -> String?) {
    val eligible = state.wallets.filter { !it.archived && it.type != WalletType.CREDIT_CARD }
    var sourceId by rememberSaveable { mutableStateOf(eligible.firstOrNull()?.id.orEmpty()) }
    var destinationId by rememberSaveable { mutableStateOf(eligible.getOrNull(1)?.id.orEmpty()) }
    var amount by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    val requestId = rememberSaveable { UUID.randomUUID().toString() }
    var reviewing by rememberSaveable { mutableStateOf(false) }
    var confirmed by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var submitting by remember { mutableStateOf(false) }
    val source = state.wallets.find { it.id == sourceId }
    val destination = state.wallets.find { it.id == destinationId }
    val recorded = state.transfers.find { it.id == requestId }
    val complete = confirmed && recorded != null
    val dirty = amount.isNotEmpty() || note.isNotEmpty()
    FeaturePage(if (complete) "Transfer recorded" else if (reviewing) "Review transfer" else "Transfer between wallets",
        onBack = if (reviewing && !complete) ({ reviewing = false; error = null }) else onBack,
        dirty = dirty && !reviewing && !complete,
        primaryAction = if (complete) null else ({
            ErrorText(error)
            MetalButton(onClick = {
                if (!submitting) {
                    error = transferValidation(source, destination, amount, note, state.entries)
                    if (error == null) {
                        if (!reviewing) reviewing = true else {
                            submitting = true
                            error = onConfirm(requestId, sourceId, destinationId, amount, note)
                            confirmed = error == null
                            if (!confirmed) submitting = false
                        }
                    }
                }
            }, modifier = Modifier.fillMaxWidth(), enabled = !submitting && eligible.size >= 2) {
                Text(if (reviewing) "Confirm transfer" else "Review transfer")
            }
        })) {
        item { Text("Sample workspace · bookkeeping only · no payment sent.") }
        if (complete || reviewing) {
            item { LiquidMetalSurface(Modifier.fillMaxWidth(), refined = true) {
                Text(if (complete) "Recorded in your workspace" else "Check before confirming", style = MaterialTheme.typography.titleLarge)
                Text(formatVnd(recorded?.amount ?: parseVnd(amount) ?: 0), style = MaterialTheme.typography.headlineMedium)
                Text("From: ${source?.name.orEmpty()}")
                Text("To: ${destination?.name.orEmpty()}")
                if (note.isNotBlank()) Text(note)
                Text("Internal transfer · excluded from reports")
            } }
            if (!complete && source != null) item {
                Text("Source balance after transfer: ${formatVnd(walletBalance(source, state.entries) - (parseVnd(amount) ?: 0))}")
            }
            if (complete) item {
                // Keep result actions away from the old Confirm hit target during rapid taps.
                MetalButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Done") }
                TextButton(onClick = onHistory) { Text("View transfer history") }
            }
        } else {
            if (eligible.size < 2) item { Text("Create or restore two eligible wallets in Wallets to make a transfer.") }
            item { OptionPicker("From wallet", source?.name.orEmpty(), eligible.map { it.name }, { name ->
                sourceId = eligible.first { it.name == name }.id; error = null
            }) }
            if (source != null) item { Text("Available balance: ${formatVnd(walletBalance(source, state.entries))}") }
            item { OptionPicker("To wallet", destination?.name.orEmpty(), eligible.map { it.name }, { name ->
                destinationId = eligible.first { it.name == name }.id; error = null
            }) }
            item { AmountField(amount, { amount = it; error = null }) }
            item { ChromeTextField(note, { note = it; error = null }, Modifier.fillMaxWidth(),
                label = { Text("Transfer note (optional)") }, supportingText = { Text("${note.length}/300") }) }
            item { Text("Cash, bank and e-wallet balances only. Credit cards are excluded. You cannot transfer more than the source balance.") }
        }
    }
}

@Composable
fun TransferHistoryScreen(state: FinanceState, hidden: Boolean, onBack: () -> Unit) {
    FeaturePage("Transfer history", onBack) {
        item { Text("Internal bookkeeping only. Transfers are excluded from income and expense reports.") }
        if (state.transfers.isEmpty()) item { Text("No transfers yet. Recorded transfers will appear here.") }
        items(state.transfers.asReversed(), key = { it.id }) { transfer ->
            val source = state.wallets.find { it.id == transfer.sourceId }?.name ?: "Unavailable wallet"
            val destination = state.wallets.find { it.id == transfer.destinationId }?.name ?: "Unavailable wallet"
            FinanceCard {
                Text("$source → $destination", style = MaterialTheme.typography.titleMedium)
                Text(if (hidden) "Hidden" else formatVnd(transfer.amount), style = MaterialTheme.typography.titleLarge)
                Text("${transfer.date} · Recorded")
                if (transfer.note.isNotBlank()) Text(transfer.note)
            }
        }
    }
}
