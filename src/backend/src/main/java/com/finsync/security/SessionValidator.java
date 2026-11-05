package com.finsync.security;

import com.finsync.entity.enums.UserStatus;
import com.finsync.exception.ApiException;
import com.finsync.repository.AuthSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class SessionValidator {
    private final AuthSessionRepository sessions;
    @Transactional(readOnly = true)
    public void validate(SessionPrincipal principal) {
        var session = sessions.findById(principal.sessionId()).orElseThrow(ApiException::unauthorized);
        if (!session.getUser().getId().equals(principal.userId()) || session.getRevokedAt() != null
                || !session.getExpiresAt().isAfter(Instant.now())) throw ApiException.unauthorized();
        if (session.getUser().getStatus() != UserStatus.ACTIVE)
            throw new ApiException(403, "ACCOUNT_LOCKED", "Account is locked");
    }
}
