package com.finsync

import com.finsync.domain.*
import com.finsync.ui.FinanceViewModel
import com.finsync.data.DemoFixtures
import org.junit.Assert.*
import org.junit.Test
import java.time.YearMonth

class WalletTransferTest {
    @Test fun transferIsBalancedAtomicAndExcludedFromReports() {
        val model = FinanceViewModel()
        val before = model.uiState.value
        assertNull(model.transferWallets("request", "cash", "bank", "50000", "Move funds"))
        val after = model.uiState.value
        val cash = before.wallets.first { it.id == "cash" }
        val bank = before.wallets.first { it.id == "bank" }
        assertEquals(walletBalance(cash, before.entries) - 50000, walletBalance(cash, after.entries))
        assertEquals(walletBalance(bank, before.entries) + 50000, walletBalance(bank, after.entries))
        assertEquals(personalNetWorth(before.wallets, before.entries), personalNetWorth(after.wallets, after.entries))
        assertEquals(monthlyReport(before.entries, YearMonth.from(DemoFixtures.today)), monthlyReport(after.entries, YearMonth.from(DemoFixtures.today)))
        before.budgets.forEach { assertEquals(budgetSpent(it, before.entries, DemoFixtures.today), budgetSpent(it, after.entries, DemoFixtures.today)) }
        assertEquals(2, after.entries.count { it.transferId == "request" })
        assertEquals(1, after.transfers.size)
        assertNull(model.transferWallets("request", "cash", "bank", "50000", "Move funds"))
        assertEquals(after, model.uiState.value)
        assertNotNull(model.transferWallets("request", "cash", "bank", "60000", "Move funds"))
        after.entries.filter { it.transferId == "request" }.forEach { model.deleteTransaction(it.id) }
        assertEquals(after, model.uiState.value)
        val leg = after.entries.last()
        assertNotNull(model.saveTransaction(leg.id, "100", "cash", EntryType.EXPENSE, "Food", DemoFixtures.today.toString(), ""))
    }
    @Test fun invalidTransfersDoNotMutateAnyState() {
        val model = FinanceViewModel()
        val before = model.uiState.value
        for (amount in listOf("", "0", "-1", "0.5", "9999999999999", "999999999999")) {
            assertNotNull(model.transferWallets("r", "cash", "bank", amount, ""))
            assertEquals(before, model.uiState.value)
        }
        for ((source, destination) in listOf("cash" to "cash", "missing" to "bank", "cash" to "missing", "cash" to "card", "card" to "bank")) {
            assertNotNull(model.transferWallets("r", source, destination, "1", ""))
            assertEquals(before, model.uiState.value)
        }
        assertNotNull(model.transferWallets("r", "cash", "bank", "1", "x".repeat(301)))
        model.archiveWallet("bank")
        val archived = model.uiState.value
        assertNotNull(model.transferWallets("r", "cash", "bank", "1", ""))
        assertEquals(archived, model.uiState.value)
    }
    @Test fun rechecksAvailableBalanceAtConfirmation() {
        val model = FinanceViewModel()
        val cash = model.uiState.value.wallets.first { it.id == "cash" }
        val balance = walletBalance(cash, model.uiState.value.entries)
        assertNull(model.transferWallets("first", "cash", "bank", balance.toString(), ""))
        assertEquals(0L, walletBalance(cash, model.uiState.value.entries))
        assertNotNull(model.transferWallets("second", "cash", "bank", "1", ""))
        assertEquals(1, model.uiState.value.transfers.size)
    }
}
