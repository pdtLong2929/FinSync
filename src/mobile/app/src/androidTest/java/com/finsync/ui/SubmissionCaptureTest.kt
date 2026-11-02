package com.finsync.ui

import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.finsync.auth.*
import com.finsync.domain.*
import com.finsync.ui.components.*
import com.finsync.ui.dashboard.*
import com.finsync.ui.groups.*
import com.finsync.ui.reports.*
import com.finsync.ui.transactions.*
import com.finsync.ui.wallets.*
import com.finsync.ui.theme.FinSyncTheme
import org.junit.Rule
import org.junit.Test

/** Submission screenshots of real composables and existing sample fixtures; no production changes. */
class SubmissionCaptureTest {
    @get:Rule val compose = createComposeRule()
    @Test fun captureAdditionalFeatures() {
        var page by mutableStateOf("transfer")
        val finance = FinanceViewModel()
        val keyboard = TestKeyboard()
        compose.setContent { FinSyncTheme {
            keyboard.Observe()
            val state by finance.uiState.collectAsState()
            CompositionLocalProvider(LocalCategories provides state.categories) { key(page) {
                when(page) {
                    "transfer" -> TransferScreen(state, {}, {}, finance::transferWallets)
                    "transfer-history" -> TransferHistoryScreen(state, false, {})
                    "split" -> GroupFlow(state, finance, "group/home/expense", {})
                    "settlement" -> GroupFlow(state, finance, "group/home/settle/linh/me", {})
                    "income" -> TransactionFormScreen(state, null, {}, { _, _, _, _, _, _ -> null }, { _, _ -> null })
                    "style" -> CategorySelectionScreen(state.categories.filter { it.type == EntryType.EXPENSE }, {}, {}, { null })
                    "wallets" -> WalletListScreen(state, false, {}, {}, {})
                }
            } }
        } }
        compose.onNodeWithText("Amount (VND)").performTextInput("100000")
        keyboard.close(compose)
        compose.onNodeWithText("Review transfer").performClick()
        captureScreen("submission-transfer-review", compose)
        compose.onNodeWithText("Confirm transfer").performClick()
        captureScreen("submission-transfer-result", compose)
        compose.runOnIdle { page = "transfer-history" }
        captureScreen("submission-transfer-history", compose)
        compose.runOnIdle { page = "split" }
        compose.onNodeWithText("Expense title").performTextInput("Shared lunch")
        compose.onNodeWithText("Amount (VND)").performTextInput("300000")
        keyboard.close(compose)
        compose.onNodeWithText("Review split").performClick()
        captureScreen("submission-group-split", compose)
        compose.runOnIdle { page = "settlement" }
        captureScreen("submission-group-settlement", compose)
        compose.runOnIdle { page = "income" }
        compose.onNodeWithText("+  Income").performClick()
        captureScreen("submission-income", compose)
        compose.runOnIdle { page = "style" }
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Pastel color"))
        compose.onNodeWithText("Pastel color").performScrollTo()
        captureScreen("submission-category-style", compose)
        compose.runOnIdle { page = "wallets" }
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Everyday cash"))
        captureScreen("submission-wallet-cards", compose)
    }
    @Test fun captureSubmissionScreens() {
        var page by mutableStateOf("tabs")
        val finance = FinanceViewModel()
        val state = finance.uiState.value
        val wallet = state.wallets.first()
        val entry = state.entries.first { it.type == EntryType.EXPENSE }
        val group = state.groups.first()
        compose.setContent { FinSyncTheme {
            CompositionLocalProvider(LocalCategories provides state.categories) {
                key(page) {
                    when {
                        page == "tabs" -> FinSyncApp(DashboardUiState.Content(DemoDashboard.data), finance = finance)
                        page.startsWith("auth-") -> {
                            val scope = rememberCoroutineScope()
                            val auth = remember { AuthController(UnavailableAuthRepository(), scope).apply {
                                open(AuthPage.valueOf(page.removePrefix("auth-")))
                            } }
                            AuthHost(auth) { }
                        }
                        page == "transaction" -> TransactionFormScreen(state, null, {}, { _, _, _, _, _, _ -> null }, { _, _ -> null })
                        page == "categories" -> CategorySelectionScreen(state.categories.filter { it.type == EntryType.EXPENSE }, {}, {}, { null })
                        page == "history" -> TransactionHistoryScreen(state, false, {}, {}, {}, {})
                        page == "details" -> TransactionDetailsScreen(entry, wallet, false, {}, {}, {})
                        page == "wallet-new" -> WalletFormScreen(null, {}, { _, _, _ -> null })
                        page == "wallet-detail" -> WalletDetailsScreen(wallet, state.entries, false, {}, {}, {}, {}, {})
                        page == "wallet-history" -> WalletHistoryScreen(wallet, state.entries, false, {})
                        page == "transfer" -> TransferScreen(state, {}, {}, { _, _, _, _, _ -> null })
                        page.startsWith("group") -> GroupFlow(state, finance, when(page) {
                            "group-new" -> "group-new"
                            "group-detail" -> "group/${group.id}"
                            "group-members" -> "group/${group.id}/members"
                            "group-expense" -> "group/${group.id}/expense"
                            else -> "group/${group.id}/debts"
                        }, {})
                        page == "budgets" -> BudgetScreen(state, {}, {})
                        page == "budget-form" -> BudgetForm(state, state.budgets.first(), {}, { _, _, _, _ -> null })
                        page == "goals" -> GoalsScreen(state.goals, {}, {})
                        page == "goal-form" -> GoalForm(state.goals.first(), {}, { _, _, _, _, _ -> null })
                        page == "guide" -> InsightsScreen({})
                        page.startsWith("chart-") -> AnalyticsScreen(state, {}, {}, initialQuestion = AnalyticsQuestion.valueOf(page.removePrefix("chart-")))
                    }
                }
            }
        } }
        for(tab in listOf("Overview", "Wallets", "Groups", "Reports")) {
            compose.onNodeWithText(tab).performClick()
            captureScreen("submission-tab-${tab.lowercase()}", compose)
        }
        val pages = listOf("auth-Welcome", "auth-Login", "auth-Register", "auth-ForgotPassword",
            "transaction", "categories", "history", "details", "wallet-new", "wallet-detail", "wallet-history",
            "transfer", "group-new", "group-detail", "group-members", "group-expense", "group-debts",
            "budgets", "budget-form", "goals", "goal-form", "guide")
        pages.forEach { next ->
            compose.runOnIdle { page = next }
            captureScreen("submission-${next.lowercase()}", compose)
        }
        for(question in listOf(AnalyticsQuestion.Categories, AnalyticsQuestion.IncomeExpense, AnalyticsQuestion.Trend,
            AnalyticsQuestion.Composition, AnalyticsQuestion.CashFlow)) {
            compose.runOnIdle { page = "chart-${question.name}" }
            if(question.advanced) compose.onNodeWithText("Advanced").performClick()
            val kind = compatibleCharts(question).first()
            val label = "${kind.name} chart. Exact values and accessible selection in numbered rows below."
            compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasContentDescription(label))
            compose.onNodeWithContentDescription(label).performScrollTo()
            captureScreen("submission-chart-${question.name.lowercase()}", compose)
        }
    }
}
