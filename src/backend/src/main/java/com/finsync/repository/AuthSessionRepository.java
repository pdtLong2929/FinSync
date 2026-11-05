package com.finsync.repository;

import com.finsync.entity.AuthSession;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthSessionRepository extends JpaRepository<AuthSession, UUID> {

    // Discovery only: the service must lock and recheck hash/status/expiry before rotation.
    Optional<AuthSession> findByRefreshTokenHash(String refreshTokenHash);

    interface SessionIdentity {
        UUID getId();
        UUID getUserId();
    }

    // Scalar discovery avoids caching a stale entity before acquiring the user lock.
    @Query("select s.id as id, s.user.id as userId from AuthSession s where s.refreshTokenHash = :hash")
    Optional<SessionIdentity> findIdentityByHash(@Param("hash") String hash);

    /** Requires a transaction; acquire the user lock first when both are needed. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from AuthSession s where s.id = :id")
    Optional<AuthSession> findByIdForUpdate(@Param("id") UUID id);

    /** Includes expired sessions; suitable for revoking every unrevoked session. */
    List<AuthSession> findAllByUser_IdAndRevokedAtIsNullOrderById(UUID userId);
}
