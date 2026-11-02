package com.finsync.data

import com.finsync.domain.*

object ReportFixtures {
    val budgets = listOf(Budget("food", "Food", 1_500_000, BudgetCycle.Monthly), Budget("transport", "Transport", 500_000, BudgetCycle.Monthly))
    val goals = listOf(SavingsGoal("laptop", "Laptop fund", 12_000_000, 3_000_000, DemoFixtures.today.plusMonths(8)))
    // Authored sample copy, never a result from a model or financial advisory service.
    val insights = listOf(
        "Review recurring costs" to "Example suggestion: set aside time to check subscriptions you no longer use.",
        "Plan before a shared trip" to "Example suggestion: agree on categories and a spending limit with your group before you travel.",
        "Keep goals realistic" to "Example suggestion: choose a savings target and review your progress each month.",
    )
}
