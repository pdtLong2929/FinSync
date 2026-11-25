package com.finsync.service.impl;

import com.finsync.entity.enums.CurrencyCode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.UUID;

/**
 * Greedy two-heap cash-flow minimization engine running in O(V log V) time.
 * Matches the largest creditor with the largest debtor to minimize external settlement transfers.
 */
@Component
public class DebtSimplificationEngine {

    public record MemberNet(UUID memberId, String displayName, int memberOrder, long netMinor) {}

    public record TransferPlanItem(UUID fromMemberId, String fromDisplayName,
                                   UUID toMemberId, String toDisplayName, BigDecimal amount) {}

    private static final class HeapNode {
        final UUID memberId;
        final String displayName;
        final int memberOrder;
        long remainingMinor;

        HeapNode(UUID memberId, String displayName, int memberOrder, long remainingMinor) {
            this.memberId = memberId;
            this.displayName = displayName;
            this.memberOrder = memberOrder;
            this.remainingMinor = remainingMinor;
        }
    }

    public List<TransferPlanItem> simplify(List<MemberNet> balances, CurrencyCode currency) {
        Comparator<HeapNode> heapOrder = Comparator
                .<HeapNode>comparingLong(n -> n.remainingMinor).reversed()
                .thenComparingInt(n -> n.memberOrder)
                .thenComparing(n -> n.memberId);

        PriorityQueue<HeapNode> creditors = new PriorityQueue<>(heapOrder);
        PriorityQueue<HeapNode> debtors = new PriorityQueue<>(heapOrder);

        long checksum = 0L;
        for (MemberNet entry : balances) {
            checksum += entry.netMinor();
            if (entry.netMinor() > 0L) {
                creditors.add(new HeapNode(entry.memberId(), entry.displayName(), entry.memberOrder(), entry.netMinor()));
            } else if (entry.netMinor() < 0L) {
                debtors.add(new HeapNode(entry.memberId(), entry.displayName(), entry.memberOrder(), -entry.netMinor()));
            }
        }
        if (checksum != 0L) {
            throw new IllegalStateException("Group net balance conservation invariant violated: sum=" + checksum);
        }

        List<TransferPlanItem> transfers = new ArrayList<>();
        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            HeapNode creditor = creditors.poll();
            HeapNode debtor = debtors.poll();
            long settled = Math.min(creditor.remainingMinor, debtor.remainingMinor);

            transfers.add(new TransferPlanItem(
                    debtor.memberId,
                    debtor.displayName,
                    creditor.memberId,
                    creditor.displayName,
                    MoneyValidation.fromMinorUnits(settled, currency)
            ));

            creditor.remainingMinor -= settled;
            debtor.remainingMinor -= settled;
            if (creditor.remainingMinor > 0L) {
                creditors.add(creditor);
            }
            if (debtor.remainingMinor > 0L) {
                debtors.add(debtor);
            }
        }
        return transfers;
    }
}
