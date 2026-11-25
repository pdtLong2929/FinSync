package com.finsync.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.finsync.ui.dashboard.DashboardUiState
import com.finsync.ui.dashboard.DemoDashboard
import com.finsync.ui.theme.FinSyncTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MainTabSurfaceTest {
    @get:Rule val compose = createComposeRule()

    @Test fun headersAlignWithAndWithoutActionAndBreakdownKeepsItsSurface() {
        compose.setContent { FinSyncTheme { FinSyncApp(DashboardUiState.Content(DemoDashboard.data)) } }
        var headerTop: Float? = null
        var headerHeight: Float? = null
        for (tab in listOf("Wallets", "Groups", "Reports")) {
            compose.onNodeWithText(tab).performClick()
            val bounds = compose.onNodeWithTag("feature-page-header").assertIsDisplayed().getUnclippedBoundsInRoot()
            if (headerTop == null) {
                headerTop = bounds.top.value
                headerHeight = (bounds.bottom - bounds.top).value
            } else {
                assertEquals("Header top: $tab", headerTop!!, bounds.top.value, .5f)
                assertEquals("Header height: $tab", headerHeight!!, (bounds.bottom - bounds.top).value, .5f)
            }
            captureScreen("surface-${tab.lowercase()}", compose, nativeWindow = true)
        }
        compose.onNodeWithText("Spending by Category").performScrollTo().assertIsDisplayed()
        // Bring the last legend row into view so the native evidence includes the whole donut.
        compose.onAllNodesWithText("Transport").onFirst().performScrollTo().assertIsDisplayed()
        captureScreen("surface-reports-breakdown", compose, nativeWindow = true)
    }
}
