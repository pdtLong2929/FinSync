package com.finsync;

import com.finsync.repository.UserRepository;
import com.finsync.security.JwtTokens;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountApiTests {
    @Value("${local.server.port}") int port;
    @Value("${jwt.secret}") String secret;
    @Autowired ObjectMapper json;
    @Autowired JdbcTemplate jdbc;
    @Autowired UserRepository users;
    @Autowired PasswordEncoder passwords;
    @Autowired JwtTokens jwt;
    @MockitoSpyBean StringRedisTemplate redis;
    private final HttpClient http = HttpClient.newHttpClient();
    private static final String PASSWORD = "Demo-Password-123";
    record Reply(int status, JsonNode body, HttpHeaders headers) {}
    record Account(String email, UUID id, String access, String refresh) {}

    private Reply call(String method, String path, Object payload, String access) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/v1" + path))
                .header("Content-Type", "application/json");
        if (access != null) builder.header("Authorization", "Bearer " + access);
        var publisher = payload == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(json.writeValueAsString(payload));
        var response = http.send(builder.method(method, publisher).build(), HttpResponse.BodyHandlers.ofString());
        return new Reply(response.statusCode(), response.body().isBlank() ? null : json.readTree(response.body()), response.headers());
    }
    private Account account() throws Exception {
        String email = UUID.randomUUID() + "@example.com";
        var created = call("POST", "/auth/register", Map.of("email", email, "password", PASSWORD, "displayName", "Demo"), null);
        assertEquals(201, created.status(), created.toString());
        var login = call("POST", "/auth/login", Map.of("email", email, "password", PASSWORD), null);
        assertEquals(200, login.status(), login.toString());
        return new Account(email, UUID.fromString(created.body().get("id").stringValue()),
                login.body().get("accessToken").stringValue(), login.body().get("refreshToken").stringValue());
    }
    private void error(Reply reply, int status, String code) {
        assertEquals(status, reply.status(), reply.toString());
        assertEquals(code, reply.body().get("code").stringValue());
        assertTrue(reply.body().has("fieldErrors"));
        assertTrue(reply.body().has("traceId"));
    }

    @Test void registrationValidationNormalizationAndHashing() throws Exception {
        var a = account();
        var saved = users.findByEmail(a.email()).orElseThrow();
        assertNotEquals(PASSWORD, saved.getPasswordHash());
        assertTrue(passwords.matches(PASSWORD, saved.getPasswordHash()));
        assertEquals("USER", saved.getRole().name());
        error(call("POST", "/auth/register", Map.of("email", "  " + a.email().toUpperCase(Locale.ROOT) + "  ",
                "password", PASSWORD, "displayName", "Demo"), null), 409, "EMAIL_ALREADY_EXISTS");
        error(call("POST", "/auth/register", Map.of("email", "bad", "password", PASSWORD, "displayName", "Demo"), null), 400, "VALIDATION_ERROR");
        error(call("POST", "/auth/register", Map.of("email", "x@example.com", "password", "\u00e9".repeat(37), "displayName", "Demo"), null), 400, "VALIDATION_ERROR");
        error(call("POST", "/auth/register", Map.of("email", "x@example.com", "password", PASSWORD,
                "displayName", "Demo", "role", "ADMIN"), null), 400, "VALIDATION_ERROR");
        error(call("POST", "/auth/login", Map.of("email", a.email(), "password", "wrong"), null), 401, "INVALID_CREDENTIALS");
    }

    @Test void profileOwnershipPartialUpdateAndVersionConflict() throws Exception {
        var a = account(); var other = account();
        var before = call("GET", "/users/me", null, a.access());
        assertEquals(200, before.status());
        long version = before.body().get("version").longValue();
        var updated = call("PATCH", "/users/me", Map.of("version", version, "displayName", " Updated ", "phoneNumber", "+84 123 456"), a.access());
        assertEquals(200, updated.status(), updated.toString());
        assertEquals("Updated", updated.body().get("displayName").stringValue());
        assertTrue(updated.body().get("version").longValue() > version);
        assertEquals("Updated", users.findById(a.id()).orElseThrow().getDisplayName());
        error(call("PATCH", "/users/me", Map.of("version", version, "displayName", "Stale"), a.access()), 409, "PROFILE_VERSION_CONFLICT");
        error(call("PATCH", "/users/me", Map.of("version", version, "userId", other.id(), "role", "ADMIN"), a.access()), 400, "VALIDATION_ERROR");
        var patch = new HashMap<String, Object>(); patch.put("version", updated.body().get("version").longValue()); patch.put("phoneNumber", null);
        var cleared = call("PATCH", "/users/me", patch, a.access());
        assertEquals(200, cleared.status()); assertTrue(cleared.body().get("phoneNumber").isNull());
        assertEquals("Updated", cleared.body().get("displayName").stringValue());
        assertEquals("Demo", call("GET", "/users/me", null, other.access()).body().get("displayName").stringValue());
        assertFalse(before.body().has("passwordHash"));
        error(call("GET", "/users/me", null, null), 401, "UNAUTHORIZED");
    }

    @Test void refreshRotatesWithoutExtendingSessionAndLogoutRevokesOnlyCurrentSession() throws Exception {
        var a = account();
        var login2 = call("POST", "/auth/login", Map.of("email", a.email(), "password", PASSWORD), null);
        var refreshed = call("POST", "/auth/refresh", Map.of("refreshToken", a.refresh()), null);
        assertEquals(200, refreshed.status(), refreshed.toString());
        assertEquals("no-store", refreshed.headers().firstValue("Cache-Control").orElseThrow());
        String refresh2 = refreshed.body().get("refreshToken").stringValue();
        assertNotEquals(a.refresh(), refresh2);
        error(call("POST", "/auth/refresh", Map.of("refreshToken", a.refresh()), null), 401, "INVALID_REFRESH_TOKEN");
        var again = call("POST", "/auth/refresh", Map.of("refreshToken", refresh2), null);
        assertEquals(refreshed.body().get("refreshExpiresAt"), again.body().get("refreshExpiresAt"));
        var sid = jwt.verify(a.access()).sessionId();
        String hash = jdbc.queryForObject("select refresh_token_hash from auth_sessions where id = ?", String.class, sid);
        assertEquals(64, hash.length()); assertNotEquals(again.body().get("refreshToken").stringValue(), hash);
        assertEquals(204, call("POST", "/auth/logout", null, a.access()).status());
        error(call("GET", "/users/me", null, again.body().get("accessToken").stringValue()), 401, "UNAUTHORIZED");
        error(call("POST", "/auth/refresh", Map.of("refreshToken", again.body().get("refreshToken").stringValue()), null), 401, "INVALID_REFRESH_TOKEN");
        assertEquals(200, call("GET", "/users/me", null, login2.body().get("accessToken").stringValue()).status());
    }

    @Test void concurrentRefreshAcceptsExactlyOneRequest() throws Exception {
        var a = account();
        try (var pool = Executors.newFixedThreadPool(2)) {
            var gate = new CountDownLatch(1);
            Callable<Integer> action = () -> { gate.await(); return call("POST", "/auth/refresh", Map.of("refreshToken", a.refresh()), null).status(); };
            var one = pool.submit(action); var two = pool.submit(action); gate.countDown();
            var results = new ArrayList<>(List.of(one.get(20, TimeUnit.SECONDS), two.get(20, TimeUnit.SECONDS)));
            Collections.sort(results); assertEquals(List.of(200, 401), results);
        }
    }

    @Test void concurrentRegistrationCreatesOneAccount() throws Exception {
        String email = UUID.randomUUID() + "@example.com";
        try (var pool = Executors.newFixedThreadPool(2)) {
            var gate = new CountDownLatch(1);
            Callable<Integer> action = () -> { gate.await(); return call("POST", "/auth/register",
                    Map.of("email", email, "displayName", "Demo", "password", PASSWORD), null).status(); };
            var one = pool.submit(action); var two = pool.submit(action); gate.countDown();
            var results = new ArrayList<>(List.of(one.get(20, TimeUnit.SECONDS), two.get(20, TimeUnit.SECONDS)));
            Collections.sort(results); assertEquals(List.of(201, 409), results);
            assertEquals(1, jdbc.queryForObject("select count(*) from users where email = ?", Integer.class, email));
        }
    }

    @Test void lockedAndExpiredSessionsCannotAuthenticateOrRefresh() throws Exception {
        var a = account();
        jdbc.update("update users set status='LOCKED', locked_at=now(), lock_reason='test' where id=?", a.id());
        error(call("GET", "/users/me", null, a.access()), 403, "ACCOUNT_LOCKED");
        error(call("POST", "/auth/login", Map.of("email", a.email(), "password", PASSWORD), null), 403, "ACCOUNT_LOCKED");
        error(call("POST", "/auth/refresh", Map.of("refreshToken", a.refresh()), null), 403, "ACCOUNT_LOCKED");
        var b = account();
        jdbc.update("update auth_sessions set created_at=now()-interval '2 days', expires_at=now()-interval '1 day' where id=?", jwt.verify(b.access()).sessionId());
        error(call("GET", "/users/me", null, b.access()), 401, "UNAUTHORIZED");
        error(call("POST", "/auth/refresh", Map.of("refreshToken", b.refresh()), null), 401, "INVALID_REFRESH_TOKEN");
    }

    @Test void rejectsExpiredForgedWrongAudienceAndBlacklistedJwt() throws Exception {
        var a = account();
        var original = SignedJWT.parse(a.access()).getJWTClaimsSet();
        var expired = new JWTClaimsSet.Builder(original).issueTime(Date.from(Instant.now().minusSeconds(120)))
                .expirationTime(Date.from(Instant.now().minusSeconds(60))).build();
        error(call("GET", "/users/me", null, signed(expired, secret)), 401, "UNAUTHORIZED");
        error(call("GET", "/users/me", null, signed(new JWTClaimsSet.Builder(original).audience("another-app").build(), secret)), 401, "UNAUTHORIZED");
        error(call("GET", "/users/me", null, signed(new JWTClaimsSet.Builder(original).issuer("another-issuer").build(), secret)), 401, "UNAUTHORIZED");
        error(call("GET", "/users/me", null, signed(original, "different-test-key-32-bytes-long-123456")), 401, "UNAUTHORIZED");
        redis.opsForValue().set("auth:blacklist:" + original.getJWTID(), "1", java.time.Duration.ofMinutes(1));
        error(call("GET", "/users/me", null, a.access()), 401, "UNAUTHORIZED");
    }
    private String signed(JWTClaimsSet claims, String key) throws Exception {
        var token = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build(), claims);
        token.sign(new MACSigner(key.getBytes(StandardCharsets.UTF_8))); return token.serialize();
    }

    @Test void redisFailureFailsClosedAndLogoutRevocationSurvivesWriteFailure() throws Exception {
        var a = account();
        doThrow(new RedisConnectionFailureException("simulated outage")).when(redis).hasKey(anyString());
        try { error(call("GET", "/users/me", null, a.access()), 503, "AUTH_SERVICE_UNAVAILABLE"); }
        finally { reset(redis); }
        // Force failure after the security filter's successful read, during logout blacklist write.
        doThrow(new RedisConnectionFailureException("simulated write outage")).when(redis).opsForValue();
        try { error(call("POST", "/auth/logout", null, a.access()), 503, "AUTH_SERVICE_UNAVAILABLE"); }
        finally { reset(redis); }
        assertNotNull(jdbc.queryForObject("select revoked_at from auth_sessions where id=?", java.sql.Timestamp.class, jwt.verify(a.access()).sessionId()));
        error(call("POST", "/auth/refresh", Map.of("refreshToken", a.refresh()), null), 401, "INVALID_REFRESH_TOKEN");
    }
}
