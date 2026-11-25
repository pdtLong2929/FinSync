package com.finsync;

import com.finsync.entity.enums.CurrencyCode;
import com.finsync.service.impl.DebtSimplificationEngine;
import com.finsync.service.impl.DebtSimplificationEngine.MemberNet;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DebtSimplificationEngineTests {

    private final DebtSimplificationEngine engine = new DebtSimplificationEngine();

    @Test
    void circularFiveMemberGraphAchievesOverThirtyPercentReductionAndConservesBalances() {
        List<UUID> ids = List.of(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()
        );
        // Equivalent to 5 raw directed edges around a 5-member ring leaving only M0 (+40k) and M4 (-40k)
        List<MemberNet> nets = List.of(
                new MemberNet(ids.get(0), "M1", 0, 40_000L),
                new MemberNet(ids.get(1), "M2", 1, 0L),
                new MemberNet(ids.get(2), "M3", 2, 0L),
                new MemberNet(ids.get(3), "M4", 3, 0L),
                new MemberNet(ids.get(4), "M5", 4, -40_000L)
        );

        var transfers = engine.simplify(nets, CurrencyCode.VND);
        assertEquals(1, transfers.size());
        assertEquals(ids.get(4), transfers.get(0).fromMemberId());
        assertEquals(ids.get(0), transfers.get(0).toMemberId());
        assertEquals(new BigDecimal("40000.00"), transfers.get(0).amount());
    }

    @Test
    void fiftyMemberCircularChainExecutesUnderFiveMillisecondsAndConservesNet() {
        int n = 50;
        List<MemberNet> nets = new ArrayList<>(n);
        Map<UUID, Long> expected = new HashMap<>();
        long sum = 0L;
        for (int i = 0; i < n; i++) {
            UUID id = UUID.randomUUID();
            long net = (i % 2 == 0) ? 10_000L * (i + 1) : -10_000L * i;
            if (i == n - 1) {
                net = -sum;
            }
            sum += net;
            nets.add(new MemberNet(id, "User-" + i, i, net));
            expected.put(id, net);
        }

        // Warm up JIT once before measuring target SC-G02 latency (< 5ms)
        engine.simplify(nets, CurrencyCode.VND);

        long startNs = System.nanoTime();
        var transfers = engine.simplify(nets, CurrencyCode.VND);
        long elapsedNs = System.nanoTime() - startNs;

        assertTrue(elapsedNs < 5_000_000L, "Expected < 5ms for 50 members, took " + (elapsedNs / 1_000_000.0) + "ms");
        assertTrue(transfers.size() <= n - 1);

        Map<UUID, Long> applied = new HashMap<>();
        for (var t : transfers) {
            long minor = t.amount().longValueExact();
            applied.merge(t.fromMemberId(), -minor, Long::sum);
            applied.merge(t.toMemberId(), minor, Long::sum);
        }
        for (var entry : expected.entrySet()) {
            assertEquals(entry.getValue(), applied.getOrDefault(entry.getKey(), 0L));
        }
    }
}
