package com.finsync.controller;

import com.finsync.dto.GroupExpenseDtos.CreateFundEntryRequest;
import com.finsync.dto.GroupExpenseDtos.GroupFundEntryResponse;
import com.finsync.dto.GroupExpenseDtos.GroupFundOverviewResponse;
import com.finsync.security.SessionPrincipal;
import com.finsync.service.GroupFundService;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/fund")
@RequiredArgsConstructor
public class GroupFundController {

    private final GroupFundService fundService;

    @GetMapping
    public GroupFundOverviewResponse overview(
            @AuthenticationPrincipal SessionPrincipal principal,
            @PathVariable UUID groupId
    ) {
        return fundService.getFundOverview(principal, groupId);
    }

    @PostMapping("/entries")
    @ResponseStatus(HttpStatus.CREATED)
    public GroupFundEntryResponse createEntry(
            @AuthenticationPrincipal SessionPrincipal principal,
            @PathVariable UUID groupId,
            @Valid @RequestBody CreateFundEntryRequest request
    ) {
        return fundService.createFundEntry(principal, groupId, request);
    }
}
