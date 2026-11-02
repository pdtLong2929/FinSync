package com.finsync.ui.dashboard

import com.finsync.R
import com.finsync.data.DemoFixtures
import com.finsync.domain.*
import com.finsync.ui.FinanceState

fun FinanceState.toDashboardData(): DashboardData {
    val monthEntries = entries.filter { it.date.month == DemoFixtures.today.month && it.date.year == DemoFixtures.today.year }
    val groupNets = groups.filter { it.joined }.map { groupBalances(it, groupExpenses, settlements)["me"] ?: 0L }
    return DemoDashboard.data.copy(
        spendingTrend = spendingTrend(entries, java.time.YearMonth.from(DemoFixtures.today)).map { it.first.toString() to it.second },
        group = GroupSummary(R.string.sample_group, groupNets.filter { it > 0 }.sum(), -groupNets.filter { it < 0 }.sum(), "All shared groups"),
        personal = PersonalSummary(personalNetWorth(wallets, entries),
            monthEntries.filter { it.type == EntryType.INCOME }.sumOf { it.amount },
            monthEntries.filter { it.type == EntryType.EXPENSE }.sumOf { it.amount }),
        transactions = entries.filter { it.type.isTransaction }.sortedByDescending { it.date }.take(3).map { entry ->
            PersonalTransaction(entry.id, R.string.sample_lunch, R.string.sample_lunch_detail,
                entry.amount * if (entry.type == EntryType.EXPENSE) -1 else 1,
                entry.note.ifBlank { entry.category }, "${entry.category} · ${wallets.find { it.id == entry.walletId }?.name} · ${entry.date}", category = entry.category)
        },
        diningSpent = monthEntries.filter { it.type == EntryType.EXPENSE && it.category == "Food" }.sumOf { it.amount },
        diningLimit = budgets.find { it.category == "Food" && it.cycle == BudgetCycle.Monthly }?.limit ?: DemoDashboard.data.diningLimit,
    )
}
