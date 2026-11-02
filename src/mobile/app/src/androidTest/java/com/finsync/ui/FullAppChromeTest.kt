package com.finsync.ui
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.finsync.ui.dashboard.*
import com.finsync.ui.theme.FinSyncTheme
import org.junit.Rule
import org.junit.Test

class FullAppChromeTest {
    @get:Rule val compose = createComposeRule()
    @Test fun allTabsRemainUsableAtCompactLargeTextAndBothSystemThemes() {
        var variant by mutableStateOf("light")
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, if (variant.contains("large")) 1.5f else 1f)) {
                FinSyncTheme(darkTheme = variant.startsWith("dark")) {
                    key(variant) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                            Box(if (variant.contains("compact")) Modifier.width(320.dp).fillMaxHeight() else Modifier.fillMaxSize()) {
                                FinSyncApp(DashboardUiState.Content(DemoDashboard.data))
                            }
                        }
                    }
                }
            }
        }
        val tabs = listOf("Overview", "Wallets", "Groups", "Reports")
        val titles = listOf("Personal net worth", "Wallets", "Groups", "Reports")
        for (mode in listOf("light", "dark", "light-compact-large", "dark-large")) {
            compose.runOnIdle { variant = mode }
            tabs.forEachIndexed { index, tab ->
                compose.onNode(hasText(tab) and hasClickAction()).performClick().assertIsSelected()
                compose.onNode(hasText(titles[index]) and !hasClickAction()).assertIsDisplayed()
                compose.onNodeWithContentDescription(if (tab == "Groups") "Add group expense" else "Add expense").assertIsDisplayed()
                captureScreen("tabs-${tab.lowercase()}-$mode", compose)
            }
        }
    }
}
