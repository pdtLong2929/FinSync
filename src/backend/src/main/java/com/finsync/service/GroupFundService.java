package com.finsync.service;

import com.finsync.dto.GroupExpenseDtos.CreateFundEntryRequest;
import com.finsync.dto.GroupExpenseDtos.GroupFundEntryResponse;
import com.finsync.dto.GroupExpenseDtos.GroupFundOverviewResponse;
import com.finsync.security.SessionPrincipal;

import java.util.UUID;

public interface GroupFundService {
    GroupFundOverviewResponse getFundOverview(SessionPrincipal principal, UUID groupId);
    GroupFundEntryResponse createFundEntry(SessionPrincipal principal, UUID groupId, CreateFundEntryRequest request);
}
