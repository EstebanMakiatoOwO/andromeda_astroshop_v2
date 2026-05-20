package com.andromedaastroshop.crudfullstack.crud_fullstack.categories.dto;

import java.time.LocalDateTime;

public record CategoryResponse(
        Long id,
        String name,
        String description,
        String slug,
        Boolean isActive,
        Boolean showInMenu,
        Integer sortOrder,
        String imageUrl,
        Long parentId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}