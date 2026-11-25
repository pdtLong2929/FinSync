package com.finsync

import com.finsync.social.*
import com.finsync.ui.FinanceViewModel
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Test

class PeopleTest {
    private val own = "me@example.test"
    private class Memory : PeopleRepository {
        override val connected = false
        var data = PeopleData()
        var failLoad = false
        var failSave = false
        var entered: CompletableDeferred<Unit>? = null
        var release: CompletableDeferred<Unit>? = null
        override suspend fun load(): PeopleData { if (failLoad) error("disk"); return data }
        override suspend fun execute(command: PeopleCommand): PeopleData {
            entered?.complete(Unit); release?.await()
            if (failSave) error("disk")
            return reducePeople(data, command, "me@example.test").also { data = it }
        }
    }
    @Test fun requestsValidateIdentityNormalizeAndNeverBecomeRealFriends() {
        val data = reducePeople(PeopleData(), PeopleCommand.AddFriend("  LINH@example.test "), own)
        assertEquals("linh@example.test", data.friends.single().person.email)
        assertEquals(ConnectionStatus.Draft, data.friends.single().status)
        assertFalse(data.friends.single().incoming)
        for (email in listOf("bad", "ME@example.test", "Linh@example.test"))
            assertTrue(runCatching { reducePeople(data, PeopleCommand.AddFriend(email), own) }.isFailure)
        assertTrue(reducePeople(data, PeopleCommand.RemoveFriend(data.friends.single().id), own).friends.isEmpty())
    }
    @Test fun groupInvitesAndCodesDoNotChangeMembershipAndRejectDuplicates() {
        var data = reducePeople(PeopleData(), PeopleCommand.Invite("trip", "Trip", "linh@example.test"), own)
        assertEquals(ConnectionStatus.Draft, data.invitations.single().status)
        assertTrue(runCatching { reducePeople(data, PeopleCommand.Invite("trip", "Trip", "LINH@example.test"), own) }.isFailure)
        data = reducePeople(data, PeopleCommand.Join(" a1b2c3 "), own)
        assertEquals("A1B2C3", data.joins.single().code)
        assertTrue(runCatching { reducePeople(data, PeopleCommand.Join("A1B2C3"), own) }.isFailure)
        listOf("", "123", "ABC-123", "http://host/group", "A".repeat(13)).forEach { assertTrue(runCatching { normalizeInviteCode(it) }.isFailure) }
        assertTrue(runCatching { reducePeople(data, PeopleCommand.ReplyInvite(data.invitations.single().id, true), own) }.isFailure)
        data = reducePeople(data, PeopleCommand.CancelInvite(data.invitations.single().id), own)
        data = reducePeople(data, PeopleCommand.CancelJoin(data.joins.single().id), own)
        assertTrue(data.invitations.isEmpty() && data.joins.isEmpty())
    }
    @Test fun storageFailureKeepsStateAndRetryDoesNotCreateTwoRequests() = runBlocking {
        val repo = Memory(); val controller = PeopleController(repo); controller.reload()
        repo.failSave = true
        assertNotNull(controller.submit(PeopleCommand.AddFriend("linh@example.test")))
        assertTrue(controller.state.value.data.friends.isEmpty())
        repo.failSave = false
        assertNull(controller.submit(PeopleCommand.AddFriend("linh@example.test")))
        assertNotNull(controller.submit(PeopleCommand.AddFriend("linh@example.test")))
        assertEquals(1, controller.state.value.data.friends.size)
        repo.failLoad = true; controller.reload()
        assertTrue(controller.state.value.loadFailed)
        assertNotNull(controller.submit(PeopleCommand.Join("ABC123")))
        assertEquals(1, repo.data.friends.size)
    }
    @Test fun doubleTapIsRejectedWhileFirstRequestIsInFlight() = runBlocking {
        val repo = Memory(); val controller = PeopleController(repo); controller.reload()
        repo.entered = CompletableDeferred(); repo.release = CompletableDeferred()
        val first = async { controller.submit(PeopleCommand.Join("ABC123")) }
        repo.entered!!.await()
        assertNotNull(controller.submit(PeopleCommand.Join("ABC123")))
        repo.release!!.complete(Unit); assertNull(first.await())
        assertEquals(1, repo.data.joins.size)
    }
    @Test fun memberRenameAndRestoreKeepFinancialRecordsAndIds() {
        val vm = FinanceViewModel(); val before = vm.uiState.value
        val group = before.groups.first { it.owner == "me" && it.members.any { m -> m.id != "me" } }
        val member = group.members.first { it.id != "me" }
        assertNull(vm.updateGroupMember(group.id, member.id, "Linh Updated", false))
        assertEquals(member.id, vm.uiState.value.groups.first { it.id == group.id }.members.first { it.name == "Linh Updated" }.id)
        assertEquals(before.groupExpenses, vm.uiState.value.groupExpenses)
        assertEquals(before.settlements, vm.uiState.value.settlements)
        assertEquals(before.entries, vm.uiState.value.entries)
        assertNotNull(vm.updateGroupMember(group.id, member.id, "", false))
        assertNotNull(vm.updateGroupMember(group.id, "me", "Another name", false))
    }
    @Test fun codePreviewRejectsUnverifiedGroupMetadataAndDoesNotSubmit() = runBlocking {
        val local = PeopleController(Memory())
        assertTrue(runCatching { local.previewCode("ABC123") }.isFailure)
        var invalid = true
        var submitted = false
        val service = object : PeopleRepository {
            override val connected = true
            override suspend fun load() = PeopleData()
            override suspend fun execute(command: PeopleCommand): PeopleData { submitted = true; return PeopleData() }
            override suspend fun resolveCode(code: String) = GroupPreview("trip", "Trip", "Owner", 3, if (invalid) "OTHER1" else code)
        }
        val controller = PeopleController(service)
        assertTrue(runCatching { controller.previewCode("ABC123") }.isFailure)
        invalid = false
        assertEquals("ABC123", controller.previewCode("abc123").code)
        assertFalse(submitted)
    }
}
