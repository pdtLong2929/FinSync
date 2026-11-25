package com.finsync.controller;

import com.finsync.dto.SettlementDtos.CreateSettlementRequest;
import com.finsync.dto.SettlementDtos.GroupDebtSummaryResponse;
import com.finsync.dto.SettlementDtos.SettlementResponse;
import com.finsync.security.SessionPrincipal;
import com.finsync.service.DebtSettlementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups/{groupId}")
@RequiredArgsConstructor
public class DebtSettlementController {

    private final DebtSettlementService settlementService;

    @GetMapping("/debts")
    public GroupDebtSummaryResponse getDebtSummary(
            @AuthenticationPrincipal SessionPrincipal principal,
            @PathVariable UUID groupId
    ) {
        return settlementService.getDebtSummary(principal, groupId);
    }

    @GetMapping("/settlements")
    public List<SettlementResponse> listSettlements(
            @AuthenticationPrincipal SessionPrincipal principal,
            @PathVariable UUID groupId
    ) {
        return settlementService.listSettlements(principal, groupId);
    }

    @PostMapping("/settlements")
    @ResponseStatus(HttpStatus.CREATED)
    public SettlementResponse createSettlement(
            @AuthenticationPrincipal SessionPrincipal principal,
            @PathVariable UUID groupId,
            @Valid @RequestBody CreateSettlementRequest request
    ) {
        return settlementService.createSettlement(principal, groupId, request);
    }
}
