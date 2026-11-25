package com.finsync.entity;

import com.finsync.entity.enums.CategoryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/** Shared income/expense category managed by system administrators. */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
public class Category extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private CategoryType type;

    @Column(name = "icon_key", nullable = false, length = 50)
    private String iconKey = "category";

    @Column(name = "color_key", nullable = false, length = 30)
    private String colorKey = "violet";

    @Column(name = "archived_at")
    private Instant archivedAt;
}
