package com.finsync.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.test.junit4.ComposeTestRule

/** Poll real window IME visibility/insets on the UI thread; never substitute a fake IME. */
class TestKeyboard {
    private lateinit var focus: FocusManager
    private var controller: SoftwareKeyboardController? = null
    private lateinit var hostView: android.view.View

    private fun imeBottom(rule: ComposeTestRule): Int = rule.runOnIdle {
        val insets = androidx.core.view.ViewCompat.getRootWindowInsets(hostView)
        if (insets?.isVisible(androidx.core.view.WindowInsetsCompat.Type.ime()) == true)
            insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.ime()).bottom else 0
    }

    @Composable fun Observe() {
        val manager = LocalFocusManager.current
        val keyboard = LocalSoftwareKeyboardController.current
        val view = androidx.compose.ui.platform.LocalView.current
        androidx.compose.runtime.DisposableEffect(view) {
            val window = (view.context as android.app.Activity).window
            val previousMode = window.attributes.softInputMode
            // The generic Compose test Activity must match MainActivity's edge-to-edge window.
            androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)
            window.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            onDispose { window.setSoftInputMode(previousMode) }
        }
        SideEffect { focus = manager; controller = keyboard; hostView = view }
    }

    fun close(rule: ComposeTestRule) {
        rule.runOnIdle { focus.clearFocus(); controller?.hide() }
        // A floating/transitioning IME can report zero inset while still consuming Back.
        rule.waitUntil(timeoutMillis = 5_000) { rule.runOnIdle {
            val insets = androidx.core.view.ViewCompat.getRootWindowInsets(hostView)
            insets?.isVisible(androidx.core.view.WindowInsetsCompat.Type.ime()) != true
        } }
        rule.waitForIdle()
    }

    fun awaitOpen(rule: ComposeTestRule) {
        // Synthetic text input does not guarantee that Android requests the software IME.
        // Request it after the focused input session is idle, then assert real visible insets.
        rule.runOnIdle { controller?.show() }
        try {
            // Cold-start Gboard can report visible before its docked frame/insets arrive.
            // Keep both real visibility and a positive height mandatory.
            rule.waitUntil(timeoutMillis = 15_000) { imeBottom(rule) > 0 }
        } catch (failure: Throwable) {
            captureScreen("ime-open-diagnostic", rule, nativeWindow = true)
            val diagnostic = rule.runOnIdle {
                val insets = androidx.core.view.ViewCompat.getRootWindowInsets(hostView)
                "imeVisible=${insets?.isVisible(androidx.core.view.WindowInsetsCompat.Type.ime())} imeInsets=${insets?.getInsets(androidx.core.view.WindowInsetsCompat.Type.ime())} focus=${hostView.findFocus()}"
            }
            throw AssertionError("Real IME failed to open: $diagnostic", failure)
        }
        rule.waitForIdle()
    }
}
