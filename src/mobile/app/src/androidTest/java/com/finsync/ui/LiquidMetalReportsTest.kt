package com.finsync.ui

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.finsync.MainActivity
import com.finsync.ui.dashboard.*
import com.finsync.ui.theme.FinSyncTheme
import org.junit.Rule
import org.junit.Test

class LiquidMetalReportsTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private fun host(large: Boolean = false, dark: Boolean = false) {
        compose.activityRule.scenario.onActivity { activity -> activity.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, if(large) 1.5f else 1f)) {
                FinSyncTheme(darkTheme = dark) { Box(Modifier.widthIn(max = if(large) 320.dp else 500.dp).fillMaxHeight()) {
                    FinSyncApp(DashboardUiState.Content(DemoDashboard.data))
                } }
            }
        } }
        compose.onNode(hasText("Reports") and hasClickAction()).performClick()
    }
    private fun reveal(matcher: SemanticsMatcher) {
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(matcher)
    }
    private fun click(label: String) {
        val matcher = hasText(label) and hasClickAction()
        if(runCatching { compose.onNode(matcher).assertIsDisplayed() }.isFailure) reveal(matcher)
        compose.onNode(matcher).performClick()
    }
    @Test fun comparisonExactValuesDrilldownAndReturn() {
        host()
        compose.onNodeWithTag("compare-month-1").assertIsDisplayed()
        captureScreen("liquid-metal-reports", compose, nativeWindow = true)
        compose.onNodeWithTag("compare-month-0").performClick()
        compose.onNodeWithText("No expenses recorded in this period").assertIsDisplayed()
        click("Close")
        compose.onNodeWithTag("compare-month-1").performClick()
        compose.onNodeWithTag("report-detail-amount").assertTextEquals("2.300.000 ₫").assertIsDisplayed()
        click("Lunch")
        compose.onNodeWithText("Transaction details").assertIsDisplayed()
        click("Back")
        compose.onNodeWithText("Lunch").assertIsDisplayed()
        click("Close")
        click("Category")
        captureScreen("liquid-metal-categories", compose, nativeWindow = true)
        click("Choose data")
        compose.onAllNodesWithText("Food").onLast().performClick()
        compose.onNodeWithTag("report-detail-amount").assertTextEquals("1.200.000 ₫")
        click("Close")
        reveal(hasText("Spending by Category"))
        captureScreen("liquid-metal-breakdown", compose, nativeWindow = true)
        click("Trend")
        captureScreen("liquid-metal-trend", compose, nativeWindow = true)
        click("Compare")
        compose.onNodeWithTag("report-period").performClick()
        compose.onNodeWithText("September 2026").performClick()
        captureScreen("liquid-metal-empty", compose, nativeWindow = true)
        compose.onNodeWithTag("compare-month-1").performClick()
        compose.onNodeWithText("No expenses recorded in this period").assertIsDisplayed()
    }
    @Test fun compactLargeFontAndDarkRemainUsable() {
        host(large = true, dark = true)
        reveal(hasTestTag("compare-month-1"))
        compose.onNodeWithTag("compare-month-1").assertIsDisplayed().performClick()
        compose.onNodeWithTag("report-detail-amount").assertTextEquals("2.300.000 ₫").assertIsDisplayed()
        captureScreen("liquid-metal-large-detail", compose, nativeWindow = true)
        click("Close")
        captureScreen("liquid-metal-large", compose, nativeWindow = true)
        click("Budgets")
        compose.onNodeWithText("Create budget").assertIsDisplayed()
    }
}


