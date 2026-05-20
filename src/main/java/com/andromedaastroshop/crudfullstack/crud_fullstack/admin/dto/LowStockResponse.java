package com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto;

public record LowStockResponse(
        Long id,
        String name,
        String sku,
        Integer stock,
        Integer stockAlertThreshold
) {}