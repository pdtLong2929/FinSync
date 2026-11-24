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
import com.finsync.social.*

internal val LocalDeviceGroupLedger = compositionLocalOf { false }
internal val LocalGroupToday = compositionLocalOf { DemoFixtures.today }

@Composable
internal fun GroupStorageNotice() {
    if (LocalDeviceGroupLedger.current) Text("Saved on this device", style = MaterialTheme.typography.labelSmall)
    else DemoNotice()
}

@Composable
fun GroupFlow(state: FinanceState, finance: FinanceViewModel, route: String, navigate: (String) -> Unit, onFeedback: (String) -> Unit = {},
    onUndo: ((String, suspend () -> String?) -> Unit)? = null) {
    val storage by finance.groupStorage.state.collectAsState()
    val people = LocalPeopleController.current
    val recordScope = rememberCoroutineScope()
    var recordBusy by remember { mutableStateOf(false) }
    var recordError by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(route) { recordError = null }
    val workspace = storage.workspace
    suspend fun persist(action: () -> String?): String? = finance.groupStorage.update {
        if (finance.groupStorage.state.value.workspace != workspace) "Your workspace changed. Open Groups again." else action()
    }
    fun removeRecord(message: String, destination: String, remove: () -> String?, restore: () -> String?) {
        if (!recordBusy) {
            recordBusy = true
            recordScope.launch {
                try {
                    recordError = persist(remove)
                    if (recordError == null) {
                        navigate(destination)
                        if (onUndo != null) onUndo(message) { persist(restore) } else onFeedback(message)
                    } else onFeedback(recordError!!)
                } finally { recordBusy = false }
            }
        }
    }
    if (storage.loading || storage.loadFailed) {
        FeaturePage("Groups") {
            item { StateNotice(if (storage.loading) LoadState.Loading else LoadState.Error, false,
                "Couldn't open your saved groups. Try again.", finance.groupStorage::retry) {} }
        }
        return
    }
    val group = state.groups.find { it.id == route.split('/').getOrNull(1) }
    val home = group?.let { "group/${it.id}" }.orEmpty()
    val section: (String) -> Unit = { label -> navigate(home + when (label) { "Balances" -> "/debts"; "Members" -> "/members"; else -> "" }) }
    CompositionLocalProvider(LocalDeviceGroupLedger provides (storage.workspace != null), LocalGroupToday provides finance.groupToday) {
        key(storage.workspace, route) {
            when {
                route.startsWith("friends") && people != null -> PeopleScreen(people) {
                    navigate(route.substringAfter('/', "").takeIf { it.isNotBlank() }?.let { "group/$it/invite" } ?: "")
                }
                route == "join-code" && people != null -> JoinGroupCodeScreen(people, state.groups.filter { it.joined }.map { it.invite }.toSet()) { navigate("") }
                route.endsWith("/invite") && group != null && people != null -> InviteMembersScreen(group, people,
                    { navigate("$home/members") }, { navigate("friends/${group.id}") })
                route == "group-new" -> CreateGroupScreen({ navigate("") }) { name, type, description ->
                    finance.groupStorage.update { finance.createGroup(name, type, description) }
                        .also { if (it == null) { navigate(""); onFeedback("Group created") } }
                }
                group == null -> GroupListScreen(state, { navigate("group/$it") }, { navigate("group-new") },
                    { code -> finance.groupStorage.update { finance.joinGroup(code) } },
                    onPeople = if (people != null) ({ navigate("friends") }) else null,
                    onCode = if (people != null) ({ navigate("join-code") }) else null)
                route.contains("/expense/edit/") -> {
                    val expense = state.groupExpenses.find { it.id == route.substringAfterLast('/') && it.groupId == group.id }
                    if (expense == null) MissingGroupRecord({ navigate(home) }, recordBusy || storage.saving)
                    else GroupExpenseForm(group, { navigate("$home/expense/${expense.id}") }, expense) { title, amount, payer, date, category, people, mode, inputs ->
                        persist { finance.editGroupExpense(expense.id, title, amount, payer, date, category, people, mode, inputs) }
                            .also { if (it == null) { navigate("$home/expense/${expense.id}"); onFeedback("Shared expense updated") } }
                    }
                }
                route.contains("/expense/") -> {
                    val expense = state.groupExpenses.find { it.id == route.substringAfterLast('/') && it.groupId == group.id }
                    if (expense == null) MissingGroupRecord({ navigate(home) }, recordBusy || storage.saving)
                    else SharedExpenseDetailsScreen(group, expense, state, { navigate(home) },
                        { navigate("$home/expense/edit/${expense.id}") }, { section("Balances") },
                        { removeRecord("Shared expense removed", home, { finance.removeGroupExpense(expense.id) }, { finance.restoreGroupExpense(expense) }) },
                        recordBusy || storage.saving, recordError)
                }
                route.contains("/payment/") -> {
                    val payment = state.settlements.find { it.id == route.substringAfterLast('/') && it.groupId == group.id }
                    if (payment == null) MissingGroupRecord({ section("Balances") }, recordBusy || storage.saving)
                    else PaymentRecordScreen(group, payment, { section("Balances") }, { amount, date ->
                        persist { finance.editSettlement(payment.id, amount, date) }
                            .also { if (it == null) { section("Balances"); onFeedback("Payment record corrected") } }
                    }, { removeRecord("Payment record removed", "$home/debts", { finance.removeSettlement(payment.id) }, { finance.restoreSettlement(payment) }) },
                        recordBusy || storage.saving, recordError)
                }
                route.endsWith("/edit") -> CreateGroupScreen({ navigate("group/${group.id}") }, group) { name, type, description ->
                    finance.groupStorage.update { finance.editGroup(group.id, name, type, description) }
                        .also { if (it == null) { navigate("group/${group.id}"); onFeedback("Group updated") } }
                }
                route.endsWith("/members") -> MembersScreen(group, state, { navigate("group/${group.id}") },
                    { id, name, remove -> finance.groupStorage.update { finance.changeMember(id, name, remove) } }, onSection = section,
                    onInvite = if (people != null) ({ navigate("$home/invite") }) else null,
                    onEditMember = { id, name, restore -> finance.groupStorage.update { finance.updateGroupMember(group.id, id, name, restore) } })
                route.endsWith("/expense") -> {
                    val requestId = rememberSaveable { UUID.randomUUID().toString() }
                    GroupExpenseForm(group, { navigate("group/${group.id}") }) { title, amount, payer, date, category, people, mode, inputs ->
                        finance.groupStorage.update { finance.addGroupExpense(group.id, title, amount, payer, date, category, people, mode, inputs, requestId) }
                            .also { if (it == null) { navigate("group/${group.id}"); onFeedback("Shared expense saved") } }
                    }
                }
                route.contains("/debt/") -> {
                    val parts = route.split('/')
                    if (parts.size >= 5) DebtDetailsScreen(group, state, parts[3], parts[4],
                        { navigate("group/${group.id}/debts") },
                        { navigate("group/${group.id}/settle/${parts[3]}/${parts[4]}") }, onPayment = { navigate("$home/payment/${it.id}") })
                    else GroupDetailsScreen(group, state, { navigate("") }, { navigate("group/${group.id}/$it") })
                }
                route.contains("/settle/") -> {
                    val requestId = rememberSaveable { UUID.randomUUID().toString() }
                    SettlementScreen(group, route.split('/')[3], route.split('/')[4], state,
                    { navigate("group/${group.id}/debts") }) { from, to, amount, date ->
                        finance.groupStorage.update { finance.recordSettlement(group.id, from, to, amount, date, requestId) }
                            .also { if (it == null) { navigate("group/${group.id}/debts"); onFeedback("Settlement recorded") } }
                    }
                }
                route.endsWith("/debts") -> DebtScreen(group, state, { navigate("group/${group.id}") },
                    { navigate("group/${group.id}/debt/${it.from}/${it.to}") }, onSection = section, onPayment = { navigate("$home/payment/${it.id}") },
                    onRecord = { navigate("$home/settle/${it.from}/${it.to}") })
                else -> GroupDetailsScreen(group, state, { navigate("") }, { navigate("group/${group.id}/$it") })
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GroupListScreen(state: FinanceState, onOpen: (String) -> Unit, onCreate: () -> Unit, onJoin: suspend (String) -> String?,
                    mode: LoadState = LoadState.Ready, onPeople: (() -> Unit)? = null, onCode: (() -> Unit)? = null) {
    var search by rememberSaveable { mutableStateOf("") }
    var showInvitation by rememberSaveable { mutableStateOf(false) }
    var invitation by rememberSaveable { mutableStateOf("") }
    var feedback by rememberSaveable { mutableStateOf<String?>(null) }
    var joining by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val deviceLedger = LocalDeviceGroupLedger.current
    val groups = state.groups.filter { it.joined && it.name.contains(search, true) }
    FeaturePage("Groups", compact = true) {
        if (deviceLedger) item { GroupStorageNotice() }
        if (mode in listOf(LoadState.Ready, LoadState.Offline)) item { GroupOverviewCard(state) }
        item { FlowRow(horizontalArrangement = Arrangement.spacedBy(Space.small)) {
            MetalButton(onClick = onCreate) { Text("Create group") }
            TextButton(onClick = { if (onCode != null) onCode() else showInvitation = !showInvitation }) { Text(if (onCode != null) "Join with code" else if (deviceLedger) "Online invitations" else "Join with invitation") }
            if (onPeople != null) TextButton(onClick = onPeople) { Text("Friends & invitations") }
        } }
        item { ChromeTextField(search, { search = it }, Modifier.fillMaxWidth(), label = { Text("Search groups") }) }
        if (mode != LoadState.Ready || groups.isEmpty()) item { StateNotice(mode, groups.isEmpty(), if (search.isNotBlank()) "No groups match your search." else "No groups yet. Create a group to start sharing expenses.") {} }
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
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Shared spending", style = MaterialTheme.typography.labelSmall, color = LiquidTokens.mutedLight)
                        Text(formatVnd(state.groupExpenses.filter { it.groupId == group.id }.sumOf { it.amount }),
                            style = MaterialTheme.typography.labelLarge)
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
        val recent = state.groupExpenses.filter { expense -> groups.any { it.id == expense.groupId } }.sortedByDescending { it.date }.take(5)
        item {
            LiquidCard {
                Text("Recent group expenses", style = MaterialTheme.typography.titleMedium)
                if (recent.isEmpty()) Text("No shared expenses yet.")
                recent.forEach { expense ->
                    LedgerRow(expense.title, "${groups.find { it.id == expense.groupId }?.name.orEmpty()} · ${expense.date}",
                        "−${formatVnd(expense.amount)}", { onOpen(expense.groupId) }, category = expense.category, compact = true, tone = LiquidTokens.expenseLight)
                }
            }
        }
    }
    if (showInvitation) ModalBottomSheet(onDismissRequest = { showInvitation = false },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = LiquidSurfaceTokens.frost) {
        androidx.compose.foundation.lazy.LazyColumn(Modifier.fillMaxWidth().imePadding(),
            contentPadding = PaddingValues(Space.medium), verticalArrangement = Arrangement.spacedBy(Space.small)) {
            item { Text("Join with invitation", style = MaterialTheme.typography.titleLarge) }
            item { Text(if (deviceLedger) "Online invitations need group sync. You can create a group and add members on this device."
                else "Sample invitation: TRIP26. Joining updates this workspace only.") }
            if (deviceLedger) item {
                MetalButton(onClick = { showInvitation = false; onCreate() }) { Text("Create a group on this device") }
            } else {
                item { ChromeTextField(invitation, { invitation = it; feedback = null }, Modifier.fillMaxWidth(), enabled = !joining, label = { Text("Invitation code or link") }) }
                item { Text(feedback.orEmpty()) }
                item { MetalButton(onClick = {
                    if (!joining) {
                        joining = true
                        scope.launch {
                            try { feedback = onJoin(invitation) ?: "Group added to your workspace" }
                            finally { joining = false }
                        }
                    }
                }, enabled = !joining && invitation.isNotBlank()) { Text(if (joining) "Joining…" else "Join group") } }
            }
            item { TextButton(onClick = { showInvitation = false }) { Text("Close") } }
        }
    }
}

@Composable
fun GroupDetailsScreen(group: ExpenseGroup, state: FinanceState, onBack: () -> Unit, onOpen: (String) -> Unit) {
    val expenses = state.groupExpenses.filter { it.groupId == group.id }.sortedByDescending { it.date }
    FeaturePage(group.name, onBack, headerAction = {
        if (group.owner == "me") TextButton(onClick = { onOpen("edit") }) { Text("Edit group") }
    }) {
        item { GroupStorageNotice() }
        item { GroupSummary(group, state) }
        item { GroupSectionTabs("Expenses", { section -> onOpen(if (section == "Balances") "debts" else if (section == "Members") "members" else "") }) }
        item { MetalButton(onClick = { onOpen("expense") }, modifier = Modifier.fillMaxWidth()) { Text("Add group expense") } }
        item { TextButton(onClick = { onOpen("debts") }) { Text("Who owes whom") } }
        item { Text("Expense history", style = MaterialTheme.typography.titleMedium) }
        if (expenses.isEmpty()) item { EmptyState("No shared expenses yet.") }
        items(expenses, key = { it.id }) { expense ->
            LedgerRow(expense.title, "Paid by ${group.memberName(expense.payer)} · ${expense.date}",
                formatVnd(expense.amount), { onOpen("expense/${expense.id}") }, category = expense.category,
                tone = LocalFinanceColors.current.expense)
        }
    }
}
internal fun ExpenseGroup.memberName(id: String) = members.find { it.id == id }?.name ?: "Former member"
@Preview(showBackground = true)
@Composable private fun GroupListPreview() { FinSyncTheme { GroupListScreen(FinanceState(), {}, {}, { null }) } }
@Preview(showBackground = true)
@Composable private fun GroupDarkPreview() { FinSyncTheme(darkTheme = true) { GroupDetailsScreen(FinanceState().groups.first(), FinanceState(), {}, {}) } }
@Preview(showBackground = true)
@Composable private fun GroupOfflinePreview() { FinSyncTheme { GroupListScreen(FinanceState(), {}, {}, { null }, LoadState.Offline) } }
