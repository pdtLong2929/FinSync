package com.finsync.security;

import com.finsync.exception.ApiException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.Instant;

@Component
public class TokenBlacklist {
    private final StringRedisTemplate redis;
    public TokenBlacklist(StringRedisTemplate redis) { this.redis = redis; }
    public boolean contains(String jwtId) {
        try { return Boolean.TRUE.equals(redis.hasKey("auth:blacklist:" + jwtId)); }
        catch (RuntimeException ex) { throw unavailable(); }
    }
    public void revoke(SessionPrincipal principal) {
        Duration ttl = Duration.between(Instant.now(), principal.expiresAt());
        if (ttl.isNegative() || ttl.isZero()) return;
        try { redis.opsForValue().set("auth:blacklist:" + principal.jwtId(), "1", ttl); }
        catch (RuntimeException ex) { throw unavailable(); }
    }
    private ApiException unavailable() {
        return new ApiException(503, "AUTH_SERVICE_UNAVAILABLE", "Authentication service unavailable");
    }
}
