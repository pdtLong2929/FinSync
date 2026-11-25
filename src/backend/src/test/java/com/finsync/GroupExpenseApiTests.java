package com.finsync;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
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
class GroupExpenseApiTests {

    @Value("${local.server.port}") int port;
    @Autowired ObjectMapper json;
    @Autowired JdbcTemplate jdbc;

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
        assertEquals(201, created.status(), created.toString());
        var login = call("POST", "/auth/login",
                Map.of("email", email, "password", PASSWORD), null);
        assertEquals(200, login.status(), login.toString());
        return new Account(UUID.fromString(created.body().get("id").stringValue()),
                login.body().get("accessToken").stringValue());
    }

    @Test
    void groupLifecycleGuestClaimFundAndFourSplitModesWorkEndToEnd() throws Exception {
        Account alice = account("Alice");
        Account bob = account("Bob");

        // 1. Create group atomically with owner, guest Bob, and shared wallet
        Reply createdGroup = call("POST", "/groups", Map.of(
                "name", "Da Lat Trip",
                "description", "Group expenses",
                "currencyCode", "VND",
                "guestMemberNames", List.of("Guest Bob", "Guest Charlie")
        ), alice.accessToken());
        assertEquals(201, createdGroup.status(), createdGroup.toString());
        String groupId = createdGroup.body().get("id").stringValue();
        assertEquals("0.00", createdGroup.body().get("fundBalance").stringValue());
        assertEquals(3, createdGroup.body().get("members").size());
        String aliceMemberId = createdGroup.body().get("members").get(0).get("id").stringValue();
        String guestBobId = createdGroup.body().get("members").get(1).get("id").stringValue();
        String charlieId = createdGroup.body().get("members").get(2).get("id").stringValue();
        assertTrue(createdGroup.body().get("members").get(1).get("guest").booleanValue());

        // 2. Targeted single-use guest invitation claimed by Bob
        Reply invite = call("POST", "/groups/" + groupId + "/invitations",
                Map.of("targetMemberId", guestBobId, "maxUses", 1, "expiresInHours", 24), alice.accessToken());
        assertEquals(201, invite.status(), invite.toString());
        String token = invite.body().get("token").stringValue();

        Reply accepted = call("POST", "/groups/invitations/accept", Map.of("token", token), bob.accessToken());
        assertEquals(200, accepted.status(), accepted.toString());
        assertFalse(accepted.body().get("members").get(1).get("guest").booleanValue());
        assertEquals(bob.userId().toString(), accepted.body().get("members").get(1).get("userId").stringValue());
        assertEquals(409, call("POST", "/groups/invitations/accept", Map.of("token", token), account("Dave").accessToken()).status());

        // 3. Shared group fund contribution + insufficient refund rejection
        UUID contribReq = UUID.randomUUID();
        Reply contrib = call("POST", "/groups/" + groupId + "/fund/entries", Map.of(
                "requestId", contribReq.toString(),
                "type", "CONTRIBUTION",
                "memberId", guestBobId,
                "amount", 500000,
                "entryDate", "2026-11-25",
                "note", "Initial pool"
        ), alice.accessToken());
        assertEquals(201, contrib.status(), contrib.toString());
        assertEquals(422, call("POST", "/groups/" + groupId + "/fund/entries", Map.of(
                "requestId", UUID.randomUUID().toString(),
                "type", "REFUND",
                "memberId", guestBobId,
                "amount", 600000,
                "entryDate", "2026-11-25"
        ), alice.accessToken()).status());

        // 4. MEMBERS EQUAL split on 100,000 VND + idempotent replay + conflict detection
        UUID expReq = UUID.randomUUID();
        var expensePayload = Map.of(
                "requestId", expReq.toString(),
                "categoryId", FOOD_CATEGORY_ID,
                "paidByMemberId", aliceMemberId,
                "title", "Hotpot Dinner",
                "amount", 100000,
                "expenseDate", "2026-11-25",
                "fundingSource", "MEMBERS",
                "splitMode", "EQUAL",
                "splits", List.of(
                        Map.of("memberId", aliceMemberId),
                        Map.of("memberId", guestBobId),
                        Map.of("memberId", charlieId)
                )
        );
        Reply exp1 = call("POST", "/groups/" + groupId + "/expenses", expensePayload, alice.accessToken());
        assertEquals(201, exp1.status(), exp1.toString());
        assertEquals("33334.00", exp1.body().get("splits").get(0).get("amount").stringValue());
        assertEquals("33333.00", exp1.body().get("splits").get(1).get("amount").stringValue());
        assertEquals("33333.00", exp1.body().get("splits").get(2).get("amount").stringValue());

        // Idempotent replay returns same ID
        Reply replay = call("POST", "/groups/" + groupId + "/expenses", expensePayload, alice.accessToken());
        assertEquals(201, replay.status());
        assertEquals(exp1.body().get("id").stringValue(), replay.body().get("id").stringValue());

        // Same requestId with different amount returns 409 conflict
        var conflictingPayload = Map.of(
                "requestId", expReq.toString(),
                "categoryId", FOOD_CATEGORY_ID,
                "paidByMemberId", aliceMemberId,
                "title", "Hotpot Dinner",
                "amount", 120000,
                "expenseDate", "2026-11-25",
                "fundingSource", "MEMBERS",
                "splitMode", "EQUAL",
                "splits", List.of(
                        Map.of("memberId", aliceMemberId),
                        Map.of("memberId", guestBobId),
                        Map.of("memberId", charlieId)
                )
        );
        assertEquals(409, call("POST", "/groups/" + groupId + "/expenses", conflictingPayload, alice.accessToken()).status());

        // 5. GROUP_FUND expense deducts from shared fund atomically
        Reply fundExp = call("POST", "/groups/" + groupId + "/expenses", Map.of(
                "requestId", UUID.randomUUID().toString(),
                "categoryId", FOOD_CATEGORY_ID,
                "title", "Taxi from pool",
                "amount", 200000,
                "expenseDate", "2026-11-25",
                "fundingSource", "GROUP_FUND",
                "splitMode", "SHARES",
                "splits", List.of(
                        Map.of("memberId", aliceMemberId, "inputValue", 1),
                        Map.of("memberId", guestBobId, "inputValue", 1)
                )
        ), bob.accessToken());
        assertEquals(201, fundExp.status(), fundExp.toString());

        Reply fundOverview = call("GET", "/groups/" + groupId + "/fund", null, bob.accessToken());
        assertEquals(200, fundOverview.status());
        assertEquals("300000.00", fundOverview.body().get("balance").stringValue());

        // 6. Archived group blocks mutations
        assertEquals(200, call("POST", "/groups/" + groupId + "/archive", null, alice.accessToken()).status());
        assertEquals(409, call("POST", "/groups/" + groupId + "/members",
                Map.of("displayName", "Blocked Guest"), alice.accessToken()).status());

        // Verify categories endpoint seeded from V3
        Reply cats = call("GET", "/categories?type=EXPENSE", null, alice.accessToken());
        assertEquals(200, cats.status());
        assertTrue(cats.body().size() >= 8);
    }
}
