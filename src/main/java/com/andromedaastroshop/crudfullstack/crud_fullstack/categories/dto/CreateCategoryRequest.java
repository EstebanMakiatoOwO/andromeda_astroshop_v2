package com.andromedaastroshop.crudfullstack.crud_fullstack.categories.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
        @NotBlank @Size(max = 50) String name,

        @Size(max = 200) String description,

        @Size(max = 100) String slug,

        Boolean isActive,

        Integer sortOrder,

        @Size(max = 255) String imageUrl,

        @Size(max = 100) String metaTitle,

        @Size(max = 200) String metaDescription,

        Long parentId
) {}