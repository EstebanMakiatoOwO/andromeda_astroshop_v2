package com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AdminCategoryResponse(
        Long id,
        String name,
        String description,
        String slug,
        Boolean isActive,
        Integer sortOrder,
        String imageUrl,
        String metaTitle,
        String metaDescription,
        Long parentId,
        String parentName,
        Long productCount,
        List<AdminCategoryResponse> children,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}