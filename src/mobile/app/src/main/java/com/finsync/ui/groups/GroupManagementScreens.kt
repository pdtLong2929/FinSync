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
fun CreateGroupScreen(onBack: () -> Unit, onSave: suspend (String, String, String) -> String?) =
    CreateGroupScreen(onBack, null, onSave)

@Composable
fun CreateGroupScreen(onBack: () -> Unit, existing: ExpenseGroup?,
                      onSave: suspend (String, String, String) -> String?) {
    val server = com.finsync.groups.LocalServerGroups.current
    var name by rememberSaveable { mutableStateOf(existing?.name.orEmpty()) }
    var description by rememberSaveable { mutableStateOf(existing?.description.orEmpty()) }
    var type by rememberSaveable { mutableStateOf(existing?.type ?: "HOME") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val dirty = name != existing?.name.orEmpty() || description != existing?.description.orEmpty() || type != (existing?.type ?: "HOME")
    FeaturePage(if (existing == null) "Create group" else "Edit group", { if (!saving) onBack() }, dirty, primaryAction = {
        ErrorText(error)
        MetalButton(onClick = {
            if (!saving) {
                saving = true
                scope.launch {
                    error = onSave(name, type, description)
                    if (error != null) saving = false
                }
            }
        }, enabled = !saving, modifier = Modifier.fillMaxWidth()) { Text(if (saving) "Saving…" else "Save group") }
    }) {
        item { GroupStorageNotice() }
        item { ChromeTextField(name, { name = it; error = null }, Modifier.fillMaxWidth(), enabled = !saving, label = { Text("Group name") }, singleLine = true) }
        if (server == null) item { OptionPicker("Group type", groupTypeLabel(type), groupTypes.values.toList(), { label -> type = groupTypes.entries.first { it.value == label }.key }) }
        item { ChromeTextField(description, { description = it; error = null }, Modifier.fillMaxWidth(), enabled = !saving, label = { Text("Description (optional)") }) }
        item { Text("VND · You are the owner. Add members after creating the group.") }
    }
}

@Composable
fun MembersScreen(group: ExpenseGroup, state: FinanceState, onBack: () -> Unit, onChange: suspend (String, String, String?) -> String?, onSection: ((String) -> Unit)? = null,
    onInvite: (() -> Unit)? = null, onEditMember: (suspend (String, String, Boolean) -> String?)? = null) {
    var name by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var remove by rememberSaveable { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }
    var success by remember { mutableStateOf<String?>(null) }
    var editing by rememberSaveable { mutableStateOf<String?>(null) }
    var editedName by rememberSaveable { mutableStateOf("") }
    var editError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val balances = currentGroupBalances(group, state)
    FeaturePage("Group members", { if (!saving) onBack() }, name.isNotBlank()) {
        item { GroupStorageNotice(); Text(if (com.finsync.groups.LocalServerGroups.current != null) "Guest members are saved to this group's server ledger."
            else if (LocalDeviceGroupLedger.current) "Add people to this device's shared ledger."
            else "Local invite code: ${group.invite} · No online sharing") }
        if (onSection != null) item { GroupSectionTabs("Members", onSection) }
        if (onInvite != null && group.owner == "me") item { MetalButton(onClick = onInvite, enabled = !saving,
            modifier = Modifier.fillMaxWidth()) { Text("Invite members") }
            Text(if (com.finsync.groups.LocalServerGroups.current != null) "Adding a guest does not register an account. Share an invitation token to invite a real user." else "Adding a local member below does not create an account or send an invitation.", style = MaterialTheme.typography.bodySmall)
        }
        items(group.members, key = { it.id }) { member ->
            Column {
                LedgerRow(member.name, if (member.id == group.owner) "Owner" else if (member.active) "Member" else "Former member",
                    balances.getValue(member.id).let { if (it >= 0) "Is owed ${formatVnd(it)}" else "Owes ${formatVnd(-it)}" })
                if (group.owner == "me" && member.id != "me" && member.active) TextButton(onClick = { remove = member.id }, enabled = !saving) { Text("Remove ${member.name}") }
                if (onEditMember != null && group.owner == "me" && member.id != "me") TextButton(
                    onClick = { editing = member.id; editedName = member.name; editError = null }, enabled = !saving) {
                    Text(if (member.active) "Edit ${member.name}" else "Restore ${member.name}")
                }
            }
        }
        if (group.owner == "me") {
            item { ChromeTextField(name, { name = it; error = null; success = null }, Modifier.fillMaxWidth(), enabled = !saving, label = { Text("Member name") }, singleLine = true) }
            item { MetalButton(onClick = {
                if (!saving) {
                    saving = true
                    scope.launch {
                        try {
                            error = onChange(group.id, name, null)
                            if (error == null) { name = ""; success = "Member added" }
                        } finally { saving = false }
                    }
                }
            }, enabled = !saving) { Text(if (saving) "Saving…" else "Add member") } }
        } else item { Text("Only the owner can manage members.") }
        item { ErrorText(error) }
        success?.let { item { Text(it, color = LocalFinanceColors.current.income) } }
    }
    if (remove != null) MetalDialog(onDismissRequest = { remove = null }, title = { Text("Remove member?") },
        text = { Text("History is retained. Members with unpaid balances cannot be removed.") },
        confirmButton = { TextButton(onClick = {
            val member = remove
            if (!saving && member != null) {
                saving = true; remove = null
                scope.launch {
                    try { error = onChange(group.id, "", member); if (error == null) success = "Member removed" }
                    finally { saving = false }
                }
            }
        }, enabled = !saving) { Text("Remove") } },
        dismissButton = { TextButton(onClick = { remove = null }) { Text("Cancel") } })
    editing?.let { id ->
        val member = group.members.find { it.id == id }
        MetalDialog(onDismissRequest = { if (!saving) editing = null }, title = { Text(if (member?.active == false) "Restore member?" else "Edit member name") },
            text = { Column(verticalArrangement = Arrangement.spacedBy(Space.small)) {
                val dialogFocus = androidx.compose.ui.platform.LocalFocusManager.current
                val dialogKeyboard = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
                ChromeTextField(editedName, { editedName = it; editError = null }, label = { Text("Member display name") }, singleLine = true, enabled = !saving,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(onDone = { dialogFocus.clearFocus(); dialogKeyboard?.hide() }))
                Text("This is a name in your local ledger. It doesn't change their account profile.", style = MaterialTheme.typography.bodySmall)
                ErrorText(editError)
            } }, confirmButton = { TextButton(onClick = {
                if (!saving && onEditMember != null) {
                    saving = true
                    scope.launch { try {
                        editError = onEditMember(id, editedName, member?.active == false)
                        if (editError == null) { editing = null; success = "Member updated" }
                    } finally { saving = false } }
                }
            }, enabled = !saving) { Text(if (saving) "Saving…" else "Save member") } },
            dismissButton = { TextButton(onClick = { editing = null }, enabled = !saving) { Text("Cancel") } })
    }
}
