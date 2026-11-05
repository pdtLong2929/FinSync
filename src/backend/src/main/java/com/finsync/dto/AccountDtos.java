package com.finsync.dto;

import java.time.Instant;
import java.util.UUID;
import com.finsync.entity.User;

public final class AccountDtos {
    private AccountDtos() {}
    public record RegisterRequest(String displayName, String email, String password) {
        @Override public String toString() { return "RegisterRequest[redacted]"; }
    }
    public record LoginRequest(String email, String password, Boolean rememberMe, String deviceLabel) {
        @Override public String toString() { return "LoginRequest[redacted]"; }
    }
    public record RefreshRequest(String refreshToken) {
        @Override public String toString() { return "RefreshRequest[redacted]"; }
    }
    public record RegisteredUser(UUID id, String email, String displayName) {}
    public record TokenPair(String tokenType, String accessToken, long expiresIn,
                            String refreshToken, Instant refreshExpiresAt) {
        @Override public String toString() { return "TokenPair[redacted]"; }
    }
    public record UserProfile(UUID id, String email, String displayName, String phoneNumber, Long version) {
        public static UserProfile from(User user) {
            return new UserProfile(user.getId(), user.getEmail(), user.getDisplayName(),
                    user.getPhoneNumber(), user.getVersion());
        }
    }
}
