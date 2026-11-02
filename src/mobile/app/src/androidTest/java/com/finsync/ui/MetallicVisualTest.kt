package com.finsync.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import com.finsync.domain.EntryType
import com.finsync.ui.dashboard.*
import com.finsync.ui.groups.*
import com.finsync.ui.reports.*
import com.finsync.ui.theme.FinSyncTheme
import com.finsync.ui.transactions.*
import com.finsync.ui.wallets.*
import org.junit.Rule
import org.junit.Test

/** Real emulator captures; interaction regressions remain in the dedicated flow tests. */
class MetallicVisualTest {
    @get:Rule val compose = createComposeRule()

    @Test fun captureLightAndDarkScreenMatrix() {
        var page by mutableStateOf("dashboard")
        var dark by mutableStateOf(false)
        val state = FinanceState()
        compose.setContent {
            FinSyncTheme(darkTheme = dark) {
                key(page, dark) {
                    Surface(Modifier.fillMaxSize().safeDrawingPadding()) {
                        when (page) {
                            "dashboard" -> FinSyncApp(DashboardUiState.Content(DemoDashboard.data))
                            "wallets" -> WalletListScreen(state, false, {}, {}, {})
                            "wallet-detail" -> WalletDetailsScreen(state.wallets.first(), state.entries, false, {}, {}, {}, {}, {})
                            "transaction-form" -> TransactionFormScreen(state, null, {}, { _, _, _, _, _, _ -> null }, { _, _ -> null })
                            "history" -> TransactionHistoryScreen(state, false, {}, {}, {}, {})
                            "groups" -> GroupListScreen(state, {}, {}, { null })
                            "group-detail" -> GroupDetailsScreen(state.groups.first(), state, {}, {})
                            "debts" -> DebtScreen(state.groups.first(), state, {}, {})
                            "reports" -> ReportsScreen(state, "", {})
                            "budgets" -> BudgetScreen(state, {}, {})
                            "savings" -> GoalsScreen(state.goals, {}, {})
                            "insights" -> InsightsScreen({})
                        }
                    }
                }
            }
        }
        for (night in listOf(false, true)) {
            for (screen in listOf("dashboard", "wallets", "wallet-detail", "transaction-form", "history", "groups", "group-detail", "debts", "reports", "budgets", "savings", "insights")) {
                compose.runOnIdle { dark = night; page = screen }
                compose.waitForIdle()
                captureScreen("titanium-$screen-${if (night) "dark" else "light"}", compose)
            }
        }
    }
}
