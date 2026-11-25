package com.finsync.service.impl;

import com.finsync.exception.ApiException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/** Computes SHA-256 hashes for invitation tokens and canonical idempotency payloads. */
public final class IdempotencySupport {
    private IdempotencySupport() {}

    public static String sha256(String raw) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 unavailable", ex);
        }
    }

    public static void verifySameHash(String existingHash, String incomingHash) {
        if (!existingHash.equals(incomingHash)) {
            throw new ApiException(409, "IDEMPOTENCY_CONFLICT",
                    "requestId was already used with a different payload");
        }
    }
}
