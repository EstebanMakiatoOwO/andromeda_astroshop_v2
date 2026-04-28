package com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto;

import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.dto.CategoryResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
        Long id,
        String sku,
        String barcode,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String name,
        String shortDescription,
        String longDescription,
        Integer stock,
        Integer stockAlertThreshold,
        BigDecimal costPrice,
        BigDecimal price,
        String imgUrl,
        Boolean isActive,
        Boolean isCatalog,
        List<CategoryResponse> categories
) {
}
