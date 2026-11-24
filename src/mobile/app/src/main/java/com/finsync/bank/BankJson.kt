package com.finsync.bank

import com.finsync.domain.*
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

internal object BankJson {
    private fun <T> array(values: List<T>, encode: (T) -> JSONObject) = JSONArray().also { a -> values.forEach { a.put(encode(it)) } }
    private fun <T> read(root: JSONObject, key: String, decode: (JSONObject) -> T): List<T> = root.getJSONArray(key).let { a ->
        require(a.length() <= 50_000); (0 until a.length()).map { decode(a.getJSONObject(it)) }
    }
    private fun JSONObject.nullable(key: String) = if (isNull(key)) null else getString(key)
    fun encode(data: BankData): String {
        val root = JSONObject().put("version", 1).put("candidates", array(data.candidates) { c ->
            JSONObject().put("id", c.id).put("source", c.sourcePackage).put("time", c.observedAt)
                .put("amount", c.amount ?: JSONObject.NULL).put("direction", c.direction.name).put("status", c.status.name)
                .put("duplicate", c.possibleDuplicate).put("entries", JSONArray(c.entryIds))
        })
        data.personal?.let { p -> root.put("personal", JSONObject()
            .put("wallets", array(p.wallets) { JSONObject().put("id", it.id).put("name", it.name).put("type", it.type.name).put("archived", it.archived) })
            .put("entries", array(p.entries) { JSONObject().put("id", it.id).put("wallet", it.walletId).put("type", it.type.name).put("amount", it.amount)
                .put("category", it.category).put("date", it.date.toString()).put("note", it.note).put("transfer", it.transferId ?: JSONObject.NULL).put("categoryId", it.categoryId ?: JSONObject.NULL) })
            .put("categories", array(p.categories) { JSONObject().put("id", it.id).put("name", it.name).put("type", it.type.name).put("icon", it.icon.name).put("color", it.color.name) })
            .put("transfers", array(p.transfers) { JSONObject().put("id", it.id).put("source", it.sourceId).put("destination", it.destinationId)
                .put("amount", it.amount).put("note", it.note).put("date", it.date.toString()) })
            .put("recentWallet", p.recentWalletId).put("recentCategory", p.recentExpenseCategory)) }
        return root.toString()
    }
    fun decode(text: String): BankData {
        val root = JSONObject(text); require(root.getInt("version") == 1)
        val candidates = read(root, "candidates") { c -> BankCandidate(c.getString("id"), c.getString("source"), c.getLong("time"),
            if (c.isNull("amount")) null else c.getLong("amount"), BankDirection.valueOf(c.getString("direction")),
            CaptureStatus.valueOf(c.getString("status")), c.getBoolean("duplicate"),
            c.getJSONArray("entries").let { a -> (0 until a.length()).map { a.getString(it) } }) }
        val p = root.optJSONObject("personal")?.let { obj -> PersonalLedger(
            read(obj, "wallets") { Wallet(it.getString("id"), it.getString("name"), WalletType.valueOf(it.getString("type")), it.getBoolean("archived")) },
            read(obj, "entries") { LedgerEntry(it.getString("id"), it.getString("wallet"), EntryType.valueOf(it.getString("type")), it.getLong("amount"),
                it.getString("category"), LocalDate.parse(it.getString("date")), it.getString("note"), it.nullable("transfer"), it.nullable("categoryId")) },
            read(obj, "categories") { Category(it.getString("name"), EntryType.valueOf(it.getString("type")), it.getString("id"), CategoryIcon.valueOf(it.getString("icon")), CategoryColor.valueOf(it.getString("color"))) },
            read(obj, "transfers") { WalletTransfer(it.getString("id"), it.getString("source"), it.getString("destination"), it.getLong("amount"), it.getString("note"), LocalDate.parse(it.getString("date"))) },
            obj.getString("recentWallet"), obj.getString("recentCategory")) }
        require(candidates.size <= 10_000 && candidates.map { it.id }.distinct().size == candidates.size)
        candidates.forEach { require(it.id.matches(Regex("[a-f0-9]{64}")) && it.sourcePackage.length in 1..200 && it.observedAt > 0 && (it.amount == null || it.amount in 1..MAX_AMOUNT)) }
        p?.let { ledger ->
            require(ledger.wallets.map { it.id }.distinct().size == ledger.wallets.size)
            require(ledger.entries.map { it.id }.distinct().size == ledger.entries.size)
            require(ledger.categories.map { it.id }.distinct().size == ledger.categories.size)
            ledger.entries.forEach { require(ledger.wallets.any { w -> w.id == it.walletId } && it.amount in 0..MAX_AMOUNT) }
        }
        return BankData(candidates, p)
    }
}
