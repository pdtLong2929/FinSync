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

@Composable
fun GroupFlow(state: FinanceState, finance: FinanceViewModel, route: String, navigate: (String) -> Unit, onFeedback: (String) -> Unit = {}) {
    val group = state.groups.find { it.id == route.split('/').getOrNull(1) }
    key(route) {
        when {
            route == "group-new" -> CreateGroupScreen({ navigate("") }) { name, type, description ->
                finance.createGroup(name, type, description).also { if (it == null) { navigate(""); onFeedback("Group created") } }
            }
            group == null -> GroupListScreen(state, { navigate("group/$it") }, { navigate("group-new") }, finance::joinGroup)
            route.endsWith("/members") -> MembersScreen(group, state, { navigate("group/${group.id}") }, finance::changeMember)
            route.endsWith("/expense") -> GroupExpenseForm(group, { navigate("group/${group.id}") }) { title, amount, payer, date, category, people, mode, inputs ->
                finance.addGroupExpense(group.id, title, amount, payer, date, category, people, mode, inputs)
                    .also { if (it == null) { navigate("group/${group.id}"); onFeedback("Shared expense saved") } }
            }
            route.contains("/debt/") -> {
                val parts = route.split('/')
                if (parts.size >= 5) DebtDetailsScreen(group, state, parts[3], parts[4],
                    { navigate("group/${group.id}/debts") },
                    { navigate("group/${group.id}/settle/${parts[3]}/${parts[4]}") })
                else GroupDetailsScreen(group, state, { navigate("") }, { navigate("group/${group.id}/$it") })
            }
            route.contains("/settle/") -> SettlementScreen(group, route.split('/')[3], route.split('/')[4], state,
                { navigate("group/${group.id}/debts") }) { from, to, amount, date ->
                finance.recordSettlement(group.id, from, to, amount, date).also { if (it == null) { navigate("group/${group.id}/debts"); onFeedback("Settlement recorded · No money transferred") } }
            }
            route.endsWith("/debts") -> DebtScreen(group, state, { navigate("group/${group.id}") },
                { navigate("group/${group.id}/debt/${it.from}/${it.to}") })
            else -> GroupDetailsScreen(group, state, { navigate("") }, { navigate("group/${group.id}/$it") })
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GroupListScreen(state: FinanceState, onOpen: (String) -> Unit, onCreate: () -> Unit, onJoin: (String) -> String?,
                    mode: LoadState = LoadState.Ready) {
    var search by rememberSaveable { mutableStateOf("") }
    var showInvitation by rememberSaveable { mutableStateOf(false) }
    var invitation by rememberSaveable { mutableStateOf("") }
    var feedback by rememberSaveable { mutableStateOf<String?>(null) }
    val groups = state.groups.filter { it.joined && it.name.contains(search, true) }
    FeaturePage("Groups", compact = true) {
        item { FlowRow(horizontalArrangement = Arrangement.spacedBy(Space.small)) {
            MetalButton(onClick = onCreate) { Text("Create group") }
            TextButton(onClick = { showInvitation = !showInvitation }) { Text("Join with invitation") }
        } }
        item { ChromeTextField(search, { search = it }, Modifier.fillMaxWidth(), label = { Text("Search groups") }) }
        if (mode != LoadState.Ready || groups.isEmpty()) item { StateNotice(mode, groups.isEmpty(), if (search.isNotBlank()) "No groups match your search." else "No groups yet. Create one or join with an invitation.") {} }
        if (mode in listOf(LoadState.Ready, LoadState.Offline)) items(groups, key = { it.id }) { group ->
            Surface(shape = MaterialTheme.shapes.large, color = androidx.compose.ui.graphics.Color.Transparent,
                modifier = Modifier.fillMaxWidth().liquidGlass(MaterialTheme.shapes.large,4.dp).clickable(role = Role.Button) { onOpen(group.id) }) {
                Column(Modifier.fillMaxWidth().padding(Space.medium), verticalArrangement = Arrangement.spacedBy(Space.small)) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.small)) {
                        CategoryBadge(when(group.type) { "HOME" -> "Housing"; "TRIP" -> "Transport"; else -> "Other" })
                        Column(Modifier.weight(1f)) {
                            Text(group.name, style = MaterialTheme.typography.titleMedium)
                            Text("${group.members.count { it.active }} members", style = MaterialTheme.typography.labelSmall)
                        }
                        Text("›", style = MaterialTheme.typography.titleLarge)
                    }
                    val debts = simplifiedDebts(groupBalances(group, state.groupExpenses, state.settlements))
                    val net = debts.filter { it.to == "me" }.sumOf { it.amount } - debts.filter { it.from == "me" }.sumOf { it.amount }
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(Space.small), verticalArrangement = Arrangement.spacedBy(Space.small)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(-Space.small)) {
                            group.members.filter { it.active }.take(4).forEach { MemberAvatar(it.name) }
                        }
                        Surface(shape = LiquidTokens.rowShape, color = if(net >= 0) LiquidTokens.cyanTile else LiquidTokens.lilacTile) {
                            Text((if(net > 0) "Owed to you " else if(net < 0) "You owe " else "Settled ") + formatVnd(kotlin.math.abs(net)),
                                Modifier.padding(Space.small), style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
        item { Text("Recent group expenses", style = MaterialTheme.typography.titleMedium) }
        val recent = state.groupExpenses.filter { expense -> groups.any { it.id == expense.groupId } }.sortedByDescending { it.date }.take(5)
        if(recent.isEmpty()) item { Text("No shared expenses yet.") }
        items(recent, key = { "recent-${it.id}" }) { expense ->
            LedgerRow(expense.title, "${groups.find { it.id == expense.groupId }?.name.orEmpty()} · ${expense.date}",
                "−${formatVnd(expense.amount)}", { onOpen(expense.groupId) }, category = expense.category, compact = true, tone = LiquidTokens.expenseLight)
        }
    }
    if (showInvitation) ModalBottomSheet(onDismissRequest = { showInvitation = false },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = LiquidSurfaceTokens.frost) {
        androidx.compose.foundation.lazy.LazyColumn(Modifier.fillMaxWidth().imePadding(),
            contentPadding = PaddingValues(Space.medium), verticalArrangement = Arrangement.spacedBy(Space.small)) {
            item { Text("Join with invitation", style = MaterialTheme.typography.titleLarge) }
            item { Text("Sample invitation: TRIP26. Joining updates this workspace only.") }
            item { ChromeTextField(invitation, { invitation = it; feedback = null }, Modifier.fillMaxWidth(), label = { Text("Invitation code or link") }) }
            item { Text(feedback.orEmpty()) }
            item { MetalButton(onClick = { feedback = onJoin(invitation) ?: "Group added to your workspace" }) { Text("Join group") } }
            item { TextButton(onClick = { showInvitation = false }) { Text("Close") } }
        }
    }
}

@Composable
fun CreateGroupScreen(onBack: () -> Unit, onSave: (String, String, String) -> String?) {
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf("HOME") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    FeaturePage("Create group", onBack, name.isNotEmpty() || description.isNotEmpty() || type != "HOME", primaryAction = {
        ErrorText(error)
        MetalButton(onClick = { error = onSave(name, type, description) }, modifier = Modifier.fillMaxWidth()) { Text("Save group") }
    }) {
        item { DemoNotice() }
        item { ChromeTextField(name, { name = it }, Modifier.fillMaxWidth(), label = { Text("Group name") }) }
        item { OptionPicker("Group type", type, listOf("HOME", "TRIP", "COUPLE", "PROJECT", "OTHER"), { type = it }) }
        item { ChromeTextField(description, { description = it }, Modifier.fillMaxWidth(), label = { Text("Description (optional)") }) }
        item { Text("VND · You are the owner. Add members after creating the group.") }
    }
}

@Composable
fun GroupDetailsScreen(group: ExpenseGroup, state: FinanceState, onBack: () -> Unit, onOpen: (String) -> Unit) {
    var expandedExpense by rememberSaveable { mutableStateOf<String?>(null) }
    val expenses = state.groupExpenses.filter { it.groupId == group.id }.sortedByDescending { it.date }
    val settlements = state.settlements.filter { it.groupId == group.id }.sortedByDescending { it.date }
    val balances = groupBalances(group, state.groupExpenses, state.settlements)
    val debts = simplifiedDebts(balances)
    val owedByMe = debts.filter { it.from == "me" }.sumOf { it.amount }
    val owedToMe = debts.filter { it.to == "me" }.sumOf { it.amount }
    FeaturePage(group.name, onBack) {
        item { DemoNotice() }
        item { LiquidMetalSurface(Modifier.fillMaxWidth(), refined = true) {
            Text("Shared expenses · VND"); Text(formatVnd(expenses.sumOf { it.amount }), style = MaterialTheme.typography.headlineMedium)
            Text("${group.members.count { it.active }} members · ${group.type}")
            Row(horizontalArrangement=Arrangement.spacedBy(Space.small)) {
                group.members.filter { it.active }.take(5).forEach { MemberAvatar(it.name) }
            }
            Text(group.members.filter { it.active }.joinToString(" · ") { it.name },style=MaterialTheme.typography.bodySmall)
            if(group.description.isNotBlank()) Text(group.description,style=MaterialTheme.typography.bodySmall)
        } }
        item { FinanceCard {
            Text("Your group balance", style = MaterialTheme.typography.titleMedium)
            Text("You owe: ${formatVnd(owedByMe)}", color = LocalFinanceColors.current.expense)
            Text("Owed to you: ${formatVnd(owedToMe)}", color = LocalFinanceColors.current.income)
            if (debts.isEmpty()) Text("All settled · No outstanding debts.")
        } }
        item { MetalButton(onClick = { onOpen("expense") }) { Text("Add group expense") } }
        item { MetalOutlineButton(onClick = { onOpen("debts") }) { Text("Who owes whom") }; TextButton(onClick = { onOpen("members") }) { Text("Group members") } }
        item { SectionHeading("Expense history") }
        if (expenses.isEmpty()) item { EmptyState("No shared expenses yet.", "Add group expense", { onOpen("expense") }) }
        items(expenses, key = { it.id }) { expense ->
            FinanceCard {
                MetalEyebrow("SHARED EXPENSE")
                Text(expense.title, style = MaterialTheme.typography.titleLarge)
                CategoryBadge(expense.category)
                Text("Paid by ${group.memberName(expense.payer)} · ${expense.date} · ${expense.category}")
                Text(formatVnd(expense.amount), style = MaterialTheme.typography.headlineSmall.copy(fontFeatureSettings = "tnum"), color = LocalFinanceColors.current.expense)
                TextButton(onClick = { expandedExpense = if (expandedExpense == expense.id) null else expense.id }) {
                    Text(if (expandedExpense == expense.id) "Hide split" else "Split details")
                }
                if (expandedExpense == expense.id) expense.shares.forEach { (id, share) ->
                    if (id == expense.payer) Text("${group.memberName(id)} share: ${formatVnd(share)} · Paid by payer")
                    else Text("${group.memberName(id)} owes ${group.memberName(expense.payer)} ${formatVnd(share)} · Recorded split")
                }
            }
        }
        item { SectionHeading("Settlement history") }
        if (settlements.isEmpty()) item { Text("No settlements recorded. Money transfers happen outside FinSync.") }
        items(settlements, key = { it.id }) {
            LedgerRow("${group.memberName(it.from)} → ${group.memberName(it.to)}", "CONFIRMED · ${it.date} · Recorded only", formatVnd(it.amount))
        }
    }
}
private fun ExpenseGroup.memberName(id: String) = members.find { it.id == id }?.name ?: "Former member"
@Composable
fun MembersScreen(group: ExpenseGroup, state: FinanceState, onBack: () -> Unit, onChange: (String, String, String?) -> String?) {
    var name by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var remove by rememberSaveable { mutableStateOf<String?>(null) }
    val balances = groupBalances(group, state.groupExpenses, state.settlements)
    FeaturePage("Group members", onBack, name.isNotBlank()) {
        item { DemoNotice(); Text("Local invite code: ${group.invite} · No online sharing") }
        items(group.members, key = { it.id }) { member ->
            Column {
                LedgerRow(member.name, if (member.id == group.owner) "Owner" else if (member.active) "Member" else "Former member",
                    balances.getValue(member.id).let { if (it >= 0) "Is owed ${formatVnd(it)}" else "Owes ${formatVnd(-it)}" })
                if (group.owner == "me" && member.id != "me" && member.active) TextButton(onClick = { remove = member.id }) { Text("Remove ${member.name}") }
            }
        }
        if (group.owner == "me") {
            item { ChromeTextField(name, { name = it }, Modifier.fillMaxWidth(), label = { Text("Member name") }) }
            item { MetalButton(onClick = { error = onChange(group.id, name, null); if (error == null) name = "" }) { Text("Add member") } }
        } else item { Text("Only the owner can manage members.") }
        item { ErrorText(error) }
    }
    if (remove != null) MetalDialog(onDismissRequest = { remove = null }, title = { Text("Remove member?") },
        text = { Text("History is retained. Members with unpaid balances cannot be removed.") },
        confirmButton = { TextButton(onClick = { error = onChange(group.id, "", remove); remove = null }) { Text("Remove") } },
        dismissButton = { TextButton(onClick = { remove = null }) { Text("Cancel") } })
}
@Composable
fun GroupExpenseForm(group: ExpenseGroup, onBack: () -> Unit,
    onSave: (String, String, String, String, String, List<String>, SplitMode, Map<String, String>) -> String?) {
    val members = group.members.filter { it.active }
    var title by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var payer by rememberSaveable { mutableStateOf("me") }
    var date by rememberSaveable { mutableStateOf(DemoFixtures.today.toString()) }
    var category by rememberSaveable { mutableStateOf("Food") }
    var mode by rememberSaveable { mutableStateOf(SplitMode.Equal.name) }
    var selected by rememberSaveable { mutableStateOf(members.map { it.id }) }
    var inputs by rememberSaveable { mutableStateOf(mapOf<String, String>()) }
    var splitPage by rememberSaveable { mutableStateOf(false) }
    var roundingDetails by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var validationRequested by rememberSaveable { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    val fields = if (validationRequested) sharedExpenseErrors(title, amount, date, DemoFixtures.today) else SharedExpenseErrors()
    val allocation = runCatching { splitExpense(parseVnd(amount) ?: 0, payer, selected, SplitMode.valueOf(mode), inputs) }
    val progress = parseVnd(amount)?.let { total ->
        runCatching { splitProgress(total, payer, selected, SplitMode.valueOf(mode), inputs) }.getOrNull()
    }
    FeaturePage(if (splitPage) "Split expense" else "Add group expense", if (splitPage) ({ splitPage = false }) else onBack,
        !splitPage && (title.isNotBlank() || amount.isNotBlank() || date != DemoFixtures.today.toString() || category != "Food" || payer != "me" ||
            mode != SplitMode.Equal.name || selected != members.map { it.id } || inputs.isNotEmpty()), primaryAction = {
            ErrorText(error)
            if (splitPage) MetalButton(onClick = {
                if (!saving) {
                    saving = true
                    error = onSave(title, amount, payer, date, category, selected, SplitMode.valueOf(mode), inputs)
                    if (error != null) saving = false
                }
            }, modifier = Modifier.fillMaxWidth(), enabled = allocation.isSuccess && !saving) {
                if (saving) { CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp); Spacer(Modifier.width(Space.small)); Text("Saving…") }
                else Text("Save shared expense")
            }
            else MetalButton(onClick = {
                validationRequested = true
                error = sharedExpenseErrors(title, amount, date, DemoFixtures.today).first
                if (error == null) splitPage = true
            }, modifier = Modifier.fillMaxWidth()) { Text("Review split") }
        }) {
        item { DemoNotice(); Text("${group.name} · Shared ledger only") }
        if (!splitPage) {
            item { ChromeTextField(title, { title = it; error = null }, Modifier.fillMaxWidth(), label = { Text("Expense title") },
                isError = fields.title != null, supportingText = { ErrorText(fields.title) }) }
            item { AmountField(amount, { amount = it; error = null }, errorMessage = fields.amount) }
            item { OptionPicker("Paid by", group.memberName(payer), members.map { it.name }, { name -> payer = members.first { it.name == name }.id; selected = (selected + payer).distinct() }) }
            item { DateField(date, { date = it; error = null }, errorMessage = fields.date) }
            item { CategoryBadge(category); OptionPicker("Category", category, listOf("Food", "Housing", "Transport", "Other"), { category = it }) }
        } else {
            item { Text("${title.ifBlank { "Untitled expense" }} · ${formatVnd(parseVnd(amount) ?: 0)}") }
            item { Text("Paid by ${group.memberName(payer)} · ${selected.size} participants", style = MaterialTheme.typography.titleMedium) }
            item { LiquidMetalSelector(SplitMode.entries.map { it.name }, mode, { mode = it }) }
            item { Text("Choose participants · payer stays included") }
            progress?.let { preview ->
                item {
                    Column(Modifier.fillMaxWidth().padding(Space.small)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Text("Allocated preview: ${formatVnd(preview.allocated)}", Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
                            TextButton(onClick = { roundingDetails = !roundingDetails }) { Text(if(roundingDetails) "Hide rounding" else "Rounding") }
                        }
                        if (roundingDetails) Text(preview.detail, style = MaterialTheme.typography.labelSmall)
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
                MemberAllocationRow(member, member.id == payer, member.id in selected, SplitMode.valueOf(mode),
                    inputs[member.id].orEmpty(), allocation.getOrNull()?.get(member.id),
                    onToggle = { selected = if(member.id in selected) selected - member.id else selected + member.id },
                    onInput = { inputs = inputs + (member.id to it) })
            }
            item { ErrorText(allocation.exceptionOrNull()?.message) }

        }
    }
}
@Composable
fun DebtScreen(group: ExpenseGroup, state: FinanceState, onBack: () -> Unit, onOpenDebt: (Debt) -> Unit) {
    val debts = simplifiedDebts(groupBalances(group, state.groupExpenses, state.settlements))
    FeaturePage("Who owes whom", onBack) {
        item { DemoNotice(); Text("Suggested transfers simplify the shared ledger. Personal wallets are unchanged.") }
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
                MetalButton(onClick = { onOpenDebt(debt) }) { Text("View debt details") }
            }
        }
        item { Text("Pay outside FinSync, then record the completed transfer here. No money is sent by this app.") }
        val confirmed = state.settlements.filter { it.groupId == group.id }.asReversed()
        item { SectionHeading("Confirmed records") }
        if (confirmed.isEmpty()) item { Text("No confirmed settlement records yet.") }
        items(confirmed, key = { it.id }) { settlement ->
            LedgerRow("${group.memberName(settlement.from)} → ${group.memberName(settlement.to)}",
                "Confirmed record · ${settlement.date} · Paid outside FinSync", formatVnd(settlement.amount))
        }
    }
}
@Composable
fun DebtDetailsScreen(group: ExpenseGroup, state: FinanceState, from: String, to: String,
                      onBack: () -> Unit, onMarkSettled: () -> Unit) {
    val debt = simplifiedDebts(groupBalances(group, state.groupExpenses, state.settlements))
        .find { it.from == from && it.to == to }
    val history = state.settlements.filter { it.groupId == group.id && it.from == from && it.to == to }
        .sortedByDescending { it.date }
    FeaturePage("Debt details", onBack, primaryAction = if (debt != null) ({
        MetalButton(onClick = onMarkSettled, modifier = Modifier.fillMaxWidth()) { Text("Mark as settled") }
    }) else null) {
        item { DemoNotice(); Text("${group.memberName(from)} owes ${group.memberName(to)}", style = MaterialTheme.typography.titleLarge) }
        item { FinanceCard(emphasized = debt != null) {
            Text(if (debt == null) "Settled" else "Outstanding · Not settled", style = MaterialTheme.typography.titleMedium)
            Text(formatVnd(debt?.amount ?: 0L), style = MaterialTheme.typography.headlineMedium,
                color = if (debt == null) MaterialTheme.colorScheme.primary else LocalFinanceColors.current.expense)
            Text("Shared ledger only · Any payment is made outside FinSync.")
        } }
        item { SectionHeading("Settlement history") }
        if (history.isEmpty()) item { Text("No payments recorded for this debt yet.") }
        items(history, key = { it.id }) { settlement ->
            LedgerRow("${group.memberName(settlement.from)} paid ${group.memberName(settlement.to)}",
                "Confirmed · ${settlement.date} · Recorded only", formatVnd(settlement.amount))
        }
    }
}
@Composable
fun SettlementScreen(group: ExpenseGroup, from: String, to: String, state: FinanceState, onBack: () -> Unit,
    onSave: (String, String, String, String) -> String?) {
    val debt = simplifiedDebts(groupBalances(group, state.groupExpenses, state.settlements)).find { it.from == from && it.to == to }
    var amount by rememberSaveable { mutableStateOf(debt?.amount?.toString().orEmpty()) }
    var date by rememberSaveable { mutableStateOf(DemoFixtures.today.toString()) }
    var confirm by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }
    val enteredAmount = parseVnd(amount)
    val fullSettlement = debt != null && enteredAmount == debt.amount
    val remainingAfter = (debt?.amount ?: 0L) - (enteredAmount ?: 0L)
    FeaturePage("Record settlement", onBack, amount != debt?.amount?.toString() || date != DemoFixtures.today.toString(), primaryAction = {
        ErrorText(error)
        MetalButton(onClick = {
            error = settlementValidation(debt, amount, date, DemoFixtures.today)
            if (error == null) confirm = true
        }, modifier = Modifier.fillMaxWidth(), enabled = debt != null && !saving) { Text("Review settlement") }
    }) {
        item { DemoNotice(); Text("${group.memberName(from)} → ${group.memberName(to)}", style = MaterialTheme.typography.titleLarge) }
        item {
            Text("Outstanding: ${formatVnd(debt?.amount ?: 0)}. Record an external payment only; FinSync never transfers money.")
            Text(when {
                debt == null -> "This debt is no longer outstanding."
                enteredAmount == null -> "Enter a whole VND amount."
                enteredAmount > debt.amount -> "Amount exceeds the outstanding debt."
                remainingAfter == 0L -> "This payment clears the full outstanding debt."
                else -> "Remaining after this payment: ${formatVnd(remainingAfter)}"
            }, color = if (debt != null && enteredAmount != null && enteredAmount in 1..debt.amount)
                MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error)
        }
        item { AmountField(amount, { amount = it }) }
        item { DateField(date, { date = it }) }
        if (debt == null) item { Text("This debt is no longer outstanding. Return to the debt summary.") }
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
                error = onSave(from, to, amount, date)
                if (error != null) saving = false
            }
        }, enabled = !saving) { Text(if (fullSettlement) "Confirm settlement" else "Confirm partial payment") } },
        dismissButton = { TextButton(onClick = { confirm = false }) { Text("Cancel") } })
}
@Preview(showBackground = true)
@Composable private fun GroupListPreview() { FinSyncTheme { GroupListScreen(FinanceState(), {}, {}, { null }) } }
@Preview(showBackground = true)
@Composable private fun GroupDarkPreview() { FinSyncTheme(darkTheme = true) { GroupDetailsScreen(FinanceState().groups.first(), FinanceState(), {}, {}) } }
@Preview(showBackground = true)
@Composable private fun GroupOfflinePreview() { FinSyncTheme { GroupListScreen(FinanceState(), {}, {}, { null }, LoadState.Offline) } }
