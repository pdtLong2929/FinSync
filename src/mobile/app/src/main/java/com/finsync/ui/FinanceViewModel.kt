package com.finsync.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsync.data.DemoFixtures
import com.finsync.data.GroupFixtures
import com.finsync.data.ReportFixtures
import com.finsync.domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import java.time.LocalDate
import com.finsync.bank.*

data class FinanceState(
    val wallets: List<Wallet> = DemoFixtures.wallets,
    val entries: List<LedgerEntry> = DemoFixtures.entries,
    val categories: List<Category> = defaultCategories,
    val groups: List<ExpenseGroup> = GroupFixtures.groups,
    val groupExpenses: List<GroupExpense> = GroupFixtures.expenses,
    val settlements: List<Settlement> = emptyList(),
    val budgets: List<Budget> = ReportFixtures.budgets,
    val goals: List<SavingsGoal> = ReportFixtures.goals,
    val recentWalletId: String = "cash",
    val recentExpenseCategory: String = "Food",
    val transfers: List<WalletTransfer> = emptyList(),
)

/** Personal demo data plus an account-scoped device ledger for groups. */
class FinanceViewModel : ViewModel() {
    private val stream = MutableStateFlow(FinanceState())
    private var personalObserver: Pair<Any, (PersonalLedger) -> Unit>? = null
    private var personalWorkspace: String? = null
    private val personalCache = mutableMapOf<String, PersonalLedger>()
    var durablePersonal: Boolean = false
    val personalToday: LocalDate get() = if (durablePersonal) LocalDate.now() else DemoFixtures.today
    private val state = object {
        var value: FinanceState
            get() = stream.value
            set(next) {
                val before = stream.value.personalLedger()
                stream.value = next
                val after = next.personalLedger()
                if (before != after) personalObserver?.second?.invoke(after)
            }
    }
    val uiState = stream.asStateFlow()
    fun observePersonal(owner: Any, listener: (PersonalLedger) -> Unit) { personalObserver = owner to listener }
    fun stopObservingPersonal(owner: Any) { if (personalObserver?.first === owner) personalObserver = null }
    fun restorePersonal(ledger: PersonalLedger) { state.value = state.value.withPersonal(ledger) }
    fun selectPersonalWorkspace(workspace: String) {
        if (workspace == personalWorkspace) return
        personalWorkspace?.let { personalCache[it] = state.value.personalLedger() }
        val initial = if (personalWorkspace == null) state.value.personalLedger() else FinanceState().personalLedger()
        personalWorkspace = workspace; durablePersonal = false
        restorePersonal(personalCache[workspace] ?: initial)
    }
    val groupStorage = GroupLedgerPersistence(viewModelScope, { state.value }) { ledger ->
        state.value = state.value.copy(groups = ledger.groups, groupExpenses = ledger.expenses, settlements = ledger.settlements)
    }
    val groupToday: LocalDate get() = if (groupStorage.state.value.workspace == null) DemoFixtures.today else LocalDate.now()

    /** One local atomic state update; request ID makes repeated confirmation idempotent. */
    @Synchronized
    fun transferWallets(requestId: String, sourceId: String, destinationId: String, amount: String, note: String): String? {
        val current = state.value
        if (requestId.isBlank()) return "Unable to identify this transfer. Reopen the form."
        current.transfers.find { it.id == requestId }?.let { previous ->
            return if (previous.sourceId == sourceId && previous.destinationId == destinationId &&
                previous.amount == parseVnd(amount) && previous.note == note.trim()) null
                else "This confirmation has already been used. Start a new transfer."
        }
        val source = current.wallets.find { it.id == sourceId }
        val destination = current.wallets.find { it.id == destinationId }
        transferValidation(source, destination, amount, note, current.entries)?.let { return it }
        val transfer = WalletTransfer(requestId, sourceId, destinationId, parseVnd(amount)!!, note.trim(), personalToday)
        val pair = listOf(
            LedgerEntry(UUID.randomUUID().toString(), sourceId, EntryType.TRANSFER_OUT, transfer.amount,
                "To ${destination!!.name}", transfer.date, transfer.note, requestId),
            LedgerEntry(UUID.randomUUID().toString(), destinationId, EntryType.TRANSFER_IN, transfer.amount,
                "From ${source!!.name}", transfer.date, transfer.note, requestId),
        )
        state.value = current.copy(entries = current.entries + pair, transfers = current.transfers + transfer)
        return null
    }

    fun saveWallet(id: String?, name: String, type: WalletType, opening: String): String? {
        val current = state.value
        walletValidation(name, opening, current.wallets, id)?.let { return it }
        if (id != null) {
            val old = current.wallets.find { it.id == id } ?: return "Wallet is no longer available."
            state.value = current.copy(wallets = current.wallets.map { if (it.id == id) old.copy(name = name.trim()) else it })
        } else {
            val wallet = Wallet(UUID.randomUUID().toString(), name.trim(), type)
            val entry = LedgerEntry(UUID.randomUUID().toString(), wallet.id, EntryType.OPENING,
                parseVnd(opening)!!, "Opening balance", DemoFixtures.today)
            state.value = current.copy(wallets = current.wallets + wallet, entries = current.entries + entry)
        }
        return null
    }

    fun archiveWallet(id: String) {
        state.value = state.value.copy(wallets = state.value.wallets.map {
            if (it.id == id) it.copy(archived = !it.archived) else it
        })
    }

    fun saveTransaction(id: String?, amount: String, walletId: String, type: EntryType,
                        category: String, date: String, note: String): String? {
        val current = state.value
        transactionFieldErrors(amount, current.wallets.find { it.id == walletId }, category, type,
            date, personalToday, current.categories, note).first?.let { return it }
        if (!type.isTransaction) return "Use the dedicated transfer flow."
        if (id != null && current.entries.none { it.id == id && it.type.isTransaction }) return "Transaction is no longer editable."
        val entry = LedgerEntry(id ?: UUID.randomUUID().toString(), walletId, type, parseVnd(amount)!!, category, LocalDate.parse(date), note.trim(), categoryId = current.categories.first { it.name == category && it.type == type }.id)
        state.value = current.copy(entries = if (id == null) current.entries + entry else current.entries.map { if (it.id == id) entry else it },
            recentWalletId = walletId, recentExpenseCategory = if (type == EntryType.EXPENSE) category else current.recentExpenseCategory)
        return null
    }

    fun deleteTransaction(id: String) {
        state.value = state.value.copy(entries = state.value.entries.filterNot { it.id == id && it.type.isTransaction })
    }

    fun restoreTransaction(entry: LedgerEntry) {
        if (entry.type.isTransaction && state.value.entries.none { it.id == entry.id } && state.value.wallets.any { it.id == entry.walletId }) {
            state.value = state.value.copy(entries = state.value.entries + entry)
        }
    }

    fun addCategory(name: String, type: EntryType): String? {
        if (name.trim().length !in 1..30) return "Enter a category name (1–30 characters)."
        if (state.value.categories.any { it.name.equals(name.trim(), true) }) return "This category already exists."
        state.value = state.value.copy(categories = state.value.categories + Category(name.trim(), type))
        return null
    }

    fun setCategoryVisual(name: String, icon: CategoryIcon, color: CategoryColor) {
        state.value = state.value.copy(categories = state.value.categories.map {
            if (it.name == name) it.copy(icon = icon, color = color) else it
        })
    }

    fun createGroup(name: String, type: String, description: String): String? {
        if (name.trim().length !in 1..40 || description.length > 300) return "Use a name of 1–40 and description of up to 300 characters."
        if (state.value.groups.any { it.name.equals(name.trim(), true) }) return "A group already uses this name."
        val group = ExpenseGroup(UUID.randomUUID().toString(), name.trim(), type, description.trim(),
            listOf(GroupMember("me", "You")), invite = UUID.randomUUID().toString().take(6).uppercase())
        state.value = state.value.copy(groups = state.value.groups + group)
        return null
    }

    fun editGroup(id: String, name: String, type: String, description: String): String? {
        val current = state.value
        val group = current.groups.find { it.id == id } ?: return "Group not found."
        if (group.owner != "me") return "Only the owner can edit this group."
        if (name.trim().length !in 1..40 || description.length > 300) return "Use a name of 1–40 and description of up to 300 characters."
        if (current.groups.any { it.id != id && it.name.equals(name.trim(), true) }) return "A group already uses this name."
        state.value = current.copy(groups = current.groups.map {
            if (it.id == id) group.copy(name = name.trim(), type = type, description = description.trim()) else it
        })
        return null
    }

    fun joinGroup(input: String): String? {
        if (groupStorage.state.value.workspace != null) return "Online invitations need group sync. You can create a group and add members on this device."
        val code = input.trim().substringAfterLast('/').uppercase()
        val group = state.value.groups.find { it.invite == code } ?: return "Invitation not found. Check the code and try again."
        if (group.joined) return "You have already joined this group."
        state.value = state.value.copy(groups = state.value.groups.map {
            if (it.id == group.id) it.copy(joined = true, members = it.members + GroupMember("me", "You")) else it
        })
        return null
    }

    fun changeMember(groupId: String, name: String = "", removeId: String? = null): String? {
        val group = state.value.groups.find { it.id == groupId } ?: return "Group not found."
        if (group.owner != "me") return "Only the group owner can manage members."
        val members = if (removeId != null) {
            if (removeId == group.owner) return "The owner cannot be removed."
            if (groupBalances(group, state.value.groupExpenses, state.value.settlements)[removeId] != 0L) return "Settle this member's balance before removing them."
            group.members.map { if (it.id == removeId) it.copy(active = false) else it }
        } else {
            if (name.trim().length !in 1..30 || group.members.any { it.name.equals(name.trim(), true) }) return "Enter a unique name of 1–30 characters."
            group.members + GroupMember(UUID.randomUUID().toString(), name.trim())
        }
        state.value = state.value.copy(groups = state.value.groups.map { if (it.id == groupId) it.copy(members = members) else it })
        return null
    }

    fun updateGroupMember(groupId: String, memberId: String, name: String, restore: Boolean): String? {
        val current = state.value
        val group = current.groups.find { it.id == groupId && it.joined } ?: return "Group not found."
        if (group.owner != "me") return "Only the owner can manage members."
        val member = group.members.find { it.id == memberId } ?: return "Member no longer available."
        if (memberId == "me") return "Use My profile to change your account name."
        if (!member.active && !restore) return "Restore this member before editing."
        val clean = name.trim()
        if (clean.length !in 1..30 || group.members.any { it.id != memberId && it.name.equals(clean, true) }) return "Enter a unique name of 1–30 characters."
        val updated = group.copy(members = group.members.map { if (it.id == memberId) it.copy(name = clean, active = it.active || restore) else it })
        state.value = current.copy(groups = current.groups.map { if (it.id == groupId) updated else it })
        return null
    }

    fun addGroupExpense(groupId: String, title: String, amount: String, payer: String, date: String,
                        category: String, participants: List<String>, mode: SplitMode, inputs: Map<String, String>,
                        requestId: String = UUID.randomUUID().toString()): String? {
        val current = state.value
        if (requestId.isBlank()) return "Reopen the expense form and try again."
        val group = current.groups.find { it.id == groupId && it.joined } ?: return "Join the group first."
        sharedExpenseErrors(title, amount, date, groupToday).first?.let { return it }
        val parsedDate = LocalDate.parse(date)
        if (participants.any { id -> group.members.none { it.id == id && it.active } }) return "Choose active group members."
        val allocation = runCatching { splitExpense(parseVnd(amount) ?: 0L, payer, participants, mode, inputs) }
        if (allocation.isFailure) return allocation.exceptionOrNull()?.message ?: "Check the split."
        val expense = GroupExpense(requestId, groupId, title.trim(), parseVnd(amount)!!, payer,
            allocation.getOrThrow(), parsedDate, category, mode,
            if (mode == SplitMode.Equal) emptyMap() else participants.associateWith { inputs[it].orEmpty().trim() })
        current.groupExpenses.find { it.id == requestId }?.let { previous ->
            return if (previous == expense) null else "This expense was already recorded. Reopen the form to add another."
        }
        state.value = current.copy(groupExpenses = current.groupExpenses + expense)
        return null
    }

    fun recordSettlement(groupId: String, from: String, to: String, amount: String, date: String,
                         requestId: String = UUID.randomUUID().toString()): String? {
        val current = state.value
        if (requestId.isBlank()) return "Reopen the settlement form and try again."
        current.settlements.find { it.id == requestId }?.let { previous ->
            return if (previous.groupId == groupId && previous.from == from && previous.to == to &&
                previous.amount == parseVnd(amount) && previous.date.toString() == date) null
            else "This payment was already recorded. Reopen the form to record another."
        }
        val group = current.groups.find { it.id == groupId && it.joined } ?: return "Group not found."
        val debt = simplifiedDebts(groupBalances(group, current.groupExpenses, current.settlements)).find { it.from == from && it.to == to }
            ?: return "This debt has already changed. Return to the debt summary."
        settlementValidation(debt, amount, date, groupToday)?.let { return it }
        val value = parseVnd(amount)!!
        val day = LocalDate.parse(date)
        state.value = current.copy(settlements = current.settlements + Settlement(requestId, groupId, from, to, value, day))
        return null
    }

    private fun applyGroupEdit(edit: (FinanceState) -> GroupRecordChange): String? {
        val change = edit(state.value)
        if (change.error == null) state.value = change.state
        return change.error
    }

    fun editGroupExpense(id: String, title: String, amount: String, payer: String, date: String,
        category: String, participants: List<String>, mode: SplitMode, inputs: Map<String, String>): String? =
        applyGroupEdit { GroupRecordEdits.expense(it, id, title, amount, payer, date, category, participants, mode, inputs, groupToday) }
    fun removeGroupExpense(id: String): String? = applyGroupEdit { GroupRecordEdits.removeExpense(it, id) }
    fun restoreGroupExpense(expense: GroupExpense): String? = applyGroupEdit { GroupRecordEdits.restoreExpense(it, expense) }
    fun editSettlement(id: String, amount: String, date: String): String? =
        applyGroupEdit { GroupRecordEdits.payment(it, id, amount, date, groupToday) }
    fun removeSettlement(id: String): String? = applyGroupEdit { GroupRecordEdits.removePayment(it, id) }
    fun restoreSettlement(payment: Settlement): String? = applyGroupEdit { GroupRecordEdits.restorePayment(it, payment) }

    fun saveBudget(id: String?, category: String, limit: String, cycle: BudgetCycle): String? {
        val value = parseVnd(limit) ?: return "Enter a whole VND limit."
        if (value <= 0) return "Budget limit must be positive."
        if (state.value.categories.none { it.name == category && it.type == EntryType.EXPENSE }) return "Choose an expense category."
        if (state.value.budgets.any { it.id != id && it.category == category && it.cycle == cycle }) return "This category already has a budget for this cycle."
        if (id != null && state.value.budgets.none { it.id == id }) return "Budget not found."
        val budget = Budget(id ?: UUID.randomUUID().toString(), category, value, cycle)
        state.value = state.value.copy(budgets = if (id == null) state.value.budgets + budget else state.value.budgets.map { if (it.id == id) budget else it })
        return null
    }

    fun saveGoal(id: String?, title: String, target: String, saved: String, deadline: String): String? {
        if (title.trim().length !in 1..40) return "Enter a goal title of 1–40 characters."
        val total = parseVnd(target) ?: return "Enter a whole VND target."
        val progress = parseVnd(saved) ?: return "Enter a whole VND recorded amount."
        if (total <= 0 || progress > total) return "Target must be positive; recorded savings cannot exceed it."
        val day = runCatching { LocalDate.parse(deadline) }.getOrNull() ?: return "Enter a valid deadline."
        if (day < DemoFixtures.today) return "Choose today or a later deadline."
        if (id != null && state.value.goals.none { it.id == id }) return "Goal not found."
        val goal = SavingsGoal(id ?: UUID.randomUUID().toString(), title.trim(), total, progress, day)
        state.value = state.value.copy(goals = if (id == null) state.value.goals + goal else state.value.goals.map { if (it.id == id) goal else it })
        return null
    }
}
