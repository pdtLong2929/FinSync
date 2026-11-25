package com.finsync.repository;

import com.finsync.entity.Category;
import com.finsync.entity.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByArchivedAtIsNullOrderByTypeAscNameAsc();
    List<Category> findByTypeAndArchivedAtIsNullOrderByNameAsc(CategoryType type);
}
