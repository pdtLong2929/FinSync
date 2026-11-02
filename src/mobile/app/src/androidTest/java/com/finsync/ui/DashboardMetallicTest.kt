package com.finsync.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import com.finsync.ui.dashboard.*
import com.finsync.ui.theme.FinSyncTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class DashboardMetallicTest {
    @get:Rule val compose = createComposeRule()

    @Test fun dashboardWithNavigationAndQuickEntry() {
        var dark by mutableStateOf(false)
        compose.setContent {
            FinSyncTheme(darkTheme = dark) {
                FinSyncApp(DashboardUiState.Content(DemoDashboard.data))
            }
        }
        for (night in listOf(false, true)) {
            compose.runOnIdle { dark = night }
            compose.onNode(hasScrollToIndexAction()).performScrollToIndex(0)
            compose.onNodeWithContentDescription("Add expense").assertIsDisplayed()
            compose.waitForIdle()
            captureScreen("dashboard-refined-app-${if (night) "dark" else "light"}", compose)
        }
        compose.onNodeWithContentDescription("Add expense").performClick()
        compose.onNodeWithText("Add expense").assertIsDisplayed()
    }

    @Test fun dashboardReflowsAndRetainsActionsInBothThemes() {
        var dark by mutableStateOf(false)
        var largeText by mutableStateOf(false)
        var hidden by mutableStateOf(false)
        var selected = ""
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, if (largeText) 1.5f else 1f)) {
                FinSyncTheme(darkTheme = dark) {
                    Surface(Modifier.fillMaxSize().safeDrawingPadding()) {
                        key(dark, largeText) {
                            DashboardScreen(
                                DashboardUiState.Content(DemoDashboard.data), hidden,
                                { hidden = !hidden }, { selected = "groups" }, {}, {},
                                onTransaction = { selected = it },
                                onHistory = { selected = "history" },
                                onBudgets = { selected = "budgets" },
                            )
                        }
                    }
                }
            }
        }
        for (night in listOf(false, true)) {
            for (large in listOf(false, true)) {
                compose.runOnIdle { dark = night; largeText = large; hidden = false }
                val variant = "${if (night) "dark" else "light"}-${if (large) "large" else "normal"}"
                compose.onNodeWithText("12.450.000 ₫").assertIsDisplayed()
                compose.onNodeWithContentDescription("Hide amounts").performClick()
                compose.onNodeWithText("12.450.000 ₫").assertDoesNotExist()
                compose.onNodeWithContentDescription("Show amounts").performClick()
                compose.waitForIdle()
                captureScreen("dashboard-refined-$variant-top", compose)
                compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasContentDescription("Review budgets and spending"))
                compose.onNodeWithContentDescription("Review budgets and spending").performClick()
                compose.runOnIdle { assertEquals("budgets", selected) }
                compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("View all transactions"))
                compose.onNodeWithText("View all transactions").performClick()
                compose.runOnIdle { assertEquals("history", selected) }
                compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Lunch"))
                compose.onNodeWithText("Lunch").assertIsDisplayed().performClick()
                compose.runOnIdle { assertEquals("lunch", selected) }
                compose.waitForIdle()
                captureScreen("dashboard-refined-$variant-transactions", compose)
                compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("View groups"))
                compose.onNodeWithText("View groups").performClick()
                compose.runOnIdle { assertEquals("groups", selected) }
            }
        }
    }
}
