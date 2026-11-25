package com.finsync.groups

import com.finsync.domain.*
import java.math.BigDecimal
import java.math.BigInteger

/** Mirrors PA4 backend largest-remainder rounding; local ledgers keep their original rules. */
internal fun serverSplit(amount: Long, payer: String, people: List<String>, mode: SplitMode,
    inputs: Map<String, String>): Map<String, Long> {
    val validated = splitExpense(amount, payer, people, mode, inputs)
    if (mode == SplitMode.Exact) return validated
    val weights = people.map { id -> when (mode) {
        SplitMode.Equal -> 1L
        SplitMode.Percentage -> BigDecimal(inputs.getValue(id)).movePointRight(2).longValueExact()
        SplitMode.Shares -> (parseVnd(inputs[id].orEmpty()) ?: error("Enter whole shares.")).also {
            require(it <= 1_000_000) { "Use at most 1,000,000 shares per person." }
        }
        else -> error("Unsupported split")
    } }
    val total = weights.fold(BigInteger.ZERO) { sum, value -> sum + BigInteger.valueOf(value) }
    val products = weights.map { BigInteger.valueOf(amount) * BigInteger.valueOf(it) }
    val values = products.map { (it / total).longValueExact() }.toMutableList()
    val remainder = amount - values.sum()
    val order = people.indices.sortedWith(compareByDescending<Int> { products[it] % total }.thenBy { it })
    repeat(remainder.toInt()) { values[order[it]]++ }
    return people.indices.associate { people[it] to values[it] }
}
