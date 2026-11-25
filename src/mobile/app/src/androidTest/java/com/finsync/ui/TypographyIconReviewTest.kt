package com.finsync.ui

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.finsync.MainActivity
import com.finsync.R
import com.finsync.domain.*
import com.finsync.ui.components.*
import com.finsync.ui.theme.*
import com.finsync.ui.wallets.WalletBalanceHero
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import androidx.compose.ui.text.style.TextAlign
import org.junit.Rule
import org.junit.Test

class TypographyIconReviewTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun walletHeroUsesFullAmountRowAndKeepsPrivacyAtLargeText() {
        var large by mutableStateOf(false)
        var hidden by mutableStateOf(false)
        compose.activityRule.scenario.onActivity { activity -> activity.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, if (large) 1.5f else 1f)) {
                FinSyncTheme {
                    Box(Modifier.width(if (large) 320.dp else 393.dp).fillMaxHeight()
                        .liquidAtmosphere().safeDrawingPadding().padding(Space.medium)) {
                        Column(Modifier.verticalScroll(rememberScrollState())) {
                            WalletBalanceHero(12_450_000, hidden) { hidden = !hidden }
                        }
                    }
                }
            }
        } }
        for (scale in listOf(false, true)) {
            compose.runOnIdle { large = scale }
            val amount = compose.onNodeWithText("12.450.000 ₫").assertIsDisplayed()
            val layouts = mutableListOf<TextLayoutResult>()
            amount.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            compose.runOnIdle {
                assertFalse("Wallet amount overflow at large=$scale", layouts.single().hasVisualOverflow)
                assertEquals(TextAlign.Start, layouts.single().layoutInput.style.textAlign)
            }
            captureScreen("sharp-wallet-${if (scale) "large" else "normal"}", compose, nativeWindow = true)
            compose.onNodeWithContentDescription("Hide amounts").performClick()
            compose.onNodeWithText("Hidden").assertIsDisplayed()
            compose.onNodeWithText("12.450.000 ₫").assertDoesNotExist()
            compose.onNodeWithContentDescription("Show amounts").performClick()
        }
    }

    @Test fun vietnameseAmountsAndObjectIconsAtNormalAndLargeText() {
        var large by mutableStateOf(false)
        val categories = CategoryIcon.entries.map { Category(it.name, EntryType.EXPENSE, "review-${it.name}", it) }
        compose.activityRule.scenario.onActivity { activity -> activity.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, if(large) 1.5f else 1f), LocalCategories provides categories) {
                FinSyncTheme {
                    key(large) {
                        Column(Modifier.width(if(large) 320.dp else 393.dp).fillMaxHeight().liquidAtmosphere().safeDrawingPadding()
                            .verticalScroll(rememberScrollState()).padding(Space.medium), verticalArrangement = Arrangement.spacedBy(Space.medium)) {
                            LiquidCard {
                                Text("Nguyễn Phú Đạt", Modifier.fillMaxWidth(), style = MaterialTheme.typography.headlineMedium)
                                Text("Tổng số dư · Đăng nhập · Thu nhập", style = MaterialTheme.typography.bodyMedium)
                                MoneyText(12_450_000, style = MaterialTheme.typography.headlineLarge)
                                Text("Ă Â Ê Ô Ơ Ư Đ · ă â ê ô ơ ư đ", style = MaterialTheme.typography.bodyLarge)
                            }
                            categories.chunked(if(large) 2 else 4).forEach { row ->
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    row.forEach { category -> Column(Modifier.weight(1f)) {
                                        CategoryBadge(category.name, category.id)
                                        Text(category.name, style = MaterialTheme.typography.labelSmall)
                                    } }
                                }
                            }
                            Text("Wallet objects", style = MaterialTheme.typography.titleMedium)
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                listOf(R.drawable.art_wallet, R.drawable.art_bank, R.drawable.art_credit, R.drawable.art_cash).forEach {
                                    Image(painterResource(it), null, Modifier.size(LiquidTokens.compactChromeSize))
                                }
                            }
                        }
                    }
                }
            }
        } }
        for (scale in listOf(false, true)) {
            compose.runOnIdle { large = scale }
            captureScreen("type-icons-${if(scale) "large" else "normal"}", compose, nativeWindow = true)
            compose.onNodeWithText("Nguyễn Phú Đạt").assertIsDisplayed()
                .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { action ->
                    val result = mutableListOf<TextLayoutResult>(); action(result)
                    val layout = result.single()
                    assertFalse("Vietnamese title: large=$scale size=${layout.size} lines=${layout.lineCount} widthOverflow=${layout.didOverflowWidth} heightOverflow=${layout.didOverflowHeight} constraints=${layout.layoutInput.constraints}", layout.hasVisualOverflow)
                }
            compose.onNodeWithText("Wallet objects").performScrollTo().assertIsDisplayed()
            captureScreen("type-icons-wallets-${if(scale) "large" else "normal"}", compose, nativeWindow = true)
        }
    }
}
