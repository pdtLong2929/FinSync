package com.finsync.bank

import com.finsync.domain.*
import com.finsync.ui.FinanceState
import java.security.MessageDigest
import java.time.LocalDate

enum class BankDirection { Incoming, Outgoing, Unknown }
enum class CaptureStatus { Pending, Recorded, Linked, Ignored }
data class BankCandidate(val id: String, val sourcePackage: String, val observedAt: Long,
    val amount: Long?, val direction: BankDirection, val status: CaptureStatus = CaptureStatus.Pending,
    val possibleDuplicate: Boolean = false, val entryIds: List<String> = emptyList())

/** An optional durable copy begins only after the user's first confirmed bank record. */
data class PersonalLedger(val wallets: List<Wallet>, val entries: List<LedgerEntry>, val categories: List<Category>,
    val transfers: List<WalletTransfer>, val recentWalletId: String, val recentExpenseCategory: String)
fun FinanceState.personalLedger() = PersonalLedger(wallets, entries, categories, transfers, recentWalletId, recentExpenseCategory)
fun FinanceState.withPersonal(ledger: PersonalLedger) = copy(wallets = ledger.wallets, entries = ledger.entries,
    categories = ledger.categories, transfers = ledger.transfers, recentWalletId = ledger.recentWalletId,
    recentExpenseCategory = ledger.recentExpenseCategory)
data class BankData(val candidates: List<BankCandidate> = emptyList(), val personal: PersonalLedger? = null)

enum class BankRecordKind { Expense, Income, Transfer, Link }
data class BankReview(val kind: BankRecordKind, val amount: String, val walletId: String, val destinationId: String,
    val category: String, val date: String, val note: String, val linkedId: String = "")
data class BankRecording(val personal: PersonalLedger, val entryIds: List<String>)
internal fun digest(input: String): String = MessageDigest.getInstance("SHA-256")
    .digest(input.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }

/** Native ledger semantics are reused; no bank balance or group settlement is inferred. */
fun recordCandidate(candidate: BankCandidate, ledger: PersonalLedger, review: BankReview, today: LocalDate): BankRecording {
    require(candidate.status == CaptureStatus.Pending) { "This alert has already been handled." }
    val amount = parseVnd(review.amount)
    require(amount != null && amount > 0) { "Enter a positive whole-VND amount." }
    val day = runCatching { LocalDate.parse(review.date) }.getOrNull()
    require(day != null && day <= today) { "Choose a valid date, no later than today." }
    require(review.note.length <= 300) { "Keep notes within 300 characters." }
    if (review.kind == BankRecordKind.Link) {
        val existing = ledger.entries.find { it.id == review.linkedId } ?: error("Choose an existing transaction.")
        require(existing.amount == amount && existing.walletId == review.walletId && existing.date == day) { "The amount, wallet and date must match the existing transaction." }
        require(existing.type != EntryType.OPENING) { "Opening balances cannot be linked." }
        require(candidate.direction != BankDirection.Outgoing || existing.type in listOf(EntryType.EXPENSE, EntryType.TRANSFER_OUT)) { "Choose an outgoing entry for this alert." }
        require(candidate.direction != BankDirection.Incoming || existing.type in listOf(EntryType.INCOME, EntryType.TRANSFER_IN)) { "Choose an incoming entry for this alert." }
        return BankRecording(ledger, listOf(existing.id))
    }
    val id = "bank-${candidate.id}"
    require(ledger.entries.none { it.id == id || it.transferId == id }) { "This alert is already recorded." }
    val wallet = ledger.wallets.find { it.id == review.walletId }
    if (review.kind == BankRecordKind.Transfer) {
        val destination = ledger.wallets.find { it.id == review.destinationId }
        transferValidation(wallet, destination, review.amount, review.note, ledger.entries)?.let { throw IllegalArgumentException(it) }
        val transfer = WalletTransfer(id, wallet!!.id, destination!!.id, amount, review.note.trim(), day)
        val pair = listOf(LedgerEntry("$id-out", wallet.id, EntryType.TRANSFER_OUT, amount, "To ${destination.name}", day, transfer.note, id),
            LedgerEntry("$id-in", destination.id, EntryType.TRANSFER_IN, amount, "From ${wallet.name}", day, transfer.note, id))
        return BankRecording(ledger.copy(entries = ledger.entries + pair, transfers = ledger.transfers + transfer), pair.map { it.id })
    }
    val type = if (review.kind == BankRecordKind.Expense) EntryType.EXPENSE else EntryType.INCOME
    transactionFieldErrors(review.amount, wallet, review.category, type, review.date, today, ledger.categories, review.note)
        .first?.let { throw IllegalArgumentException(it) }
    val category = ledger.categories.first { it.name == review.category && it.type == type }
    val entry = LedgerEntry(id, wallet!!.id, type, amount, category.name, day, review.note.trim(), categoryId = category.id)
    return BankRecording(ledger.copy(entries = ledger.entries + entry, recentWalletId = wallet.id,
        recentExpenseCategory = if (type == EntryType.EXPENSE) category.name else ledger.recentExpenseCategory), listOf(entry.id))
}
