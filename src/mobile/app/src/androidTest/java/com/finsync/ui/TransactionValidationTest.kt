package com.finsync.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.finsync.data.DemoFixtures
import com.finsync.ui.theme.FinSyncTheme
import com.finsync.ui.transactions.TransactionFormScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TransactionValidationTest {
    @get:Rule val compose = createComposeRule()
    private val keyboard = TestKeyboard()

    @Test fun invalidFieldsAreReachableAtLargeTextAndSaveCanRetry() {
        var calls = 0
        compose.setContent {
            keyboard.Observe()
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 1.5f)) {
                // FeaturePage receives system-bar padding from the production app shell.
                FinSyncTheme { Box(Modifier.width(320.dp).fillMaxHeight().safeDrawingPadding()) {
                    TransactionFormScreen(FinanceState(), null, {}, { _, _, _, _, _, _ ->
                        calls++; if (calls == 1) "Unable to save. Try again." else null
                    }, { _, _ -> null })
                } }
            }
        }
        compose.onNodeWithText("Save transaction").performClick()
        compose.onNodeWithText("Enter a positive whole VND amount", substring = true).assertIsDisplayed()
        assertEquals(0, calls)
        captureScreen("transaction-inline-error-large", compose, nativeWindow = true)
        compose.onNodeWithText("Amount (VND)").performTextInput("50000")
        keyboard.close(compose)
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Date (YYYY-MM-DD)"))
        compose.onNodeWithText("Date (YYYY-MM-DD)").performTextReplacement("invalid")
        keyboard.close(compose)
        compose.onNodeWithText("Save transaction").performClick()
        compose.onNodeWithText("Enter a valid date in YYYY-MM-DD format.").assertIsDisplayed()
        assertEquals(0, calls)
        compose.onNodeWithText("Date (YYYY-MM-DD)").performTextReplacement(DemoFixtures.today.toString())
        keyboard.close(compose)
        compose.onNodeWithText("Save transaction").performClick()
        compose.onNodeWithText("Unable to save. Try again.").assertIsDisplayed()
        compose.onNodeWithText("Save transaction").assertIsEnabled().performClick()
        compose.onNodeWithText("Saving…").assertIsNotEnabled()
        assertEquals(2, calls)
    }

    @Test fun repeatedSaveBeforeNavigationWritesOnlyOnce() {
        val finance = FinanceViewModel()
        val count = finance.uiState.value.entries.size
        val snapshot = finance.uiState.value
        compose.setContent { keyboard.Observe(); FinSyncTheme {
            TransactionFormScreen(snapshot, null, {}, { a, w, t, c, d, n ->
                finance.saveTransaction(null, a, w, t, c, d, n)
            }, finance::addCategory)
        } }
        compose.onNodeWithText("Amount (VND)").performTextInput("12000")
        keyboard.close(compose)
        // Keep the form mounted instead of navigating: the second tap must not write.
        compose.onNodeWithText("Save transaction").performTouchInput { doubleClick() }
        compose.onNodeWithText("Saving…").assertIsNotEnabled()
        compose.runOnIdle { assertEquals(count + 1, finance.uiState.value.entries.size) }
    }
}
