package com.finsync.controller;

import com.finsync.dto.CategoryDtos.CategoryResponse;
import com.finsync.entity.enums.CategoryType;
import com.finsync.repository.CategoryRepository;
import com.finsync.security.SessionPrincipal;
import com.finsync.service.impl.GroupAccessSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final GroupAccessSupport access;
    private final CategoryRepository categories;

    @GetMapping
    public List<CategoryResponse> list(
            @AuthenticationPrincipal SessionPrincipal principal,
            @RequestParam(required = false) CategoryType type
    ) {
        access.requireActiveUser(principal);
        var rows = type == null
                ? categories.findByArchivedAtIsNullOrderByTypeAscNameAsc()
                : categories.findByTypeAndArchivedAtIsNullOrderByNameAsc(type);
        return rows.stream().map(CategoryResponse::from).toList();
    }
}
