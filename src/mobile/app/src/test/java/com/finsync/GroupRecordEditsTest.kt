package com.finsync

import com.finsync.data.DemoFixtures
import com.finsync.domain.*
import com.finsync.ui.*
import org.junit.Assert.*
import org.junit.Test

class GroupRecordEditsTest {
    private val today = DemoFixtures.today
    private val people = listOf("me", "linh", "minh")

    @Test fun expenseEditsKeepEachSplitMethodAndConserveWholeVnd() {
        for (mode in SplitMode.entries) {
            val vm = FinanceViewModel()
            val inputs = when (mode) {
                SplitMode.Equal -> emptyMap()
                SplitMode.Exact -> mapOf("me" to "33335", "linh" to "33333", "minh" to "33333")
                SplitMode.Percentage -> mapOf("me" to "33.34", "linh" to "33.33", "minh" to "33.33")
                SplitMode.Shares -> people.associateWith { "1" }
            }
            val id = vm.uiState.value.groupExpenses.first { it.groupId == "home" }.id
            val before = vm.uiState.value
            assertNull(vm.editGroupExpense(id, "Edited rent", "100001", "me", today.toString(), "Housing", people, mode, inputs))
            val entry = vm.uiState.value.groupExpenses.first { it.id == id }
            assertEquals(mode, entry.splitMode)
            assertEquals(100001L, entry.shares.values.sum())
            assertEquals(splitExpense(100001, "me", people, mode, inputs), entry.shares)
            assertEquals(before.entries, vm.uiState.value.entries)
            assertEquals(before.wallets, vm.uiState.value.wallets)
        }
    }

    @Test fun expenseRemovalUndoKeepsPaymentsAndIsIdempotent() {
        val vm = FinanceViewModel()
        assertNull(vm.recordSettlement("home", "linh", "me", "40000", today.toString(), "paid"))
        val before = vm.uiState.value
        val expense = before.groupExpenses.first { it.groupId == "home" }
        assertNull(vm.removeGroupExpense(expense.id))
        assertEquals(before.settlements, vm.uiState.value.settlements)
        assertEquals(0L, groupBalances(before.groups.first(), vm.uiState.value.groupExpenses, before.settlements).values.sum())
        assertNull(vm.restoreGroupExpense(expense))
        assertNull(vm.restoreGroupExpense(expense))
        assertEquals(before.groupExpenses.toSet(), vm.uiState.value.groupExpenses.toSet())
        assertEquals(before.entries, vm.uiState.value.entries)
    }

    @Test fun paymentCorrectionRemovalAndUndoRecomputeOnlySharedBalances() {
        val vm = FinanceViewModel()
        val original = vm.uiState.value
        assertNull(vm.recordSettlement("home", "linh", "me", "100000", today.toString(), "paid"))
        assertNull(vm.editSettlement("paid", "40000", today.toString()))
        val corrected = vm.uiState.value.settlements.single()
        assertEquals(-60000L, groupBalances(vm.uiState.value.groups.first(), vm.uiState.value.groupExpenses, vm.uiState.value.settlements).getValue("linh"))
        assertNotNull(vm.editSettlement("paid", "100001", today.toString()))
        assertNotNull(vm.editSettlement("paid", "-1", today.toString()))
        assertNotNull(vm.editSettlement("paid", "1", today.plusDays(1).toString()))
        assertEquals(corrected, vm.uiState.value.settlements.single())
        assertNull(vm.removeSettlement("paid"))
        assertEquals(-100000L, groupBalances(vm.uiState.value.groups.first(), vm.uiState.value.groupExpenses, emptyList()).getValue("linh"))
        assertNull(vm.restoreSettlement(corrected))
        assertNull(vm.restoreSettlement(corrected))
        assertEquals(original.entries, vm.uiState.value.entries)
        assertEquals(original.wallets, vm.uiState.value.wallets)
    }

    @Test fun invalidOrUnauthorizedExpenseEditDoesNotMutateState() {
        val state = FinanceState()
        val expense = state.groupExpenses.first { it.groupId == "home" }
        val unauthorized = state.copy(groups = state.groups.map { it.copy(owner = "linh") },
            groupExpenses = listOf(expense.copy(payer = "linh")))
        val denied = GroupRecordEdits.expense(unauthorized, expense.id, "Edited", "300000", "linh", today.toString(), "Food", people, SplitMode.Equal, emptyMap(), today)
        assertNotNull(denied.error); assertEquals(unauthorized, denied.state)
        val bad = GroupRecordEdits.expense(state, expense.id, "Edited", "-1", "me", today.toString(), "Food", people, SplitMode.Equal, emptyMap(), today)
        assertNotNull(bad.error); assertEquals(state, bad.state)
    }

    @Test fun formerMembersKeepHistoricalSharesButMetadataCanBeCorrected() {
        val state = FinanceState()
        val expense = state.groupExpenses.first { it.groupId == "home" }
        val former = state.copy(groups = state.groups.map { it.copy(members = it.members.map { member -> if (member.id == "linh") member.copy(active = false) else member }) })
        val inputs = expense.shares.mapValues { it.value.toString() }
        val metadata = GroupRecordEdits.expense(former, expense.id, "Corrected title", expense.amount.toString(), expense.payer,
            expense.date.toString(), expense.category, expense.shares.keys.toList(), SplitMode.Exact, inputs, today)
        assertNull(metadata.error)
        assertEquals(expense.shares, metadata.state.groupExpenses.first { it.id == expense.id }.shares)
        assertNotNull(GroupRecordEdits.removeExpense(former, expense.id).error)
    }
}
