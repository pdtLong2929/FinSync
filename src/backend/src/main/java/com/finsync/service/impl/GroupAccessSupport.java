package com.finsync.service.impl;

import com.finsync.entity.ExpenseGroup;
import com.finsync.entity.GroupExpense;
import com.finsync.entity.GroupExpenseSplit;
import com.finsync.entity.GroupMember;
import com.finsync.entity.Settlement;
import com.finsync.entity.User;
import com.finsync.entity.enums.CurrencyCode;
import com.finsync.entity.enums.FundingSource;
import com.finsync.entity.enums.GroupMemberRole;
import com.finsync.entity.enums.GroupMemberStatus;
import com.finsync.entity.enums.UserStatus;
import com.finsync.exception.ApiException;
import com.finsync.repository.ExpenseGroupRepository;
import com.finsync.repository.GroupExpenseRepository;
import com.finsync.repository.GroupExpenseSplitRepository;
import com.finsync.repository.GroupMemberRepository;
import com.finsync.repository.SettlementRepository;
import com.finsync.repository.UserRepository;
import com.finsync.security.SessionPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Shared authorization, pessimistic group locking, and ledger net-balance derivation helpers. */
@Component
@RequiredArgsConstructor
public class GroupAccessSupport {

    private final UserRepository users;
    private final ExpenseGroupRepository groups;
    private final GroupMemberRepository members;
    private final GroupExpenseRepository expenses;
    private final GroupExpenseSplitRepository splits;
    private final SettlementRepository settlements;

    public User requireActiveUser(SessionPrincipal principal) {
        if (principal == null || principal.userId() == null) {
            throw ApiException.unauthorized();
        }
        User user = users.findById(principal.userId()).orElseThrow(ApiException::unauthorized);
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ApiException(403, "ACCOUNT_LOCKED", "Account is locked");
        }
        return user;
    }

    public ExpenseGroup requireGroup(UUID groupId) {
        return groups.findById(groupId)
                .orElseThrow(() -> new ApiException(404, "GROUP_NOT_FOUND", "Group not found"));
    }

    public ExpenseGroup lockGroup(UUID groupId) {
        return groups.findByIdForUpdate(groupId)
                .orElseThrow(() -> new ApiException(404, "GROUP_NOT_FOUND", "Group not found"));
    }

    public GroupMember requireActiveMember(UUID groupId, UUID userId) {
        GroupMember member = members.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new ApiException(403, "GROUP_ACCESS_DENIED", "You are not a member of this group"));
        if (member.getStatus() != GroupMemberStatus.ACTIVE) {
            throw new ApiException(403, "GROUP_ACCESS_DENIED", "Membership is no longer active in this group");
        }
        return member;
    }

    public GroupMember requireOwner(UUID groupId, UUID userId) {
        GroupMember member = requireActiveMember(groupId, userId);
        if (member.getRole() != GroupMemberRole.OWNER) {
            throw new ApiException(403, "GROUP_OWNER_REQUIRED", "Only the group owner can perform this action");
        }
        return member;
    }

    public void requireNotArchived(ExpenseGroup group) {
        if (group.isArchived()) {
            throw new ApiException(409, "GROUP_ARCHIVED", "Archived group is read-only");
        }
    }

    /**
     * Computes each member's net balance in minor units from persisted MEMBERS expenses and CONFIRMED settlements:
     * net = paid(MEMBERS) - split(MEMBERS) + settledPaid - settledReceived.
     */
    public Map<UUID, Long> computeNetBalancesMinor(UUID groupId, CurrencyCode currency, List<GroupMember> groupMembers) {
        Map<UUID, Long> net = new LinkedHashMap<>();
        for (GroupMember m : groupMembers) {
            net.put(m.getId(), 0L);
        }

        List<GroupExpense> memberExpenses = expenses.findByGroupIdAndFundingSourceWithPayer(groupId, FundingSource.MEMBERS);
        for (GroupExpense e : memberExpenses) {
            long amt = MoneyValidation.toMinorUnits(e.getAmount(), currency);
            net.merge(e.getPaidByMember().getId(), amt, Long::sum);
        }

        List<GroupExpenseSplit> memberSplits = splits.findByGroupIdAndFundingSource(groupId, FundingSource.MEMBERS);
        for (GroupExpenseSplit s : memberSplits) {
            long amt = MoneyValidation.toMinorUnits(s.getAmount(), currency);
            net.merge(s.getMember().getId(), -amt, Long::sum);
        }

        List<Settlement> groupSettlements = settlements.findByGroupIdOrdered(groupId);
        for (Settlement st : groupSettlements) {
            long amt = MoneyValidation.toMinorUnits(st.getAmount(), currency);
            net.merge(st.getPayerMember().getId(), amt, Long::sum);
            net.merge(st.getReceiverMember().getId(), -amt, Long::sum);
        }
        return net;
    }
}
