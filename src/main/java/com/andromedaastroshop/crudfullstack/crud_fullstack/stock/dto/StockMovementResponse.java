package com.andromedaastroshop.crudfullstack.crud_fullstack.stock.dto;

import com.andromedaastroshop.crudfullstack.crud_fullstack.stock.model.MovementType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StockMovementResponse(
        Long id,
        Long productId,
        String productName,
        Long userId,
        String userName,
        MovementType type,
        String reason,
        Integer quantity,
        BigDecimal unitCost,
        Integer stockBefore,
        Integer stockAfter,
        Long referenceId,
        String referenceType,
        String notes,
        LocalDateTime createdAt
) {}