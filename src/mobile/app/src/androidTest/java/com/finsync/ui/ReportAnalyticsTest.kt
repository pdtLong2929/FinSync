package com.finsync.ui

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.finsync.MainActivity
import com.finsync.data.DemoFixtures
import com.finsync.ui.dashboard.*
import com.finsync.ui.theme.FinSyncTheme
import org.junit.Rule
import org.junit.Test
import java.time.YearMonth

class ReportAnalyticsTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val keyboard = TestKeyboard()
    private fun host(large: Boolean = false) {
        compose.activityRule.scenario.onActivity { it.setContent {
            keyboard.Observe()
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, if (large) 1.5f else 1f)) {
                FinSyncTheme { Box(Modifier.width(if (large) 320.dp else 393.dp).fillMaxHeight()) {
                    FinSyncApp(DashboardUiState.Content(DemoDashboard.data))
                } }
            }
        } }
        compose.onNodeWithText("Reports").performClick()
    }
    private fun scroll(text: String) { compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(text)) }
    private fun click(text: String) { scroll(text); compose.onNodeWithText(text).performClick() }
    private fun input(label: String, value: String) {
        keyboard.close(compose); scroll(label)
        compose.onNodeWithText(label).performTextReplacement(value); keyboard.close(compose)
    }
    @Test fun rangeValidationAndTransactionReturnPreserveFilters() {
        host()
        click("Income & dates")
        click("Date range")
        input("Start date (YYYY-MM-DD)", DemoFixtures.today.plusDays(1).toString())
        click("Apply dates")
        compose.onNodeWithText("End date must be on or after start date.").assertIsDisplayed()
        input("Start date (YYYY-MM-DD)", DemoFixtures.today.toString())
        input("End date (YYYY-MM-DD)", DemoFixtures.today.toString())
        click("Apply dates")
        click("View period transactions")
        click("Lunch")
        compose.onNodeWithText("Transaction details").assertIsDisplayed()
        compose.onNodeWithText("Back").performClick()
        scroll("Hide period transactions")
        compose.onNodeWithText("Hide period transactions").assertIsDisplayed()
        scroll("Showing: ${DemoFixtures.today} to ${DemoFixtures.today}")
        compose.onNodeWithText("Showing: ${DemoFixtures.today} to ${DemoFixtures.today}").assertIsDisplayed()
        captureScreen("reports-date-range", compose, nativeWindow = true)
    }
    @Test fun comparisonAndTrendRemainReadableAtLargeText() {
        host(large = true)
        val heading = "Monthly comparison"
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasContentDescription(heading))
        compose.onNodeWithContentDescription(heading).assertIsDisplayed()
        captureScreen("reports-comparison-large", compose, nativeWindow = true)
        click("Income & dates")
        scroll("Six-month spending trend")
        compose.onNodeWithText("Six-month spending trend").assertIsDisplayed()
        captureScreen("reports-trend-large", compose, nativeWindow = true)
    }
}

