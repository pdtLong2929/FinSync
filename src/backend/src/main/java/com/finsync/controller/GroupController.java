package com.finsync.controller;

import com.finsync.dto.GroupDtos.*;
import com.finsync.security.SessionPrincipal;
import com.finsync.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groups;

    @GetMapping
    public List<GroupSummaryResponse> list(
            @AuthenticationPrincipal SessionPrincipal principal,
            @RequestParam(defaultValue = "false") boolean includeArchived
    ) {
        return groups.listGroups(principal, includeArchived);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupDetailResponse create(
            @AuthenticationPrincipal SessionPrincipal principal,
            @Valid @RequestBody CreateGroupRequest request
    ) {
        return groups.createGroup(principal, request);
    }

    @GetMapping("/{groupId}")
    public GroupDetailResponse get(
            @AuthenticationPrincipal SessionPrincipal principal,
            @PathVariable UUID groupId
    ) {
        return groups.getGroup(principal, groupId);
    }

    @PatchMapping("/{groupId}")
    public GroupDetailResponse update(
            @AuthenticationPrincipal SessionPrincipal principal,
            @PathVariable UUID groupId,
            @Valid @RequestBody UpdateGroupRequest request
    ) {
        return groups.updateGroup(principal, groupId, request);
    }

    @PostMapping("/{groupId}/archive")
    public GroupDetailResponse archive(
            @AuthenticationPrincipal SessionPrincipal principal,
            @PathVariable UUID groupId
    ) {
        return groups.archiveGroup(principal, groupId);
    }

    @PostMapping("/{groupId}/unarchive")
    public GroupDetailResponse unarchive(
            @AuthenticationPrincipal SessionPrincipal principal,
            @PathVariable UUID groupId
    ) {
        return groups.unarchiveGroup(principal, groupId);
    }

    @GetMapping("/{groupId}/members")
    public List<GroupMemberResponse> listMembers(
            @AuthenticationPrincipal SessionPrincipal principal,
            @PathVariable UUID groupId
    ) {
        return groups.listMembers(principal, groupId);
    }

    @PostMapping("/{groupId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public GroupMemberResponse addGuestMember(
            @AuthenticationPrincipal SessionPrincipal principal,
            @PathVariable UUID groupId,
            @Valid @RequestBody AddGuestMemberRequest request
    ) {
        return groups.addGuestMember(principal, groupId, request);
    }

    @DeleteMapping("/{groupId}/members/{memberId}")
    public GroupMemberResponse removeMember(
            @AuthenticationPrincipal SessionPrincipal principal,
            @PathVariable UUID groupId,
            @PathVariable UUID memberId
    ) {
        return groups.removeMember(principal, groupId, memberId);
    }

    @PostMapping("/{groupId}/members/leave")
    public GroupMemberResponse leave(
            @AuthenticationPrincipal SessionPrincipal principal,
            @PathVariable UUID groupId
    ) {
        return groups.leaveGroup(principal, groupId);
    }

    @PostMapping("/{groupId}/transfer-ownership")
    public GroupDetailResponse transferOwnership(
            @AuthenticationPrincipal SessionPrincipal principal,
            @PathVariable UUID groupId,
            @Valid @RequestBody TransferOwnershipRequest request
    ) {
        return groups.transferOwnership(principal, groupId, request);
    }

    @GetMapping("/{groupId}/invitations")
    public List<GroupInvitationResponse> listInvitations(
            @AuthenticationPrincipal SessionPrincipal principal,
            @PathVariable UUID groupId
    ) {
        return groups.listInvitations(principal, groupId);
    }

    @PostMapping("/{groupId}/invitations")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateInvitationResponse createInvitation(
            @AuthenticationPrincipal SessionPrincipal principal,
            @PathVariable UUID groupId,
            @Valid @RequestBody(required = false) CreateInvitationRequest request
    ) {
        return groups.createInvitation(principal, groupId, request);
    }

    @DeleteMapping("/{groupId}/invitations/{invitationId}")
    public GroupInvitationResponse revokeInvitation(
            @AuthenticationPrincipal SessionPrincipal principal,
            @PathVariable UUID groupId,
            @PathVariable UUID invitationId
    ) {
        return groups.revokeInvitation(principal, groupId, invitationId);
    }

    @PostMapping("/invitations/accept")
    public GroupDetailResponse acceptInvitation(
            @AuthenticationPrincipal SessionPrincipal principal,
            @Valid @RequestBody AcceptInvitationRequest request
    ) {
        return groups.acceptInvitation(principal, request);
    }
}
