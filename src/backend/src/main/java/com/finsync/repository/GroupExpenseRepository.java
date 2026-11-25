package com.finsync.repository;

import com.finsync.entity.GroupExpense;
import com.finsync.entity.enums.FundingSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupExpenseRepository extends JpaRepository<GroupExpense, UUID> {

    Page<GroupExpense> findByGroupIdOrderByExpenseDateDescCreatedAtDesc(UUID groupId, Pageable pageable);

    Optional<GroupExpense> findByIdAndGroupId(UUID id, UUID groupId);

    Optional<GroupExpense> findByRequestId(UUID requestId);

    boolean existsByGroupId(UUID groupId);

    @Query("""
            select e from GroupExpense e
            join fetch e.paidByMember
            where e.group.id = :groupId
              and e.fundingSource = :fundingSource
            """)
    List<GroupExpense> findByGroupIdAndFundingSourceWithPayer(@Param("groupId") UUID groupId,
                                                              @Param("fundingSource") FundingSource fundingSource);
}
