package com.finsync.social

import android.content.Context
import android.util.AtomicFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.security.MessageDigest

/** Future service adapter owns server authority, delivery, identities and idempotency. */
interface PeopleRepository {
    val connected: Boolean
    suspend fun load(): PeopleData
    suspend fun execute(command: PeopleCommand): PeopleData
    suspend fun resolveCode(code: String): GroupPreview = throw IllegalArgumentException("Group code lookup isn't connected yet.")
}

class DevicePeopleRepository(context: Context, workspace: String, private val ownEmail: String) : PeopleRepository {
    override val connected = false
    private val directory = File(context.noBackupFilesDir, "people-drafts")
    private val key = MessageDigest.getInstance("SHA-256").digest(workspace.toByteArray()).joinToString("") { "%02x".format(it) }
    private val file = AtomicFile(File(directory, "$key.json"))
    override suspend fun load(): PeopleData = withContext(Dispatchers.IO) {
        val bytes = try { file.openRead().use { input ->
            val buffer = ByteArray(MAX_BYTES + 1)
            var count = 0
            while (count < buffer.size) {
                val read = input.read(buffer, count, buffer.size - count)
                if (read < 0) break
                count += read
            }
            buffer.copyOf(count)
        } }
        catch (missing: FileNotFoundException) {
            if (file.baseFile.exists() || File(file.baseFile.path + ".bak").exists()) throw missing
            return@withContext PeopleData()
        }
        require(bytes.size <= MAX_BYTES) { "Saved requests are too large." }
        PeopleJson.decode(String(bytes, Charsets.UTF_8))
    }
    override suspend fun execute(command: PeopleCommand): PeopleData = withContext(Dispatchers.IO) {
        val next = reducePeople(load(), command, ownEmail)
        val bytes = PeopleJson.encode(next).toByteArray(Charsets.UTF_8)
        require(bytes.size <= MAX_BYTES) { "Too many saved requests. Remove unused drafts first." }
        check(directory.isDirectory || directory.mkdirs())
        val output = file.startWrite()
        try { output.write(bytes); file.finishWrite(output) }
        catch (failed: Throwable) { file.failWrite(output); throw failed }
        next
    }
    private companion object { const val MAX_BYTES = 1024 * 1024 }
}

internal object PeopleJson {
    fun encode(data: PeopleData): String = JSONObject().put("version", 1)
        .put("friends", JSONArray().also { list -> data.friends.forEach { f -> list.put(JSONObject()
            .put("id", f.id).put("personId", f.person.id).put("name", f.person.name).put("email", f.person.email)
            .put("incoming", f.incoming).put("status", f.status.name)) } })
        .put("invitations", JSONArray().also { list -> data.invitations.forEach { i -> list.put(JSONObject()
            .put("id", i.id).put("groupId", i.groupId).put("groupName", i.groupName).put("email", i.email)
            .put("incoming", i.incoming).put("status", i.status.name)) } })
        .put("joins", JSONArray().also { list -> data.joins.forEach { j -> list.put(JSONObject()
            .put("id", j.id).put("code", j.code).put("status", j.status.name)) } }).toString()
    fun decode(text: String): PeopleData {
        val root = JSONObject(text)
        require(root.getInt("version") == 1)
        fun <T> read(key: String, decode: (JSONObject) -> T): List<T> = root.getJSONArray(key).let { array ->
            require(array.length() <= 2000)
            (0 until array.length()).map { decode(array.getJSONObject(it)) }
        }
        val result = PeopleData(
            read("friends") { Friendship(it.getString("id"), Person(it.getString("personId"), it.getString("name"), it.getString("email")), it.getBoolean("incoming"), ConnectionStatus.valueOf(it.getString("status"))) },
            read("invitations") { GroupInvitation(it.getString("id"), it.getString("groupId"), it.getString("groupName"), it.getString("email"), it.getBoolean("incoming"), ConnectionStatus.valueOf(it.getString("status"))) },
            read("joins") { JoinRequest(it.getString("id"), normalizeInviteCode(it.getString("code")), ConnectionStatus.valueOf(it.getString("status"))) })
        require(result.friends.map { it.id }.distinct().size == result.friends.size)
        require(result.invitations.map { it.id }.distinct().size == result.invitations.size)
        require(result.joins.map { it.id }.distinct().size == result.joins.size)
        return result
    }
}
