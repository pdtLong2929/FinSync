package com.finsync.social

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class PeopleUiState(val data: PeopleData = PeopleData(), val loading: Boolean = true,
    val busy: Boolean = false, val loadFailed: Boolean = false, val error: String? = null, val feedback: String? = null)
class PeopleController(val repository: PeopleRepository) {
    private val mutable = MutableStateFlow(PeopleUiState())
    val state = mutable.asStateFlow()
    private val mutex = Mutex()
    suspend fun previewCode(input: String): GroupPreview {
        val code = normalizeInviteCode(input)
        require(repository.connected) { "Group code lookup isn't connected yet." }
        return repository.resolveCode(code).also {
            require(it.code == code && it.groupId.isNotBlank() && it.name.isNotBlank() && it.ownerName.isNotBlank() && it.memberCount > 0) { "Couldn't verify this group. Ask the owner for a new code." }
        }
    }
    suspend fun reload() = mutex.withLock {
        mutable.value = mutable.value.copy(loading = true, error = null)
        try { mutable.value = PeopleUiState(data = repository.load(), loading = false) }
        catch (cancelled: CancellationException) { throw cancelled }
        catch (_: Exception) { mutable.value = mutable.value.copy(loading = false, loadFailed = true, error = "Couldn't read friends and invitations. Try again. Your saved drafts are protected.") }
    }
    suspend fun submit(command: PeopleCommand): String? {
        if (!mutex.tryLock()) return "Another request is being saved. Please wait."
        try {
            if (state.value.loading || state.value.loadFailed) return "Reload your saved requests first."
            mutable.value = state.value.copy(busy = true, error = null, feedback = null)
            return try {
                val next = repository.execute(command)
                mutable.value = state.value.copy(data = next, busy = false,
                    feedback = if (repository.connected) "Updated" else if (command is PeopleCommand.RemoveFriend || command is PeopleCommand.CancelInvite || command is PeopleCommand.CancelJoin) "Draft removed" else "Draft saved on this device · Not sent")
                null
            } catch (cancelled: CancellationException) { mutable.value = state.value.copy(busy = false); throw cancelled }
              catch (invalid: IllegalArgumentException) { invalid.message.orEmpty().ifBlank { "Check your details." }.also { mutable.value = state.value.copy(busy = false, error = it) } }
              catch (_: Exception) { "Couldn't save this request. Your previous data is unchanged. Try again.".also { mutable.value = state.value.copy(busy = false, error = it) } }
        } finally { mutex.unlock() }
    }
}
val LocalPeopleController = staticCompositionLocalOf<PeopleController?> { null }
