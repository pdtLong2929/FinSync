package com.finsync.transaction;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "wallet_id", nullable = false)
    private UUID walletId;

    @Column(name = "destination_wallet_id")
    private UUID destinationWalletId; // NULL unless direction is TRANSFER

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 10)
    private String direction; // INCOME, EXPENSE, TRANSFER

    @Column(name = "transaction_date", nullable = false)
    private Instant transactionDate;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "receipt_image_url", length = 500)
    private String receiptImageUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public boolean isIncome() {
        return "INCOME".equalsIgnoreCase(this.direction);
    }

    public boolean isExpense() {
        return "EXPENSE".equalsIgnoreCase(this.direction);
    }

    public boolean isTransfer() {
        return "TRANSFER".equalsIgnoreCase(this.direction);
    }
}
