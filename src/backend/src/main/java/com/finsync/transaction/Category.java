package com.finsync.transaction;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId; // NULL if system-wide category

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 10)
    private String direction; // INCOME or EXPENSE

    @Column(nullable = false, length = 50)
    private String icon;

    @Column(nullable = false, length = 7)
    private String color;

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String scope = "SYSTEM"; // SYSTEM or CUSTOM

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
