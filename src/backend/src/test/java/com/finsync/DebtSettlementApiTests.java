package com.finsync;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DebtSettlementApiTests {

    @Value("${local.server.port}") int port;
    @Autowired ObjectMapper json;

    private final HttpClient http = HttpClient.newHttpClient();
    private static final String PASSWORD = "Demo-Password-123";
    private static final String FOOD_CATEGORY_ID = "11111111-1111-4111-8111-111111111101";

    record Reply(int status, JsonNode body) {}
    record Account(UUID userId, String accessToken) {}

    private Reply call(String method, String path, Object payload, String access) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/v1" + path))
                .header("Content-Type", "application/json");
        if (access != null) {
            builder.header("Authorization", "Bearer " + access);
        }
        var body = payload == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(json.writeValueAsString(payload));
        var res = http.send(builder.method(method, body).build(), HttpResponse.BodyHandlers.ofString());
        return new Reply(res.statusCode(), res.body().isBlank() ? null : json.readTree(res.body()));
    }

    private Account account(String displayName) throws Exception {
        String email = UUID.randomUUID() + "@example.com";
        var created = call("POST", "/auth/register",
                Map.of("email", email, "password", PASSWORD, "displayName", displayName), null);
        assertEquals(201, created.status());
        var login = call("POST", "/auth/login",
                Map.of("email", email, "password", PASSWORD), null);
        assertEquals(200, login.status());
        return new Account(UUID.fromString(created.body().get("id").stringValue()),
                login.body().get("accessToken").stringValue());
    }

    @Test
    void circularDebtsSimplifyAndSettlePartiallyAndFullyWithGuardrails() throws Exception {
        Account owner = account("M1-Owner");
        Account m2Acc = account("M2");
        Account m3Acc = account("M3");

        Reply groupRes = call("POST", "/groups", Map.of(
                "name", "Circular Debt Group",
                "currencyCode", "VND",
                "guestMemberNames", List.of("M4", "M5")
        ), owner.accessToken());
        assertEquals(201, groupRes.status());
        String groupId = groupRes.body().get("id").stringValue();

        // Invite M2 & M3 via general link
        Reply inv = call("POST", "/groups/" + groupId + "/invitations",
                Map.of("maxUses", 5, "expiresInHours", 24), owner.accessToken());
        String inviteToken = inv.body().get("token").stringValue();
        call("POST", "/groups/invitations/accept", Map.of("token", inviteToken), m2Acc.accessToken());
        Reply afterM3 = call("POST", "/groups/invitations/accept", Map.of("token", inviteToken), m3Acc.accessToken());

        var members = afterM3.body().get("members");
        String m1 = members.get(0).get("id").stringValue();
        String m4 = members.get(1).get("id").stringValue();
        String m5 = members.get(2).get("id").stringValue();
        String m2 = members.get(3).get("id").stringValue();
        String m3 = members.get(4).get("id").stringValue();

        // Circular chain: M1 -> M2 (100k), M2 -> M3 (100k), M3 -> M4 (100k), M4 -> M5 (100k), M5 -> M1 (60k)
        record Bill(String payer, String debtor, int amount) {}
        for (Bill b : List.of(
                new Bill(m1, m2, 100000),
                new Bill(m2, m3, 100000),
                new Bill(m3, m4, 100000),
                new Bill(m4, m5, 100000),
                new Bill(m5, m1, 60000)
        )) {
            Reply exp = call("POST", "/groups/" + groupId + "/expenses", Map.of(
                    "requestId", UUID.randomUUID().toString(),
                    "categoryId", FOOD_CATEGORY_ID,
                    "paidByMemberId", b.payer(),
                    "title", "Chain bill",
                    "amount", b.amount(),
                    "expenseDate", "2026-11-25",
                    "fundingSource", "MEMBERS",
                    "splitMode", "EXACT",
                    "splits", List.of(Map.of("memberId", b.debtor(), "inputValue", b.amount()))
            ), owner.accessToken());
            assertEquals(201, exp.status(), exp.toString());
        }

        // Verify simplified plan collapses 5 pairwise edges into 1 transfer (80% reduction)
        Reply debts = call("GET", "/groups/" + groupId + "/debts", null, m2Acc.accessToken());
        assertEquals(200, debts.status(), debts.toString());
        assertEquals(5, debts.body().get("rawPairwiseTransferCount").asInt());
        assertEquals(1, debts.body().get("simplifiedTransferCount").asInt());
        assertEquals(80.0, debts.body().get("reductionPercentage").asDouble());
        var transfer = debts.body().get("simplifiedTransfers").get(0);
        assertEquals(m5, transfer.get("fromMemberId").stringValue());
        assertEquals(m1, transfer.get("toMemberId").stringValue());
        assertEquals("40000.00", transfer.get("amount").stringValue());

        // Unrelated member M3 cannot record a settlement between M5 and M1
        assertEquals(403, call("POST", "/groups/" + groupId + "/settlements", Map.of(
                "requestId", UUID.randomUUID().toString(),
                "payerMemberId", m5,
                "receiverMemberId", m1,
                "amount", 20000,
                "settlementDate", "2026-11-25"
        ), m3Acc.accessToken()).status());

        // Overpayment rejected with 422
        assertEquals(422, call("POST", "/groups/" + groupId + "/settlements", Map.of(
                "requestId", UUID.randomUUID().toString(),
                "payerMemberId", m5,
                "receiverMemberId", m1,
                "amount", 50000,
                "settlementDate", "2026-11-25"
        ), owner.accessToken()).status());

        // Partial settlement 25,000 VND
        Reply partial = call("POST", "/groups/" + groupId + "/settlements", Map.of(
                "requestId", UUID.randomUUID().toString(),
                "payerMemberId", m5,
                "receiverMemberId", m1,
                "amount", 25000,
                "settlementDate", "2026-11-25",
                "note", "Bank transfer part 1"
        ), owner.accessToken());
        assertEquals(201, partial.status(), partial.toString());
        assertEquals("CONFIRMED", partial.body().get("status").stringValue());

        // Cannot remove M5 while owing 15,000 VND
        assertEquals(409, call("DELETE", "/groups/" + groupId + "/members/" + m5, null, owner.accessToken()).status());

        // Final settlement 15,000 VND brings all balances to zero
        assertEquals(201, call("POST", "/groups/" + groupId + "/settlements", Map.of(
                "requestId", UUID.randomUUID().toString(),
                "payerMemberId", m5,
                "receiverMemberId", m1,
                "amount", 15000,
                "settlementDate", "2026-11-25"
        ), owner.accessToken()).status());

        Reply cleared = call("GET", "/groups/" + groupId + "/debts", null, owner.accessToken());
        assertEquals(0, cleared.body().get("simplifiedTransfers").size());

        // Now M5 can be marked LEFT cleanly
        Reply removed = call("DELETE", "/groups/" + groupId + "/members/" + m5, null, owner.accessToken());
        assertEquals(200, removed.status());
        assertEquals("LEFT", removed.body().get("status").stringValue());
    }
}
