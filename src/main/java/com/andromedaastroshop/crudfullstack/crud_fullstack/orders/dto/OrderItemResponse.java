package com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {}