package com.finsync.dto;

import com.finsync.entity.Category;
import com.finsync.entity.enums.CategoryType;

import java.time.Instant;
import java.util.UUID;

public final class CategoryDtos {
    private CategoryDtos() {}

    public record CategoryResponse(
            UUID id,
            String name,
            CategoryType type,
            String iconKey,
            String colorKey,
            Instant archivedAt
    ) {
        public static CategoryResponse from(Category c) {
            return new CategoryResponse(
                    c.getId(),
                    c.getName(),
                    c.getType(),
                    c.getIconKey(),
                    c.getColorKey(),
                    c.getArchivedAt()
            );
        }
    }
}
