package com.finsync.repository;

import com.finsync.entity.GroupMember;
import com.finsync.entity.enums.GroupMemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID> {

    List<GroupMember> findByGroupIdOrderByMemberOrderAsc(UUID groupId);

    Optional<GroupMember> findByGroupIdAndUserId(UUID groupId, UUID userId);

    Optional<GroupMember> findByIdAndGroupId(UUID id, UUID groupId);

    long countByGroupIdAndStatus(UUID groupId, GroupMemberStatus status);

    @Query("select coalesce(max(m.memberOrder), -1) from GroupMember m where m.group.id = :groupId")
    int findMaxMemberOrderByGroupId(@Param("groupId") UUID groupId);

    @Query("""
            select m from GroupMember m
            join fetch m.group g
            where m.user.id = :userId
              and m.status = com.finsync.entity.enums.GroupMemberStatus.ACTIVE
              and (:includeArchived = true or g.archivedAt is null)
            order by g.createdAt desc
            """)
    List<GroupMember> findActiveMembershipsByUserId(@Param("userId") UUID userId,
                                                    @Param("includeArchived") boolean includeArchived);
}
