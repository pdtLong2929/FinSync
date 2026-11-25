package com.finsync.service.impl;

import com.finsync.dto.GroupExpenseDtos.GroupExpenseSplitInput;
import com.finsync.entity.enums.CurrencyCode;
import com.finsync.entity.enums.SplitMode;
import com.finsync.exception.ApiException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Deterministic integer minor-unit cost splitter guaranteeing zero lost cents/dong across
 * EQUAL, EXACT, PERCENTAGE, and SHARES modes using largest-remainder + splitOrder tie-breaking.
 */
@Component
public class ExpenseSplitCalculator {

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final long MAX_SHARE_WEIGHT = 1_000_000L;

    public record ComputedSplit(UUID memberId, BigDecimal amount, BigDecimal inputValue, int splitOrder) {}

    public List<ComputedSplit> calculate(
            BigDecimal totalAmount,
            CurrencyCode currency,
            SplitMode splitMode,
            List<GroupExpenseSplitInput> inputs
    ) {
        if (inputs == null || inputs.isEmpty()) {
            throw ApiException.invalid("splits", "At least one participant split is required");
        }
        Set<UUID> seen = new HashSet<>();
        for (GroupExpenseSplitInput input : inputs) {
            if (input.memberId() == null || !seen.add(input.memberId())) {
                throw ApiException.invalid("splits", "Participants must be unique non-null members");
            }
        }

        long totalMinor = MoneyValidation.toMinorUnits(totalAmount, currency);
        int n = inputs.size();

        return switch (splitMode) {
            case EQUAL -> calculateEqual(totalMinor, currency, inputs);
            case EXACT -> calculateExact(totalMinor, currency, inputs);
            case PERCENTAGE -> calculatePercentage(totalMinor, currency, inputs);
            case SHARES -> calculateShares(totalMinor, currency, inputs, n);
        };
    }

    private List<ComputedSplit> calculateEqual(long totalMinor, CurrencyCode currency, List<GroupExpenseSplitInput> inputs) {
        int n = inputs.size();
        long base = totalMinor / n;
        long remainder = totalMinor % n;
        List<ComputedSplit> result = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            if (inputs.get(i).inputValue() != null) {
                throw ApiException.invalid("splits", "inputValue must be null for EQUAL split mode");
            }
            long shareMinor = base + (i < remainder ? 1L : 0L);
            result.add(new ComputedSplit(
                    inputs.get(i).memberId(),
                    MoneyValidation.fromMinorUnits(shareMinor, currency),
                    null,
                    i
            ));
        }
        return result;
    }

    private List<ComputedSplit> calculateExact(long totalMinor, CurrencyCode currency, List<GroupExpenseSplitInput> inputs) {
        long sumMinor = 0L;
        List<ComputedSplit> result = new ArrayList<>(inputs.size());
        for (int i = 0; i < inputs.size(); i++) {
            GroupExpenseSplitInput item = inputs.get(i);
            BigDecimal exact = MoneyValidation.requireNonNegativeAmount(item.inputValue(), currency, "splits[" + i + "].inputValue");
            long exactMinor = MoneyValidation.toMinorUnits(exact, currency);
            sumMinor += exactMinor;
            result.add(new ComputedSplit(item.memberId(), exact, exact, i));
        }
        if (sumMinor != totalMinor) {
            throw ApiException.invalid("splits", "Sum of exact split amounts must equal expense total");
        }
        return result;
    }

    private List<ComputedSplit> calculatePercentage(long totalMinor, CurrencyCode currency, List<GroupExpenseSplitInput> inputs) {
        int n = inputs.size();
        long[] basisPoints = new long[n];
        BigDecimal[] storedInputs = new BigDecimal[n];
        long totalBps = 0L;

        for (int i = 0; i < n; i++) {
            BigDecimal pct = inputs.get(i).inputValue();
            if (pct == null) {
                throw ApiException.invalid("splits[" + i + "].inputValue", "Percentage is required");
            }
            BigDecimal stripped = pct.stripTrailingZeros();
            if (Math.max(0, stripped.scale()) > 2 || pct.signum() < 0 || pct.compareTo(HUNDRED) > 0) {
                throw ApiException.invalid("splits[" + i + "].inputValue", "Percentage must be 0..100 with at most 2 decimals");
            }
            long bps = pct.movePointRight(2).longValueExact();
            basisPoints[i] = bps;
            storedInputs[i] = pct.setScale(2, RoundingMode.UNNECESSARY);
            totalBps += bps;
        }
        if (totalBps != 10_000L) {
            throw ApiException.invalid("splits", "Percentages must sum to exactly 100.00");
        }

        long[] allocated = allocateByWeights(totalMinor, basisPoints, 10_000L);
        List<ComputedSplit> result = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            result.add(new ComputedSplit(
                    inputs.get(i).memberId(),
                    MoneyValidation.fromMinorUnits(allocated[i], currency),
                    storedInputs[i],
                    i
            ));
        }
        return result;
    }

    private List<ComputedSplit> calculateShares(long totalMinor, CurrencyCode currency, List<GroupExpenseSplitInput> inputs, int n) {
        long[] weights = new long[n];
        BigDecimal[] storedInputs = new BigDecimal[n];
        long totalWeight = 0L;

        for (int i = 0; i < n; i++) {
            BigDecimal rawWeight = inputs.get(i).inputValue();
            if (rawWeight == null) {
                throw ApiException.invalid("splits[" + i + "].inputValue", "Share weight is required");
            }
            BigDecimal stripped = rawWeight.stripTrailingZeros();
            if (Math.max(0, stripped.scale()) > 0) {
                throw ApiException.invalid("splits[" + i + "].inputValue", "Share weight must be an integer");
            }
            long w = stripped.longValueExact();
            if (w < 1L || w > MAX_SHARE_WEIGHT) {
                throw ApiException.invalid("splits[" + i + "].inputValue", "Share weight must be between 1 and 1,000,000");
            }
            weights[i] = w;
            storedInputs[i] = BigDecimal.valueOf(w).setScale(2, RoundingMode.UNNECESSARY);
            totalWeight += w;
        }
        if (totalWeight <= 0L) {
            throw ApiException.invalid("splits", "Total share weights must be positive");
        }

        long[] allocated = allocateByWeights(totalMinor, weights, totalWeight);
        List<ComputedSplit> result = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            result.add(new ComputedSplit(
                    inputs.get(i).memberId(),
                    MoneyValidation.fromMinorUnits(allocated[i], currency),
                    storedInputs[i],
                    i
            ));
        }
        return result;
    }

    private long[] allocateByWeights(long totalMinor, long[] weights, long totalWeight) {
        int n = weights.length;
        long[] shares = new long[n];
        long[] remainders = new long[n];
        BigInteger totalBig = BigInteger.valueOf(totalMinor);
        BigInteger denomBig = BigInteger.valueOf(totalWeight);
        long distributed = 0L;

        for (int i = 0; i < n; i++) {
            BigInteger[] divRem = totalBig.multiply(BigInteger.valueOf(weights[i])).divideAndRemainder(denomBig);
            shares[i] = divRem[0].longValueExact();
            remainders[i] = divRem[1].longValueExact();
            distributed += shares[i];
        }

        long left = totalMinor - distributed;
        if (left > 0) {
            List<Integer> order = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                order.add(i);
            }
            order.sort(Comparator.<Integer>comparingLong(i -> remainders[i]).reversed().thenComparingInt(i -> i));
            for (int k = 0; k < left; k++) {
                shares[order.get(k)]++;
            }
        }
        return shares;
    }
}
