package com.finsync.ui

import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import com.finsync.ui.dashboard.*
import com.finsync.ui.theme.FinSyncTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Focused review only. Compose capture avoids the known emulator screencap crash path. */
@SdkSuppress(minSdkVersion = 29)
class DashboardLiquidChromeTest {
    @get:Rule val compose = createComposeRule()

    @Test fun dashboardReviewMatrixRetainsPrivacyBudgetAndQuickEntry() {
        var variant by mutableStateOf("light")
        compose.setContent {
            val density = LocalDensity.current
            val large = variant.contains("large")
            CompositionLocalProvider(LocalDensity provides Density(density.density, if (large) 1.5f else 1f)) {
                FinSyncTheme(darkTheme = variant.startsWith("dark")) {
                    key(variant) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                            Box(if (variant == "light-compact-large") Modifier.width(320.dp).fillMaxHeight() else Modifier.fillMaxSize()) {
                                FinSyncApp(DashboardUiState.Content(DemoDashboard.data))
                            }
                        }
                    }
                }
            }
        }
        for (mode in listOf("light", "dark", "light-compact-large", "dark-large")) {
            compose.runOnIdle { variant = mode }
            compose.onNode(hasScrollToIndexAction()).performScrollToIndex(0)
            compose.onNodeWithContentDescription("Hide amounts").performClick()
            compose.onNodeWithText("12.450.000 ₫").assertDoesNotExist()
            compose.onNodeWithContentDescription("Show amounts").performClick()
            compose.onNodeWithText("12.450.000 ₫").assertIsDisplayed()
            compose.onNodeWithContentDescription("Add expense").assertIsDisplayed()
            compose.onNode(hasScrollToIndexAction()).performScrollToIndex(0)
            capture("$mode-top")
            compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("300.000 ₫ left to spend"))
            compose.onNodeWithText("300.000 ₫ left to spend").assertIsDisplayed()
            capture("$mode-budget")
            compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Lunch"))
            compose.onNodeWithText("Lunch").assertIsDisplayed()
            capture("$mode-transactions")
        }
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Activity"))
        compose.onNodeWithText("Activity").performClick()
        compose.onNodeWithText("Personal transactions").assertIsDisplayed()
        compose.onNodeWithText("Back").performClick()
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Budgets"))
        compose.onNodeWithText("Budgets").performClick()
        compose.onNodeWithText("Budgets").assertIsDisplayed()
        compose.onNodeWithText("Back").performClick()
        compose.onNodeWithText("Overview").performClick()
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Shared"))
        compose.onNodeWithText("Shared").performClick()
        compose.onNode(hasText("Groups") and !hasClickAction()).assertIsDisplayed()
        compose.onNodeWithText("Overview").performClick()
        compose.onNodeWithText("Wallets").performClick()
        compose.onNode(hasText("Wallets") and !hasClickAction()).assertIsDisplayed()
        compose.onNodeWithText("Overview").performClick()
        compose.onNodeWithContentDescription("Add expense").performClick()
        compose.onNodeWithText("Add expense").assertIsDisplayed()
    }

    private fun capture(name: String) {
        compose.waitForIdle()
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        val resolver = InstrumentationRegistry.getInstrumentation().targetContext.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "finsync-liquid-$name-${System.currentTimeMillis()}.png")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/FinSyncReview")
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
        val uri = requireNotNull(resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values))
        requireNotNull(resolver.openOutputStream(uri)).use {
            assertTrue("Screenshot encoding failed", bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
        }
        values.clear()
        values.put(MediaStore.MediaColumns.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
    }
}
