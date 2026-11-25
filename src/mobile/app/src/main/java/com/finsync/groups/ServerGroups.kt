package com.finsync.groups

import androidx.compose.runtime.compositionLocalOf
import com.finsync.auth.*
import com.finsync.data.GroupLedger
import com.finsync.domain.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal
import java.time.LocalDate

internal val LocalServerGroups = compositionLocalOf<ServerGroups?> { null }
internal data class ServerGroupSnapshot(
    val ledger: GroupLedger = GroupLedger(),
    val callers: Map<String, String> = emptyMap(),
    val balances: Map<String, Map<String, Long>> = emptyMap(),
    val debts: Map<String, List<Debt>> = emptyMap(),
    val categories: Map<String, String> = emptyMap(),
    val loading: Boolean = true, val busy: Boolean = false, val error: String? = null,
)
internal fun JSONArray.objects() = (0 until length()).map { getJSONObject(it) }
// Reading a server record must not apply the smaller local form-entry limit.
internal fun JSONObject.vnd(key: String): Long = BigDecimal(getString(key)).longValueExact()

/** Server-authoritative groups. Never imports or overwrites device-only ledgers. */
internal class ServerGroups(private val auth: HttpAuthRepository) {
    private val mutable = MutableStateFlow(ServerGroupSnapshot())
    val state = mutable.asStateFlow()
    private val lock = Mutex()
    private var uncertainNonIdempotentWrite = false
    private suspend fun call(method: String, path: String, body: JSONObject? = null): JSONObject {
        val reply = auth.groupRequest(method, path, body)
        if (reply.status in 200..299) return reply.body
        if (reply.status == 401) throw AccountApiException(AuthFailure.SessionExpired)
        val message = when (reply.status) {
            403 -> "You don't have permission to change this group."
            404 -> "This group or record is no longer available. Refresh groups."
            409 -> "The group changed or this request was already used. Refresh and review it."
            400, 422 -> "Check the amount, date, participants and outstanding balance."
            429 -> "Too many requests. Wait a moment and try again."
            else -> "The group service is unavailable. Try again."
        }
        throw IllegalStateException(message)
    }
    private fun failure(error: Exception) = when (error) {
        is AccountApiException -> when (error.reason) {
            AuthFailure.SessionExpired -> "Your session expired. Sign in again."
            AuthFailure.Offline -> "Can't reach the server. Check your connection and retry."
            else -> "The server couldn't complete the request. Refresh before retrying."
        }
        is IllegalStateException -> error.message ?: "Refresh groups and try again."
        else -> "Couldn't read the server data. Refresh groups and try again."
    }
    suspend fun refresh() = lock.withLock {
        mutable.value = mutable.value.copy(loading = true, error = null)
        try { load(); uncertainNonIdempotentWrite = false }
        catch (cancelled: CancellationException) { throw cancelled }
        catch (e: Exception) { mutable.value = mutable.value.copy(loading = false, error = failure(e)) }
    }
    private suspend fun load() {
        val owner = auth.sessions.get()?.familyId ?: throw AccountApiException(AuthFailure.SessionExpired)
        val summaries = call("GET", "groups").getJSONArray("items").objects()
        val categories = call("GET", "categories?type=EXPENSE").getJSONArray("items").objects()
            .associate { it.getString("name") to it.getString("id") }
        val groups = mutableListOf<ExpenseGroup>(); val expenses = mutableListOf<GroupExpense>()
        val payments = mutableListOf<Settlement>(); val callers = mutableMapOf<String, String>()
        val balances = mutableMapOf<String, Map<String, Long>>(); val debts = mutableMapOf<String, List<Debt>>()
        for (summary in summaries) {
            require(summary.getString("currencyCode") == "VND") { "Only VND groups are supported in this app." }
            val id = summary.getString("id"); val caller = summary.getString("callerMemberId"); callers[id] = caller
            fun alias(member: String) = if (member == caller) "me" else member
            val detail = call("GET", "groups/$id")
            val members = detail.getJSONArray("members").objects()
            groups += ExpenseGroup(id, detail.getString("name"), "OTHER", detail.optString("description").takeUnless { it == "null" }.orEmpty(),
                members.map { GroupMember(alias(it.getString("id")), it.getString("displayName"), it.getString("status") == "ACTIVE") },
                owner = alias(members.first { it.getString("role") == "OWNER" && it.getString("status") == "ACTIVE" }.getString("id")), invite = "")
            var page = 0
            do {
                val rows = call("GET", "groups/$id/expenses?page=$page&size=50")
                for (row in rows.getJSONArray("content").objects()) {
                    val splits = row.getJSONArray("splits").objects()
                    val mode = SplitMode.entries.first { it.name.equals(row.getString("splitMode"), true) }
                    expenses += GroupExpense(row.getString("id"), id, row.getString("title"), row.vnd("amount"),
                        if (row.isNull("paidByMemberId")) "group-fund" else alias(row.getString("paidByMemberId")),
                        splits.associate { alias(it.getString("memberId")) to it.vnd("amount") },
                        LocalDate.parse(row.getString("expenseDate")), row.getString("categoryName"), mode,
                        if (mode == SplitMode.Equal) emptyMap() else splits.associate { alias(it.getString("memberId")) to it.getString("inputValue") })
                }
                page++
                require(page <= 1000) { "Too many expense pages." }
            } while (page < rows.getInt("totalPages"))
            payments += call("GET", "groups/$id/settlements").getJSONArray("items").objects().map {
                require(it.getString("status") == "CONFIRMED")
                Settlement(it.getString("id"), id, alias(it.getString("payerMemberId")), alias(it.getString("receiverMemberId")),
                    it.vnd("amount"), LocalDate.parse(it.getString("settlementDate")))
            }
            val debt = call("GET", "groups/$id/debts")
            balances[id] = debt.getJSONArray("memberBalances").objects().associate { alias(it.getString("memberId")) to it.vnd("netBalance") }
            debts[id] = debt.getJSONArray("simplifiedTransfers").objects().map {
                Debt(alias(it.getString("fromMemberId")), alias(it.getString("toMemberId")), it.vnd("amount"))
            }
        }
        if (auth.sessions.get()?.familyId != owner) throw CancellationException("Account changed")
        mutable.value = ServerGroupSnapshot(GroupLedger(groups, expenses, payments), callers, balances, debts, categories, loading = false)
    }
    private suspend fun mutate(method: String, path: String, body: JSONObject? = null): String? = lock.withLock {
        val nonIdempotent = method == "POST" && body?.has("requestId") != true
        if (nonIdempotent && uncertainNonIdempotentWrite) return@withLock "Refresh server groups before retrying. The previous request may already be saved."
        mutable.value = mutable.value.copy(busy = true, error = null)
        try {
            call(method, path, body)
            try { load() } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { mutable.value = mutable.value.copy(error = "Saved on server. Refresh to see the latest balances.") }
            null
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (e: Exception) {
            if (nonIdempotent && e is AccountApiException && e.reason in listOf(AuthFailure.Offline, AuthFailure.Unavailable)) {
                uncertainNonIdempotentWrite = true
                "Couldn't confirm the result. Return to Server groups and refresh before retrying."
            } else failure(e)
        }
        finally { mutable.value = mutable.value.copy(busy = false) }
    }
    private fun member(group: String, id: String) = if (id == "me") state.value.callers.getValue(group) else id
    suspend fun create(name: String, description: String) = if (name.trim().length !in 1..40 || description.length > 300) "Use a name of 1–40 and description of up to 300 characters." else
        mutate("POST", "groups", JSONObject().put("name", name.trim()).put("description", description.trim()).put("currencyCode", "VND"))
    suspend fun edit(group: String, name: String, description: String) = if (name.trim().length !in 1..40 || description.length > 300)
        "Use a name of 1–40 and description of up to 300 characters." else mutate("PATCH", "groups/$group",
        JSONObject().put("name", name.trim()).put("description", description.trim()))
    suspend fun addMember(group: String, name: String, remove: String?) = if (remove == null && (name.trim().length !in 1..30 ||
        state.value.ledger.groups.find { it.id == group }?.members?.any { it.name.equals(name.trim(), true) } == true))
        "Enter a unique member name of 1–30 characters." else if (remove != null)
        mutate("DELETE", "groups/$group/members/${member(group, remove)}") else
        mutate("POST", "groups/$group/members", JSONObject().put("displayName", name.trim()))
    suspend fun expense(group: String, request: String, title: String, amount: String, payer: String, date: String,
        category: String, people: List<String>, mode: SplitMode, inputs: Map<String, String>): String? {
        val categoryId = state.value.categories[category] ?: return "Choose a current server category."
        return mutate("POST", "groups/$group/expenses", JSONObject().put("requestId", request).put("categoryId", categoryId)
            .put("paidByMemberId", member(group, payer)).put("title", title.trim()).put("amount", amount)
            .put("expenseDate", date).put("fundingSource", "MEMBERS").put("splitMode", mode.name.uppercase())
            .put("splits", JSONArray(people.map { JSONObject().put("memberId", member(group, it))
                .put("inputValue", if (mode == SplitMode.Equal) JSONObject.NULL else inputs[it]) })))
    }
    suspend fun settle(group: String, request: String, from: String, to: String, amount: String, date: String) =
        mutate("POST", "groups/$group/settlements", JSONObject().put("requestId", request).put("payerMemberId", member(group, from))
            .put("receiverMemberId", member(group, to)).put("amount", amount).put("settlementDate", date))
    suspend fun accept(token: String) = mutate("POST", "groups/invitations/accept", JSONObject().put("token", token.trim()))
    suspend fun invitation(group: String): String = lock.withLock {
        call("POST", "groups/$group/invitations", JSONObject().put("maxUses", 1).put("expiresInHours", 24)).getString("token")
    }
}
