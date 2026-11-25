package com.finsync.bank

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.finsync.ui.components.*
import com.finsync.ui.dashboard.formatVnd
import com.finsync.ui.theme.Space
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalLayoutApi::class)
@Composable fun BankCaptureScreen(controller: BankController, onBack: () -> Unit) = BankCaptureScreen(controller, null, 0, onBack)

@OptIn(ExperimentalLayoutApi::class)
@Composable fun BankCaptureScreen(controller: BankController, requestedId: String?, requestToken: Int, onBack: () -> Unit) {
    val state by controller.state.collectAsState()
    var section by rememberSaveable { mutableStateOf("Inbox") }
    var selected by rememberSaveable { mutableStateOf<String?>(null) }
    var quick by rememberSaveable { mutableStateOf(true) }
    var draft by remember { mutableStateOf<BankQuickDraft?>(null) }
    LaunchedEffect(requestedId, requestToken, state.loading) {
        if (!state.loading && requestedId != null) state.data.candidates.find { it.id == requestedId }?.let {
            selected = it.id; quick = it.status == CaptureStatus.Pending; draft = null
        }
    }
    var feedback by rememberSaveable { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val captureSettings = remember { BankCaptureSettings(context) }
    var captureError by remember { mutableStateOf(captureSettings.hasCaptureError(controller.workspace)) }
    DisposableEffect(captureSettings) {
        val stop = captureSettings.observeChanges { captureError = captureSettings.hasCaptureError(controller.workspace) }
        onDispose { stop() }
    }
    if (section == "Setup") { BankSetupScreen(controller.workspace) { section = "Inbox" }; return }
    val candidate = state.data.candidates.find { it.id == selected }
    if (candidate != null && !quick) {
        BankReviewScreen(candidate, controller, draft, { selected = null }) { feedback = it; selected = null }
        return
    }
    if (candidate != null && quick) BankQuickSheet(candidate, controller, { selected = null }, { draft = it; quick = false }) { feedback = it; selected = null }
    FeaturePage("Bank notifications", onBack, compact = true) {
        item { FinanceCard {
            Text("Review before bookkeeping", style = MaterialTheme.typography.titleMedium)
            Text("Alerts are suggestions, not verified bank statements. Your wallet balance is a bookkeeping total, not a live bank balance.", style = MaterialTheme.typography.bodySmall)
            MetalOutlineButton(onClick = { section = "Setup" }, modifier = Modifier.fillMaxWidth()) { Text("Set up bank alerts") }
        } }
        if (state.loading) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
        if (captureError) item { FinanceCard {
            ErrorText("A bank alert couldn't be saved. Check your bank app and add that transaction manually. Nothing was booked from the failed capture.")
            TextButton(onClick = captureSettings::clearCaptureError) { Text("Dismiss capture notice") }
        } }
        if (state.loadFailed) item { ErrorText(state.error); TextButton(onClick = { scope.launch { controller.open() } }) { Text("Retry saved data") } }
        else if (!state.loading) {
            item { FlowRow(horizontalArrangement = Arrangement.spacedBy(Space.small)) {
                listOf("Inbox", "History").forEach { label -> FilterChip(selected = section == label, onClick = { section = label }, label = { Text(label) }) }
            } }
            feedback?.let { item { Text(it, color = MaterialTheme.colorScheme.primary) } }
            val alerts = state.data.candidates.filter { (it.status == CaptureStatus.Pending) == (section == "Inbox") }.sortedByDescending { it.observedAt }
            if (alerts.isEmpty()) item { FinanceCard {
                Text(if (section == "Inbox") "No alerts to review" else "No handled alerts yet", style = MaterialTheme.typography.titleMedium)
                Text("Choose banking apps and grant notification access. Only new supported VND transaction alerts will appear; OTPs and other apps are ignored.", style = MaterialTheme.typography.bodySmall)
            } }
            items(alerts, key = { it.id }) { alert ->
                LedgerRow(bankAppName(context, alert.sourcePackage),
                    "${Instant.ofEpochMilli(alert.observedAt).atZone(ZoneId.systemDefault()).toLocalDate()} · ${alert.status.name}",
                    alert.amount?.let { if (section == "History") "Detected ${formatVnd(it)}" else formatVnd(it) } ?: "Amount needs review", onClick = { selected = alert.id; quick = alert.status == CaptureStatus.Pending; draft = null })
            }
        }
    }
}

internal fun bankAppName(context: android.content.Context, packageName: String): String = runCatching {
    context.packageManager.getApplicationLabel(context.packageManager.getApplicationInfo(packageName, 0)).toString()
}.getOrDefault(packageName)

private data class BankApp(val packageName: String, val label: String)
@Composable fun BankSetupScreen(workspace: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val settings = remember { BankCaptureSettings(context) }
    var config by remember { mutableStateOf(settings.read()) }
    var chosen by remember { mutableStateOf(if (config.workspace == workspace) config.packages else emptySet()) }
    var acknowledged by rememberSaveable { mutableStateOf(false) }
    var access by remember { mutableStateOf(BankCaptureSettings.hasAccess(context)) }
    var reminders by remember { mutableStateOf(context.getSystemService(android.app.NotificationManager::class.java).areNotificationsEnabled()) }
    var apps by remember { mutableStateOf<List<BankApp>>(emptyList()) }
    var appsLoading by remember { mutableStateOf(true) }
    var appsFailed by remember { mutableStateOf(false) }
    var reload by remember { mutableIntStateOf(0) }
    var search by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var success by rememberSaveable { mutableStateOf<String?>(null) }
    val connected by BankNotificationListener.connected.collectAsState()
    fun acknowledge(value: Boolean) {
        try {
            if (!value && config.enabled && config.workspace == workspace) { settings.save(config.copy(enabled = false)); config = settings.read() }
            acknowledged = value
        } catch (_: Exception) { error = "Couldn't pause capture. Revoke notification access in Android settings." }
    }
    val notificationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> reminders = granted }
    val lifecycle = LocalLifecycleOwner.current
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_RESUME) {
            config = settings.read(); access = BankCaptureSettings.hasAccess(context)
            reminders = context.getSystemService(android.app.NotificationManager::class.java).areNotificationsEnabled()
        } }
        lifecycle.lifecycle.addObserver(observer); onDispose { lifecycle.lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(reload) {
        appsLoading = true; appsFailed = false
        try { apps = withContext(Dispatchers.IO) {
            context.packageManager.queryIntentActivities(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0)
                .filter { it.activityInfo.packageName != context.packageName }
                .map { BankApp(it.activityInfo.packageName, it.loadLabel(context.packageManager).toString()) }
                .distinctBy { it.packageName }.sortedBy { it.label }
        } } catch (_: Exception) { appsFailed = true } finally { appsLoading = false }
    }
    FeaturePage("Bank alert setup", onBack, dirty = chosen != (if (config.workspace == workspace) config.packages else emptySet()), primaryAction = {
        ErrorText(error)
        MetalButton(onClick = {
            try {
                require(acknowledged) { "Read and acknowledge the privacy notice first." }
                require(access) { "Grant notification access in Android settings first." }
                require(chosen.isNotEmpty()) { "Choose at least one banking app." }
                val next = CaptureConfig(workspace, true, chosen, System.currentTimeMillis())
                settings.save(next); config = next; success = "Capture enabled · New alerts only"; error = null
            } catch (invalid: IllegalArgumentException) { error = invalid.message }
              catch (_: Exception) { error = "Couldn't save capture settings. Try again." }
        }, modifier = Modifier.fillMaxWidth(), enabled = !appsLoading) { Text("Enable selected bank apps") }
    }) {
        item { FinanceCard {
            Text(if (config.enabled && config.workspace == workspace && access) "Capture enabled" else "Capture paused", style = MaterialTheme.typography.titleMedium)
            Text("Android notification access can expose notification content. FinSync processes only apps you select below, skips OTPs and never uploads or stores the original notification text.", style = MaterialTheme.typography.bodySmall)
            Text("Amounts may be hidden or unrecognized. Confirm every alert before it affects your ledger. Existing sample/bookkeeping balances are not bank balances.", style = MaterialTheme.typography.bodySmall)
            Row { Checkbox(acknowledged, ::acknowledge, Modifier.semantics { contentDescription = "Acknowledge bank notification privacy notice" }); TextButton(onClick = { acknowledge(!acknowledged) }) { Text("I understand and agree") } }
            if (config.enabled && config.workspace == workspace) TextButton(onClick = {
                try { settings.save(config.copy(enabled = false)); config = settings.read(); success = "Capture paused" }
                catch (_: Exception) { error = "Couldn't pause capture. Revoke access in Android settings." }
            }) { Text("Pause capture") }
        } }
        item { FinanceCard {
            Text("1 · Notification access", style = MaterialTheme.typography.titleSmall)
            Text(if (access) "Access granted" else "Access not granted · Capture cannot run")
            if (access && !connected) TextButton(onClick = { android.service.notification.NotificationListenerService.requestRebind(ComponentName(context, BankNotificationListener::class.java)) }) { Text("Reconnect capture service") }
            MetalOutlineButton(onClick = {
                try { context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }
                catch (_: Exception) { error = "Open Android settings → Special app access → Notification access." }
            }, enabled = acknowledged) { Text(if (access) "Manage notification access" else "Grant notification access") }
            Text("You can revoke this permission at any time in Android settings.", style = MaterialTheme.typography.bodySmall)
        } }
        item { FinanceCard {
            Text("2 · Review reminders", style = MaterialTheme.typography.titleSmall)
            Text(if (reminders) "Reminders allowed" else "Reminders disabled · Alerts still stay in the inbox")
            TextButton(onClick = {
                if (Build.VERSION.SDK_INT >= 33 && context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
                    notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                else context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName))
            }) { Text("Enable review reminders") }
        } }
        item { Text("3 · Choose banking apps", style = MaterialTheme.typography.titleMedium)
            Text("Only select your banking apps. App names and package IDs stay on this device.", style = MaterialTheme.typography.bodySmall)
            ChromeTextField(search, { search = it }, Modifier.fillMaxWidth(), label = { Text("Search installed apps") }, singleLine = true)
        }
        if (appsLoading) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
        if (appsFailed) item { ErrorText("Couldn't list installed apps."); TextButton(onClick = { reload++ }) { Text("Retry apps") } }
        val filtered = apps.filter { it.label.contains(search, true) || it.packageName.contains(search, true) }
        if (!appsLoading && !appsFailed && filtered.isEmpty()) item { Text("No apps match. Install your banking app on this Android device or clear the search.") }
        items(filtered, key = { it.packageName }) { app -> FinanceCard {
            Text(app.label, style = MaterialTheme.typography.titleSmall); Text(app.packageName, style = MaterialTheme.typography.bodySmall)
            Row { Checkbox(app.packageName in chosen, { checked -> chosen = if (checked) chosen + app.packageName else chosen - app.packageName }, Modifier.semantics { contentDescription = "Select banking app ${app.label}" })
                TextButton(onClick = { chosen = if (app.packageName in chosen) chosen - app.packageName else chosen + app.packageName }) { Text(if (app.packageName in chosen) "Selected" else "Select ${app.label}") } }
        } }
        success?.let { item { Text(it, color = MaterialTheme.colorScheme.primary) } }
    }
}
