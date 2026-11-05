package com.finsync.service;

import com.finsync.dto.AccountDtos.*;
import com.finsync.security.SessionPrincipal;
import tools.jackson.databind.JsonNode;

public interface AccountService {
    RegisteredUser register(RegisterRequest request);
    TokenPair login(LoginRequest request);
    TokenPair refresh(RefreshRequest request);
    void logout(SessionPrincipal principal);
    UserProfile profile(SessionPrincipal principal);
    UserProfile updateProfile(SessionPrincipal principal, JsonNode patch);
}
