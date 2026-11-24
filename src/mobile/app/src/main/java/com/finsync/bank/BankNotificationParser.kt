package com.finsync.bank

import com.finsync.domain.MAX_AMOUNT
import java.text.Normalizer
import java.util.Locale

/** Conservative VND candidate extraction. Never returns/stores OTPs, account numbers or raw text. */
object BankNotificationParser {
    private fun normalized(text: String) = Normalizer.normalize(text, Normalizer.Form.NFD)
        .replace(Regex("\\p{M}"), "").replace('đ', 'd').replace('Đ', 'D').lowercase(Locale.ROOT)
    private val otp = Regex("\\b(otp|verification|xac thuc|ma bao mat|one.time.password|security code)\\b")
    private val balance = Regex("\\b(so du|sd kha dung|sd tk|available balance|balance)\\b|\\bsd\\s*:")
    private val money = Regex("([+−-]?)\\s*([0-9]+(?:[.,][0-9]+)*)\\s*(vnd|vndong|vietnam dong|dong|đ)(?![a-z])", RegexOption.IGNORE_CASE)
    private val event = Regex("(giao dich|\\bgd\\b|bien dong|chuyen|thanh toan|ghi no|ghi co|debit|credit|received|spent|paid|nhan|tru tien|cong tien)")
    private val outgoing = Regex("(chuyen tien|thanh toan|ghi no|debit|spent|paid|tru tien)")
    private val incoming = Regex("(nhan tien|ghi co|credit|received|cong tien)")
    private val reference = Regex("(?:ma gd|ma giao dich|transaction id|ref(?:erence)?|\\bft)\\s*[:#-]?\\s*([a-z0-9]{6,40})", RegexOption.IGNORE_CASE)
    fun wholeVnd(input: String): Long? {
        val raw = input.trim()
        if (raw.matches(Regex("[0-9]+"))) return raw.toLongOrNull()?.takeIf { it in 1..MAX_AMOUNT }
        val integer = when {
            raw.matches(Regex("[0-9]{1,3}(\\.[0-9]{3})+")) -> raw.replace(".", "")
            raw.matches(Regex("[0-9]{1,3}(,[0-9]{3})+")) -> raw.replace(",", "")
            raw.matches(Regex("[0-9]{1,3}(,[0-9]{3})+\\.00")) -> raw.substringBeforeLast('.').replace(",", "")
            raw.matches(Regex("[0-9]{1,3}(\\.[0-9]{3})+,00")) -> raw.substringBeforeLast(',').replace(".", "")
            else -> return null
        }
        return integer.toLongOrNull()?.takeIf { it in 1..MAX_AMOUNT }
    }
    fun parse(source: String, key: String, postedAt: Long, eventTime: Long, text: String): BankCandidate? {
        if (text.length > 8000) return null
        val plain = normalized(text)
        if (otp.containsMatchIn(plain)) return null
        val transactionText = balance.find(plain)?.let { plain.take(it.range.first) } ?: plain
        // Normalize currency đ only after diacritics: Vietnamese notifications commonly use VND/dong.
        val currencyText = transactionText.replace(Regex("(?<=\\d)\\s*d\\b"), " vnd")
        val amounts = money.findAll(currencyText).toList()
        if (amounts.isEmpty()) return null
        if (!event.containsMatchIn(transactionText) && !(Regex("\\b(tai khoan|tk)\\b").containsMatchIn(transactionText) && amounts.any { it.groupValues[1].isNotBlank() })) return null
        val match = amounts.singleOrNull()
        val amount = match?.groupValues?.get(2)?.let(::wholeVnd)
        val signs = amounts.map { it.groupValues[1] }.filter { it.isNotBlank() }.toSet()
        val out = outgoing.containsMatchIn(transactionText)
        val inc = incoming.containsMatchIn(transactionText)
        val direction = when {
            signs == setOf("-") || signs == setOf("−") -> BankDirection.Outgoing
            signs == setOf("+") -> BankDirection.Incoming
            signs.isNotEmpty() -> BankDirection.Unknown
            out && !inc -> BankDirection.Outgoing
            inc && !out -> BankDirection.Incoming
            else -> BankDirection.Unknown
        }
        val ref = reference.find(transactionText)?.groupValues?.get(1)
        val fingerprint = if (ref != null) "$source|ref|$ref" else "$source|$key|$eventTime"
        return BankCandidate(digest(fingerprint), source, postedAt, amount, direction)
    }
}
