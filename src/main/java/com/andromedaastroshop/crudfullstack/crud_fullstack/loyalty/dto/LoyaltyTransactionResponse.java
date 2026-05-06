package com.andromedaastroshop.crudfullstack.crud_fullstack.loyalty.dto;

import com.andromedaastroshop.crudfullstack.crud_fullstack.loyalty.model.TransactionType;

import java.time.LocalDateTime;

public record LoyaltyTransactionResponse(
        Long id,
        TransactionType type,
        Integer points,
        Integer balanceAfter,
        String description,
        Long orderId,
        LocalDateTime createdAt
) {}