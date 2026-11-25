package com.finsync.repository;

import com.finsync.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SettlementRepository extends JpaRepository<Settlement, UUID> {

    @Query("""
            select s from Settlement s
            join fetch s.payerMember
            join fetch s.receiverMember
            where s.group.id = :groupId
            order by s.settlementDate desc, s.createdAt desc
            """)
    List<Settlement> findByGroupIdOrdered(@Param("groupId") UUID groupId);

    Optional<Settlement> findByRequestId(UUID requestId);

    boolean existsByGroupId(UUID groupId);
}
