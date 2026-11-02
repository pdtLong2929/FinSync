package com.finsync.domain

import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate

data class GroupMember(val id: String, val name: String, val active: Boolean = true)
data class ExpenseGroup(val id: String, val name: String, val type: String, val description: String,
    val members: List<GroupMember>, val owner: String = "me", val invite: String = "SYNC88", val joined: Boolean = true)
data class GroupExpense(val id: String, val groupId: String, val title: String, val amount: Long,
    val payer: String, val shares: Map<String, Long>, val date: LocalDate, val category: String)
data class Settlement(val id: String, val groupId: String, val from: String, val to: String,
    val amount: Long, val date: LocalDate)
data class Debt(val from: String, val to: String, val amount: Long)
enum class SplitMode { Equal, Exact, Percentage, Shares }

data class SplitProgress(val allocated: Long, val difference: Long, val detail: String, val inputsValid: Boolean = true)

private fun percentageBasisPoints(input: String): Long? = runCatching {
    if (!input.matches(Regex("[0-9]{1,3}(\\.[0-9]{1,2})?"))) return null
    BigDecimal(input).movePointRight(2).longValueExact().takeIf { it in 0..10_000 }
}.getOrNull()

/** Live whole-VND preview, including the remaining/overallocated amount while editing a split. */
fun splitProgress(amount: Long, payer: String, participants: List<String>, mode: SplitMode,
                  inputs: Map<String, String>): SplitProgress {
    require(amount in 1..MAX_AMOUNT) { "Enter a positive whole-VND amount." }
    require(participants.isNotEmpty() && participants.distinct().size == participants.size && payer in participants) {
        "Select participants including the payer."
    }
    return when (mode) {
        SplitMode.Equal -> SplitProgress(amount, 0, "Equal shares · payer receives any whole-VND remainder.")
        SplitMode.Exact -> {
            val entered = participants.mapNotNull { parseVnd(inputs[it].orEmpty()) }
            val total = entered.sum()
            SplitProgress(total, amount - total, "Assigned amounts · ${entered.size}/${participants.size} valid shares.",
                inputsValid = entered.size == participants.size)
        }
        SplitMode.Percentage -> {
            val points = participants.mapNotNull { percentageBasisPoints(inputs[it].orEmpty()) }
            val totalPoints = points.sum()
            val allocated = BigInteger.valueOf(amount).multiply(BigInteger.valueOf(totalPoints))
                .divide(BigInteger.valueOf(10_000)).longValueExact()
            SplitProgress(allocated, amount - allocated,
                "Percent total · ${BigDecimal.valueOf(totalPoints, 2).stripTrailingZeros().toPlainString()}% · ${points.size}/${participants.size} valid.",
                inputsValid = points.size == participants.size)
        }
        SplitMode.Shares -> {
            val values = participants.map { parseVnd(inputs[it].orEmpty()) }
            if (values.any { it == null }) {
                SplitProgress(0, amount, "Enter a nonnegative whole share count for each participant.", inputsValid = false)
            } else {
                val totalShares = values.filterNotNull().sum()
                if (totalShares == 0L) SplitProgress(0, amount, "Total shares must be greater than zero.", inputsValid = false)
                else SplitProgress(amount, 0, "Total shares · $totalShares · payer receives any rounding remainder.")
            }
        }
    }
}

data class SharedExpenseErrors(val title: String? = null, val amount: String? = null, val date: String? = null) {
    val first get() = title ?: amount ?: date
}

fun sharedExpenseErrors(title: String, amount: String, date: String, today: LocalDate): SharedExpenseErrors {
    val day = runCatching { LocalDate.parse(date) }.getOrNull()
    return SharedExpenseErrors(
        title = if (title.trim().length !in 1..60) "Enter a title of 1–60 characters." else null,
        amount = if ((parseVnd(amount) ?: 0L) <= 0) "Enter a positive whole VND amount." else null,
        date = when { day == null -> "Enter a valid date."; day > today -> "The date cannot be in the future."; else -> null },
    )
}

fun settlementValidation(debt: Debt?, amount: String, date: String, today: LocalDate): String? {
    if (debt == null) return "This debt has already changed. Return to the debt summary."
    val value = parseVnd(amount) ?: return "Enter a whole VND amount."
    if (value !in 1..debt.amount) return "Amount must be positive and no greater than the outstanding debt."
    val day = runCatching { LocalDate.parse(date) }.getOrNull() ?: return "Enter a valid date."
    if (day > today) return "The date cannot be in the future."
    return null
}

/** Validate before allocating. All currency arithmetic is whole VND, never binary floating point. */
fun splitExpense(amount: Long, payer: String, participants: List<String>, mode: SplitMode,
                 inputs: Map<String, String> = emptyMap()): Map<String, Long> {
    require(amount in 1..MAX_AMOUNT) { "Enter a positive whole VND amount." }
    require(participants.isNotEmpty() && participants.distinct().size == participants.size && payer in participants) {
        "Select participants including the payer."
    }
    val shares = when (mode) {
        SplitMode.Equal -> participants.associateWith { amount / participants.size }
        SplitMode.Exact -> participants.associateWith { parseVnd(inputs[it].orEmpty()) ?: error("Enter each exact share as a nonnegative whole VND amount.") }
        SplitMode.Percentage -> {
            val points = participants.associateWith { id ->
                percentageBasisPoints(inputs[id].orEmpty()) ?: error("Use percentages from 0% to 100% with up to 2 decimal places.")
            }
            require(points.values.sum() == 10_000L) { "Percentages must total 100%." }
            points.mapValues { amount * it.value / 10_000 }
        }
        SplitMode.Shares -> {
            val counts = participants.associateWith { id ->
                parseVnd(inputs[id].orEmpty()) ?: error("Enter each share count as a nonnegative whole number.")
            }
            val total = counts.values.fold(BigInteger.ZERO) { sum, value -> sum + BigInteger.valueOf(value) }
            require(total > BigInteger.ZERO) { "Total shares must be greater than zero." }
            counts.mapValues { (_, count) ->
                BigInteger.valueOf(amount).multiply(BigInteger.valueOf(count)).divide(total).longValueExact()
            }
        }
    }.toMutableMap()
    if (mode == SplitMode.Exact) require(shares.values.sum() == amount) { "Exact shares must equal the total expense." }
    else shares[payer] = shares.getValue(payer) + amount - shares.values.sum()
    return shares
}

fun groupBalances(group: ExpenseGroup, expenses: List<GroupExpense>, settlements: List<Settlement>): Map<String, Long> {
    val result = group.members.associate { it.id to 0L }.toMutableMap()
    expenses.filter { it.groupId == group.id }.forEach { entry ->
        result[entry.payer] = result.getValue(entry.payer) + entry.amount
        entry.shares.forEach { (id, share) -> result[id] = result.getValue(id) - share }
    }
    settlements.filter { it.groupId == group.id }.forEach {
        result[it.from] = result.getValue(it.from) + it.amount
        result[it.to] = result.getValue(it.to) - it.amount
    }
    return result
}

/** Deterministic largest-debtor / largest-creditor matching; at most N-1 suggestions. */
fun simplifiedDebts(balances: Map<String, Long>): List<Debt> {
    require(balances.values.sum() == 0L)
    val remaining = balances.toMutableMap()
    val debts = mutableListOf<Debt>()
    while (true) {
        val debtor = remaining.entries.filter { it.value < 0 }.minByOrNull { it.value } ?: break
        val creditor = remaining.entries.maxBy { it.value }
        val amount = minOf(-debtor.value, creditor.value)
        debts += Debt(debtor.key, creditor.key, amount)
        remaining[debtor.key] = debtor.value + amount
        remaining[creditor.key] = creditor.value - amount
    }
    return debts
}
