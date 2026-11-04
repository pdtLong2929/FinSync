package com.finsync.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Page<Transaction> findByUserIdOrderByTransactionDateDesc(UUID userId, Pageable pageable);

    Page<Transaction> findByUserIdAndWalletIdOrderByTransactionDateDesc(UUID userId, UUID walletId, Pageable pageable);

    Page<Transaction> findByUserIdAndCategoryIdOrderByTransactionDateDesc(UUID userId, UUID categoryId, Pageable pageable);

    Optional<Transaction> findByIdAndUserId(UUID id, UUID userId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.walletId = :walletId OR t.destinationWalletId = :walletId")
    long countTransactionsByWalletId(@Param("walletId") UUID walletId);

    @Query("SELECT COALESCE(SUM(t.amount), 0.00) FROM Transaction t " +
           "WHERE t.userId = :userId AND t.categoryId = :categoryId AND t.direction = 'EXPENSE' " +
           "AND t.transactionDate >= :startDate AND t.transactionDate <= :endDate")
    BigDecimal sumExpenseByCategoryInDateRange(
            @Param("userId") UUID userId,
            @Param("categoryId") UUID categoryId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );
}
