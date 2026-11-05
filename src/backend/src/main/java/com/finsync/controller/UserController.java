package com.finsync.controller;

import com.finsync.dto.AccountDtos.UserProfile;
import com.finsync.security.SessionPrincipal;
import com.finsync.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class UserController {
    private final AccountService accounts;
    @GetMapping public UserProfile profile(@AuthenticationPrincipal SessionPrincipal principal) {
        return accounts.profile(principal);
    }
    @PatchMapping public UserProfile update(@AuthenticationPrincipal SessionPrincipal principal, @RequestBody JsonNode patch) {
        return accounts.updateProfile(principal, patch);
    }
}
