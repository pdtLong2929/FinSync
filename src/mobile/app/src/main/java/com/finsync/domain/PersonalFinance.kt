package com.finsync.domain

import java.time.LocalDate

enum class WalletType(val label: String, val symbol: String) {
    CASH("Cash", "◉"), BANK_ACCOUNT("Bank account", "▤"),
    CREDIT_CARD("Credit card", "▰"), E_WALLET("E-wallet", "◇")
}
enum class EntryType { INCOME, EXPENSE, OPENING, TRANSFER_IN, TRANSFER_OUT }
val EntryType.isTransaction get() = this == EntryType.INCOME || this == EntryType.EXPENSE
data class Wallet(val id: String, val name: String, val type: WalletType, val archived: Boolean = false)
data class LedgerEntry(
    val id: String, val walletId: String, val type: EntryType, val amount: Long,
    val category: String, val date: LocalDate, val note: String = "", val transferId: String? = null, val categoryId: String? = null,
)

const val MAX_AMOUNT = 999_999_999_999L
fun parseVnd(input: String): Long? = input.trim().takeIf { it.matches(Regex("[0-9]{1,12}")) }
    ?.toLongOrNull()?.takeIf { it in 0..MAX_AMOUNT }

fun walletBalance(wallet: Wallet, entries: List<LedgerEntry>): Long = entries
    .filter { it.walletId == wallet.id }.sumOf {
        when (it.type) {
            EntryType.OPENING -> it.amount
            EntryType.INCOME -> if (wallet.type == WalletType.CREDIT_CARD) -it.amount else it.amount
            EntryType.EXPENSE -> if (wallet.type == WalletType.CREDIT_CARD) it.amount else -it.amount
            EntryType.TRANSFER_IN -> it.amount
            EntryType.TRANSFER_OUT -> -it.amount
        }
    }

fun personalNetWorth(wallets: List<Wallet>, entries: List<LedgerEntry>): Long = wallets.sumOf {
    walletBalance(it, entries) * if (it.type == WalletType.CREDIT_CARD) -1 else 1
}

fun walletValidation(name: String, amount: String, wallets: List<Wallet>, id: String?): String? = when {
    name.trim().length !in 1..40 -> "Enter a wallet name (1–40 characters)."
    wallets.any { it.id != id && it.name.equals(name.trim(), ignoreCase = true) } -> "A wallet already uses this name."
    id == null && parseVnd(amount) == null -> "Enter a whole VND amount from 0 to 999,999,999,999."
    else -> null
}

enum class CategoryIcon { Food, Shopping, Transport, Housing, Utilities, Entertainment, Health, Education, Travel, Salary, Gift, Other }
enum class CategoryColor { Lavender, Cyan, Rose }
data class Category(val name: String, val type: EntryType,
    val id: String = java.util.UUID.randomUUID().toString(),
    val icon: CategoryIcon = CategoryIcon.Other, val color: CategoryColor = CategoryColor.Lavender)
fun resolveCategory(categories: List<Category>, entry: LedgerEntry): Category? =
    entry.categoryId?.let { id -> categories.find { it.id == id } }
        ?: categories.find { it.name == entry.category && it.type == entry.type }

val defaultCategories = listOf(
    Category("Food", EntryType.EXPENSE, "builtin-food", CategoryIcon.Food, CategoryColor.Lavender), Category("Transport", EntryType.EXPENSE, "builtin-transport", CategoryIcon.Transport, CategoryColor.Cyan),
    Category("Housing", EntryType.EXPENSE, "builtin-housing", CategoryIcon.Housing, CategoryColor.Rose), Category("Utilities", EntryType.EXPENSE, "builtin-utilities", CategoryIcon.Utilities, CategoryColor.Lavender),
    Category("Education", EntryType.EXPENSE, "builtin-education", CategoryIcon.Education, CategoryColor.Cyan), Category("Healthcare", EntryType.EXPENSE, "builtin-healthcare", CategoryIcon.Health, CategoryColor.Rose),
    Category("Entertainment", EntryType.EXPENSE, "builtin-entertainment", CategoryIcon.Entertainment, CategoryColor.Lavender), Category("Shopping", EntryType.EXPENSE, "builtin-shopping", CategoryIcon.Shopping, CategoryColor.Cyan),
    Category("Salary", EntryType.INCOME, "builtin-salary", CategoryIcon.Salary, CategoryColor.Rose), Category("Gift", EntryType.INCOME, "builtin-gift", CategoryIcon.Gift, CategoryColor.Lavender), Category("Other income", EntryType.INCOME, "builtin-other-income", CategoryIcon.Other, CategoryColor.Cyan),
)

data class TransactionFieldErrors(
    val amount: String? = null, val wallet: String? = null, val category: String? = null,
    val date: String? = null, val note: String? = null,
) {
    val first get() = amount ?: wallet ?: category ?: date ?: note
    // Field order is shared with the form's error navigation.
    val firstFieldIndex get() = listOf(amount, wallet, category, date, note).indexOfFirst { it != null }
}

fun transactionFieldErrors(amount: String, wallet: Wallet?, category: String, type: EntryType,
                           date: String, today: LocalDate, categories: List<Category>, note: String = ""): TransactionFieldErrors {
    val parsedAmount = parseVnd(amount)
    val parsedDate = runCatching { LocalDate.parse(date) }.getOrNull()
    return TransactionFieldErrors(
        amount = if (parsedAmount == null || parsedAmount == 0L) "Enter a positive whole VND amount (up to 999,999,999,999)." else null,
        wallet = if (wallet == null || wallet.archived) "Select an active personal wallet." else null,
        category = if (categories.none { it.name == category && it.type == type }) "Choose a category for this transaction type." else null,
        date = when {
            parsedDate == null -> "Enter a valid date in YYYY-MM-DD format."
            parsedDate.isAfter(today) -> "The transaction date cannot be in the future."
            else -> null
        },
        note = if (note.length > 300) "Keep notes within 300 characters." else null,
    )
}

fun transactionValidation(amount: String, wallet: Wallet?, category: String, type: EntryType,
                          date: String, today: LocalDate, categories: List<Category>): String? =
    transactionFieldErrors(amount, wallet, category, type, date, today, categories).first
