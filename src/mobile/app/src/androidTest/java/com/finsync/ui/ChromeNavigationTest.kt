package com.finsync.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.finsync.R
import com.finsync.ui.components.ChromeNavigation
import com.finsync.ui.components.ChromeTab
import com.finsync.ui.theme.FinSyncTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class ChromeNavigationTest {
    @get:Rule val compose = createComposeRule()

    @Test fun fourSlotsStayAlignedAcrossSelectionWidthsAndFontScale() {
        var width by mutableStateOf(393)
        var scale by mutableStateOf(1f)
        var selected by mutableStateOf("Overview")
        val tabs = listOf(ChromeTab("Overview", R.string.overview, R.drawable.ic_overview),
            ChromeTab("Wallets", R.string.wallets, R.drawable.ic_wallet),
            ChromeTab("Groups", R.string.groups, R.drawable.ic_groups),
            ChromeTab("Reports", R.string.reports, R.drawable.ic_reports))
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, scale)) {
                FinSyncTheme { Box(Modifier.width(width.dp)) { ChromeNavigation(tabs, selected) { selected = it } } }
            }
        }
        for (screen in listOf(393, 320)) for (font in listOf(1f, 1.5f)) {
            compose.runOnIdle { width = screen; scale = font }
            var referenceIconY: Float? = null
            var referenceLabelY: Float? = null
            var referenceSlotWidth: Float? = null
            for (active in tabs) {
                compose.onNodeWithTag("chrome-tab-${active.key}").performClick().assertIsSelected()
                for (tab in tabs) {
                    val slot = compose.onNodeWithTag("chrome-tab-${tab.key}").fetchSemanticsNode().boundsInRoot
                    val icon = compose.onNodeWithTag("chrome-icon-${tab.key}", true).fetchSemanticsNode().boundsInRoot
                    val label = compose.onNodeWithTag("chrome-label-${tab.key}", true).fetchSemanticsNode().boundsInRoot
                    if (referenceIconY == null) { referenceIconY = icon.center.y; referenceLabelY = label.top; referenceSlotWidth = slot.width }
                    assertEquals(referenceIconY!!, icon.center.y, 1f)
                    assertEquals(referenceLabelY!!, label.top, 1f)
                    assertEquals(referenceSlotWidth!!, slot.width, 1f)
                    assertEquals(slot.center.x, icon.center.x, 1f)
                    assertEquals(slot.center.x, label.center.x, 1f)
                    compose.onNodeWithTag("chrome-label-${tab.key}", true)
                        .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { action ->
                            val result = mutableListOf<TextLayoutResult>(); action(result)
                            assertEquals(1, result.single().lineCount)
                            assertFalse("${tab.key} label overflows at $screen / $font", result.single().hasVisualOverflow)
                        }
                }
            }
        }
    }
}
