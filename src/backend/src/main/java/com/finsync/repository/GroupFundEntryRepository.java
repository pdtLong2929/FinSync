package com.finsync.repository;

import com.finsync.entity.GroupFundEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupFundEntryRepository extends JpaRepository<GroupFundEntry, UUID> {

    @Query("""
            select e from GroupFundEntry e
            left join fetch e.member
            join fetch e.recordedByMember
            where e.groupWallet.id = :walletId
            order by e.entryDate desc, e.createdAt desc
            """)
    List<GroupFundEntry> findByGroupWalletIdOrdered(@Param("walletId") UUID walletId);

    Optional<GroupFundEntry> findByRequestId(UUID requestId);

    boolean existsByGroupWalletId(UUID walletId);

    @Query("""
            select coalesce(sum(
                case
                    when e.type = com.finsync.entity.enums.GroupFundEntryType.CONTRIBUTION then e.amount
                    when e.type = com.finsync.entity.enums.GroupFundEntryType.REFUND then -e.amount
                    when e.type = com.finsync.entity.enums.GroupFundEntryType.EXPENSE then -e.amount
                    else 0
                end
            ), 0)
            from GroupFundEntry e
            where e.groupWallet.id = :walletId
            """)
    BigDecimal computeBalanceByWalletId(@Param("walletId") UUID walletId);
}
