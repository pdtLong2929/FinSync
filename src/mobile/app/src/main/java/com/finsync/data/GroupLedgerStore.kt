package com.finsync.data

import android.content.Context
import android.util.AtomicFile
import com.finsync.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.security.MessageDigest
import java.time.LocalDate

data class GroupLedger(
    val groups: List<ExpenseGroup> = emptyList(),
    val expenses: List<GroupExpense> = emptyList(),
    val settlements: List<Settlement> = emptyList(),
)

interface GroupLedgerStore {
    suspend fun load(): GroupLedger
    suspend fun save(ledger: GroupLedger)
}

/** Account-scoped, device-only storage. AtomicFile retains the last complete write. */
class FileGroupLedgerStore(context: Context, workspace: String) : GroupLedgerStore {
    private val directory = File(context.noBackupFilesDir, "group-ledgers")
    private val name = MessageDigest.getInstance("SHA-256").digest(workspace.toByteArray())
        .joinToString("") { "%02x".format(it) }
    private val file = AtomicFile(File(directory, "$name.json"))

    override suspend fun load(): GroupLedger = withContext(Dispatchers.IO) {
        val bytes = try {
            file.openRead().use { input ->
                val buffer = ByteArray(MAX_BYTES + 1)
                var count = 0
                while (count < buffer.size) {
                    val read = input.read(buffer, count, buffer.size - count)
                    if (read < 0) break
                    count += read
                }
                require(count <= MAX_BYTES) { "Group ledger is too large." }
                buffer.copyOf(count)
            }
        } catch (missing: FileNotFoundException) {
            // A first launch is empty. A damaged/unreadable existing file is an error.
            if (file.baseFile.exists() || File(file.baseFile.path + ".bak").exists()) throw missing
            return@withContext GroupLedger()
        }
        GroupLedgerJson.decode(String(bytes, Charsets.UTF_8))
    }

    override suspend fun save(ledger: GroupLedger) = withContext(Dispatchers.IO) {
        val bytes = GroupLedgerJson.encode(ledger).toByteArray(Charsets.UTF_8)
        require(bytes.size <= MAX_BYTES) { "Group ledger is too large." }
        check(directory.isDirectory || directory.mkdirs()) { "Cannot open group storage." }
        val output = file.startWrite()
        try {
            output.write(bytes)
            file.finishWrite(output)
        } catch (error: Throwable) {
            file.failWrite(output)
            throw error
        }
    }

    private companion object { const val MAX_BYTES = 2 * 1024 * 1024 }
}

internal object GroupLedgerJson {
    fun encode(ledger: GroupLedger): String {
        validate(ledger)
        return JSONObject().put("version", 1)
            .put("groups", JSONArray(ledger.groups.map { group ->
                JSONObject().put("id", group.id).put("name", group.name).put("type", group.type)
                    .put("description", group.description).put("owner", group.owner)
                    .put("invite", group.invite).put("joined", group.joined)
                    .put("members", JSONArray(group.members.map { member ->
                        JSONObject().put("id", member.id).put("name", member.name).put("active", member.active)
                    }))
            })).put("expenses", JSONArray(ledger.expenses.map { expense ->
                JSONObject().put("id", expense.id).put("groupId", expense.groupId).put("title", expense.title)
                    .put("amount", expense.amount).put("payer", expense.payer).put("shares", JSONObject(expense.shares))
                    .put("date", expense.date.toString()).put("category", expense.category)
                    .also { if (expense.splitMode != null) it.put("splitMode", expense.splitMode.name).put("splitInputs", JSONObject(expense.splitInputs)) }
            })).put("settlements", JSONArray(ledger.settlements.map { settlement ->
                JSONObject().put("id", settlement.id).put("groupId", settlement.groupId)
                    .put("from", settlement.from).put("to", settlement.to).put("amount", settlement.amount)
                    .put("date", settlement.date.toString())
            })).toString()
    }

    fun decode(text: String): GroupLedger {
        val root = JSONObject(text)
        require(root.getInt("version") == 1) { "Unsupported group ledger version." }
        val groups = objects(root.getJSONArray("groups")).map { value ->
            ExpenseGroup(value.getString("id"), value.getString("name"), value.getString("type"),
                value.getString("description"), objects(value.getJSONArray("members")).map { member ->
                    GroupMember(member.getString("id"), member.getString("name"), member.getBoolean("active"))
                }, value.getString("owner"), value.getString("invite"), value.getBoolean("joined"))
        }
        val expenses = objects(root.getJSONArray("expenses")).map { value ->
            val shares = value.getJSONObject("shares")
            GroupExpense(value.getString("id"), value.getString("groupId"), value.getString("title"),
                whole(value, "amount"), value.getString("payer"), shares.keys().asSequence().associateWith { whole(shares, it) },
                LocalDate.parse(value.getString("date")), value.getString("category"),
                value.optString("splitMode", "").takeIf { it.isNotBlank() }?.let(SplitMode::valueOf),
                value.optJSONObject("splitInputs")?.let { input -> input.keys().asSequence().associateWith { input.getString(it) } }.orEmpty())
        }
        val settlements = objects(root.getJSONArray("settlements")).map { value ->
            Settlement(value.getString("id"), value.getString("groupId"), value.getString("from"),
                value.getString("to"), whole(value, "amount"), LocalDate.parse(value.getString("date")))
        }
        return GroupLedger(groups, expenses, settlements).also(::validate)
    }

    private fun whole(value: JSONObject, key: String): Long =
        value.get(key).toString().toLongOrNull() ?: error("Expected a whole VND amount.")

    private fun objects(array: JSONArray) = (0 until array.length()).map(array::getJSONObject)

    private fun validate(ledger: GroupLedger) {
        fun unique(ids: List<String>) = ids.all { it.matches(Regex("[A-Za-z0-9_-]{1,80}")) } && ids.distinct().size == ids.size
        require(unique(ledger.groups.map { it.id }) && unique(ledger.expenses.map { it.id }) && unique(ledger.settlements.map { it.id }))
        val groups = ledger.groups.associateBy { it.id }
        ledger.groups.forEach { group ->
            require(group.name.trim().length in 1..40 && group.description.length <= 300)
            require(unique(group.members.map { it.id }) && group.members.any { it.id == group.owner && it.active })
            require(group.members.all { it.name.trim().length in 1..30 })
        }
        ledger.expenses.forEach { expense ->
            val members = groups.getValue(expense.groupId).members.map { it.id }.toSet()
            require(expense.title.trim().length in 1..60 && expense.amount in 1..MAX_AMOUNT)
            require(expense.payer in expense.shares && members.containsAll(expense.shares.keys))
            require(expense.shares.values.all { it in 0..MAX_AMOUNT } && expense.shares.values.sum() == expense.amount)
            expense.splitMode?.let { mode ->
                require(splitExpense(expense.amount, expense.payer, expense.shares.keys.toList(), mode, expense.splitInputs) == expense.shares)
            } ?: require(expense.splitInputs.isEmpty())
        }
        ledger.settlements.forEach { settlement ->
            val members = groups.getValue(settlement.groupId).members.map { it.id }.toSet()
            require(settlement.from != settlement.to && settlement.from in members && settlement.to in members)
            require(settlement.amount in 1..MAX_AMOUNT)
        }
    }
}
