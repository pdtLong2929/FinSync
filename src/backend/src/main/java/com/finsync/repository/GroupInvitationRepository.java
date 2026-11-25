package com.finsync.repository;

import com.finsync.entity.GroupInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, UUID> {

    Optional<GroupInvitation> findByTokenHash(String tokenHash);

    Optional<GroupInvitation> findByIdAndGroupId(UUID id, UUID groupId);

    List<GroupInvitation> findByGroupIdOrderByCreatedAtDesc(UUID groupId);
}
