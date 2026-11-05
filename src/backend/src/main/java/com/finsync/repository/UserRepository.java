package com.finsync.repository;

import com.finsync.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // Callers must trim/lowercase email before lookup, as registration does.
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("select u.id from User u where u.email = :email")
    Optional<UUID> findIdByEmail(@Param("email") String email);

    /** Requires a transaction. Lock user before session for account-wide changes. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.id = :id")
    Optional<User> findByIdForUpdate(@Param("id") UUID id);
}
