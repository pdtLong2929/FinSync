package com.finsync.service.impl;

import com.finsync.dto.GroupExpenseDtos.CreateFundEntryRequest;
import com.finsync.dto.GroupExpenseDtos.GroupFundEntryResponse;
import com.finsync.dto.GroupExpenseDtos.GroupFundOverviewResponse;
import com.finsync.entity.ExpenseGroup;
import com.finsync.entity.GroupFundEntry;
import com.finsync.entity.GroupMember;
import com.finsync.entity.GroupWallet;
import com.finsync.entity.User;
import com.finsync.entity.enums.GroupFundEntryType;
import com.finsync.exception.ApiException;
import com.finsync.repository.GroupFundEntryRepository;
import com.finsync.repository.GroupMemberRepository;
import com.finsync.repository.GroupWalletRepository;
import com.finsync.security.SessionPrincipal;
import com.finsync.service.GroupFundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupFundServiceImpl implements GroupFundService {

    private final GroupAccessSupport access;
    private final GroupWalletRepository wallets;
    private final GroupFundEntryRepository entries;
    private final GroupMemberRepository members;

    @Override
    @Transactional(readOnly = true)
    public GroupFundOverviewResponse getFundOverview(SessionPrincipal principal, UUID groupId) {
        User user = access.requireActiveUser(principal);
        ExpenseGroup group = access.requireGroup(groupId);
        access.requireActiveMember(groupId, user.getId());
        GroupWallet wallet = wallets.findByGroupId(groupId)
                .orElseThrow(() -> new IllegalStateException("Missing GroupWallet for group " + groupId));

        BigDecimal balance = entries.computeBalanceByWalletId(wallet.getId());
        List<GroupFundEntryResponse> list = entries.findByGroupWalletIdOrdered(wallet.getId()).stream()
                .map(GroupFundServiceImpl::toResponse)
                .toList();

        return new GroupFundOverviewResponse(
                wallet.getId(),
                group.getId(),
                wallet.getName(),
                group.getCurrencyCode(),
                MoneyValidation.format(balance),
                list
        );
    }

    @Override
    @Transactional
    public GroupFundEntryResponse createFundEntry(SessionPrincipal principal, UUID groupId, CreateFundEntryRequest request) {
        User user = access.requireActiveUser(principal);
        ExpenseGroup group = access.lockGroup(groupId);
        GroupMember owner = access.requireOwner(groupId, user.getId());
        access.requireNotArchived(group);

        if (request.type() != GroupFundEntryType.CONTRIBUTION && request.type() != GroupFundEntryType.REFUND) {
            throw ApiException.invalid("type", "Only CONTRIBUTION or REFUND entries can be recorded directly");
        }
        BigDecimal amount = MoneyValidation.requirePositiveAmount(request.amount(), group.getCurrencyCode(), "amount");
        String note = request.note() == null ? null : request.note().trim();
        if (note != null && note.isEmpty()) {
            note = null;
        }
        String requestHash = IdempotencySupport.sha256(String.join("|",
                groupId.toString(),
                request.type().name(),
                request.memberId().toString(),
                MoneyValidation.format(amount),
                request.entryDate().toString(),
                note == null ? "" : note
        ));

        GroupWallet wallet = wallets.findByGroupId(groupId)
                .orElseThrow(() -> new IllegalStateException("Missing GroupWallet for group " + groupId));

        var existing = entries.findByRequestId(request.requestId());
        if (existing.isPresent()) {
            GroupFundEntry found = existing.get();
            if (!found.getGroupWallet().getId().equals(wallet.getId())) {
                throw new ApiException(409, "IDEMPOTENCY_CONFLICT", "requestId belongs to a different group");
            }
            IdempotencySupport.verifySameHash(found.getRequestHash(), requestHash);
            return toResponse(found);
        }

        GroupMember member = members.findByIdAndGroupId(request.memberId(), groupId)
                .orElseThrow(() -> new ApiException(404, "MEMBER_NOT_FOUND", "Target group member not found"));
        if (!member.isActive()) {
            throw new ApiException(409, "MEMBER_NOT_ACTIVE", "Cannot record fund entries for a member who has left");
        }

        if (request.type() == GroupFundEntryType.REFUND) {
            BigDecimal currentBalance = entries.computeBalanceByWalletId(wallet.getId());
            if (currentBalance.compareTo(amount) < 0) {
                throw new ApiException(422, "INSUFFICIENT_GROUP_FUND",
                        "Refund amount exceeds current shared group fund balance");
            }
        }

        GroupFundEntry entry = new GroupFundEntry();
        entry.setGroupWallet(wallet);
        entry.setMember(member);
        entry.setExpense(null);
        entry.setRecordedByMember(owner);
        entry.setType(request.type());
        entry.setAmount(amount);
        entry.setEntryDate(request.entryDate());
        entry.setNote(note);
        entry.setRequestId(request.requestId());
        entry.setRequestHash(requestHash);
        entry = entries.saveAndFlush(entry);

        return toResponse(entry);
    }

    public static GroupFundEntryResponse toResponse(GroupFundEntry e) {
        return new GroupFundEntryResponse(
                e.getId(),
                e.getGroupWallet().getId(),
                e.getMember() != null ? e.getMember().getId() : null,
                e.getMember() != null ? e.getMember().getDisplayName() : null,
                e.getExpense() != null ? e.getExpense().getId() : null,
                e.getRecordedByMember().getId(),
                e.getType(),
                MoneyValidation.format(e.getAmount()),
                e.getEntryDate(),
                e.getNote(),
                e.getRequestId(),
                e.getCreatedAt()
        );
    }
}
