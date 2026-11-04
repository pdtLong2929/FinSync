package com.finsync.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    @Query("SELECT c FROM Category c WHERE c.scope = 'SYSTEM' OR c.userId = :userId ORDER BY c.name ASC")
    List<Category> findAllAvailableForUser(@Param("userId") UUID userId);

    List<Category> findByScope(String scope);
}
