package com.finsync

import com.finsync.domain.*
import com.finsync.ui.FinanceViewModel
import org.junit.Assert.*
import org.junit.Test
import com.finsync.data.DemoFixtures

class TransactionTest {
    @Test fun fieldValidationReportsEveryInvalidFieldWithoutChangingLedger() {
        val errors = transactionFieldErrors("9999999999999", null, "", EntryType.EXPENSE,
            "invalid", DemoFixtures.today, emptyList(), "x".repeat(301))
        assertNotNull(errors.amount); assertNotNull(errors.wallet); assertNotNull(errors.category)
        assertNotNull(errors.date); assertNotNull(errors.note)
        assertEquals(0, errors.firstFieldIndex)
        val model = FinanceViewModel()
        val before = model.uiState.value
        for (amount in listOf("", "0", "-1", "1.5", "9999999999999", "9223372036854775808")) {
            assertNotNull(model.saveTransaction(null, amount, "cash", EntryType.EXPENSE,
                "Food", DemoFixtures.today.toString(), ""))
            assertEquals(before, model.uiState.value)
        }
    }
    @Test fun maximumAmountAndNoteBoundaryRemainValid() {
        val model = FinanceViewModel()
        val before = model.uiState.value
        assertNull(model.saveTransaction(null, MAX_AMOUNT.toString(), "bank", EntryType.INCOME,
            "Salary", DemoFixtures.today.toString(), "x".repeat(300)))
        val saved = model.uiState.value
        assertEquals(MAX_AMOUNT, saved.entries.last().amount)
        assertEquals(personalNetWorth(before.wallets, before.entries) + MAX_AMOUNT,
            personalNetWorth(saved.wallets, saved.entries))
        assertNotNull(model.saveTransaction(null, "1", "bank", EntryType.INCOME,
            "Salary", DemoFixtures.today.toString(), "x".repeat(301)))
        assertEquals(saved, model.uiState.value)
    }
    @Test fun undoRestoresExactlyOnceAndRecentDefaultsFollowSavedEntry() {
        val model = FinanceViewModel()
        model.saveTransaction(null, "10000", "bank", EntryType.EXPENSE, "Transport", DemoFixtures.today.toString(), "")
        val saved = model.uiState.value
        val entry = saved.entries.last()
        assertEquals("bank", saved.recentWalletId)
        assertEquals("Transport", saved.recentExpenseCategory)
        model.deleteTransaction(entry.id)
        model.restoreTransaction(entry)
        model.restoreTransaction(entry)
        assertEquals(saved.entries, model.uiState.value.entries)
    }
    @Test fun editsMoveAmountsBetweenWalletsWithoutDoubleCounting() {
        val model = FinanceViewModel()
        val before = model.uiState.value
        assertNull(model.saveTransaction(null, "50000", "cash", EntryType.EXPENSE, "Food", "2026-10-25", "Test lunch"))
        val id = model.uiState.value.entries.last().id
        assertEquals(personalNetWorth(before.wallets, before.entries) - 50_000, personalNetWorth(before.wallets, model.uiState.value.entries))
        assertNull(model.saveTransaction(id, "80000", "bank", EntryType.EXPENSE, "Food", "2026-10-25", "Changed"))
        assertEquals(walletBalance(before.wallets.first(), before.entries), walletBalance(before.wallets.first(), model.uiState.value.entries))
        assertEquals(personalNetWorth(before.wallets, before.entries) - 80_000, personalNetWorth(before.wallets, model.uiState.value.entries))
        model.deleteTransaction(id)
        assertEquals(before.entries, model.uiState.value.entries)
    }
    @Test fun validationRejectsZeroWrongCategoryFutureDateAndArchivedWallet() {
        val model = FinanceViewModel()
        assertNotNull(model.saveTransaction(null, "0", "cash", EntryType.EXPENSE, "Food", "2026-10-25", ""))
        assertNotNull(model.saveTransaction(null, "20", "cash", EntryType.INCOME, "Food", "2026-10-25", ""))
        assertNotNull(model.saveTransaction(null, "20", "cash", EntryType.EXPENSE, "Food", DemoFixtures.today.plusDays(1).toString(), ""))
        model.archiveWallet("cash")
        assertNotNull(model.saveTransaction(null, "20", "cash", EntryType.EXPENSE, "Food", "2026-10-25", ""))
    }
    @Test fun creditCardExpenseIncreasesDebt() {
        val model = FinanceViewModel()
        val card = model.uiState.value.wallets.first { it.id == "card" }
        assertNull(model.saveTransaction(null, "100000", "card", EntryType.EXPENSE, "Food", "2026-10-25", ""))
        assertEquals(600_000L, walletBalance(card, model.uiState.value.entries))
    }
}
