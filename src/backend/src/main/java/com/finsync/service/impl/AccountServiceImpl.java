package com.finsync.service.impl;

import com.finsync.dto.AccountDtos.*;
import com.finsync.entity.*;
import com.finsync.entity.enums.UserStatus;
import com.finsync.exception.ApiException;
import com.finsync.repository.*;
import com.finsync.security.*;
import com.finsync.service.AccountService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.databind.JsonNode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class AccountServiceImpl implements AccountService {
    private final UserRepository users;
    private final AuthSessionRepository sessions;
    private final PasswordEncoder passwords;
    private final JwtTokens tokens;
    private final TokenBlacklist blacklist;
    private final TransactionTemplate tx;
    private final SecureRandom random = new SecureRandom();
    private final String dummyHash;

    public AccountServiceImpl(UserRepository users, AuthSessionRepository sessions, PasswordEncoder passwords,
                              JwtTokens tokens, TokenBlacklist blacklist, PlatformTransactionManager manager) {
        this.users = users; this.sessions = sessions; this.passwords = passwords;
        this.tokens = tokens; this.blacklist = blacklist; this.tx = new TransactionTemplate(manager);
        this.dummyHash = passwords.encode(UUID.randomUUID().toString());
    }

    @Override public RegisteredUser register(RegisterRequest request) {
        String email = email(request.email());
        String name = name(request.displayName());
        String password = request.password();
        if (password == null || password.length() < 8 || password.getBytes(StandardCharsets.UTF_8).length > 72)
            throw ApiException.invalid("password", "Use at least 8 characters and at most 72 UTF-8 bytes");
        String encoded = passwords.encode(password);
        return tx.execute(status -> {
            if (users.existsByEmail(email)) throw new ApiException(409, "EMAIL_ALREADY_EXISTS", "Email is already registered");
            User user = new User();
            user.setEmail(email); user.setDisplayName(name); user.setPasswordHash(encoded);
            users.saveAndFlush(user);
            return new RegisteredUser(user.getId(), user.getEmail(), user.getDisplayName());
        });
    }

    @Override public TokenPair login(LoginRequest request) {
        String email = email(request.email());
        String password = request.password();
        if (password == null || password.isEmpty() || password.getBytes(StandardCharsets.UTF_8).length > 72)
            throw invalidCredentials();
        if (request.deviceLabel() != null && request.deviceLabel().length() > 200)
            throw ApiException.invalid("deviceLabel", "Maximum length is 200");
        return tx.execute(status -> {
            // Project only the ID before locking to avoid stale managed account state.
            var id = users.findIdByEmail(email);
            if (id.isEmpty()) { passwords.matches(password, dummyHash); throw invalidCredentials(); }
            User user = users.findByIdForUpdate(id.get()).orElseThrow(this::invalidCredentials);
            if (!passwords.matches(password, user.getPasswordHash())) throw invalidCredentials();
            requireActive(user);
            AuthSession session = new AuthSession();
            String refresh = newRefresh();
            session.setUser(user); session.setRefreshTokenHash(hash(refresh));
            session.setDeviceLabel(request.deviceLabel());
            session.setExpiresAt(Instant.now().plus(Boolean.TRUE.equals(request.rememberMe()) ? 30 : 7, ChronoUnit.DAYS));
            sessions.saveAndFlush(session);
            return pair(session, refresh);
        });
    }

    @Override public TokenPair refresh(RefreshRequest request) {
        if (request.refreshToken() == null || !request.refreshToken().matches("[A-Za-z0-9_-]{43}")) throw invalidRefresh();
        String oldHash = hash(request.refreshToken());
        return tx.execute(status -> {
            var identity = sessions.findIdentityByHash(oldHash).orElseThrow(this::invalidRefresh);
            User user = users.findByIdForUpdate(identity.getUserId()).orElseThrow(this::invalidRefresh);
            AuthSession session = sessions.findByIdForUpdate(identity.getId()).orElseThrow(this::invalidRefresh);
            if (!oldHash.equals(session.getRefreshTokenHash()) || session.getRevokedAt() != null
                    || !session.getExpiresAt().isAfter(Instant.now().plusSeconds(1))) throw invalidRefresh();
            requireActive(user);
            String refresh = newRefresh();
            session.setRefreshTokenHash(hash(refresh)); session.setLastUsedAt(Instant.now());
            sessions.flush();
            return pair(session, refresh);
        });
    }

    @Override public void logout(SessionPrincipal principal) {
        tx.executeWithoutResult(status -> {
            lockedUser(principal);
            AuthSession session = sessions.findByIdForUpdate(principal.sessionId()).orElseThrow(ApiException::unauthorized);
            session.setRevokedAt(Instant.now());
            sessions.flush();
        });
        // Commit PostgreSQL revocation first. Redis failure must never resurrect the session.
        blacklist.revoke(principal);
    }

    @Override public UserProfile profile(SessionPrincipal principal) {
        return tx.execute(status -> UserProfile.from(lockedUser(principal)));
    }

    @Override public UserProfile updateProfile(SessionPrincipal principal, JsonNode patch) {
        if (!patch.isObject()) throw ApiException.invalid("body", "Expected a JSON object");
        for (String field : patch.propertyNames()) {
            if (!Set.of("version", "displayName", "phoneNumber").contains(field))
                throw ApiException.invalid(field, "Field is not allowed");
        }
        JsonNode version = patch.get("version");
        if (version == null || !version.isIntegralNumber() || !version.canConvertToLong() || version.longValue() < 0)
            throw ApiException.invalid("version", "A non-negative integer is required");
        boolean hasName = patch.has("displayName"), hasPhone = patch.has("phoneNumber");
        if (!hasName && !hasPhone) throw ApiException.invalid("body", "Provide a profile field to update");
        String displayName = hasName ? name(text(patch.get("displayName"), "displayName", false)) : null;
        String phone = hasPhone ? phone(text(patch.get("phoneNumber"), "phoneNumber", true)) : null;
        return tx.execute(status -> {
            User user = lockedUser(principal);
            if (user.getVersion() != version.longValue())
                throw new ApiException(409, "PROFILE_VERSION_CONFLICT", "Profile changed; reload before saving");
            if (hasName) user.setDisplayName(displayName);
            if (hasPhone) user.setPhoneNumber(phone);
            users.flush();
            return UserProfile.from(user);
        });
    }

    private User lockedUser(SessionPrincipal principal) {
        User user = users.findByIdForUpdate(principal.userId()).orElseThrow(ApiException::unauthorized);
        AuthSession session = sessions.findByIdForUpdate(principal.sessionId()).orElseThrow(ApiException::unauthorized);
        if (!session.getUser().getId().equals(user.getId()) || session.getRevokedAt() != null
                || !session.getExpiresAt().isAfter(Instant.now())) throw ApiException.unauthorized();
        requireActive(user);
        return user;
    }
    private TokenPair pair(AuthSession session, String refresh) {
        String access = tokens.issue(session.getUser().getId(), session.getId(), session.getExpiresAt());
        long seconds = Math.min(tokens.ttlSeconds(), Math.max(0, ChronoUnit.SECONDS.between(Instant.now(), session.getExpiresAt())));
        return new TokenPair("Bearer", access, seconds, refresh, session.getExpiresAt());
    }
    private void requireActive(User user) {
        if (user.getStatus() != UserStatus.ACTIVE) throw new ApiException(403, "ACCOUNT_LOCKED", "Account is locked");
    }
    private ApiException invalidCredentials() { return new ApiException(401, "INVALID_CREDENTIALS", "Invalid email or password"); }
    private ApiException invalidRefresh() { return new ApiException(401, "INVALID_REFRESH_TOKEN", "Refresh token is invalid or expired"); }
    private String email(String value) {
        if (value == null) throw ApiException.invalid("email", "Email is required");
        String email = value.trim().toLowerCase(Locale.ROOT);
        if (email.length() > 254 || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))
            throw ApiException.invalid("email", "Enter a valid email address");
        return email;
    }
    private String name(String value) {
        if (value == null || value.trim().isEmpty() || value.trim().length() > 100)
            throw ApiException.invalid("displayName", "Use 1 to 100 characters");
        return value.trim();
    }
    private String text(JsonNode node, String field, boolean nullable) {
        if (nullable && node.isNull()) return null;
        if (!node.isTextual()) throw ApiException.invalid(field, "Expected a string");
        return node.stringValue();
    }
    private String phone(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        String phone = value.trim();
        if (phone.length() > 30 || !phone.matches("[0-9+() -]+") || !phone.matches(".*[0-9].*"))
            throw ApiException.invalid("phoneNumber", "Enter a phone number of at most 30 characters");
        return phone;
    }
    private String newRefresh() {
        byte[] bytes = new byte[32]; random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    private String hash(String value) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8))); }
        catch (NoSuchAlgorithmException ex) { throw new IllegalStateException(ex); }
    }
}
