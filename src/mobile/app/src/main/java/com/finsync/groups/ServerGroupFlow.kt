package com.finsync.groups

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.finsync.domain.*
import com.finsync.ui.FinanceState
import com.finsync.ui.components.*
import com.finsync.ui.dashboard.formatVnd
import com.finsync.ui.groups.*
import com.finsync.ui.theme.Space
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

@Composable internal fun ServerGroupFlow(server: ServerGroups, route: String, navigate: (String) -> Unit,
    feedback: (String) -> Unit) {
    val snapshot by server.state.collectAsState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(server) { if (server.state.value.loading) server.refresh() }
    val state = remember(snapshot.ledger) { FinanceState(groups = snapshot.ledger.groups,
        groupExpenses = snapshot.ledger.expenses, settlements = snapshot.ledger.settlements) }
    val group = state.groups.find { it.id == route.split('/').getOrNull(1) }
    if (snapshot.loading) { FeaturePage("Server groups") { item { LoadingState() } }; return }
    if (snapshot.error != null) {
        FeaturePage("Server groups", if (route.isEmpty()) null else ({ navigate("") })) {
            item { ErrorText(snapshot.error); MetalButton(onClick = { scope.launch { server.refresh() } }) { Text("Refresh server groups") } }
        }; return
    }
    val clipboard = LocalClipboardManager.current
    CompositionLocalProvider(LocalServerGroups provides server, LocalGroupToday provides LocalDate.now()) {
        key(route) {
            when {
                route == "group/new" -> CreateGroupScreen({ navigate("") }) { name, _, description ->
                    server.create(name, description).also { if (it == null) { navigate(""); feedback("Group saved on server") } }
                }
                route == "group/join-server" -> {
                    var token by rememberSaveable { mutableStateOf("") }
                    var error by remember { mutableStateOf<String?>(null) }
                    var busy by remember { mutableStateOf(false) }
                    var confirm by remember { mutableStateOf(false) }
                    FeaturePage("Join server group", { if (!busy) navigate("") }, token.isNotBlank()) {
                        item { Text("Paste an invitation token from the group owner. Joining changes your server membership.") }
                        item { ChromeTextField(token, { token = it.take(128); error = null }, label = { Text("Invitation token") }, enabled = !busy) }
                        item { ErrorText(error); MetalButton(onClick = {
                            if (token.trim().length !in 8..128) error = "Enter the full invitation token (8–128 characters)." else confirm = true
                        }, enabled = !busy) { Text("Review invitation") } }
                    }
                    if (confirm) MetalDialog(onDismissRequest = { confirm = false }, title = { Text("Join this group?") },
                        text = { Text("The server will validate this token and add your account if it is active and permitted. No money is transferred.") },
                        confirmButton = { TextButton(onClick = { confirm = false; busy = true; scope.launch {
                            error = server.accept(token); busy = false
                            if (error == null) { navigate(""); feedback("Joined server group") }
                        } }) { Text("Confirm join") } }, dismissButton = { TextButton(onClick = { confirm = false }) { Text("Cancel") } })
                }
                group == null && route.isNotEmpty() -> MissingGroupRecord({ navigate("") }, snapshot.busy)
                group == null -> Column {
                    TextButton(onClick = { scope.launch { server.refresh() } }) { Text("Refresh server groups") }
                    GroupListScreen(state, { navigate("group/$it") }, { navigate("group/new") }, { "Use a server invitation token." },
                        onCode = { navigate("group/join-server") })
                }
                else -> {
                    val home = "group/${group.id}"
                    fun section(name: String) = navigate(home + when (name) { "Balances" -> "/debts"; "Members" -> "/members"; else -> "" })
                    when {
                        route.endsWith("/edit") -> CreateGroupScreen({ navigate(home) }, group) { name, _, description ->
                            server.edit(group.id, name, description).also { if (it == null) { navigate(home); feedback("Group updated") } }
                        }
                        route.endsWith("/members") -> MembersScreen(group, state, { navigate(home) },
                            { id, name, remove -> server.addMember(id, name, remove) }, ::section,
                            onInvite = { navigate("$home/invite-server") })
                        route.endsWith("/invite-server") -> {
                            var token by remember { mutableStateOf<String?>(null) }
                            var error by remember { mutableStateOf<String?>(null) }
                            var busy by remember { mutableStateOf(false) }
                            FeaturePage("Invite to server group", { navigate("$home/members") }) {
                                item { Text("Create a single-use invitation valid for 24 hours. Share it privately with the person you want to join.") }
                                token?.let { value -> item { ChromeTextField(value, {}, label = { Text("Invitation token") }, readOnly = true)
                                    MetalButton(onClick = { clipboard.setText(AnnotatedString(value)); feedback("Invitation copied") }) { Text("Copy invitation token") } } }
                                if (token == null) item { MetalButton(onClick = { if (!busy) { busy = true; scope.launch {
                                    try { token = server.invitation(group.id) } catch (e: kotlinx.coroutines.CancellationException) { throw e }
                                    catch (_: Exception) { error = "Couldn't create an invitation. Check your connection and permission." }
                                    finally { busy = false }
                                } } }, enabled = !busy) { Text(if (busy) "Creating…" else "Create invitation token") }; ErrorText(error) }
                            }
                        }
                        route.endsWith("/expense") -> {
                            val request = rememberSaveable { UUID.randomUUID().toString() }
                            GroupExpenseForm(group, { navigate(home) }) { title, amount, payer, date, category, people, mode, inputs ->
                                server.expense(group.id, request, title, amount, payer, date, category, people, mode, inputs)
                                    .also { if (it == null) { navigate(home); feedback("Shared expense saved on server") } }
                            }
                        }
                        route.contains("/expense/") -> {
                            val expense = state.groupExpenses.find { it.id == route.substringAfterLast('/') && it.groupId == group.id }
                            if (expense == null) MissingGroupRecord({ navigate(home) }, snapshot.busy)
                            else SharedExpenseDetailsScreen(group, expense, state, { navigate(home) }, {}, { section("Balances") }, {}, false, null)
                        }
                        route.contains("/settle/") -> {
                            val parts = route.split('/'); val request = rememberSaveable { UUID.randomUUID().toString() }
                            if (parts.size == 5) SettlementScreen(group, parts[3], parts[4], state, { section("Balances") }) { from, to, amount, date ->
                                server.settle(group.id, request, from, to, amount, date)
                                    .also { if (it == null) { section("Balances"); feedback("Payment recorded on server") } }
                            }
                        }
                        route.contains("/debt/") -> {
                            val parts = route.split('/')
                            if (parts.size == 5) DebtDetailsScreen(group, state, parts[3], parts[4], { section("Balances") },
                                { navigate("$home/settle/${parts[3]}/${parts[4]}") }, { navigate("$home/payment/${it.id}") })
                        }
                        route.contains("/payment/") -> {
                            val payment = state.settlements.find { it.id == route.substringAfterLast('/') && it.groupId == group.id }
                            FeaturePage("Server payment record", { section("Balances") }) {
                                if (payment == null) item { EmptyState("Record no longer available. Refresh server groups.") }
                                else item { LiquidCard {
                                    Text("${group.memberName(payment.from)} → ${group.memberName(payment.to)}")
                                    Text(formatVnd(payment.amount), style = MaterialTheme.typography.headlineMedium)
                                    Text("Confirmed record · ${payment.date}")
                                    Text("Payment was made outside FinSync. The current API does not support corrections or deletion.")
                                } }
                            }
                        }
                        route.endsWith("/debts") -> DebtScreen(group, state, { navigate(home) },
                            { navigate("$home/debt/${it.from}/${it.to}") }, ::section, { navigate("$home/payment/${it.id}") },
                            { navigate("$home/settle/${it.from}/${it.to}") })
                        else -> GroupDetailsScreen(group, state, { navigate("") }, { navigate("$home/$it".trimEnd('/')) })
                    }
                }
            }
        }
    }
}
