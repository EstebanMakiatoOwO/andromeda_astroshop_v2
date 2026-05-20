package com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto;

import com.andromedaastroshop.crudfullstack.crud_fullstack.brands.dto.BrandResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.dto.CategoryResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.model.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AdminProductResponse(
        Long id,
        String sku,
        String barcode,
        String name,
        String shortDescription,
        String longDescription,
        Integer stock,
        Integer stockAlertThreshold,
        BigDecimal costPrice,
        BigDecimal price,
        Boolean isActive,
        Boolean isCatalog,
        ProductStatus status,
        List<String> images,
        List<CategoryResponse> categories,
        BrandResponse brand,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}