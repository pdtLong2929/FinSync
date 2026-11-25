package com.finsync.bank

import android.content.Context
import android.util.AtomicFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.util.concurrent.ConcurrentHashMap

interface BankStore {
    suspend fun load(): BankData
    suspend fun edit(change: (BankData) -> BankData): BankData
}
class FileBankStore(context: Context, workspace: String) : BankStore {
    private val directory = File(context.noBackupFilesDir, "bank-notifications")
    private val file = AtomicFile(File(directory, "${digest(workspace)}.json"))
    private val slot = slots.getOrPut(file.baseFile.absolutePath) { Slot() }
    val updates = slot.data.asStateFlow()
    override suspend fun load() = withContext(Dispatchers.IO) { slot.mutex.withLock { read().also { slot.data.value = it } } }
    private fun read(): BankData {
        val bytes = try { file.openRead().use { input ->
            val buffer = ByteArray(MAX_BYTES + 1); var count = 0
            while (count < buffer.size) { val n = input.read(buffer, count, buffer.size - count); if (n < 0) break; count += n }
            buffer.copyOf(count)
        } } catch (missing: FileNotFoundException) {
            if (file.baseFile.exists() || File(file.baseFile.path + ".bak").exists()) throw missing
            return BankData()
        }
        require(bytes.size <= MAX_BYTES)
        return BankJson.decode(String(bytes, Charsets.UTF_8))
    }
    override suspend fun edit(change: (BankData) -> BankData) = withContext(Dispatchers.IO) { slot.mutex.withLock {
        val next = change(read())
        val encoded = BankJson.encode(next)
        BankJson.decode(encoded) // Reject invalid snapshots before touching the previous file.
        val bytes = encoded.toByteArray(Charsets.UTF_8); require(bytes.size <= MAX_BYTES)
        check(directory.isDirectory || directory.mkdirs())
        val output = file.startWrite()
        try { output.write(bytes); file.finishWrite(output) } catch (failed: Throwable) { file.failWrite(output); throw failed }
        slot.data.value = next; next
    } }
    private class Slot { val mutex = Mutex(); val data = MutableStateFlow<BankData?>(null) }
    private companion object { const val MAX_BYTES = 10 * 1024 * 1024; val slots = ConcurrentHashMap<String, Slot>() }
}
