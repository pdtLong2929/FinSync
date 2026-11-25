package com.finsync.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.finsync.R
import com.finsync.domain.*
import com.finsync.ui.components.*
import com.finsync.ui.groups.GroupOverviewCard
import com.finsync.ui.theme.FinSyncTheme
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FinanceDepthTest {
    @get:Rule val compose = createComposeRule()

    @Test fun sharedSummaryKeepsOppositeDebtsSeparateAndUpdatesAfterPayment() {
        val members = listOf(GroupMember("me", "You"), GroupMember("other", "Alex"))
        val groups = listOf("a", "b").map { ExpenseGroup(it, it, "TRIP", "", members) }
        val day = LocalDate.of(2026, 11, 23)
        var state by mutableStateOf(FinanceState(groups = groups, groupExpenses = listOf(
            GroupExpense("e1", "a", "Lunch", 200_000, "me", mapOf("me" to 100_000, "other" to 100_000), day, "Food"),
            GroupExpense("e2", "b", "Dinner", 120_000, "other", mapOf("me" to 60_000, "other" to 60_000), day, "Food"),
        )))
        compose.setContent { FinSyncTheme { GroupOverviewCard(state) } }
        compose.onNodeWithText("100.000 ₫").assertIsDisplayed()
        compose.onNodeWithText("60.000 ₫").assertIsDisplayed()
        compose.onNodeWithText("40.000 ₫").assertDoesNotExist()
        compose.runOnIdle { state = state.copy(settlements = listOf(Settlement("s1", "a", "other", "me", 30_000, day))) }
        compose.onNodeWithText("70.000 ₫").assertIsDisplayed()
        compose.onNodeWithText("60.000 ₫").assertIsDisplayed()
    }

    @Test fun actionCardsRemainReadableAndClickableAtLargeFont() {
        var clicked = ""
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 1.5f)) {
                FinSyncTheme { Box(Modifier.width(320.dp)) {
                    FinanceActionGrid(listOf(
                        FinanceAction("Income & dates", R.drawable.ic_filter) { clicked = "income" },
                        FinanceAction("Savings goals", R.drawable.ic_wallet) { clicked = "goals" },
                    ))
                } }
            }
        }
        compose.onNodeWithText("Income & dates").assertIsDisplayed().performClick()
        compose.runOnIdle { assertEquals("income", clicked) }
        compose.onNodeWithText("Savings goals").assertIsDisplayed().performClick()
        compose.runOnIdle { assertEquals("goals", clicked) }
    }
}
