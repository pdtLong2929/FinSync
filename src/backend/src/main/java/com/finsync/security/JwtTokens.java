package com.finsync.security;

import com.finsync.exception.ApiException;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokens {
    private final byte[] secret;
    private final String issuer;
    private final String audience;
    private final long ttlSeconds;
    public JwtTokens(@Value("${jwt.secret}") String secret,
                     @Value("${jwt.issuer}") String issuer,
                     @Value("${jwt.audience}") String audience,
                     @Value("${jwt.expiration}") long ttlMillis) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        if (this.secret.length < 32) throw new IllegalArgumentException("JWT_SECRET must contain at least 32 UTF-8 bytes");
        if (ttlMillis < 1000 || ttlMillis > 900000) throw new IllegalArgumentException("JWT TTL must be 1-900 seconds");
        this.issuer = issuer;
        this.audience = audience;
        this.ttlSeconds = ttlMillis / 1000;
    }
    public long ttlSeconds() { return ttlSeconds; }
    public String issue(UUID userId, UUID sessionId, Instant sessionExpiry) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(ttlSeconds);
        if (sessionExpiry.isBefore(expiry)) expiry = sessionExpiry;
        var claims = new JWTClaimsSet.Builder().subject(userId.toString()).issuer(issuer).audience(audience)
                .issueTime(Date.from(now)).expirationTime(Date.from(expiry))
                .jwtID(UUID.randomUUID().toString()).claim("sid", sessionId.toString()).build();
        var token = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build(), claims);
        try { token.sign(new MACSigner(secret)); return token.serialize(); }
        catch (JOSEException ex) { throw new IllegalStateException("Cannot sign access token", ex); }
    }
    public SessionPrincipal verify(String encoded) {
        try {
            if (encoded.length() > 8192) throw ApiException.unauthorized();
            SignedJWT jwt = SignedJWT.parse(encoded);
            if (!JWSAlgorithm.HS256.equals(jwt.getHeader().getAlgorithm())
                    || !JOSEObjectType.JWT.equals(jwt.getHeader().getType())
                    || !jwt.verify(new MACVerifier(secret))) throw ApiException.unauthorized();
            var c = jwt.getJWTClaimsSet();
            Instant now = Instant.now();
            if (!issuer.equals(c.getIssuer()) || !c.getAudience().contains(audience)
                    || c.getExpirationTime() == null || !c.getExpirationTime().toInstant().isAfter(now)
                    || c.getIssueTime() == null || c.getIssueTime().toInstant().isAfter(now)
                    || !c.getExpirationTime().after(c.getIssueTime())
                    || (c.getNotBeforeTime() != null && c.getNotBeforeTime().toInstant().isAfter(now)))
                throw ApiException.unauthorized();
            UUID.fromString(c.getJWTID());
            return new SessionPrincipal(UUID.fromString(c.getSubject()), UUID.fromString(c.getStringClaim("sid")),
                    c.getJWTID(), c.getExpirationTime().toInstant());
        } catch (Exception ex) { throw ApiException.unauthorized(); }
    }
}
