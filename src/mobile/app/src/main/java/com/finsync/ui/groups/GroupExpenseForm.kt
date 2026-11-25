package com.finsync.ui.groups

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.clickable
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.finsync.ui.theme.LiquidTokens
import com.finsync.ui.theme.LocalFinanceColors
import androidx.compose.ui.tooling.preview.Preview
import com.finsync.data.DemoFixtures
import com.finsync.domain.*
import com.finsync.ui.*
import com.finsync.ui.components.*
import com.finsync.ui.dashboard.formatVnd
import com.finsync.ui.theme.FinSyncTheme
import com.finsync.ui.theme.Space
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun GroupExpenseForm(group: ExpenseGroup, onBack: () -> Unit,
    onSave: suspend (String, String, String, String, String, List<String>, SplitMode, Map<String, String>) -> String?) =
    GroupExpenseForm(group, onBack, null, onSave)

@Composable
fun GroupExpenseForm(group: ExpenseGroup, onBack: () -> Unit, existing: GroupExpense?,
    onSave: suspend (String, String, String, String, String, List<String>, SplitMode, Map<String, String>) -> String?) {
    val today = LocalGroupToday.current
    val scope = rememberCoroutineScope()
    val members = group.members.filter { it.active || existing?.shares?.containsKey(it.id) == true }
    val initialMode = existing?.splitMode ?: if (existing == null) SplitMode.Equal else SplitMode.Exact
    val initialInputs = existing?.let { if (it.splitMode == null) it.shares.mapValues { entry -> entry.value.toString() } else it.splitInputs }.orEmpty()
    val initialPeople = existing?.shares?.keys?.toList() ?: members.map { it.id }
    val lockedSplit = existing != null && group.hasFormerParticipant(existing.shares.keys)
    var title by rememberSaveable { mutableStateOf(existing?.title.orEmpty()) }
    var amount by rememberSaveable { mutableStateOf(existing?.amount?.toString().orEmpty()) }
    var payer by rememberSaveable { mutableStateOf(existing?.payer ?: "me") }
    var date by rememberSaveable { mutableStateOf(existing?.date?.toString() ?: today.toString()) }
    val server = com.finsync.groups.LocalServerGroups.current
    val categoryNames = server?.state?.value?.categories?.keys?.toList() ?: listOf("Food", "Housing", "Transport", "Other")
    var category by rememberSaveable { mutableStateOf(existing?.category ?: categoryNames.firstOrNull().orEmpty()) }
    var mode by rememberSaveable { mutableStateOf(initialMode.name) }
    var selected by rememberSaveable { mutableStateOf(initialPeople) }
    var inputs by rememberSaveable { mutableStateOf(initialInputs) }
    var splitPage by rememberSaveable { mutableStateOf(false) }
    var roundingDetails by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var validationRequested by rememberSaveable { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    val fields = if (validationRequested) sharedExpenseErrors(title, amount, date, today) else SharedExpenseErrors()
    val allocation = runCatching { if (server != null) com.finsync.groups.serverSplit(parseVnd(amount) ?: 0, payer, selected, SplitMode.valueOf(mode), inputs)
        else splitExpense(parseVnd(amount) ?: 0, payer, selected, SplitMode.valueOf(mode), inputs) }
    val progress = parseVnd(amount)?.let { total ->
        runCatching { splitProgress(total, payer, selected, SplitMode.valueOf(mode), inputs) }.getOrNull()
    }
    FeaturePage(if (splitPage) "Split expense" else if (existing != null) "Edit shared expense" else "Add group expense", { if (!saving) { if (splitPage) splitPage = false else onBack() } },
        !splitPage && (title != existing?.title.orEmpty() || amount != existing?.amount?.toString().orEmpty() ||
            date != (existing?.date?.toString() ?: today.toString()) || category != (existing?.category ?: "Food") ||
            payer != (existing?.payer ?: "me") || mode != initialMode.name || selected != initialPeople || inputs != initialInputs), primaryAction = {
            ErrorText(error)
            if (splitPage) MetalButton(onClick = {
                if (!saving) {
                    saving = true
                    scope.launch {
                        error = onSave(title, amount, payer, date, category, selected, SplitMode.valueOf(mode), inputs)
                        if (error != null) saving = false
                    }
                }
            }, modifier = Modifier.fillMaxWidth(), enabled = allocation.isSuccess && !saving) {
                if (saving) { CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp); Spacer(Modifier.width(Space.small)); Text("Saving…") }
                else Text("Save shared expense")
            }
            else MetalButton(onClick = {
                validationRequested = true
                error = sharedExpenseErrors(title, amount, date, today).first
                if (error == null) splitPage = true
            }, modifier = Modifier.fillMaxWidth()) { Text("Review split") }
        }) {
        item { GroupStorageNotice(); Text("${group.name} · Shared ledger") }
        if (!splitPage) {
            item { ChromeTextField(title, { title = it; error = null }, Modifier.fillMaxWidth(), label = { Text("Expense title") },
                isError = fields.title != null, supportingText = { ErrorText(fields.title) }) }
            if (lockedSplit) item { FinanceCard {
                Text(formatVnd(existing!!.amount), style = MaterialTheme.typography.headlineSmall)
                Text("Paid by ${group.memberName(payer)}")
                Text("This expense includes former members. Its title, date and category can still be edited.")
            } } else {
                item { AmountField(amount, { amount = it; error = null }, errorMessage = fields.amount) }
                item { OptionPicker("Paid by", group.memberName(payer), members.map { it.name }, { name -> payer = members.first { it.name == name }.id; selected = (selected + payer).distinct() }) }
            }
            item { DateField(date, { date = it; error = null }, errorMessage = fields.date) }
            item { CategoryBadge(category); OptionPicker("Category", category, categoryNames, { category = it }) }
        } else {
            item { Text("${title.ifBlank { "Untitled expense" }} · ${formatVnd(parseVnd(amount) ?: 0)}") }
            item { Text("Paid by ${group.memberName(payer)} · ${selected.size} participants", style = MaterialTheme.typography.titleMedium) }
            if (!lockedSplit) item { LiquidMetalSelector(SplitMode.entries.map { it.name }, mode, { mode = it }) }
            item { Text(if (lockedSplit) "Recorded allocation" else "Choose participants · payer stays included") }
            if (existing != null) item { Text("Changes recalculate group balances. Existing payment records are retained.", style = MaterialTheme.typography.bodySmall) }
            progress?.let { preview ->
                item {
                    Column(Modifier.fillMaxWidth().padding(Space.small)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Text("Allocated preview: ${formatVnd(preview.allocated)}", Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
                            TextButton(onClick = { roundingDetails = !roundingDetails }) { Text(if(roundingDetails) "Hide rounding" else "Rounding") }
                        }
                        if (roundingDetails) Text(if (server != null) "Server rounding · largest fractional remainder first; ties follow participant order." else preview.detail, style = MaterialTheme.typography.labelSmall)
                        Text(when {
                            preview.difference > 0 -> "Difference: ${formatVnd(preview.difference)} left to allocate"
                            preview.difference < 0 -> "Over allocated by ${formatVnd(-preview.difference)}"
                            !preview.inputsValid -> "Difference: ${formatVnd(0)} · complete all participant fields"
                            else -> "Difference: ${formatVnd(0)} · Matches total"
                        }, color = if (preview.inputsValid && preview.difference == 0L) LocalFinanceColors.current.income else LocalFinanceColors.current.expense)
                    }
                }
            }
            items(members, key = { it.id }) { member ->
                if (lockedSplit) Text("${member.name}: ${formatVnd(existing!!.shares[member.id] ?: 0)}") else MemberAllocationRow(member, member.id == payer, member.id in selected, SplitMode.valueOf(mode),
                    inputs[member.id].orEmpty(), allocation.getOrNull()?.get(member.id),
                    onToggle = { selected = if(member.id in selected) selected - member.id else selected + member.id },
                    onInput = { inputs = inputs + (member.id to it) })
            }
            item { ErrorText(allocation.exceptionOrNull()?.message) }

        }
    }
}
