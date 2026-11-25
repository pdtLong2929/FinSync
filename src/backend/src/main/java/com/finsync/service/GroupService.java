package com.finsync.service;

import com.finsync.dto.GroupDtos.*;
import com.finsync.security.SessionPrincipal;

import java.util.List;
import java.util.UUID;

public interface GroupService {
    List<GroupSummaryResponse> listGroups(SessionPrincipal principal, boolean includeArchived);
    GroupDetailResponse createGroup(SessionPrincipal principal, CreateGroupRequest request);
    GroupDetailResponse getGroup(SessionPrincipal principal, UUID groupId);
    GroupDetailResponse updateGroup(SessionPrincipal principal, UUID groupId, UpdateGroupRequest request);
    GroupDetailResponse archiveGroup(SessionPrincipal principal, UUID groupId);
    GroupDetailResponse unarchiveGroup(SessionPrincipal principal, UUID groupId);
    List<GroupMemberResponse> listMembers(SessionPrincipal principal, UUID groupId);
    GroupMemberResponse addGuestMember(SessionPrincipal principal, UUID groupId, AddGuestMemberRequest request);
    GroupMemberResponse removeMember(SessionPrincipal principal, UUID groupId, UUID memberId);
    GroupMemberResponse leaveGroup(SessionPrincipal principal, UUID groupId);
    GroupDetailResponse transferOwnership(SessionPrincipal principal, UUID groupId, TransferOwnershipRequest request);
    List<GroupInvitationResponse> listInvitations(SessionPrincipal principal, UUID groupId);
    CreateInvitationResponse createInvitation(SessionPrincipal principal, UUID groupId, CreateInvitationRequest request);
    GroupInvitationResponse revokeInvitation(SessionPrincipal principal, UUID groupId, UUID invitationId);
    GroupDetailResponse acceptInvitation(SessionPrincipal principal, AcceptInvitationRequest request);
}
