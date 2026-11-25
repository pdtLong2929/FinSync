package com.finsync.service.impl;

import com.finsync.entity.enums.CurrencyCode;
import com.finsync.exception.ApiException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Validates VND/USD monetary precision before PostgreSQL casts and converts minor units cleanly. */
public final class MoneyValidation {
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("999999999999999.99");

    private MoneyValidation() {}

    public static int allowedScale(CurrencyCode currency) {
        return currency == CurrencyCode.VND ? 0 : 2;
    }

    public static BigDecimal requirePositiveAmount(BigDecimal raw, CurrencyCode currency, String field) {
        if (raw == null) {
            throw ApiException.invalid(field, "Amount is required");
        }
        BigDecimal normalized = raw.stripTrailingZeros();
        int scale = Math.max(0, normalized.scale());
        int allowed = allowedScale(currency);
        if (scale > allowed) {
            throw ApiException.invalid(field, currency == CurrencyCode.VND
                    ? "VND amounts must be whole numbers without fractional decimals"
                    : "USD amounts support at most 2 decimal places");
        }
        if (normalized.signum() <= 0) {
            throw ApiException.invalid(field, "Amount must be greater than zero");
        }
        if (normalized.compareTo(MAX_AMOUNT) > 0) {
            throw ApiException.invalid(field, "Amount exceeds supported numeric precision");
        }
        return raw.setScale(2, RoundingMode.UNNECESSARY);
    }

    public static BigDecimal requireNonNegativeAmount(BigDecimal raw, CurrencyCode currency, String field) {
        if (raw == null) {
            throw ApiException.invalid(field, "Amount is required");
        }
        BigDecimal normalized = raw.stripTrailingZeros();
        int scale = Math.max(0, normalized.scale());
        int allowed = allowedScale(currency);
        if (scale > allowed) {
            throw ApiException.invalid(field, currency == CurrencyCode.VND
                    ? "VND amounts must be whole numbers without fractional decimals"
                    : "USD amounts support at most 2 decimal places");
        }
        if (normalized.signum() < 0) {
            throw ApiException.invalid(field, "Amount must not be negative");
        }
        if (normalized.compareTo(MAX_AMOUNT) > 0) {
            throw ApiException.invalid(field, "Amount exceeds supported numeric precision");
        }
        return raw.setScale(2, RoundingMode.UNNECESSARY);
    }

    public static long toMinorUnits(BigDecimal amount, CurrencyCode currency) {
        int shift = allowedScale(currency);
        return amount.movePointRight(shift).longValueExact();
    }

    public static BigDecimal fromMinorUnits(long minorUnits, CurrencyCode currency) {
        int shift = allowedScale(currency);
        return BigDecimal.valueOf(minorUnits, shift).setScale(2, RoundingMode.UNNECESSARY);
    }

    public static String format(BigDecimal amount) {
        return (amount == null ? BigDecimal.ZERO : amount).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
