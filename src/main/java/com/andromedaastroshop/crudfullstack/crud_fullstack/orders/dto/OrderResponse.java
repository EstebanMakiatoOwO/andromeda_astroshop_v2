package com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto;

import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long userId,
        String guestEmail,
        String guestName,
        OrderStatus status,
        BigDecimal subtotal,
        BigDecimal shippingCost,
        BigDecimal total,
        String mpPreferenceId,
        String notes,
        List<OrderItemResponse> items,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}