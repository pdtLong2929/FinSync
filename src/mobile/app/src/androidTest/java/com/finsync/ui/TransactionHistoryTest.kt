package com.finsync.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.finsync.data.DemoFixtures
import com.finsync.domain.EntryType
import com.finsync.domain.LedgerEntry
import com.finsync.ui.components.LoadState
import com.finsync.ui.theme.FinSyncTheme
import com.finsync.ui.transactions.TransactionHistoryScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TransactionHistoryTest {
    @get:Rule val compose = createComposeRule()

    private fun sampleState() = FinanceState(entries = listOf(
        LedgerEntry("coffee", "cash", EntryType.EXPENSE, 50_000, "Food", DemoFixtures.today, "Coffee run"),
        LedgerEntry("salary", "bank", EntryType.INCOME, 1_000_000, "Salary", DemoFixtures.today.minusDays(2), "October pay"),
    ))

    @Test fun historyShowsWalletSearchesAndAppliesAndResetsFilters() {
        var opened: String? = null
        compose.setContent { FinSyncTheme {
            TransactionHistoryScreen(sampleState(), false, {}, {}, { opened = it }, {})
        } }

        compose.onNodeWithText("Coffee run").assertIsDisplayed()
        compose.onNodeWithText("Food · Everyday cash").assertIsDisplayed()
        compose.onNodeWithText("Filters").performClick()
        compose.onNodeWithText("Reset").assertIsDisplayed()
        compose.onNodeWithText("Apply").assertIsDisplayed()
        compose.onNodeWithText("Income").performClick()
        compose.onNodeWithText("Apply").performClick()
        compose.onNodeWithText("October pay").assertIsDisplayed()
        compose.onNodeWithText("Coffee run").assertDoesNotExist()

        compose.onNodeWithText("Filters · 1 active").performClick()
        compose.onNodeWithText("Reset").performClick()
        compose.onNodeWithText("Apply").performClick()
        compose.onNodeWithText("Coffee run").assertIsDisplayed()
        compose.onNodeWithText("Coffee run").performClick()
        compose.runOnIdle { assertEquals("coffee", opened) }
    }

    @Test fun historyDisplaysLoadingState() {
        compose.setContent { FinSyncTheme {
            TransactionHistoryScreen(sampleState(), false, {}, {}, {}, {}, mode = LoadState.Loading)
        } }
        compose.onNodeWithText("Loading your ledger…").assertIsDisplayed()
    }

    @Test fun historyRetriesErrorState() {
        var retries = 0
        compose.setContent { FinSyncTheme {
            TransactionHistoryScreen(sampleState(), false, {}, {}, {}, {}, mode = LoadState.Error, onRetry = { retries++ })
        } }
        compose.onNodeWithText("Unable to load this view. Your entries are unchanged.").assertIsDisplayed()
        compose.onNodeWithText("Try again").performClick()
        compose.runOnIdle { assertEquals(1, retries) }
    }
}
