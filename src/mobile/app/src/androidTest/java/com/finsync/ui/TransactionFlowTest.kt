package com.finsync.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.activity.compose.setContent
import com.finsync.MainActivity
import com.finsync.ui.dashboard.*
import com.finsync.ui.theme.FinSyncTheme
import org.junit.Rule
import org.junit.Test
import android.view.KeyEvent
import androidx.test.platform.app.InstrumentationRegistry

class TransactionFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val keyboard = TestKeyboard()
    @Test fun addAndEditTransactionAndGuardUnfinishedForm() {
        compose.activityRule.scenario.onActivity { activity -> activity.setContent {
            keyboard.Observe(); FinSyncTheme { FinSyncApp(DashboardUiState.Content(DemoDashboard.data)) }
        } }
        compose.onNodeWithContentDescription("Add expense").performClick()
        compose.onNodeWithText("Amount (VND)").performTextInput("50000")
        compose.onNodeWithText("Back").performClick()
        compose.onNodeWithText("Discard unfinished changes?").assertIsDisplayed()
        compose.onNodeWithText("Keep editing").performClick()
        keyboard.close(compose) // Finish IME reflow before scrolling the lazy form.
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Notes (optional)"))
        compose.onNodeWithText("Notes (optional)").performTextInput("Demo coffee")
        keyboard.close(compose)
        InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        try {
            compose.waitUntil(5_000) {
                runCatching { compose.onNodeWithText("Discard unfinished changes?").assertIsDisplayed() }.isSuccess
            }
        } catch (_: androidx.compose.ui.test.ComposeTimeoutException) {
            // Android may consume the first physical Back to finish dismissing the IME.
            // Confirm that no input/navigation was lost before requesting form Back again.
            compose.onNodeWithText("Notes (optional)").assertTextContains("Demo coffee")
            compose.onNodeWithText("Save transaction").assertIsDisplayed()
            InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
            compose.waitUntil(5_000) {
                runCatching { compose.onNodeWithText("Discard unfinished changes?").assertIsDisplayed() }.isSuccess
            }
        }
        compose.onNodeWithText("Discard unfinished changes?").assertIsDisplayed()
        compose.onNodeWithText("Keep editing").performClick()
        keyboard.close(compose)
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Notes (optional)"))
        compose.onNodeWithText("Notes (optional)").assertTextContains("Demo coffee")
        compose.onNodeWithText("Save transaction").performClick()
        compose.onNodeWithText("Search transactions").performTextInput("Demo coffee")
        keyboard.close(compose)
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Demo coffee") and !hasSetTextAction())
        compose.onNode(hasText("Demo coffee") and !hasSetTextAction()).performClick()
        compose.onNodeWithText("Edit transaction").performClick()
        compose.onNodeWithText("Amount (VND)").performTextReplacement("60000")
        keyboard.close(compose)
        compose.onNodeWithText("Save transaction").performClick()
        compose.onNodeWithText("60.000 ₫").assertIsDisplayed()
        compose.waitForIdle(); captureScreen("transaction-detail", compose)
    }
}
