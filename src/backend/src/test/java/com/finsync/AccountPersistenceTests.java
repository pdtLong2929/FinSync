package com.finsync;

import com.finsync.entity.AuthSession;
import com.finsync.entity.User;
import com.finsync.repository.AuthSessionRepository;
import com.finsync.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.jpa.show-sql=false")
@Transactional
class AccountPersistenceTests {

    @Autowired private UserRepository users;
    @Autowired private AuthSessionRepository sessions;
    @Autowired private EntityManager entityManager;

    private User createUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setDisplayName("Demo User");
        user.setPasswordHash("test-only-hash");
        return users.saveAndFlush(user);
    }

    private AuthSession createSession(User user) {
        AuthSession session = new AuthSession();
        session.setUser(user);
        session.setRefreshTokenHash("a".repeat(64));
        session.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));
        return sessions.saveAndFlush(session);
    }

    @Test
    void persistsNormalizedUserAndRejectsDuplicateEmail() {
        User user = createUser("  DEMO@example.com  ");
        assertNotNull(user.getId());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getVersion());
        assertTrue(users.existsByEmail("demo@example.com"));
        assertEquals(user.getId(), users.findByEmail("demo@example.com").orElseThrow().getId());
        assertThrows(DataIntegrityViolationException.class, () -> createUser("demo@example.com"));
    }

    @Test
    void rotatesAndRevokesLockedSessionWithVersionUpdates() {
        User user = createUser("session@example.com");
        AuthSession session = createSession(user);
        Long oldVersion = session.getVersion();
        entityManager.clear();
        users.findByIdForUpdate(user.getId()).orElseThrow();
        AuthSession locked = sessions.findByIdForUpdate(session.getId()).orElseThrow();
        assertEquals(user.getId(), locked.getUser().getId());
        locked.setRefreshTokenHash("b".repeat(64));
        locked.setLastUsedAt(Instant.now());
        sessions.flush();
        assertTrue(locked.getVersion() > oldVersion);
        assertTrue(sessions.findByRefreshTokenHash("a".repeat(64)).isEmpty());
        assertTrue(sessions.findByRefreshTokenHash("b".repeat(64)).isPresent());
        assertEquals(1, sessions.findAllByUser_IdAndRevokedAtIsNullOrderById(user.getId()).size());
        locked.setRevokedAt(Instant.now());
        sessions.flush();
        assertTrue(sessions.findAllByUser_IdAndRevokedAtIsNullOrderById(user.getId()).isEmpty());
    }

    @Test
    void preventsDeletingAccountReferencedBySession() {
        User user = createUser("referenced@example.com");
        createSession(user);
        // Reload only the account so the database FK, not a managed child check, is exercised.
        entityManager.clear();
        users.deleteById(user.getId());
        assertThrows(DataIntegrityViolationException.class, () -> users.flush());
    }

    @Test
    void rejectsSessionThatExpiresBeforeCreation() {
        User user = createUser("expired@example.com");
        AuthSession session = new AuthSession();
        session.setUser(user);
        session.setRefreshTokenHash("c".repeat(64));
        session.setExpiresAt(Instant.now().minusSeconds(60));
        assertThrows(DataIntegrityViolationException.class, () -> sessions.saveAndFlush(session));
    }
}
