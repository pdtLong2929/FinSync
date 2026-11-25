package com.finsync.service;

import com.finsync.dto.SettlementDtos.CreateSettlementRequest;
import com.finsync.dto.SettlementDtos.GroupDebtSummaryResponse;
import com.finsync.dto.SettlementDtos.SettlementResponse;
import com.finsync.security.SessionPrincipal;

import java.util.List;
import java.util.UUID;

public interface DebtSettlementService {
    GroupDebtSummaryResponse getDebtSummary(SessionPrincipal principal, UUID groupId);
    List<SettlementResponse> listSettlements(SessionPrincipal principal, UUID groupId);
    SettlementResponse createSettlement(SessionPrincipal principal, UUID groupId, CreateSettlementRequest request);
}
