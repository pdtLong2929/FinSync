package com.finsync.bank

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.finsync.ui.components.LocalAtmosphereProvided

/** An incoming alert overlays the current page so existing forms/navigation survive. */
@Composable internal fun BankNotificationPrompt(candidate: BankCandidate, controller: BankController,
    onDismiss: () -> Unit, onSaved: (String) -> Unit) {
    val state by controller.state.collectAsState()
    var advanced by rememberSaveable(candidate.id) { mutableStateOf(candidate.status != CaptureStatus.Pending) }
    var draft by remember(candidate.id) { mutableStateOf<BankQuickDraft?>(null) }
    if (!advanced) BankQuickSheet(candidate, controller, onDismiss, { draft = it; advanced = true }, onSaved)
    else Dialog(onDismissRequest = { if (!state.busy) onDismiss() }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        CompositionLocalProvider(LocalAtmosphereProvided provides false) {
            Box(Modifier.fillMaxSize().safeDrawingPadding().testTag("bank-advanced-overlay")) {
                BankReviewScreen(candidate, controller, draft, onDismiss, onSaved)
            }
        }
    }
}
