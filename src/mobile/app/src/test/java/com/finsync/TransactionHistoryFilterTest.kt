package com.finsync

import com.finsync.domain.*
import com.finsync.ui.transactions.TransactionFilterState
import com.finsync.ui.transactions.filterTransactions
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class TransactionHistoryFilterTest {
    private val today = LocalDate.parse("2026-10-30")
    private val wallets = listOf(
        Wallet("cash", "Everyday Cash", WalletType.CASH),
        Wallet("bank", "Blue Bank", WalletType.BANK_ACCOUNT),
    )
    private val entries = listOf(
        LedgerEntry("coffee", "cash", EntryType.EXPENSE, 50_000, "Food", today, "Coffee run"),
        LedgerEntry("salary", "bank", EntryType.INCOME, 1_000_000, "Salary", today.minusDays(5), "October pay"),
        LedgerEntry("bus", "bank", EntryType.EXPENSE, 120_000, "Transport", LocalDate.parse("2025-09-29"), "Bus card"),
        LedgerEntry("opening", "cash", EntryType.OPENING, 500_000, "Opening balance", today),
    )

    @Test fun searchesAcrossNameNotesCategoryAndWalletWithoutIncludingNonTransactions() {
        assertEquals(listOf("coffee"), filterTransactions(entries, wallets, "coffee", TransactionFilterState(), today).map { it.id })
        assertEquals(listOf("coffee"), filterTransactions(entries, wallets, "food", TransactionFilterState(), today).map { it.id })
        assertEquals(listOf("coffee"), filterTransactions(entries, wallets, "everyday cash", TransactionFilterState(), today).map { it.id })
        assertEquals(listOf("bus"), filterTransactions(entries, wallets, "card", TransactionFilterState(), today).map { it.id })
    }

    @Test fun combinesTypeTimeCategoryWalletAndInclusiveAmountBounds() {
        val filters = TransactionFilterState(
            type = "Expense", period = "Last 30 days", category = "Food", walletId = "cash",
            minimumAmount = "50000", maximumAmount = "50000",
        )
        assertEquals(listOf("coffee"), filterTransactions(entries, wallets, "", filters, today).map { it.id })
        assertEquals(emptyList<String>(), filterTransactions(entries, wallets, "", filters.copy(maximumAmount = "49999"), today).map { it.id })
    }

    @Test fun datePresetsIncludeExpectedBoundaryAndCalendarYear() {
        assertEquals(listOf("coffee", "salary"), filterTransactions(entries, wallets, "", TransactionFilterState(period = "This month"), today).map { it.id })
        assertEquals(listOf("coffee"), filterTransactions(entries, wallets, "", TransactionFilterState(period = "This week"), today).map { it.id })
        assertEquals(listOf("coffee", "salary"), filterTransactions(entries, wallets, "", TransactionFilterState(period = "This year"), today).map { it.id })
        assertEquals(listOf("coffee", "salary", "bus"), filterTransactions(entries, wallets, "", TransactionFilterState(), today).map { it.id })
    }
}
