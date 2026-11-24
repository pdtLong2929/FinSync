package com.finsync.ui

import com.finsync.domain.*
import java.time.LocalDate

internal data class GroupRecordChange(val state: FinanceState, val error: String? = null)

internal fun canEditExpense(group: ExpenseGroup, expense: GroupExpense) = group.owner == "me" || expense.payer == "me"
internal fun canEditPayment(group: ExpenseGroup, payment: Settlement) = group.owner == "me" || payment.from == "me" || payment.to == "me"
internal fun ExpenseGroup.hasFormerParticipant(ids: Collection<String>) = ids.any { id -> members.none { it.id == id && it.active } }

/** Pure record corrections. Existing split/rounding and balance algorithms are reused. */
internal object GroupRecordEdits {
    fun expense(state: FinanceState, id: String, title: String, amount: String, payer: String, date: String,
        category: String, people: List<String>, mode: SplitMode, inputs: Map<String, String>, today: LocalDate): GroupRecordChange {
        fun fail(message: String) = GroupRecordChange(state, message)
        val old = state.groupExpenses.find { it.id == id } ?: return fail("This expense is no longer available.")
        val group = state.groups.find { it.id == old.groupId && it.joined } ?: return fail("Group not found.")
        if (!canEditExpense(group, old)) return fail("Only the owner or payer can change this expense.")
        sharedExpenseErrors(title, amount, date, today).first?.let { return fail(it) }
        if (people.any { id -> group.members.none { it.id == id } }) return fail("Choose group members.")
        val shares = runCatching { splitExpense(parseVnd(amount)!!, payer, people, mode, inputs) }
            .getOrElse { return fail(it.message ?: "Check the split.") }
        val financialChange = old.amount != parseVnd(amount) || old.payer != payer || old.shares != shares
        if (financialChange && group.hasFormerParticipant(old.shares.keys + people))
            return fail("Expenses involving former members can only change title, date and category.")
        val updated = old.copy(title = title.trim(), amount = parseVnd(amount)!!, payer = payer, shares = shares,
            date = LocalDate.parse(date), category = category, splitMode = mode,
            splitInputs = if (mode == SplitMode.Equal) emptyMap() else people.associateWith { inputs[it].orEmpty().trim() })
        return GroupRecordChange(state.copy(groupExpenses = state.groupExpenses.map { if (it.id == id) updated else it }))
    }

    fun removeExpense(state: FinanceState, id: String): GroupRecordChange {
        val old = state.groupExpenses.find { it.id == id } ?: return GroupRecordChange(state, "Expense already removed.")
        val group = state.groups.find { it.id == old.groupId && it.joined } ?: return GroupRecordChange(state, "Group not found.")
        if (!canEditExpense(group, old)) return GroupRecordChange(state, "Only the owner or payer can remove this expense.")
        if (group.hasFormerParticipant(old.shares.keys)) return GroupRecordChange(state, "This expense involves former members. Keep its financial history.")
        return GroupRecordChange(state.copy(groupExpenses = state.groupExpenses.filterNot { it.id == id }))
    }

    fun restoreExpense(state: FinanceState, expense: GroupExpense): GroupRecordChange {
        state.groupExpenses.find { it.id == expense.id }?.let {
            return GroupRecordChange(state, if (it == expense) null else "A different expense uses this ID.")
        }
        val group = state.groups.find { it.id == expense.groupId && it.joined } ?: return GroupRecordChange(state, "Group no longer available.")
        if (!canEditExpense(group, expense) || group.hasFormerParticipant(expense.shares.keys))
            return GroupRecordChange(state, "Group membership changed. This expense cannot be restored.")
        return GroupRecordChange(state.copy(groupExpenses = state.groupExpenses + expense))
    }

    fun payment(state: FinanceState, id: String, amount: String, date: String, today: LocalDate): GroupRecordChange {
        val old = state.settlements.find { it.id == id } ?: return GroupRecordChange(state, "Payment record no longer available.")
        val group = state.groups.find { it.id == old.groupId && it.joined } ?: return GroupRecordChange(state, "Group not found.")
        if (!canEditPayment(group, old)) return GroupRecordChange(state, "Only the owner or payment participants can correct this record.")
        if (group.hasFormerParticipant(listOf(old.from, old.to))) return GroupRecordChange(state, "This record involves a former member and cannot be changed.")
        val before = groupBalances(group, state.groupExpenses, state.settlements.filterNot { it.id == id })
        val capacity = minOf((-before.getValue(old.from)).coerceAtLeast(0), before.getValue(old.to).coerceAtLeast(0))
        // A correction may reduce a historical payment even when later expenses changed its route.
        val limit = maxOf(old.amount, capacity)
        settlementValidation(Debt(old.from, old.to, limit), amount, date, today)?.let { return GroupRecordChange(state, it) }
        return GroupRecordChange(state.copy(settlements = state.settlements.map {
            if (it.id == id) old.copy(amount = parseVnd(amount)!!, date = LocalDate.parse(date)) else it
        }))
    }

    fun removePayment(state: FinanceState, id: String): GroupRecordChange {
        val old = state.settlements.find { it.id == id } ?: return GroupRecordChange(state, "Payment record already removed.")
        val group = state.groups.find { it.id == old.groupId && it.joined } ?: return GroupRecordChange(state, "Group not found.")
        if (!canEditPayment(group, old)) return GroupRecordChange(state, "Only the owner or payment participants can remove this record.")
        if (group.hasFormerParticipant(listOf(old.from, old.to))) return GroupRecordChange(state, "This record involves a former member. Keep its financial history.")
        return GroupRecordChange(state.copy(settlements = state.settlements.filterNot { it.id == id }))
    }

    fun restorePayment(state: FinanceState, payment: Settlement): GroupRecordChange {
        state.settlements.find { it.id == payment.id }?.let {
            return GroupRecordChange(state, if (it == payment) null else "A different payment uses this ID.")
        }
        val group = state.groups.find { it.id == payment.groupId && it.joined } ?: return GroupRecordChange(state, "Group no longer available.")
        if (!canEditPayment(group, payment) || group.hasFormerParticipant(listOf(payment.from, payment.to)))
            return GroupRecordChange(state, "Group membership changed. This record cannot be restored.")
        return GroupRecordChange(state.copy(settlements = state.settlements + payment))
    }
}
