package com.finsync.data

import com.finsync.domain.*

object GroupFixtures {
    val groups = listOf(
        ExpenseGroup("home", "Roommates", "HOME", "Shared household expenses", listOf(
            GroupMember("me", "You"), GroupMember("linh", "Linh"), GroupMember("minh", "Minh"))),
        ExpenseGroup("trip", "Weekend trip", "TRIP", "Weekend plans", listOf(GroupMember("an", "An")),
            owner = "an", invite = "TRIP26", joined = false),
    )
    val expenses = listOf(GroupExpense("dinner", "home", "Roommates dinner", 300_000, "me",
        mapOf("me" to 100_000, "linh" to 100_000, "minh" to 100_000), DemoFixtures.today, "Food"))
}
