package com.finsync.social

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.finsync.domain.ExpenseGroup
import com.finsync.ui.components.*
import com.finsync.ui.theme.Space
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

@OptIn(ExperimentalLayoutApi::class)
@Composable fun InviteMembersScreen(group: ExpenseGroup, controller: PeopleController, onBack: () -> Unit, onFriends: () -> Unit) {
    val state by controller.state.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var method by rememberSaveable { mutableStateOf("Email") }
    var email by rememberSaveable { mutableStateOf("") }
    var search by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var feedback by rememberSaveable { mutableStateOf<String?>(null) }
    var selected by rememberSaveable { mutableStateOf<String?>(null) }
    var revoke by remember { mutableStateOf<PeopleCommand?>(null) }
    val friends = state.data.friends.filter { it.status == ConnectionStatus.Accepted }
    val selectedFriend = friends.find { it.id == selected }
    FeaturePage("Invite members", { if (!state.busy) onBack() }, primaryAction = if (method == "Code" || group.owner != "me") null else ({
        ErrorText(error)
        MetalButton(onClick = {
            scope.launch {
                val target = if (method == "Friends") selectedFriend?.person?.email.orEmpty() else email
                error = controller.submit(PeopleCommand.Invite(group.id, group.name, target))
                if (error == null) { email = ""; selected = null; feedback = if (controller.repository.connected) "Invitation created" else "Invitation draft saved · Not sent" }
            }
        }, enabled = !state.busy && !state.loading && !state.loadFailed && (method != "Friends" || selectedFriend != null), modifier = Modifier.fillMaxWidth()) {
            Text(if (state.busy) "Saving…" else if (controller.repository.connected) "Invite to group" else "Save invitation draft")
        }
    })) {
        item { Text(group.name, style = MaterialTheme.typography.titleLarge); PeopleNotice(controller) }
        if (state.loading) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
        if (state.loadFailed) item { ErrorText(state.error); TextButton(onClick = { scope.launch { controller.reload() } }) { Text("Try again") } }
        if (group.owner != "me") item { Text("Only the group owner can invite members.") }
        else if (!state.loading && !state.loadFailed) {
            item { FlowRow(horizontalArrangement = Arrangement.spacedBy(Space.small)) {
                listOf("Email", "Friends", "Code").forEach { label -> FilterChip(selected = method == label, onClick = { method = label; error = null; feedback = null }, label = { Text(label) }) }
            } }
            when (method) {
                "Code" -> item { FinanceCard {
                    Text(if (controller.repository.connected) "Group invitation code" else "Local group code", style = MaterialTheme.typography.titleSmall)
                    Text(group.invite, style = MaterialTheme.typography.headlineMedium)
                    Text(if (controller.repository.connected) "People with this code can request to join." else "This code is not active on other devices yet. Group sync must activate it first.", style = MaterialTheme.typography.bodySmall)
                    MetalOutlineButton(onClick = {
                        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("FinSync group code", group.invite))
                        feedback = "Code copied"
                    }) { Text("Copy code") }
                } }
                "Friends" -> {
                    item { ChromeTextField(search, { search = it }, Modifier.fillMaxWidth(), label = { Text("Search friends") }, singleLine = true) }
                    val filtered = friends.filter { it.person.name.contains(search, true) || it.person.email.contains(search, true) }
                    if (filtered.isEmpty()) item { FinanceCard { Text(if (friends.isEmpty()) "No accepted friends yet" else "No friends match your search.")
                        TextButton(onClick = onFriends) { Text("Open friends") } } }
                    items(filtered, key = { it.id }) { friend -> FinanceCard {
                        PersonHeading(friend.person)
                        val existing = state.data.invitations.any { it.groupId == group.id && it.email.equals(friend.person.email, true) && it.status in listOf(ConnectionStatus.Draft, ConnectionStatus.Pending, ConnectionStatus.Accepted) }
                        Row {
                            RadioButton(selected == friend.id, onClick = { selected = friend.id }, enabled = !existing && !state.busy,
                                modifier = Modifier.semantics { contentDescription = "Select ${friend.person.name.ifBlank { friend.person.email }}" })
                            TextButton(onClick = { selected = friend.id }, enabled = !existing && !state.busy) { Text(if (existing) "Already invited" else "Choose ${friend.person.name.ifBlank { friend.person.email }}") }
                        }
                    } }
                }
                else -> item { ChromeTextField(email, { email = it; error = null }, Modifier.fillMaxWidth(), enabled = !state.busy,
                    label = { Text("Invitee email") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Email)) }
            }
            feedback?.let { item { Text(it, color = MaterialTheme.colorScheme.primary) } }
            item { Text("Invitations for this group", style = MaterialTheme.typography.titleMedium) }
            val invitations = state.data.invitations.filter { it.groupId == group.id && !it.incoming }
            if (invitations.isEmpty()) item { Text("No invitations prepared yet.", style = MaterialTheme.typography.bodySmall) }
            items(invitations, key = { it.id }) { InvitationCard(it, controller, state.busy) { command -> revoke = command } }
        }
    }
    revoke?.let { command -> MetalDialog(onDismissRequest = { revoke = null }, title = { Text("Remove invitation?") },
        text = { Text("Existing group members and expenses will stay unchanged.") },
        confirmButton = { TextButton(onClick = { revoke = null; scope.launch { error = controller.submit(command) } }) { Text("Remove") } },
        dismissButton = { TextButton(onClick = { revoke = null }) { Text("Cancel") } }) }
}

@Composable fun JoinGroupCodeScreen(controller: PeopleController, ownCodes: Set<String>, onBack: () -> Unit) {
    val state by controller.state.collectAsState()
    val scope = rememberCoroutineScope()
    var code by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var feedback by rememberSaveable { mutableStateOf<String?>(null) }
    var preview by remember { mutableStateOf<GroupPreview?>(null) }
    var lookingUp by remember { mutableStateOf(false) }
    FeaturePage("Join with code", { if (!state.busy) onBack() }, primaryAction = {
        ErrorText(error)
        MetalButton(onClick = { if (!lookingUp) scope.launch {
            error = null; feedback = null
            val normalized = runCatching { normalizeInviteCode(code) }.getOrElse { error = it.message; return@launch }
            if (normalized in ownCodes) { error = "You already belong to this group."; return@launch }
            if (controller.repository.connected && preview == null) {
                lookingUp = true
                try { preview = controller.previewCode(normalized) }
                catch (cancelled: CancellationException) { throw cancelled }
                catch (invalid: IllegalArgumentException) { error = invalid.message ?: "Check the invitation code." }
                catch (_: Exception) { error = "Couldn't check this code. Try again." }
                finally { lookingUp = false }
            } else {
                error = controller.submit(PeopleCommand.Join(preview?.code ?: normalized))
                if (error == null) { code = ""; preview = null; feedback = if (controller.repository.connected) "Join request submitted" else "Code saved · Waiting for group sync. You haven't joined yet." }
            }
        } }, enabled = !lookingUp && !state.busy && !state.loading && !state.loadFailed, modifier = Modifier.fillMaxWidth()) {
            Text(if (lookingUp) "Checking code…" else if (state.busy) "Saving…" else if (controller.repository.connected) { if (preview == null) "Review group" else "Confirm join request" } else "Save code for later")
        }
    }) {
        item { PeopleNotice(controller) }
        if (state.loading) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
        if (state.loadFailed) item { ErrorText(state.error); TextButton(onClick = { scope.launch { controller.reload() } }) { Text("Try again") } }
        item { ChromeTextField(code, { code = it; preview = null; error = null; feedback = null }, Modifier.fillMaxWidth(), enabled = !state.busy && !lookingUp,
            label = { Text("Invitation code") }, singleLine = true, keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)) }
        item { Text("Review the group and owner before confirming membership when group sync is available.", style = MaterialTheme.typography.bodySmall) }
        if (lookingUp) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
        preview?.let { group -> item { FinanceCard {
            Text(group.name, style = MaterialTheme.typography.titleLarge)
            Text("Owner: ${group.ownerName}")
            Text("${group.memberCount} members", style = MaterialTheme.typography.bodySmall)
            Text("Check these details before requesting to join. No membership has changed yet.", style = MaterialTheme.typography.bodySmall)
        } } }
        feedback?.let { item { Text(it, color = MaterialTheme.colorScheme.primary) } }
        items(state.data.joins, key = { it.id }) { request -> FinanceCard {
            Text(request.code, style = MaterialTheme.typography.titleMedium); Text(connectionLabel(request.status))
            if (request.status == ConnectionStatus.Draft) TextButton(onClick = { scope.launch { error = controller.submit(PeopleCommand.CancelJoin(request.id)) } }, enabled = !state.busy) { Text("Remove saved code") }
        } }
    }
}
