package com.finsync.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.finsync.MainActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.finsync.data.DemoFixtures
import com.finsync.domain.EntryType
import com.finsync.ui.dashboard.*
import com.finsync.ui.theme.FinSyncTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ConvenienceTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private fun setContent(content: @Composable () -> Unit) {
        compose.activityRule.scenario.onActivity { it.setContent(content = content) }
    }

    @Test fun dailyExpenseUsesRecentDefaultsAndSavesWithKeyboardOpen() {
        val finance = FinanceViewModel()
        finance.saveTransaction(null, "20000", "bank", EntryType.EXPENSE, "Transport", DemoFixtures.today.toString(), "Seed")
        val count = finance.uiState.value.entries.size
        val keyboard = TestKeyboard()
        setContent { keyboard.Observe(); Box(Modifier.width(320.dp).fillMaxHeight()) {
            FinSyncTheme { FinSyncApp(DashboardUiState.Content(DemoDashboard.data), finance = finance) }
        } }
        compose.onNodeWithContentDescription("Add expense").performClick()
        compose.onNodeWithText("Personal bank ledger").assertIsDisplayed()
        // Exercise the actual tap/input connection, not only the semantics click action.
        compose.onNodeWithText("Amount (VND)").performTouchInput { click() }
        compose.onNodeWithText("Amount (VND)").assertIsFocused()
        keyboard.awaitOpen(compose)
        compose.onNodeWithText("Amount (VND)").performTextInput("50000")
        compose.onNodeWithText("Amount (VND)").assertIsFocused()
        keyboard.awaitOpen(compose)
        compose.onNodeWithText("Save transaction").assertIsDisplayed()
        compose.waitForIdle(); captureScreen("quick-expense-keyboard", compose, nativeWindow = true)
        compose.onNodeWithText("Save transaction").performClick()
        compose.onNodeWithText("Personal transactions").assertIsDisplayed()
        compose.runOnIdle {
            val entry = finance.uiState.value.entries.last()
            assertEquals(count + 1, finance.uiState.value.entries.size)
            assertEquals(50000L, entry.amount)
            assertEquals("bank", entry.walletId)
            assertEquals("Transport", entry.category)
            assertEquals(DemoFixtures.today, entry.date)
        }
    }

    @Test fun searchSurvivesDetailsAndDeletionCanBeUndone() {
        val keyboard = TestKeyboard()
        setContent { keyboard.Observe(); FinSyncTheme { FinSyncApp(DashboardUiState.Content(DemoDashboard.data)) } }
        compose.onNodeWithText("Wallets").performClick()
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("All personal transactions"))
        compose.onNodeWithText("All personal transactions").performClick()
        compose.onNodeWithText("Search transactions").performTextInput("Lunch")
        keyboard.close(compose)
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Lunch") and !hasSetTextAction())
        compose.onNode(hasText("Lunch") and !hasSetTextAction()).performClick()
        compose.onNodeWithText("Back").performClick()
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Search transactions"))
        compose.onNodeWithText("Search transactions").assertTextContains("Lunch")
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Lunch") and !hasSetTextAction())
        compose.onNode(hasText("Lunch") and !hasSetTextAction()).performClick()
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Delete transaction"))
        compose.onNodeWithText("Delete transaction").performClick()
        compose.onNodeWithText("Delete").performClick()
        compose.onNodeWithText("Undo").performClick()
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Lunch") and !hasSetTextAction())
        compose.onNode(hasText("Lunch") and !hasSetTextAction()).assertIsDisplayed()
    }
}
