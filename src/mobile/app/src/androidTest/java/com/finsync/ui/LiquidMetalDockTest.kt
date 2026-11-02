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
import org.junit.Assert.assertFalse
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.semantics.SemanticsActions

class LiquidMetalDockTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private fun host(large: Boolean) {
        compose.activityRule.scenario.onActivity { it.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, if(large) 1.5f else 1f)) {
                FinSyncTheme { Box(Modifier.widthIn(max = if(large) 320.dp else 500.dp).fillMaxHeight()) {
                    FinSyncApp(DashboardUiState.Content(DemoDashboard.data))
                } }
            }
        } }
    }
    private fun review(large: Boolean) {
        host(large)
        for (tab in listOf("Overview", "Wallets", "Groups", "Reports")) {
            compose.onNodeWithTag("chrome-tab-$tab").assertIsDisplayed().performClick().assertIsSelected()
            for(other in listOf("Overview", "Wallets", "Groups", "Reports").filter { it != tab })
                compose.onNodeWithTag("chrome-tab-$other").assertIsNotSelected().assertIsDisplayed()
            compose.onNodeWithContentDescription(if(tab == "Groups") "Add group expense" else "Add expense").assertIsDisplayed()
            for(label in listOf("Overview", "Wallets", "Groups", "Reports")) {
                compose.onNodeWithTag("chrome-label-$label", useUnmergedTree = true)
                    .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { action ->
                        val results = mutableListOf<TextLayoutResult>()
                        action(results)
                        assertFalse("$label navigation label must fit fully", results.single().hasVisualOverflow)
                    }
            }
            captureScreen("liquid-metal-f-${tab.lowercase()}-${if(large) "large" else "normal"}", compose, nativeWindow = true)
        }
        compose.onNodeWithContentDescription("Add expense").performClick()
        compose.onNodeWithText("Amount (VND)").assertIsDisplayed()
        compose.onNodeWithText("Save transaction").assertIsDisplayed()
        compose.onNodeWithTag("chrome-tab-Reports").assertDoesNotExist()
    }
    @Test fun fourTabsAndPrimaryAction() = review(false)
    @Test fun compactLargeTextRetainsLabelsAndAction() = review(true)
}

