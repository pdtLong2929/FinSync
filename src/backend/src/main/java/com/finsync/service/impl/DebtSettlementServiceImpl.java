package com.finsync.service.impl;

import com.finsync.dto.SettlementDtos.*;
import com.finsync.entity.ExpenseGroup;
import com.finsync.entity.GroupExpenseSplit;
import com.finsync.entity.GroupMember;
import com.finsync.entity.Settlement;
import com.finsync.entity.User;
import com.finsync.entity.enums.CurrencyCode;
import com.finsync.entity.enums.FundingSource;
import com.finsync.entity.enums.GroupMemberRole;
import com.finsync.entity.enums.SettlementStatus;
import com.finsync.exception.ApiException;
import com.finsync.repository.GroupExpenseSplitRepository;
import com.finsync.repository.GroupMemberRepository;
import com.finsync.repository.SettlementRepository;
import com.finsync.security.SessionPrincipal;
import com.finsync.service.DebtSettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DebtSettlementServiceImpl implements DebtSettlementService {

    private final GroupAccessSupport access;
    private final GroupMemberRepository members;
    private final GroupExpenseSplitRepository splits;
    private final SettlementRepository settlements;
    private final DebtSimplificationEngine debtEngine;

    @Override
    @Transactional(readOnly = true)
    public GroupDebtSummaryResponse getDebtSummary(SessionPrincipal principal, UUID groupId) {
        User user = access.requireActiveUser(principal);
        ExpenseGroup group = access.requireGroup(groupId);
        access.requireActiveMember(groupId, user.getId());

        CurrencyCode currency = group.getCurrencyCode();
        List<GroupMember> groupMembers = members.findByGroupIdOrderByMemberOrderAsc(groupId);
        Map<UUID, Long> netMap = access.computeNetBalancesMinor(groupId, currency, groupMembers);

        List<MemberBalanceItem> balanceItems = groupMembers.stream()
                .map(m -> new MemberBalanceItem(
                        m.getId(),
                        m.getDisplayName(),
                        m.getStatus(),
                        m.isGuest(),
                        MoneyValidation.format(MoneyValidation.fromMinorUnits(netMap.getOrDefault(m.getId(), 0L), currency))
                ))
                .toList();

        List<DebtSimplificationEngine.MemberNet> engineInputs = groupMembers.stream()
                .map(m -> new DebtSimplificationEngine.MemberNet(
                        m.getId(),
                        m.getDisplayName(),
                        m.getMemberOrder(),
                        netMap.getOrDefault(m.getId(), 0L)
                ))
                .toList();

        List<SimplifiedTransferItem> simplified = debtEngine.simplify(engineInputs, currency).stream()
                .map(t -> new SimplifiedTransferItem(
                        t.fromMemberId(),
                        t.fromDisplayName(),
                        t.toMemberId(),
                        t.toDisplayName(),
                        MoneyValidation.format(t.amount())
                ))
                .toList();

        int rawEdges = countRawPairwiseDebtEdges(groupId, currency);
        int simplifiedCount = simplified.size();
        double reduction = rawEdges == 0
                ? 0.0
                : Math.max(0.0, ((rawEdges - simplifiedCount) * 100.0) / rawEdges);

        return new GroupDebtSummaryResponse(
                groupId,
                currency,
                balanceItems,
                simplified,
                rawEdges,
                simplifiedCount,
                Math.round(reduction * 100.0) / 100.0
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<SettlementResponse> listSettlements(SessionPrincipal principal, UUID groupId) {
        User user = access.requireActiveUser(principal);
        ExpenseGroup group = access.requireGroup(groupId);
        access.requireActiveMember(groupId, user.getId());

        return settlements.findByGroupIdOrdered(groupId).stream()
                .map(s -> toResponse(s, group.getCurrencyCode()))
                .toList();
    }

    @Override
    @Transactional
    public SettlementResponse createSettlement(SessionPrincipal principal, UUID groupId, CreateSettlementRequest request) {
        User user = access.requireActiveUser(principal);
        ExpenseGroup group = access.lockGroup(groupId);
        GroupMember caller = access.requireActiveMember(groupId, user.getId());
        access.requireNotArchived(group);

        if (request.payerMemberId().equals(request.receiverMemberId())) {
            throw ApiException.invalid("receiverMemberId", "Payer and receiver must be different members");
        }
        CurrencyCode currency = group.getCurrencyCode();
        BigDecimal amount = MoneyValidation.requirePositiveAmount(request.amount(), currency, "amount");
        String note = request.note() == null || request.note().isBlank() ? null : request.note().trim();

        String requestHash = IdempotencySupport.sha256(String.join("|",
                groupId.toString(),
                request.payerMemberId().toString(),
                request.receiverMemberId().toString(),
                MoneyValidation.format(amount),
                request.settlementDate().toString(),
                note == null ? "" : note
        ));

        var existing = settlements.findByRequestId(request.requestId());
        if (existing.isPresent()) {
            Settlement found = existing.get();
            if (!found.getGroup().getId().equals(groupId)) {
                throw new ApiException(409, "IDEMPOTENCY_CONFLICT", "requestId belongs to a different group");
            }
            IdempotencySupport.verifySameHash(found.getRequestHash(), requestHash);
            return toResponse(found, currency);
        }

        GroupMember payer = members.findByIdAndGroupId(request.payerMemberId(), groupId)
                .orElseThrow(() -> new ApiException(404, "MEMBER_NOT_FOUND", "Payer member not found"));
        GroupMember receiver = members.findByIdAndGroupId(request.receiverMemberId(), groupId)
                .orElseThrow(() -> new ApiException(404, "MEMBER_NOT_FOUND", "Receiver member not found"));
        if (!payer.isActive() || !receiver.isActive()) {
            throw new ApiException(409, "MEMBER_NOT_ACTIVE", "Settlement parties must be active members");
        }

        boolean authorized = caller.getRole() == GroupMemberRole.OWNER
                || caller.getId().equals(payer.getId())
                || caller.getId().equals(receiver.getId());
        if (!authorized) {
            throw new ApiException(403, "SETTLEMENT_FORBIDDEN",
                    "Only the payer, receiver, or group owner may record this settlement");
        }

        List<GroupMember> groupMembers = members.findByGroupIdOrderByMemberOrderAsc(groupId);
        Map<UUID, Long> netMap = access.computeNetBalancesMinor(groupId, currency, groupMembers);
        long payerNet = netMap.getOrDefault(payer.getId(), 0L);
        long receiverNet = netMap.getOrDefault(receiver.getId(), 0L);
        long maxSettleableMinor = (payerNet < 0L && receiverNet > 0L)
                ? Math.min(-payerNet, receiverNet)
                : 0L;
        long amountMinor = MoneyValidation.toMinorUnits(amount, currency);
        if (maxSettleableMinor <= 0L || amountMinor > maxSettleableMinor) {
            throw new ApiException(422, "EXCEEDS_OUTSTANDING_DEBT",
                    "Settlement amount exceeds remaining settleable debt between payer and receiver");
        }

        Settlement settlement = new Settlement();
        settlement.setGroup(group);
        settlement.setPayerMember(payer);
        settlement.setReceiverMember(receiver);
        settlement.setRecordedByMember(caller);
        settlement.setAmount(amount);
        settlement.setSettlementDate(request.settlementDate());
        settlement.setNote(note);
        settlement.setStatus(SettlementStatus.CONFIRMED);
        settlement.setRequestId(request.requestId());
        settlement.setRequestHash(requestHash);
        settlement = settlements.saveAndFlush(settlement);

        return toResponse(settlement, currency);
    }

    /** Counts unsimplified net directed pairwise edges derived directly from MEMBERS splits and settlements. */
    private int countRawPairwiseDebtEdges(UUID groupId, CurrencyCode currency) {
        Map<String, Long> directedPairNet = new HashMap<>();
        List<GroupExpenseSplit> memberSplits = splits.findByGroupIdAndFundingSource(groupId, FundingSource.MEMBERS);
        for (GroupExpenseSplit split : memberSplits) {
            UUID debtor = split.getMember().getId();
            UUID creditor = split.getExpense().getPaidByMember().getId();
            if (debtor.equals(creditor)) {
                continue;
            }
            long amt = MoneyValidation.toMinorUnits(split.getAmount(), currency);
            addPairwiseDelta(directedPairNet, debtor, creditor, amt);
        }
        for (Settlement st : settlements.findByGroupIdOrdered(groupId)) {
            long amt = MoneyValidation.toMinorUnits(st.getAmount(), currency);
            addPairwiseDelta(directedPairNet, st.getPayerMember().getId(), st.getReceiverMember().getId(), -amt);
        }
        return (int) directedPairNet.values().stream().filter(v -> v != 0L).count();
    }

    private static void addPairwiseDelta(Map<String, Long> pairNet, UUID from, UUID to, long delta) {
        boolean forward = from.compareTo(to) < 0;
        String key = forward ? from + ":" + to : to + ":" + from;
        pairNet.merge(key, forward ? delta : -delta, Long::sum);
    }

    private static SettlementResponse toResponse(Settlement s, CurrencyCode currency) {
        return new SettlementResponse(
                s.getId(),
                s.getGroup().getId(),
                s.getPayerMember().getId(),
                s.getPayerMember().getDisplayName(),
                s.getReceiverMember().getId(),
                s.getReceiverMember().getDisplayName(),
                s.getRecordedByMember().getId(),
                MoneyValidation.format(s.getAmount()),
                currency,
                s.getSettlementDate(),
                s.getNote(),
                s.getStatus(),
                s.getRequestId(),
                s.getCreatedAt()
        );
    }
}
