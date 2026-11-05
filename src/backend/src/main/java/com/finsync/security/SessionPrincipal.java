package com.finsync.security;

import java.time.Instant;
import java.util.UUID;

public record SessionPrincipal(UUID userId, UUID sessionId, String jwtId, Instant expiresAt) {}
