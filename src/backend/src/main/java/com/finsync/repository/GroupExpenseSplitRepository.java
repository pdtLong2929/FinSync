package com.finsync.repository;

import com.finsync.entity.GroupExpenseSplit;
import com.finsync.entity.enums.FundingSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface GroupExpenseSplitRepository extends JpaRepository<GroupExpenseSplit, UUID> {

    @Query("""
            select s from GroupExpenseSplit s
            join fetch s.member
            where s.expense.id = :expenseId
            order by s.splitOrder asc
            """)
    List<GroupExpenseSplit> findByExpenseIdOrdered(@Param("expenseId") UUID expenseId);

    @Query("""
            select s from GroupExpenseSplit s
            join fetch s.member
            where s.expense.id in :expenseIds
            order by s.splitOrder asc
            """)
    List<GroupExpenseSplit> findByExpenseIdInOrdered(@Param("expenseIds") Collection<UUID> expenseIds);

    @Query("""
            select s from GroupExpenseSplit s
            join fetch s.member
            where s.expense.group.id = :groupId
              and s.expense.fundingSource = :fundingSource
            """)
    List<GroupExpenseSplit> findByGroupIdAndFundingSource(@Param("groupId") UUID groupId,
                                                          @Param("fundingSource") FundingSource fundingSource);
}
