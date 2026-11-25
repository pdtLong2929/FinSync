package com.finsync.social

import java.util.Locale
import java.util.UUID

enum class ConnectionStatus { Draft, Pending, Accepted, Declined, Cancelled, Expired }
data class Person(val id: String, val name: String, val email: String)
data class Friendship(val id: String, val person: Person, val incoming: Boolean, val status: ConnectionStatus)
data class GroupInvitation(val id: String, val groupId: String, val groupName: String,
    val email: String, val incoming: Boolean, val status: ConnectionStatus)
data class JoinRequest(val id: String, val code: String, val status: ConnectionStatus)
data class GroupPreview(val groupId: String, val name: String, val ownerName: String, val memberCount: Int, val code: String)
data class PeopleData(val friends: List<Friendship> = emptyList(), val invitations: List<GroupInvitation> = emptyList(),
    val joins: List<JoinRequest> = emptyList())

sealed interface PeopleCommand {
    data class AddFriend(val email: String) : PeopleCommand
    data class ReplyFriend(val id: String, val accept: Boolean) : PeopleCommand
    data class RemoveFriend(val id: String) : PeopleCommand
    data class Invite(val groupId: String, val groupName: String, val email: String) : PeopleCommand
    data class CancelInvite(val id: String) : PeopleCommand
    data class ReplyInvite(val id: String, val accept: Boolean) : PeopleCommand
    data class Join(val code: String) : PeopleCommand
    data class CancelJoin(val id: String) : PeopleCommand
}

internal fun cleanEmail(input: String) = input.trim().lowercase(Locale.ROOT)
internal fun requireEmail(input: String, ownEmail: String): String {
    val email = cleanEmail(input)
    require(email.length <= 254 && email.matches(Regex("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))) { "Enter a valid email address." }
    require(email != cleanEmail(ownEmail)) { "Use another person's email address." }
    return email
}
fun normalizeInviteCode(input: String): String {
    val code = input.trim().uppercase(Locale.ROOT)
    require(code.matches(Regex("[A-Z0-9]{6,12}"))) { "Enter a code of 6–12 letters or numbers." }
    return code
}

/** Local drafts never claim delivery, accepted friendship or group membership. */
internal fun reducePeople(data: PeopleData, command: PeopleCommand, ownEmail: String): PeopleData {
    fun id() = UUID.randomUUID().toString()
    val active = setOf(ConnectionStatus.Draft, ConnectionStatus.Pending, ConnectionStatus.Accepted)
    return when (command) {
        is PeopleCommand.AddFriend -> {
            val email = requireEmail(command.email, ownEmail)
            require(data.friends.none { cleanEmail(it.person.email) == email && it.status in active }) { "A friend or request already uses this email." }
            data.copy(friends = data.friends + Friendship(id(), Person("", "", email), false, ConnectionStatus.Draft))
        }
        is PeopleCommand.RemoveFriend -> {
            val item = data.friends.singleOrNull { it.id == command.id } ?: error("Request no longer available.")
            require(item.status == ConnectionStatus.Draft && !item.incoming) { "This action needs the friends service." }
            data.copy(friends = data.friends.filterNot { it.id == command.id })
        }
        is PeopleCommand.Invite -> {
            require(command.groupId.isNotBlank() && command.groupName.isNotBlank()) { "Open a group first." }
            val email = requireEmail(command.email, ownEmail)
            require(data.invitations.none { it.groupId == command.groupId && cleanEmail(it.email) == email && it.status in active }) { "An invitation already exists for this person." }
            data.copy(invitations = data.invitations + GroupInvitation(id(), command.groupId, command.groupName, email, false, ConnectionStatus.Draft))
        }
        is PeopleCommand.CancelInvite -> {
            val item = data.invitations.singleOrNull { it.id == command.id } ?: error("Invitation no longer available.")
            require(item.status == ConnectionStatus.Draft && !item.incoming) { "This action needs group sync." }
            data.copy(invitations = data.invitations.filterNot { it.id == command.id })
        }
        is PeopleCommand.Join -> {
            val code = normalizeInviteCode(command.code)
            require(data.joins.none { it.code == code && it.status in active }) { "This code is already saved." }
            data.copy(joins = data.joins + JoinRequest(id(), code, ConnectionStatus.Draft))
        }
        is PeopleCommand.CancelJoin -> {
            val item = data.joins.singleOrNull { it.id == command.id } ?: error("Saved code no longer available.")
            require(item.status == ConnectionStatus.Draft) { "This action needs group sync." }
            data.copy(joins = data.joins.filterNot { it.id == command.id })
        }
        is PeopleCommand.ReplyFriend, is PeopleCommand.ReplyInvite -> error("Connect the friends and groups service to respond to invitations.")
    }
}
