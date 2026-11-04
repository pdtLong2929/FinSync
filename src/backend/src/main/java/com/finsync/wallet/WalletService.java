package com.finsync.wallet;

import com.finsync.common.ApiException;
import com.finsync.transaction.TransactionRepository;
import com.finsync.wallet.dto.WalletCreateRequest;
import com.finsync.wallet.dto.WalletResponse;
import com.finsync.wallet.dto.WalletSummaryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WalletService(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public WalletResponse createWallet(UUID userId, WalletCreateRequest request) {
        String trimmedName = request.name().trim();
        String type = request.type().trim().toUpperCase();

        if (!List.of("CASH", "BANK", "CREDIT").contains(type)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid wallet type. Allowed types: CASH, BANK, CREDIT");
        }

        if (walletRepository.existsByUserIdAndNameIgnoreCase(userId, trimmedName)) {
            throw new ApiException(HttpStatus.CONFLICT, "A wallet with the name '" + trimmedName + "' already exists");
        }

        BigDecimal initialBalance = request.initialBalance() != null ? request.initialBalance() : BigDecimal.ZERO.setScale(2);
        String currency = (request.currency() != null && !request.currency().isBlank()) ? request.currency().toUpperCase().trim() : "VND";

        Wallet wallet = Wallet.builder()
                .userId(userId)
                .name(trimmedName)
                .type(type)
                .currency(currency)
                .balance(initialBalance)
                .status("ACTIVE")
                .build();

        Wallet saved = walletRepository.save(wallet);
        return WalletResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public WalletSummaryResponse getWalletSummary(UUID userId) {
        List<Wallet> activeWallets = walletRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, "ACTIVE");

        BigDecimal totalAssets = BigDecimal.ZERO.setScale(2);
        BigDecimal totalDebts = BigDecimal.ZERO.setScale(2);

        for (Wallet w : activeWallets) {
            if ("CREDIT".equalsIgnoreCase(w.getType())) {
                totalDebts = totalDebts.add(w.getBalance());
            } else {
                totalAssets = totalAssets.add(w.getBalance());
            }
        }

        BigDecimal netWorth = totalAssets.subtract(totalDebts);
        List<WalletResponse> responses = activeWallets.stream().map(WalletResponse::from).toList();

        return new WalletSummaryResponse(netWorth, totalAssets, totalDebts, responses);
    }

    @Transactional(readOnly = true)
    public Wallet getWalletEntity(UUID walletId, UUID userId) {
        return walletRepository.findByIdAndUserId(walletId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Wallet not found"));
    }

    @Transactional
    public String deleteOrArchiveWallet(UUID walletId, UUID userId) {
        Wallet wallet = getWalletEntity(walletId, userId);

        long transactionCount = transactionRepository.countTransactionsByWalletId(walletId);

        if (transactionCount > 0) {
            // Soft delete / archive to preserve historical transaction integrity per FR-002, US-1.5
            wallet.setStatus("ARCHIVED");
            walletRepository.save(wallet);
            return "Wallet has historical transactions and was moved to ARCHIVED status.";
        } else {
            // Hard delete allowed when zero transactions exist
            walletRepository.delete(wallet);
            return "Wallet was deleted permanently.";
        }
    }
}
