package com.finsync.service.impl;

import com.finsync.dto.GroupDtos.*;
import com.finsync.entity.ExpenseGroup;
import com.finsync.entity.GroupInvitation;
import com.finsync.entity.GroupMember;
import com.finsync.entity.GroupWallet;
import com.finsync.entity.User;
import com.finsync.entity.enums.CurrencyCode;
import com.finsync.entity.enums.GroupMemberRole;
import com.finsync.entity.enums.GroupMemberStatus;
import com.finsync.exception.ApiException;
import com.finsync.repository.ExpenseGroupRepository;
import com.finsync.repository.GroupExpenseRepository;
import com.finsync.repository.GroupFundEntryRepository;
import com.finsync.repository.GroupInvitationRepository;
import com.finsync.repository.GroupMemberRepository;
import com.finsync.repository.GroupWalletRepository;
import com.finsync.repository.SettlementRepository;
import com.finsync.security.SessionPrincipal;
import com.finsync.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private static final int MAX_ACTIVE_MEMBERS = 50;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final GroupAccessSupport access;
    private final ExpenseGroupRepository groups;
    private final GroupMemberRepository members;
    private final GroupWalletRepository wallets;
    private final GroupInvitationRepository invitations;
    private final GroupFundEntryRepository fundEntries;
    private final GroupExpenseRepository expenses;
    private final SettlementRepository settlements;

    @Override
    @Transactional(readOnly = true)
    public List<GroupSummaryResponse> listGroups(SessionPrincipal principal, boolean includeArchived) {
        User user = access.requireActiveUser(principal);
        List<GroupMember> memberships = members.findActiveMembershipsByUserId(user.getId(), includeArchived);
        List<GroupSummaryResponse> result = new ArrayList<>(memberships.size());
        for (GroupMember caller : memberships) {
            ExpenseGroup group = caller.getGroup();
            List<GroupMember> allMembers = members.findByGroupIdOrderByMemberOrderAsc(group.getId());
            int activeCount = (int) allMembers.stream().filter(GroupMember::isActive).count();
            Map<UUID, Long> netMap = access.computeNetBalancesMinor(group.getId(), group.getCurrencyCode(), allMembers);
            long callerNet = netMap.getOrDefault(caller.getId(), 0L);
            GroupWallet wallet = wallets.findByGroupId(group.getId())
                    .orElseThrow(() -> new IllegalStateException("Missing GroupWallet for group " + group.getId()));
            var fundBalance = fundEntries.computeBalanceByWalletId(wallet.getId());

            result.add(new GroupSummaryResponse(
                    group.getId(),
                    group.getName(),
                    group.getDescription(),
                    group.getCurrencyCode(),
                    group.isArchived(),
                    group.getArchivedAt(),
                    activeCount,
                    caller.getId(),
                    caller.getRole(),
                    MoneyValidation.format(MoneyValidation.fromMinorUnits(callerNet, group.getCurrencyCode())),
                    MoneyValidation.format(fundBalance),
                    group.getCreatedAt()
            ));
        }
        return result;
    }

    @Override
    @Transactional
    public GroupDetailResponse createGroup(SessionPrincipal principal, CreateGroupRequest request) {
        User user = access.requireActiveUser(principal);
        String name = normalizeRequiredText(request.name(), "name", 100);
        String description = normalizeOptionalText(request.description(), "description", 1000);
        String fundName = request.fundName() == null || request.fundName().isBlank()
                ? "Quỹ chung"
                : normalizeRequiredText(request.fundName(), "fundName", 100);
        List<String> guests = request.guestMemberNames() == null ? List.of() : request.guestMemberNames();
        if (1 + guests.size() > MAX_ACTIVE_MEMBERS) {
            throw new ApiException(409, "GROUP_MEMBER_LIMIT_REACHED", "A group supports at most 50 active members");
        }

        ExpenseGroup group = new ExpenseGroup();
        group.setName(name);
        group.setDescription(description);
        group.setCurrencyCode(request.currencyCode());
        group = groups.saveAndFlush(group);

        GroupMember owner = new GroupMember();
        owner.setGroup(group);
        owner.setUser(user);
        owner.setDisplayName(user.getDisplayName());
        owner.setRole(GroupMemberRole.OWNER);
        owner.setStatus(GroupMemberStatus.ACTIVE);
        owner.setMemberOrder(0);
        owner.setJoinedAt(Instant.now());
        members.save(owner);

        int order = 1;
        for (String guestName : guests) {
            GroupMember guest = new GroupMember();
            guest.setGroup(group);
            guest.setUser(null);
            guest.setDisplayName(normalizeRequiredText(guestName, "guestMemberNames", 100));
            guest.setRole(GroupMemberRole.MEMBER);
            guest.setStatus(GroupMemberStatus.ACTIVE);
            guest.setMemberOrder(order++);
            guest.setJoinedAt(Instant.now());
            members.save(guest);
        }

        GroupWallet wallet = new GroupWallet();
        wallet.setGroup(group);
        wallet.setName(fundName);
        wallets.saveAndFlush(wallet);

        return buildGroupDetail(group, wallet);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupDetailResponse getGroup(SessionPrincipal principal, UUID groupId) {
        User user = access.requireActiveUser(principal);
        ExpenseGroup group = access.requireGroup(groupId);
        access.requireActiveMember(groupId, user.getId());
        GroupWallet wallet = wallets.findByGroupId(groupId)
                .orElseThrow(() -> new IllegalStateException("Missing GroupWallet for group " + groupId));
        return buildGroupDetail(group, wallet);
    }

    @Override
    @Transactional
    public GroupDetailResponse updateGroup(SessionPrincipal principal, UUID groupId, UpdateGroupRequest request) {
        User user = access.requireActiveUser(principal);
        ExpenseGroup group = access.lockGroup(groupId);
        access.requireOwner(groupId, user.getId());
        access.requireNotArchived(group);

        if (request.name() != null) {
            group.setName(normalizeRequiredText(request.name(), "name", 100));
        }
        if (request.description() != null) {
            group.setDescription(normalizeOptionalText(request.description(), "description", 1000));
        }
        GroupWallet wallet = wallets.findByGroupId(groupId)
                .orElseThrow(() -> new IllegalStateException("Missing GroupWallet for group " + groupId));
        if (request.currencyCode() != null && request.currencyCode() != group.getCurrencyCode()) {
            boolean hasFinancialActivity = fundEntries.existsByGroupWalletId(wallet.getId())
                    || expenses.existsByGroupId(groupId)
                    || settlements.existsByGroupId(groupId);
            if (hasFinancialActivity) {
                throw new ApiException(409, "CURRENCY_IMMUTABLE",
                        "Group currency cannot change after financial activity exists");
            }
            group.setCurrencyCode(request.currencyCode());
        }
        groups.flush();
        return buildGroupDetail(group, wallet);
    }

    @Override
    @Transactional
    public GroupDetailResponse archiveGroup(SessionPrincipal principal, UUID groupId) {
        User user = access.requireActiveUser(principal);
        ExpenseGroup group = access.lockGroup(groupId);
        access.requireOwner(groupId, user.getId());
        if (group.isArchived()) {
            throw new ApiException(409, "GROUP_ALREADY_ARCHIVED", "Group is already archived");
        }
        group.setArchivedAt(Instant.now());
        groups.flush();
        GroupWallet wallet = wallets.findByGroupId(groupId).orElseThrow();
        return buildGroupDetail(group, wallet);
    }

    @Override
    @Transactional
    public GroupDetailResponse unarchiveGroup(SessionPrincipal principal, UUID groupId) {
        User user = access.requireActiveUser(principal);
        ExpenseGroup group = access.lockGroup(groupId);
        access.requireOwner(groupId, user.getId());
        if (!group.isArchived()) {
            throw new ApiException(409, "GROUP_NOT_ARCHIVED", "Group is not archived");
        }
        group.setArchivedAt(null);
        groups.flush();
        GroupWallet wallet = wallets.findByGroupId(groupId).orElseThrow();
        return buildGroupDetail(group, wallet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupMemberResponse> listMembers(SessionPrincipal principal, UUID groupId) {
        User user = access.requireActiveUser(principal);
        ExpenseGroup group = access.requireGroup(groupId);
        access.requireActiveMember(groupId, user.getId());
        return mapMembers(group);
    }

    @Override
    @Transactional
    public GroupMemberResponse addGuestMember(SessionPrincipal principal, UUID groupId, AddGuestMemberRequest request) {
        User user = access.requireActiveUser(principal);
        ExpenseGroup group = access.lockGroup(groupId);
        access.requireOwner(groupId, user.getId());
        access.requireNotArchived(group);

        long activeCount = members.countByGroupIdAndStatus(groupId, GroupMemberStatus.ACTIVE);
        if (activeCount >= MAX_ACTIVE_MEMBERS) {
            throw new ApiException(409, "GROUP_MEMBER_LIMIT_REACHED", "A group supports at most 50 active members");
        }
        int nextOrder = members.findMaxMemberOrderByGroupId(groupId) + 1;
        GroupMember guest = new GroupMember();
        guest.setGroup(group);
        guest.setUser(null);
        guest.setDisplayName(normalizeRequiredText(request.displayName(), "displayName", 100));
        guest.setRole(GroupMemberRole.MEMBER);
        guest.setStatus(GroupMemberStatus.ACTIVE);
        guest.setMemberOrder(nextOrder);
        guest.setJoinedAt(Instant.now());
        guest = members.saveAndFlush(guest);
        return toMemberResponse(guest, 0L, group.getCurrencyCode());
    }

    @Override
    @Transactional
    public GroupMemberResponse removeMember(SessionPrincipal principal, UUID groupId, UUID memberId) {
        User user = access.requireActiveUser(principal);
        ExpenseGroup group = access.lockGroup(groupId);
        access.requireOwner(groupId, user.getId());
        access.requireNotArchived(group);

        GroupMember target = members.findByIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> new ApiException(404, "MEMBER_NOT_FOUND", "Group member not found"));
        if (target.getRole() == GroupMemberRole.OWNER) {
            throw new ApiException(409, "CANNOT_REMOVE_OWNER", "Transfer ownership before removing the owner");
        }
        if (target.getStatus() == GroupMemberStatus.LEFT) {
            throw new ApiException(409, "MEMBER_ALREADY_LEFT", "Member has already left the group");
        }

        List<GroupMember> allMembers = members.findByGroupIdOrderByMemberOrderAsc(groupId);
        long net = access.computeNetBalancesMinor(groupId, group.getCurrencyCode(), allMembers)
                .getOrDefault(target.getId(), 0L);
        if (net != 0L) {
            throw new ApiException(409, "NON_ZERO_BALANCE",
                    "Cannot remove a member with an unsettled balance");
        }

        target.setStatus(GroupMemberStatus.LEFT);
        target.setLeftAt(Instant.now());
        members.flush();
        return toMemberResponse(target, 0L, group.getCurrencyCode());
    }

    @Override
    @Transactional
    public GroupMemberResponse leaveGroup(SessionPrincipal principal, UUID groupId) {
        User user = access.requireActiveUser(principal);
        ExpenseGroup group = access.lockGroup(groupId);
        GroupMember caller = access.requireActiveMember(groupId, user.getId());
        access.requireNotArchived(group);

        if (caller.getRole() == GroupMemberRole.OWNER) {
            throw new ApiException(409, "OWNER_MUST_TRANSFER_BEFORE_LEAVE",
                    "Transfer group ownership to another registered member before leaving");
        }

        List<GroupMember> allMembers = members.findByGroupIdOrderByMemberOrderAsc(groupId);
        long net = access.computeNetBalancesMinor(groupId, group.getCurrencyCode(), allMembers)
                .getOrDefault(caller.getId(), 0L);
        if (net != 0L) {
            throw new ApiException(409, "NON_ZERO_BALANCE",
                    "Settle all balances to zero before leaving the group");
        }

        caller.setStatus(GroupMemberStatus.LEFT);
        caller.setLeftAt(Instant.now());
        members.flush();
        return toMemberResponse(caller, 0L, group.getCurrencyCode());
    }

    @Override
    @Transactional
    public GroupDetailResponse transferOwnership(SessionPrincipal principal, UUID groupId, TransferOwnershipRequest request) {
        User user = access.requireActiveUser(principal);
        ExpenseGroup group = access.lockGroup(groupId);
        GroupMember currentOwner = access.requireOwner(groupId, user.getId());
        access.requireNotArchived(group);

        GroupMember nextOwner = members.findByIdAndGroupId(request.newOwnerMemberId(), groupId)
                .orElseThrow(() -> new ApiException(404, "MEMBER_NOT_FOUND", "Target member not found"));
        if (!nextOwner.isActive() || nextOwner.isGuest()) {
            throw new ApiException(409, "INVALID_OWNER_TARGET",
                    "Ownership can only be transferred to an active registered member");
        }
        if (nextOwner.getId().equals(currentOwner.getId())) {
            throw new ApiException(409, "ALREADY_OWNER", "Target member is already the group owner");
        }

        // Demote and flush first so partial unique index uk_group_members_active_owner stays satisfied.
        currentOwner.setRole(GroupMemberRole.MEMBER);
        members.flush();
        nextOwner.setRole(GroupMemberRole.OWNER);
        members.flush();

        GroupWallet wallet = wallets.findByGroupId(groupId).orElseThrow();
        return buildGroupDetail(group, wallet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupInvitationResponse> listInvitations(SessionPrincipal principal, UUID groupId) {
        User user = access.requireActiveUser(principal);
        access.requireGroup(groupId);
        access.requireOwner(groupId, user.getId());
        Instant now = Instant.now();
        return invitations.findByGroupIdOrderByCreatedAtDesc(groupId).stream()
                .map(inv -> toInvitationResponse(inv, now))
                .toList();
    }

    @Override
    @Transactional
    public CreateInvitationResponse createInvitation(SessionPrincipal principal, UUID groupId, CreateInvitationRequest request) {
        User user = access.requireActiveUser(principal);
        ExpenseGroup group = access.lockGroup(groupId);
        GroupMember owner = access.requireOwner(groupId, user.getId());
        access.requireNotArchived(group);

        GroupMember target = null;
        int maxUses = request != null && request.maxUses() != null ? request.maxUses() : 1;
        if (request != null && request.targetMemberId() != null) {
            target = members.findByIdAndGroupId(request.targetMemberId(), groupId)
                    .orElseThrow(() -> new ApiException(404, "MEMBER_NOT_FOUND", "Target guest member not found"));
            if (!target.isActive() || !target.isGuest()) {
                throw new ApiException(409, "INVALID_TARGET_MEMBER",
                        "Targeted invitations require an active unlinked guest member");
            }
            if (maxUses != 1) {
                throw ApiException.invalid("maxUses", "Targeted guest invitations must have maxUses = 1");
            }
        }

        int expiresInHours = request != null && request.expiresInHours() != null ? request.expiresInHours() : 72;
        byte[] rawBytes = new byte[24];
        RANDOM.nextBytes(rawBytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(rawBytes);

        GroupInvitation inv = new GroupInvitation();
        inv.setGroup(group);
        inv.setCreatedByMember(owner);
        inv.setTargetMember(target);
        inv.setTokenHash(IdempotencySupport.sha256(rawToken));
        inv.setMaxUses(maxUses);
        inv.setUseCount(0);
        inv.setExpiresAt(Instant.now().plus(expiresInHours, ChronoUnit.HOURS));
        inv = invitations.saveAndFlush(inv);

        return new CreateInvitationResponse(toInvitationResponse(inv, Instant.now()), rawToken);
    }

    @Override
    @Transactional
    public GroupInvitationResponse revokeInvitation(SessionPrincipal principal, UUID groupId, UUID invitationId) {
        User user = access.requireActiveUser(principal);
        ExpenseGroup group = access.lockGroup(groupId);
        access.requireOwner(groupId, user.getId());
        access.requireNotArchived(group);

        GroupInvitation inv = invitations.findByIdAndGroupId(invitationId, groupId)
                .orElseThrow(() -> new ApiException(404, "INVITATION_NOT_FOUND", "Invitation not found"));
        if (inv.getRevokedAt() == null) {
            inv.setRevokedAt(Instant.now());
            invitations.flush();
        }
        return toInvitationResponse(inv, Instant.now());
    }

    @Override
    @Transactional
    public GroupDetailResponse acceptInvitation(SessionPrincipal principal, AcceptInvitationRequest request) {
        User user = access.requireActiveUser(principal);
        String tokenHash = IdempotencySupport.sha256(request.token().trim());
        GroupInvitation lookup = invitations.findByTokenHash(tokenHash)
                .orElseThrow(() -> new ApiException(404, "INVITATION_NOT_FOUND", "Invalid invitation token"));

        ExpenseGroup group = access.lockGroup(lookup.getGroup().getId());
        access.requireNotArchived(group);

        GroupInvitation inv = invitations.findByTokenHash(tokenHash)
                .orElseThrow(() -> new ApiException(404, "INVITATION_NOT_FOUND", "Invalid invitation token"));
        Instant now = Instant.now();
        if (!inv.isUsableAt(now)) {
            throw new ApiException(409, "INVITATION_UNUSABLE", "Invitation has expired, been revoked, or reached max uses");
        }

        var existingMembership = members.findByGroupIdAndUserId(group.getId(), user.getId());
        if (existingMembership.isPresent() && existingMembership.get().isActive()) {
            throw new ApiException(409, "ALREADY_GROUP_MEMBER", "You are already an active member of this group");
        }

        if (inv.getTargetMember() != null) {
            GroupMember target = inv.getTargetMember();
            if (!target.isActive() || !target.isGuest()) {
                throw new ApiException(409, "TARGET_MEMBER_UNAVAILABLE", "Target guest member is already linked or inactive");
            }
            if (existingMembership.isPresent()) {
                throw new ApiException(409, "ALREADY_GROUP_MEMBER",
                        "Users with prior membership history in this group cannot claim another member slot");
            }
            target.setUser(user);
            members.flush();
        } else if (existingMembership.isPresent()) {
            ensureCapacity(group.getId());
            GroupMember rejoining = existingMembership.get();
            rejoining.setStatus(GroupMemberStatus.ACTIVE);
            rejoining.setLeftAt(null);
            rejoining.setJoinedAt(now);
            rejoining.setDisplayName(user.getDisplayName());
            members.flush();
        } else {
            ensureCapacity(group.getId());
            int nextOrder = members.findMaxMemberOrderByGroupId(group.getId()) + 1;
            GroupMember member = new GroupMember();
            member.setGroup(group);
            member.setUser(user);
            member.setDisplayName(user.getDisplayName());
            member.setRole(GroupMemberRole.MEMBER);
            member.setStatus(GroupMemberStatus.ACTIVE);
            member.setMemberOrder(nextOrder);
            member.setJoinedAt(now);
            members.saveAndFlush(member);
        }

        inv.setUseCount(inv.getUseCount() + 1);
        invitations.flush();

        GroupWallet wallet = wallets.findByGroupId(group.getId()).orElseThrow();
        return buildGroupDetail(group, wallet);
    }

    private void ensureCapacity(UUID groupId) {
        if (members.countByGroupIdAndStatus(groupId, GroupMemberStatus.ACTIVE) >= MAX_ACTIVE_MEMBERS) {
            throw new ApiException(409, "GROUP_MEMBER_LIMIT_REACHED", "A group supports at most 50 active members");
        }
    }

    private GroupDetailResponse buildGroupDetail(ExpenseGroup group, GroupWallet wallet) {
        var fundBalance = fundEntries.computeBalanceByWalletId(wallet.getId());
        return new GroupDetailResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getCurrencyCode(),
                group.isArchived(),
                group.getArchivedAt(),
                group.getVersion(),
                wallet.getId(),
                wallet.getName(),
                MoneyValidation.format(fundBalance),
                mapMembers(group),
                group.getCreatedAt()
        );
    }

    private List<GroupMemberResponse> mapMembers(ExpenseGroup group) {
        List<GroupMember> list = members.findByGroupIdOrderByMemberOrderAsc(group.getId());
        Map<UUID, Long> netMap = access.computeNetBalancesMinor(group.getId(), group.getCurrencyCode(), list);
        return list.stream()
                .map(m -> toMemberResponse(m, netMap.getOrDefault(m.getId(), 0L), group.getCurrencyCode()))
                .toList();
    }

    private GroupMemberResponse toMemberResponse(GroupMember m, long netMinor, CurrencyCode currency) {
        return new GroupMemberResponse(
                m.getId(),
                m.getUser() != null ? m.getUser().getId() : null,
                m.getDisplayName(),
                m.getRole(),
                m.getStatus(),
                m.isGuest(),
                m.getMemberOrder(),
                MoneyValidation.format(MoneyValidation.fromMinorUnits(netMinor, currency)),
                m.getJoinedAt(),
                m.getLeftAt()
        );
    }

    private GroupInvitationResponse toInvitationResponse(GroupInvitation inv, Instant now) {
        return new GroupInvitationResponse(
                inv.getId(),
                inv.getGroup().getId(),
                inv.getCreatedByMember().getId(),
                inv.getTargetMember() != null ? inv.getTargetMember().getId() : null,
                inv.getMaxUses(),
                inv.getUseCount(),
                inv.getExpiresAt(),
                inv.getRevokedAt(),
                inv.isUsableAt(now)
        );
    }

    private static String normalizeRequiredText(String raw, String field, int maxLen) {
        if (raw == null || raw.isBlank()) {
            throw ApiException.invalid(field, field + " must not be blank");
        }
        String trimmed = raw.trim();
        if (trimmed.length() > maxLen) {
            throw ApiException.invalid(field, "Maximum length is " + maxLen);
        }
        return trimmed;
    }

    private static String normalizeOptionalText(String raw, String field, int maxLen) {
        if (raw == null) {
            return null;
        }
        String trimmed = raw.trim();
        if (trimmed.length() > maxLen) {
            throw ApiException.invalid(field, "Maximum length is " + maxLen);
        }
        return trimmed.isEmpty() ? null : trimmed;
    }
}
