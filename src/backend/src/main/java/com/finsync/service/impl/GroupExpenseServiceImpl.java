package com.finsync.service.impl;

import com.finsync.dto.GroupExpenseDtos.*;
import com.finsync.entity.Category;
import com.finsync.entity.ExpenseGroup;
import com.finsync.entity.GroupExpense;
import com.finsync.entity.GroupExpenseSplit;
import com.finsync.entity.GroupFundEntry;
import com.finsync.entity.GroupMember;
import com.finsync.entity.GroupWallet;
import com.finsync.entity.User;
import com.finsync.entity.enums.CategoryType;
import com.finsync.entity.enums.FundingSource;
import com.finsync.entity.enums.GroupFundEntryType;
import com.finsync.exception.ApiException;
import com.finsync.repository.CategoryRepository;
import com.finsync.repository.GroupExpenseRepository;
import com.finsync.repository.GroupExpenseSplitRepository;
import com.finsync.repository.GroupFundEntryRepository;
import com.finsync.repository.GroupMemberRepository;
import com.finsync.repository.GroupWalletRepository;
import com.finsync.security.SessionPrincipal;
import com.finsync.service.GroupExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupExpenseServiceImpl implements GroupExpenseService {

    private final GroupAccessSupport access;
    private final CategoryRepository categories;
    private final GroupMemberRepository members;
    private final GroupWalletRepository wallets;
    private final GroupFundEntryRepository fundEntries;
    private final GroupExpenseRepository expenses;
    private final GroupExpenseSplitRepository splits;
    private final ExpenseSplitCalculator splitCalculator;

    @Override
    @Transactional(readOnly = true)
    public GroupExpensePageResponse listExpenses(SessionPrincipal principal, UUID groupId, int page, int size) {
        User user = access.requireActiveUser(principal);
        ExpenseGroup group = access.requireGroup(groupId);
        access.requireActiveMember(groupId, user.getId());

        int boundedSize = Math.min(Math.max(size, 1), 100);
        int boundedPage = Math.max(page, 0);
        Page<GroupExpense> result = expenses.findByGroupIdOrderByExpenseDateDescCreatedAtDesc(
                groupId, PageRequest.of(boundedPage, boundedSize));

        List<UUID> ids = result.getContent().stream().map(GroupExpense::getId).toList();
        Map<UUID, List<GroupExpenseSplit>> splitsByExpense = ids.isEmpty()
                ? Map.of()
                : splits.findByExpenseIdInOrdered(ids).stream()
                .collect(Collectors.groupingBy(s -> s.getExpense().getId()));

        List<GroupExpenseResponse> content = result.getContent().stream()
                .map(e -> toResponse(e, splitsByExpense.getOrDefault(e.getId(), List.of())))
                .toList();

        return new GroupExpensePageResponse(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public GroupExpenseResponse getExpense(SessionPrincipal principal, UUID groupId, UUID expenseId) {
        User user = access.requireActiveUser(principal);
        access.requireGroup(groupId);
        access.requireActiveMember(groupId, user.getId());

        GroupExpense expense = expenses.findByIdAndGroupId(expenseId, groupId)
                .orElseThrow(() -> new ApiException(404, "EXPENSE_NOT_FOUND", "Group expense not found"));
        List<GroupExpenseSplit> splitList = splits.findByExpenseIdOrdered(expense.getId());
        return toResponse(expense, splitList);
    }

    @Override
    @Transactional
    public GroupExpenseResponse createExpense(SessionPrincipal principal, UUID groupId, CreateGroupExpenseRequest request) {
        User user = access.requireActiveUser(principal);
        ExpenseGroup group = access.lockGroup(groupId);
        GroupMember creator = access.requireActiveMember(groupId, user.getId());
        access.requireNotArchived(group);

        String title = request.title() == null ? "" : request.title().trim();
        if (title.isEmpty()) {
            throw ApiException.invalid("title", "Title must not be blank");
        }
        String note = request.note() == null || request.note().isBlank() ? null : request.note().trim();
        BigDecimal amount = MoneyValidation.requirePositiveAmount(request.amount(), group.getCurrencyCode(), "amount");

        String canonicalSplits = request.splits().stream()
                .map(s -> s.memberId() + ":" + (s.inputValue() == null ? "null" : s.inputValue().stripTrailingZeros().toPlainString()))
                .collect(Collectors.joining(","));
        String requestHash = IdempotencySupport.sha256(String.join("|",
                groupId.toString(),
                request.categoryId().toString(),
                request.paidByMemberId() == null ? "null" : request.paidByMemberId().toString(),
                title,
                note == null ? "" : note,
                MoneyValidation.format(amount),
                request.expenseDate().toString(),
                request.fundingSource().name(),
                request.splitMode().name(),
                canonicalSplits
        ));

        var existing = expenses.findByRequestId(request.requestId());
        if (existing.isPresent()) {
            GroupExpense found = existing.get();
            if (!found.getGroup().getId().equals(groupId)) {
                throw new ApiException(409, "IDEMPOTENCY_CONFLICT", "requestId belongs to a different group");
            }
            IdempotencySupport.verifySameHash(found.getRequestHash(), requestHash);
            return toResponse(found, splits.findByExpenseIdOrdered(found.getId()));
        }

        Category category = categories.findById(request.categoryId())
                .orElseThrow(() -> new ApiException(404, "CATEGORY_NOT_FOUND", "Category not found"));
        if (category.getType() != CategoryType.EXPENSE || category.getArchivedAt() != null) {
            throw ApiException.invalid("categoryId", "Group expenses require an active EXPENSE category");
        }

        Map<UUID, GroupMember> memberMap = members.findByGroupIdOrderByMemberOrderAsc(groupId).stream()
                .collect(Collectors.toMap(GroupMember::getId, m -> m));

        GroupMember payer = null;
        GroupWallet wallet = null;
        if (request.fundingSource() == FundingSource.MEMBERS) {
            if (request.paidByMemberId() == null) {
                throw ApiException.invalid("paidByMemberId", "paidByMemberId is required when fundingSource is MEMBERS");
            }
            payer = memberMap.get(request.paidByMemberId());
            if (payer == null || !payer.isActive()) {
                throw new ApiException(409, "INVALID_PAYER", "Payer must be an active member of this group");
            }
        } else {
            if (request.paidByMemberId() != null) {
                throw ApiException.invalid("paidByMemberId", "paidByMemberId must be null when fundingSource is GROUP_FUND");
            }
            wallet = wallets.findByGroupId(groupId)
                    .orElseThrow(() -> new IllegalStateException("Missing GroupWallet for group " + groupId));
            BigDecimal balance = fundEntries.computeBalanceByWalletId(wallet.getId());
            if (balance.compareTo(amount) < 0) {
                throw new ApiException(422, "INSUFFICIENT_GROUP_FUND",
                        "Shared group fund balance is insufficient for this expense");
            }
        }

        for (GroupExpenseSplitInput input : request.splits()) {
            GroupMember participant = memberMap.get(input.memberId());
            if (participant == null || !participant.isActive()) {
                throw new ApiException(409, "INVALID_PARTICIPANT",
                        "All split participants must be active members of this group");
            }
        }

        List<ExpenseSplitCalculator.ComputedSplit> computed = splitCalculator.calculate(
                amount, group.getCurrencyCode(), request.splitMode(), request.splits());

        GroupExpense expense = new GroupExpense();
        expense.setGroup(group);
        expense.setCategory(category);
        expense.setCreatedByMember(creator);
        expense.setPaidByMember(payer);
        expense.setTitle(title);
        expense.setNote(note);
        expense.setAmount(amount);
        expense.setExpenseDate(request.expenseDate());
        expense.setFundingSource(request.fundingSource());
        expense.setSplitMode(request.splitMode());
        expense.setRequestId(request.requestId());
        expense.setRequestHash(requestHash);
        expense = expenses.saveAndFlush(expense);

        List<GroupExpenseSplit> savedSplits = new ArrayList<>(computed.size());
        for (ExpenseSplitCalculator.ComputedSplit cs : computed) {
            GroupExpenseSplit row = new GroupExpenseSplit();
            row.setExpense(expense);
            row.setMember(memberMap.get(cs.memberId()));
            row.setAmount(cs.amount());
            row.setInputValue(cs.inputValue());
            row.setSplitOrder(cs.splitOrder());
            savedSplits.add(splits.save(row));
        }
        splits.flush();

        if (request.fundingSource() == FundingSource.GROUP_FUND) {
            UUID derivedRequestId = UUID.nameUUIDFromBytes(
                    ("fund-expense:" + expense.getId()).getBytes(StandardCharsets.UTF_8));
            GroupFundEntry fundEntry = new GroupFundEntry();
            fundEntry.setGroupWallet(wallet);
            fundEntry.setMember(null);
            fundEntry.setExpense(expense);
            fundEntry.setRecordedByMember(creator);
            fundEntry.setType(GroupFundEntryType.EXPENSE);
            fundEntry.setAmount(amount);
            fundEntry.setEntryDate(request.expenseDate());
            fundEntry.setNote(title);
            fundEntry.setRequestId(derivedRequestId);
            fundEntry.setRequestHash(IdempotencySupport.sha256("fund-expense:" + expense.getId()));
            fundEntries.saveAndFlush(fundEntry);
        }

        return toResponse(expense, savedSplits);
    }

    private GroupExpenseResponse toResponse(GroupExpense e, List<GroupExpenseSplit> splitList) {
        List<GroupExpenseSplitResponse> splitDtos = splitList.stream()
                .map(s -> new GroupExpenseSplitResponse(
                        s.getId(),
                        s.getMember().getId(),
                        s.getMember().getDisplayName(),
                        MoneyValidation.format(s.getAmount()),
                        s.getInputValue() != null ? MoneyValidation.format(s.getInputValue()) : null,
                        s.getSplitOrder()
                ))
                .toList();

        return new GroupExpenseResponse(
                e.getId(),
                e.getGroup().getId(),
                e.getCategory().getId(),
                e.getCategory().getName(),
                e.getCreatedByMember().getId(),
                e.getPaidByMember() != null ? e.getPaidByMember().getId() : null,
                e.getPaidByMember() != null ? e.getPaidByMember().getDisplayName() : null,
                e.getTitle(),
                e.getNote(),
                MoneyValidation.format(e.getAmount()),
                e.getGroup().getCurrencyCode(),
                e.getExpenseDate(),
                e.getFundingSource(),
                e.getSplitMode(),
                splitDtos,
                e.getRequestId(),
                e.getCreatedAt()
        );
    }
}
