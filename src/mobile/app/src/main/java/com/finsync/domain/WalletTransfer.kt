package com.finsync.domain

import java.time.LocalDate

data class WalletTransfer(val id: String, val sourceId: String, val destinationId: String,
                          val amount: Long, val note: String, val date: LocalDate)

fun transferValidation(source: Wallet?, destination: Wallet?, amount: String, note: String,
                       entries: List<LedgerEntry>): String? {
    if (source == null || destination == null) return "Select two wallets."
    if (source.id == destination.id) return "Choose different source and destination wallets."
    if (source.archived || destination.archived) return "Choose active wallets."
    if (source.type == WalletType.CREDIT_CARD || destination.type == WalletType.CREDIT_CARD)
        return "Credit cards are not supported for internal transfers."
    val value = parseVnd(amount)
    if (value == null || value == 0L) return "Enter a positive whole VND amount (up to 999,999,999,999)."
    if (value > walletBalance(source, entries)) return "Insufficient balance in the source wallet."
    if (note.length > 300) return "Keep notes within 300 characters."
    if (walletBalance(destination, entries) > Long.MAX_VALUE - value) return "The destination balance is too large."
    return null
}
