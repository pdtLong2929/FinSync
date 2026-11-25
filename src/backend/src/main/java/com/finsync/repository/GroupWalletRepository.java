package com.finsync.repository;

import com.finsync.entity.GroupWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GroupWalletRepository extends JpaRepository<GroupWallet, UUID> {

    Optional<GroupWallet> findByGroupId(UUID groupId);
}
