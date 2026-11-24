package com.finsync.social

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.finsync.auth.UserAvatar
import com.finsync.ui.components.*
import com.finsync.ui.theme.Space
import kotlinx.coroutines.launch

internal fun connectionLabel(status: ConnectionStatus) = when (status) {
    ConnectionStatus.Draft -> "Not sent · Saved on this device"
    ConnectionStatus.Pending -> "Pending response"
    ConnectionStatus.Accepted -> "Accepted"
    ConnectionStatus.Declined -> "Declined"
    ConnectionStatus.Cancelled -> "Cancelled"
    ConnectionStatus.Expired -> "Expired"
}

@Composable internal fun PeopleNotice(controller: PeopleController) {
    if (!controller.repository.connected) FinanceCard {
        Text("Friends & group sync isn't connected", style = MaterialTheme.typography.titleSmall)
        Text("Requests stay on this device as unsent drafts. Other people won't receive them yet.", style = MaterialTheme.typography.bodySmall)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable fun PeopleScreen(controller: PeopleController, onBack: () -> Unit) {
    val state by controller.state.collectAsState()
    val scope = rememberCoroutineScope()
    var tab by rememberSaveable { mutableStateOf("Friends") }
    var search by rememberSaveable { mutableStateOf("") }
    var adding by rememberSaveable { mutableStateOf(false) }
    var confirm by remember { mutableStateOf<PeopleCommand?>(null) }
    fun run(command: PeopleCommand) { scope.launch { controller.submit(command) } }
    if (adding) {
        EmailRequestScreen("Add a friend", "Friend's email", controller, { adding = false }) { email ->
            controller.submit(PeopleCommand.AddFriend(email)).also { if (it == null) { adding = false; tab = "Requests" } }
        }
        return
    }
    FeaturePage("Friends & invitations", { if (!state.busy) onBack() }, compact = true) {
        item { PeopleNotice(controller) }
        if (state.loading) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
        if (state.loadFailed) item { ErrorText(state.error); TextButton(onClick = { scope.launch { controller.reload() } }) { Text("Try again") } }
        else if (!state.loading) {
            item { FlowRow(horizontalArrangement = Arrangement.spacedBy(Space.small), verticalArrangement = Arrangement.spacedBy(Space.small)) {
                listOf("Friends", "Requests", "Invitations").forEach { label ->
                    FilterChip(selected = tab == label, onClick = { tab = label }, label = { Text(label) })
                }
            } }
            item { MetalButton(onClick = { adding = true }, enabled = !state.busy, modifier = Modifier.fillMaxWidth()) { Text("Add a friend") } }
            item { ChromeTextField(search, { search = it }, Modifier.fillMaxWidth(), label = { Text("Search people or groups") }, singleLine = true) }
            val people = state.data.friends.filter { (it.status == ConnectionStatus.Accepted) == (tab == "Friends") &&
                (it.person.name.contains(search, true) || it.person.email.contains(search, true)) }
            if (tab != "Invitations") {
                if (people.isEmpty()) item { FinanceCard {
                    Text(if (search.isNotBlank()) "No people match your search." else if (tab == "Friends") "No friends yet" else "No friend requests")
                    Text(if (tab == "Friends") "Add a friend by email. Accepted friends will appear here." else "Incoming requests and your saved requests appear here.", style = MaterialTheme.typography.bodySmall)
                    if (search.isNotBlank()) TextButton(onClick = { search = "" }) { Text("Clear search") }
                } }
                items(people, key = { it.id }) { friend -> FinanceCard {
                    PersonHeading(friend.person)
                    Text(if (friend.status == ConnectionStatus.Accepted) "Friend" else (if (friend.incoming) "Received · " else "Outgoing · ") + connectionLabel(friend.status), style = MaterialTheme.typography.bodySmall)
                    if (friend.incoming && friend.status == ConnectionStatus.Pending) {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(Space.small)) {
                            MetalButton(onClick = { confirm = PeopleCommand.ReplyFriend(friend.id, true) }, enabled = controller.repository.connected && !state.busy) { Text("Accept friend") }
                            TextButton(onClick = { confirm = PeopleCommand.ReplyFriend(friend.id, false) }, enabled = controller.repository.connected && !state.busy) { Text("Decline friend") }
                        }
                    } else if (!friend.incoming || friend.status == ConnectionStatus.Accepted) TextButton(
                        onClick = { confirm = PeopleCommand.RemoveFriend(friend.id) },
                        enabled = !state.busy && (friend.status == ConnectionStatus.Draft || controller.repository.connected)) {
                        Text(if (friend.status == ConnectionStatus.Draft) "Remove friend draft" else if (friend.status == ConnectionStatus.Accepted) "Remove friend" else "Cancel friend request")
                    }
                } }
            } else {
                val invitations = state.data.invitations.filter { it.groupName.contains(search, true) || it.email.contains(search, true) }
                if (invitations.isEmpty()) item { FinanceCard { Text("No group invitations"); Text("Open a group → Members → Invite members to prepare one.", style = MaterialTheme.typography.bodySmall) } }
                items(invitations, key = { it.id }) { invite -> InvitationCard(invite, controller, state.busy) { confirm = it } }
            }
            state.feedback?.let { item { Text(it, color = MaterialTheme.colorScheme.primary) } }
            if (state.error != null) item { ErrorText(state.error) }
        }
    }
    confirm?.let { command ->
        val accepting = command is PeopleCommand.ReplyFriend && command.accept || command is PeopleCommand.ReplyInvite && command.accept
        MetalDialog(onDismissRequest = { confirm = null }, title = { Text(if (accepting) "Accept invitation?" else "Confirm change?") },
            text = { Text(if (command is PeopleCommand.RemoveFriend) "This changes your friends list only. Existing shared expenses and group memberships are retained." else "Review this action before continuing. Unsent drafts have not been delivered to anyone.") },
            confirmButton = { TextButton(onClick = { confirm = null; run(command) }, enabled = !state.busy) { Text("Confirm") } },
            dismissButton = { TextButton(onClick = { confirm = null }) { Text("Cancel") } })
    }
}

@Composable internal fun PersonHeading(person: Person) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.small)) {
        UserAvatar(person.name.ifBlank { person.email }, null, Modifier.size(com.finsync.ui.theme.LiquidTokens.minTarget))
        Column(Modifier.weight(1f)) {
            if (person.name.isNotBlank()) Text(person.name, style = MaterialTheme.typography.titleSmall)
            Text(person.email, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable internal fun InvitationCard(invite: GroupInvitation, controller: PeopleController, busy: Boolean, onAction: (PeopleCommand) -> Unit) {
    FinanceCard {
        Text(invite.groupName, style = MaterialTheme.typography.titleMedium)
        Text(invite.email, style = MaterialTheme.typography.bodyMedium)
        Text((if (invite.incoming) "Received · " else "Outgoing · ") + connectionLabel(invite.status), style = MaterialTheme.typography.bodySmall)
        if (invite.incoming && invite.status == ConnectionStatus.Pending) FlowRow(horizontalArrangement = Arrangement.spacedBy(Space.small)) {
            TextButton(onClick = { onAction(PeopleCommand.ReplyInvite(invite.id, true)) }, enabled = !busy && controller.repository.connected) { Text("Accept group invite") }
            TextButton(onClick = { onAction(PeopleCommand.ReplyInvite(invite.id, false)) }, enabled = !busy && controller.repository.connected) { Text("Decline") }
        } else if (!invite.incoming && invite.status in listOf(ConnectionStatus.Draft, ConnectionStatus.Pending)) TextButton(
            onClick = { onAction(PeopleCommand.CancelInvite(invite.id)) }, enabled = !busy && (invite.status == ConnectionStatus.Draft || controller.repository.connected)) {
            Text(if (invite.status == ConnectionStatus.Draft) "Remove invitation draft" else "Revoke invitation")
        }
    }
}

@Composable internal fun EmailRequestScreen(title: String, field: String, controller: PeopleController, onBack: () -> Unit,
    onSave: suspend (String) -> String?) {
    var email by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    val state by controller.state.collectAsState()
    val scope = rememberCoroutineScope()
    FeaturePage(title, { if (!state.busy) onBack() }, email.isNotBlank(), primaryAction = {
        ErrorText(error)
        MetalButton(onClick = { scope.launch { error = onSave(email) } }, enabled = !state.loading && !state.busy && !state.loadFailed,
            modifier = Modifier.fillMaxWidth()) { Text(if (state.busy) "Saving…" else if (controller.repository.connected) "Send request" else "Save unsent request") }
    }) {
        item { PeopleNotice(controller) }
        item { ChromeTextField(email, { email = it; error = null }, Modifier.fillMaxWidth(), enabled = !state.busy,
            label = { Text(field) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), singleLine = true, isError = error != null) }
    }
}
