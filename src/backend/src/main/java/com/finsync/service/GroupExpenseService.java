package com.finsync.service;

import com.finsync.dto.GroupExpenseDtos.CreateGroupExpenseRequest;
import com.finsync.dto.GroupExpenseDtos.GroupExpensePageResponse;
import com.finsync.dto.GroupExpenseDtos.GroupExpenseResponse;
import com.finsync.security.SessionPrincipal;

import java.util.UUID;

public interface GroupExpenseService {
    GroupExpensePageResponse listExpenses(SessionPrincipal principal, UUID groupId, int page, int size);
    GroupExpenseResponse getExpense(SessionPrincipal principal, UUID groupId, UUID expenseId);
    GroupExpenseResponse createExpense(SessionPrincipal principal, UUID groupId, CreateGroupExpenseRequest request);
}
