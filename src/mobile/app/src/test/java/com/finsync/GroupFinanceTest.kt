package com.finsync

import com.finsync.domain.*
import com.finsync.ui.FinanceViewModel
import org.junit.Assert.*
import org.junit.Test

class GroupFinanceTest {
    @Test fun invalidSharedDetailsAndSettlementsNeverMutateLedgers() {
        val vm = FinanceViewModel()
        val before = vm.uiState.value
        val today = com.finsync.data.DemoFixtures.today
        val errors = sharedExpenseErrors("", "0", "bad-date", today)
        assertNotNull(errors.title); assertNotNull(errors.amount); assertNotNull(errors.date)
        for ((title, amount, date) in listOf(Triple("", "100", today.toString()),
            Triple("Lunch", "9999999999999", today.toString()), Triple("Lunch", "100", today.plusDays(1).toString()))) {
            assertNotNull(vm.addGroupExpense("home", title, amount, "me", date, "Food", listOf("me", "linh"), SplitMode.Equal, emptyMap()))
            assertEquals(before, vm.uiState.value)
        }
        for (amount in listOf("0", "-1", "100001", "bad")) {
            assertNotNull(vm.recordSettlement("home", "linh", "me", amount, today.toString()))
            assertEquals(before, vm.uiState.value)
        }
        assertNotNull(settlementValidation(null, "1", today.toString(), today))
        assertNotNull(settlementValidation(Debt("a", "b", 100), "1", today.plusDays(1).toString(), today))
    }
    @Test fun payerRoundingAndParticipantSubsetStayExplicit() {
        val split = splitExpense(100001, "linh", listOf("me", "linh"), SplitMode.Equal)
        assertEquals(50000L, split.getValue("me")); assertEquals(50001L, split.getValue("linh"))
        assertFalse(split.containsKey("minh"))
        assertTrue(runCatching { splitExpense(100, "me", listOf("linh"), SplitMode.Equal) }.isFailure)
        assertEquals(mapOf("me" to 0L, "linh" to 100L), splitExpense(100, "me", listOf("me", "linh"), SplitMode.Percentage,
            mapOf("me" to "0", "linh" to "100")))
    }
    @Test fun roundingAndExactAndPercentageConserveVnd() {
        val people = listOf("a", "b", "c")
        assertEquals(mapOf("a" to 33334L, "b" to 33333L, "c" to 33333L), splitExpense(100000, "a", people, SplitMode.Equal))
        assertEquals(100001L, splitExpense(100001, "a", people, SplitMode.Percentage,
            mapOf("a" to "33.34", "b" to "33.33", "c" to "33.33")).values.sum())
        assertTrue(runCatching { splitExpense(100, "a", people, SplitMode.Exact, people.associateWith { "30" }) }.isFailure)
        assertTrue(runCatching { splitExpense(100, "a", people, SplitMode.Percentage, people.associateWith { "30" }) }.isFailure)
    }
    @Test fun sharesModeUsesIntegerRatiosAndAssignsRoundingRemainderToPayer() {
        val equalWeights = splitExpense(10, "a", listOf("a", "b", "c"), SplitMode.Shares,
            mapOf("a" to "1", "b" to "1", "c" to "1"))
        assertEquals(mapOf("a" to 4L, "b" to 3L, "c" to 3L), equalWeights)
        val weighted = splitExpense(100001, "b", listOf("a", "b"), SplitMode.Shares,
            mapOf("a" to "1", "b" to "2"))
        assertEquals(mapOf("a" to 33333L, "b" to 66668L), weighted)
        assertEquals(100001L, weighted.values.sum())
        assertTrue(runCatching {
            splitExpense(100, "a", listOf("a", "b"), SplitMode.Shares, mapOf("a" to "-1", "b" to "2"))
        }.isFailure)
        assertTrue(runCatching {
            splitExpense(100, "a", listOf("a", "b"), SplitMode.Shares, mapOf("a" to "0", "b" to "0"))
        }.isFailure)
    }
    @Test fun splitProgressShowsExactAndPercentageDifferenceAsInputsChange() {
        val exact = splitProgress(100000, "a", listOf("a", "b"), SplitMode.Exact,
            mapOf("a" to "30000", "b" to "50000"))
        assertEquals(80000L, exact.allocated)
        assertEquals(20000L, exact.difference)
        assertTrue(exact.inputsValid)
        val invalidExact = splitProgress(100000, "a", listOf("a", "b"), SplitMode.Exact,
            mapOf("a" to "100000", "b" to "-1"))
        assertEquals(0L, invalidExact.difference)
        assertFalse(invalidExact.inputsValid)
        val percentage = splitProgress(100000, "a", listOf("a", "b"), SplitMode.Percentage,
            mapOf("a" to "40", "b" to "30"))
        assertEquals(70000L, percentage.allocated)
        assertEquals(30000L, percentage.difference)
        assertTrue(percentage.inputsValid)
        val invalidPercentage = splitProgress(100000, "a", listOf("a", "b"), SplitMode.Percentage,
            mapOf("a" to "100", "b" to "-1"))
        assertEquals(0L, invalidPercentage.difference)
        assertFalse(invalidPercentage.inputsValid)
        assertTrue(runCatching { splitExpense(100, "a", listOf("a", "b"), SplitMode.Percentage,
            mapOf("a" to "120", "b" to "-20")) }.isFailure)
    }
    @Test fun settlementReducesDebtAndNeverChangesPersonalLedger() {
        val vm = FinanceViewModel()
        val before = vm.uiState.value
        assertNotNull(vm.changeMember("home", removeId = "linh"))
        assertNotNull(vm.recordSettlement("home", "linh", "me", "100001", "2026-10-25"))
        assertNull(vm.recordSettlement("home", "linh", "me", "100000", "2026-10-25"))
        assertNull(vm.changeMember("home", removeId = "linh"))
        val after = vm.uiState.value
        assertEquals(before.entries, after.entries)
        assertEquals(before.wallets, after.wallets)
        assertEquals(0L, groupBalances(after.groups.first(), after.groupExpenses, after.settlements).getValue("linh"))
        assertNotNull(vm.recordSettlement("home", "linh", "me", "100000", "2026-10-25"))
    }
    @Test fun debtSimplificationConservesEveryMemberBalance() {
        val balances = mapOf("a" to -700L, "b" to 200L, "c" to 500L, "d" to 0L)
        val debts = simplifiedDebts(balances)
        val result = balances.toMutableMap()
        debts.forEach { result[it.from] = result.getValue(it.from) + it.amount; result[it.to] = result.getValue(it.to) - it.amount }
        assertTrue(result.values.all { it == 0L })
        assertTrue(simplifiedDebts(mapOf("a" to 0L, "b" to 0L)).isEmpty())
    }
}
