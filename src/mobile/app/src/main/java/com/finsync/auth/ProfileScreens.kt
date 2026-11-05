package com.finsync.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import com.finsync.R
import com.finsync.ui.components.*
import com.finsync.ui.theme.LiquidTokens
import com.finsync.ui.theme.Space
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

@Composable
fun ProfileScreen(user: AuthUser, profile: LocalProfile?, loading: Boolean, loadError: Boolean,
    store: LocalProfileStore, onRetry: () -> Unit, onSaved: (LocalProfile) -> Unit,
    onBack: () -> Unit, onWorkspace: () -> Unit, onSignOut: () -> Unit,
    accountApi: AuthRepository? = null, remoteBusy: Boolean = false, remoteFailure: AuthFailure? = null,
    onIdentity: (AuthUser) -> Unit = {}, onExpired: () -> Unit = {}) {
    var editing by rememberSaveable(user.id) { mutableStateOf(false) }
    var confirmSignOut by rememberSaveable { mutableStateOf(false) }
    var saved by rememberSaveable { mutableStateOf(false) }
    if (editing && profile != null) {
        ProfileEditor(profile, user.email, store, accountApi, onIdentity, onExpired,
            onReload = { editing = false; onRetry() }, onBack = { editing = false }, onSaved = {
            onSaved(it); editing = false; saved = true
        })
        return
    }
    FeaturePage("My profile", onBack, compact = true) {
        item { FinanceCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.medium)) {
                UserAvatar(profile?.displayName ?: user.displayName, profile?.avatarPath, Modifier.size(LiquidTokens.chromeSize))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Space.small)) {
                    Text(profile?.displayName ?: user.displayName, style = MaterialTheme.typography.titleLarge)
                    Text(user.email, style = MaterialTheme.typography.bodyMedium)
                    Text("Signed in", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                }
            }
        } }
        if (loading || remoteBusy) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
        if (remoteFailure != null) item { ErrorText("Couldn't sign out on the server. Check your connection and try again.") }
        if (loadError) item { FinanceCard {
            ErrorText(if (accountApi != null) "Couldn't load your account. Check your connection and try again." else "Couldn't read your profile on this device.")
            TextButton(onClick = onRetry) { Text("Try again") }
        } }
        if (saved) item { Text(if (accountApi != null) "Account profile saved." else "Profile saved on this device.", color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium) }
        item { Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Personal information", Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = { editing = true; saved = false }, enabled = profile != null && !loading && !remoteBusy) {
                Icon(painterResource(R.drawable.ic_profile_edit), null)
                Spacer(Modifier.width(Space.small)); Text("Edit profile")
            }
        } }
        item { FinanceCard {
            ProfileDetail("Display name", profile?.displayName ?: user.displayName)
            HorizontalDivider()
            ProfileDetail("Email address", user.email)
            HorizontalDivider()
            ProfileDetail("Phone number", profile?.phone.orEmpty().ifBlank { "Not added" })
            HorizontalDivider()
            ProfileDetail("Date of birth", profile?.birthday.orEmpty().ifBlank { "Not added" })
            HorizontalDivider()
            ProfileDetail("About you", profile?.bio.orEmpty().ifBlank { "Add a little about yourself" })
        } }
        item { FinanceCard {
            Text(if (accountApi != null) "Account sync" else "On this device", style = MaterialTheme.typography.titleSmall)
            Text(if (accountApi != null) "Name and phone sync with your account. Photo, date of birth and about you stay on this device." else "Your photo and profile edits stay on this device. Sync across devices isn't available yet.",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } }
        item { FinanceCard {
            Text("Account & security", style = MaterialTheme.typography.titleMedium)
            Text("Email and password use your registered account. You'll need to sign in again when the app restarts.",
                style = MaterialTheme.typography.bodyMedium)
            Text("Password recovery is not available yet.", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        } }
        item { MetalButton(onClick = onWorkspace, enabled = !remoteBusy, modifier = Modifier.fillMaxWidth()) { Text("Open sample workspace") } }
        item { Text("Financial screens still use sample data.", style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant) }
        item { MetalOutlineButton(onClick = { confirmSignOut = true }, enabled = !remoteBusy, modifier = Modifier.fillMaxWidth()) {
            Text(if (accountApi != null) "Sign out of account" else "Sign out on this device")
        } }
    }
    if (confirmSignOut) MetalDialog(onDismissRequest = { confirmSignOut = false },
        title = { Text("Sign out?") }, text = { Text("You'll need your email and password to sign in again. Your saved profile stays on this device.") },
        confirmButton = { TextButton(onClick = { confirmSignOut = false; onSignOut() }) { Text("Sign out") } },
        dismissButton = { TextButton(onClick = { confirmSignOut = false }) { Text("Stay signed in") } })
}

@Composable
private fun ProfileDetail(label: String, value: String) {
    Column(Modifier.fillMaxWidth().padding(vertical = Space.small), verticalArrangement = Arrangement.spacedBy(Space.small)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ProfileEditor(initial: LocalProfile, email: String, store: LocalProfileStore,
    accountApi: AuthRepository?, onIdentity: (AuthUser) -> Unit, onExpired: () -> Unit, onReload: () -> Unit,
    onBack: () -> Unit, onSaved: (LocalProfile) -> Unit) {
    var name by rememberSaveable(initial.userId) { mutableStateOf(initial.displayName) }
    var phone by rememberSaveable(initial.userId) { mutableStateOf(initial.phone) }
    var birthday by rememberSaveable(initial.userId) { mutableStateOf(initial.birthday) }
    var bio by rememberSaveable(initial.userId) { mutableStateOf(initial.bio) }
    var avatar by rememberSaveable(initial.userId) { mutableStateOf(initial.avatarPath) }
    var errors by remember { mutableStateOf(ProfileErrors()) }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var version by remember { mutableStateOf(initial.serverVersion) }
    var conflict by remember { mutableStateOf(false) }
    var confirmReload by remember { mutableStateOf(false) }
    var accountSaved by remember { mutableStateOf(false) }
    val importedPhotos = remember { mutableSetOf<String>() }
    var keptPhoto by remember { mutableStateOf(initial.avatarPath) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        try { awaitCancellation() }
        finally { withContext(NonCancellable) {
            importedPhotos.filter { it != keptPhoto }.forEach { store.discardAvatar(it) }
        } }
    }
    val draft = initial.copy(displayName = name, phone = phone, birthday = birthday, bio = bio, avatarPath = avatar, serverVersion = version)
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) scope.launch {
            busy = true; error = null
            try { avatar = store.importAvatar(uri).also { importedPhotos.add(it) } }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { error = "Couldn't open that photo. Choose another image." }
            finally { busy = false }
        }
    }
    val save = {
        errors = validateProfile(draft)
        if (!errors.any && !busy) scope.launch {
            busy = true; error = null; conflict = false
            try {
                val synced = if (accountApi != null) {
                    val server = accountApi.updateProfile(draft)
                    version = server.version; accountSaved = true
                    onIdentity(server)
                    draft.copy(displayName = server.displayName, phone = server.phoneNumber.orEmpty(), serverVersion = server.version)
                } else draft
                val result = store.save(synced)
                keptPhoto = result.avatarPath
                if (initial.avatarPath != result.avatarPath) store.discardAvatar(initial.avatarPath)
                onSaved(result)
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (failed: AccountApiException) {
                conflict = failed.reason == AuthFailure.ProfileConflict
                error = when (failed.reason) {
                    AuthFailure.ProfileConflict -> "Your profile changed elsewhere. Reload the latest profile and review it before saving."
                    AuthFailure.Offline -> "You're offline. Your draft is still here. Check your connection and try again."
                    AuthFailure.SessionExpired, AuthFailure.AccountLocked -> { onExpired(); "Sign in again to continue." }
                    AuthFailure.InvalidInput -> "Check your name and phone number. Your draft is still here."
                    else -> "Account service is unavailable. Your draft is still here. Try again."
                }
            }
            catch (_: Exception) { error = if (accountSaved) "Account saved, but device-only details couldn't be saved. Try again." else "Couldn't save your profile. Your changes are still here. Try again." }
            finally { busy = false }
        }
        Unit
    }
    FeaturePage("Edit profile", { if (!busy) onBack() }, dirty = draft != initial, compact = true, primaryAction = {
        error?.let { ErrorText(it) }
        if (conflict) TextButton(onClick = { confirmReload = true }, enabled = !busy) { Text("Reload latest profile") }
        if (busy) LinearProgressIndicator(Modifier.fillMaxWidth())
        MetalButton(onClick = save, enabled = !busy, modifier = Modifier.fillMaxWidth()) {
            Text(if (busy) "Please wait..." else if (accountApi != null) "Save profile" else "Save on this device")
        }
    }) {
        item { Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Space.small)) {
            UserAvatar(name, avatar, Modifier.size(LiquidTokens.chromeSize))
            TextButton(onClick = { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, enabled = !busy) {
                Text("Choose photo")
            }
            if (avatar != null) TextButton(onClick = { avatar = null }, enabled = !busy) { Text("Remove photo") }
            Text("Only the photo you select is shared with FinSync.", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        } }
        item { Text(if (accountApi != null) "Name and phone are saved to your account. Photo, birthday and about you are saved on this device." else "Photo and changes are saved on this device only.", style = MaterialTheme.typography.bodyMedium) }
        item { ChromeTextField(name, { name = it; errors = errors.copy(name = null) }, Modifier.fillMaxWidth(),
            label = { Text("Display name") }, singleLine = true, enabled = !busy, isError = errors.name != null,
            supportingText = { ErrorText(errors.name) }, keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next)) }
        item { ChromeTextField(email, {}, Modifier.fillMaxWidth(), label = { Text("Email address") }, readOnly = true,
            singleLine = true, supportingText = { Text("Your registered email. Email changes aren't available yet.") }) }
        item { ChromeTextField(phone, { phone = it; errors = errors.copy(phone = null) }, Modifier.fillMaxWidth(),
            label = { Text("Phone number (optional)") }, singleLine = true, enabled = !busy, isError = errors.phone != null,
            supportingText = { ErrorText(errors.phone) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)) }
        item { ChromeTextField(birthday, { birthday = it; errors = errors.copy(birthday = null) }, Modifier.fillMaxWidth(),
            label = { Text("Date of birth (optional)") }, singleLine = true, enabled = !busy, isError = errors.birthday != null,
            supportingText = { if (errors.birthday != null) ErrorText(errors.birthday) else Text("YYYY-MM-DD") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii, imeAction = ImeAction.Next)) }
        item { ChromeTextField(bio, { bio = it; errors = errors.copy(bio = null) }, Modifier.fillMaxWidth(),
            label = { Text("About you (optional)") }, minLines = 2, maxLines = 4, enabled = !busy, isError = errors.bio != null,
            supportingText = { if (errors.bio != null) ErrorText(errors.bio) else Text("${bio.length}/200") }) }
    }
    if (confirmReload) MetalDialog(onDismissRequest = { confirmReload = false },
        title = { Text("Load the latest profile?") },
        text = { Text("Your unsaved changes will be discarded. Review the latest account details before editing again.") },
        confirmButton = { TextButton(onClick = { confirmReload = false; onReload() }) { Text("Load latest") } },
        dismissButton = { TextButton(onClick = { confirmReload = false }) { Text("Keep editing") } })
}
