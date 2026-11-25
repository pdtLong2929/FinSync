package com.finsync.controller;

import com.finsync.dto.GroupExpenseDtos.CreateGroupExpenseRequest;
import com.finsync.dto.GroupExpenseDtos.GroupExpensePageResponse;
import com.finsync.dto.GroupExpenseDtos.GroupExpenseResponse;
import com.finsync.security.SessionPrincipal;
import com.finsync.service.GroupExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/expenses")
@RequiredArgsConstructor
public class GroupExpenseController {

    private final GroupExpenseService expenseService;

    @GetMapping
    public GroupExpensePageResponse list(
            @AuthenticationPrincipal SessionPrincipal principal,
            @PathVariable UUID groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return expenseService.listExpenses(principal, groupId, page, size);
    }

    @GetMapping("/{expenseId}")
    public GroupExpenseResponse get(
            @AuthenticationPrincipal SessionPrincipal principal,
            @PathVariable UUID groupId,
            @PathVariable UUID expenseId
    ) {
        return expenseService.getExpense(principal, groupId, expenseId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupExpenseResponse create(
            @AuthenticationPrincipal SessionPrincipal principal,
            @PathVariable UUID groupId,
            @Valid @RequestBody CreateGroupExpenseRequest request
    ) {
        return expenseService.createExpense(principal, groupId, request);
    }
}
