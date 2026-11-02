package com.finsync.data

import com.finsync.domain.*
import java.time.LocalDate

/** Fictional seed only. Recreated when the app process restarts; never sent anywhere. */
object DemoFixtures {
    val today: LocalDate = LocalDate.now()
    private fun recentDay(daysAgo: Long) = today.minusDays(daysAgo).coerceAtLeast(today.withDayOfMonth(1))
    val wallets = listOf(
        Wallet("cash", "Everyday cash", WalletType.CASH),
        Wallet("bank", "Personal bank ledger", WalletType.BANK_ACCOUNT),
        Wallet("card", "Credit card", WalletType.CREDIT_CARD),
        Wallet("ewallet", "Pocket e-wallet", WalletType.E_WALLET),
    )
    val entries = listOf(
        LedgerEntry("opening-cash", "cash", EntryType.OPENING, 2_000_000, "Opening balance", today.minusMonths(1)),
        LedgerEntry("opening-bank", "bank", EntryType.OPENING, 5_000_000, "Opening balance", today.minusMonths(1)),
        LedgerEntry("opening-card", "card", EntryType.OPENING, 500_000, "Opening balance", today.minusMonths(1)),
        LedgerEntry("opening-ewallet", "ewallet", EntryType.OPENING, 250_000, "Opening balance", today.minusMonths(1)),
        LedgerEntry("allowance", "bank", EntryType.INCOME, 8_000_000, "Salary", recentDay(1), "Monthly allowance"),
        LedgerEntry("lunch", "cash", EntryType.EXPENSE, 65_000, "Food", today, "Lunch"),
        LedgerEntry("dining", "cash", EntryType.EXPENSE, 1_135_000, "Food", recentDay(7), "Dining this month"),
        LedgerEntry("bus", "cash", EntryType.EXPENSE, 100_000, "Transport", recentDay(1), "Bus pass"),
        LedgerEntry("rent", "bank", EntryType.EXPENSE, 1_000_000, "Housing", recentDay(15), "Room rent"),
    )
}
