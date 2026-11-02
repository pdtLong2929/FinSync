package com.finsync.ui

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.finsync.MainActivity
import com.finsync.domain.*
import com.finsync.ui.groups.*
import com.finsync.ui.reports.*
import com.finsync.ui.transactions.*
import com.finsync.ui.wallets.*
import com.finsync.ui.theme.FinSyncTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Focused coverage of child surfaces omitted from the four-tab visual matrix. */
class FinalUiQualityTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val state = FinanceState()
    private val group = state.groups.first()
    @Test fun categoryNamesDoNotBreakInsideWordsAtLargeText() {
        compose.activityRule.scenario.onActivity { it.setContent {
            val density=LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density,1.5f)) {
                FinSyncTheme { Surface(Modifier.width(320.dp).fillMaxHeight().safeDrawingPadding()) {
                    CategorySelectionScreen(state.categories, {}, {}, { null })
                } }
            }
        } }
        for(name in listOf("Transport","Shopping","Entertainment")) {
            compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(name))
            compose.onNodeWithText(name,useUnmergedTree=true).performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.GetTextLayoutResult) { action ->
                val results=mutableListOf<androidx.compose.ui.text.TextLayoutResult>()
                action(results)
                val result=results.single()
                org.junit.Assert.assertFalse("$name must not be ellipsized",result.isLineEllipsized(0))
                assertEquals("$name must remain a whole word",name.length,result.getLineEnd(0,visibleEnd=true))
            }
        }
        compose.onNode(hasScrollToIndexAction()).performScrollToIndex(0)
        captureScreen("qa-category-large-final",compose,nativeWindow=true)
    }
    private fun click(text: String) {
        if (runCatching { compose.onNodeWithText(text).assertIsDisplayed() }.isFailure)
            compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(text))
        compose.onNodeWithText(text).performClick()
    }

    @Test fun childScreensAndDialogsAtNormalAndLargeText() {
        val keyboard = TestKeyboard()
        var page by mutableStateOf("wallet-create")
        var large by mutableStateOf(false)
        compose.activityRule.scenario.onActivity { activity -> activity.setContent {
            keyboard.Observe()
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, if (large) 1.5f else 1f)) {
                FinSyncTheme { key(page, large) {
                    Surface(Modifier.width(if (large) 320.dp else 393.dp).fillMaxHeight().safeDrawingPadding()) {
                        when (page) {
                            "wallet-create" -> WalletFormScreen(null, {}, { _, _, _ -> null })
                            "wallet-edit" -> WalletFormScreen(state.wallets.first(), {}, { _, _, _ -> null })
                            "wallet-history" -> WalletHistoryScreen(state.wallets.first(), state.entries, false, {})
                            "wallet-archive" -> WalletDetailsScreen(state.wallets.first(), state.entries, false, {}, {}, {}, {}, {})
                            "wallet-restore" -> WalletDetailsScreen(state.wallets.first().copy(archived = true), state.entries, false, {}, {}, {}, {}, {})
                            "transaction-detail" -> TransactionDetailsScreen(state.entries.first { it.type == EntryType.EXPENSE }, state.wallets.first(), false, {}, {}, {})
                            "category" -> CategorySelectionScreen(state.categories, {}, {}, { null })
                            "group-create" -> CreateGroupScreen({}, { _, _, _ -> null })
                            "members" -> MembersScreen(group, state, {}, { _, _, _ -> null })
                            "group-expense" -> GroupExpenseForm(group, {}, { _, _, _, _, _, _, _, _ -> null })
                            "settlement" -> SettlementScreen(group, "linh", "me", state, {}, { _, _, _, _ -> null })
                            "budget-create" -> BudgetForm(state, null, {}, { _, _, _, _ -> null })
                            "budget-edit" -> BudgetForm(state, state.budgets.first(), {}, { _, _, _, _ -> null })
                            "goal-create" -> GoalForm(null, {}, { _, _, _, _, _ -> null })
                            "goal-edit" -> GoalForm(state.goals.first(), {}, { _, _, _, _, _ -> null })
                        }
                    }
                } }
            }
        } }
        val scales = if (androidx.test.platform.app.InstrumentationRegistry.getArguments().getString("qaLargeOnly") == "true") listOf(true) else listOf(false, true)
        for (scale in scales) {
            for (screen in listOf("wallet-create", "wallet-edit", "wallet-history", "wallet-archive", "wallet-restore", "transaction-detail", "category", "group-create", "members", "group-expense", "settlement", "budget-create", "budget-edit", "goal-create", "goal-edit")) {
                compose.runOnIdle { large = scale; page = screen }
                val suffix = if (scale) "large" else "normal"
                compose.onNodeWithText("Back").assertIsDisplayed()
                captureScreen("qa-$screen-$suffix", compose, nativeWindow = true)
                when (screen) {
                    "wallet-archive", "wallet-restore" -> {
                        click(if (screen == "wallet-archive") "Archive" else "Restore")
                        compose.onNodeWithText("Confirm").assertIsDisplayed()
                        if (screen == "wallet-restore") compose.onNodeWithText("This wallet will be available for new entries again.", substring = true).assertIsDisplayed()
                        captureScreen("qa-$screen-dialog-$suffix", compose, nativeWindow = true)
                        compose.onNodeWithText("Cancel").performClick()
                    }
                    "transaction-detail" -> {
                        click("Delete transaction")
                        compose.onNodeWithText("Delete").assertIsDisplayed()
                        captureScreen("qa-delete-dialog-$suffix", compose, nativeWindow = true)
                        compose.onNodeWithText("Cancel").performClick()
                    }
                    "members" -> {
                        click("Remove Linh")
                        compose.onNodeWithText("Remove").assertIsDisplayed()
                        captureScreen("qa-remove-dialog-$suffix", compose, nativeWindow = true)
                        compose.onNodeWithText("Cancel").performClick()
                    }
                    "settlement" -> {
                        click("Review settlement")
                        compose.onNodeWithText("Confirm settlement").assertIsDisplayed()
                        captureScreen("qa-settlement-dialog-$suffix", compose, nativeWindow = true)
                        compose.onNodeWithText("Cancel").performClick()
                    }
                    "group-expense" -> {
                        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Expense title"))
                        compose.onNodeWithText("Expense title").performTextReplacement("Shared dinner")
                        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Amount (VND)"))
                        compose.onNodeWithText("Amount (VND)").performTextReplacement("300000")
                        keyboard.close(compose)
                        click("Review split")
                        compose.onNodeWithText("Save shared expense").assertIsDisplayed()
                        captureScreen("qa-split-$suffix", compose, nativeWindow = true)
                    }
                    "goal-edit" -> {
                        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Pick"))
                        click("Pick")
                        compose.onNodeWithText("Choose date").assertIsDisplayed()
                        captureScreen("qa-date-dialog-$suffix", compose, nativeWindow = true)
                        compose.onNodeWithText("Cancel").performClick()
                    }
                }
            }
        }
    }

    @Test fun groupPickerCanReachLastGroupAtLargeText() {
        val finance = FinanceViewModel()
        repeat(20) { finance.createGroup("QA group $it", "HOME", "") }
        compose.activityRule.scenario.onActivity { activity -> activity.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 1.5f)) {
                FinSyncTheme { Box(Modifier.width(320.dp).fillMaxHeight()) {
                    FinSyncApp(com.finsync.ui.dashboard.DashboardUiState.Content(com.finsync.ui.dashboard.DemoDashboard.data), finance = finance)
                } }
            }
        } }
        compose.onNodeWithText("Groups").performClick()
        compose.onNodeWithContentDescription("Add group expense").performClick()
        compose.onNodeWithText("Choose a group").assertIsDisplayed()
        // The modal list must scroll independently from the Groups tab underneath.
        compose.onNode(hasScrollToIndexAction() and hasAnyAncestor(isDialog())).performScrollToNode(hasText("QA group 19"))
        compose.onNodeWithText("QA group 19").assertIsDisplayed()
        captureScreen("qa-group-picker-large", compose, nativeWindow = true)
        compose.onNodeWithText("QA group 19").performClick()
        compose.onNodeWithText("QA group 19", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Review split").assertIsDisplayed()
    }

    @Test fun groupSaveStaysReachableWithLargeTextAndRealKeyboard() {
        val keyboard = TestKeyboard()
        var saved = ""
        compose.activityRule.scenario.onActivity { activity -> activity.setContent {
            keyboard.Observe()
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 1.5f)) {
                FinSyncTheme { Box(Modifier.width(320.dp).fillMaxHeight().safeDrawingPadding()) {
                    CreateGroupScreen({}, { name, _, _ -> saved = name; null })
                } }
            }
        } }
        compose.onNodeWithText("Group name").performClick()
        keyboard.awaitOpen(compose)
        compose.onNodeWithText("Group name").performTextInput("QA household")
        keyboard.awaitOpen(compose)
        captureScreen("qa-group-keyboard", compose, nativeWindow = true)
        compose.onNodeWithText("Save group").assertIsDisplayed().performClick()
        compose.runOnIdle { assertEquals("QA household", saved) }
        keyboard.close(compose)
        compose.onNodeWithText("Back").performClick()
        compose.onNodeWithText("Keep editing").assertIsDisplayed()
        captureScreen("qa-discard-large", compose, nativeWindow = true)
        compose.onNodeWithText("Keep editing").performClick()
        compose.onNodeWithText("Group name").assertTextContains("QA household")
    }
}
