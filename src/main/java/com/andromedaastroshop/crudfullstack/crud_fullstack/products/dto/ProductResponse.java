package com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(Long id, String sku, LocalDateTime createdAt, LocalDateTime updatedAt, String name, String shortDescription, String longDescription, Integer stock, BigDecimal price, String imgUrl) {
}
