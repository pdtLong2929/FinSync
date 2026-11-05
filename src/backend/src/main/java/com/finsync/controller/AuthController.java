package com.finsync.controller;

import com.finsync.dto.AccountDtos.*;
import com.finsync.security.SessionPrincipal;
import com.finsync.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AccountService accounts;
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisteredUser register(@RequestBody RegisterRequest request) { return accounts.register(request); }
    @PostMapping("/login")
    public ResponseEntity<TokenPair> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok().header("Cache-Control", "no-store").body(accounts.login(request));
    }
    @PostMapping("/refresh")
    public ResponseEntity<TokenPair> refresh(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok().header("Cache-Control", "no-store").body(accounts.refresh(request));
    }
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@AuthenticationPrincipal SessionPrincipal principal) { accounts.logout(principal); }
}
