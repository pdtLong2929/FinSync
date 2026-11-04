package com.finsync.user.dto;

import java.util.UUID;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresIn,
        UUID userId,
        String email,
        String displayName,
        String role
) {
    public static AuthResponse of(String token, long expiresIn, UUID userId, String email, String displayName, String role) {
        return new AuthResponse(token, "Bearer", expiresIn, userId, email, displayName, role);
    }
}
