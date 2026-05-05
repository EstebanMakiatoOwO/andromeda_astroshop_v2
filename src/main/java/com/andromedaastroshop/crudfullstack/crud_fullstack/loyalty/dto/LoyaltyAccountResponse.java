package com.andromedaastroshop.crudfullstack.crud_fullstack.loyalty.dto;

import java.time.LocalDateTime;

public record LoyaltyAccountResponse(
        Long id,
        Long userId,
        String userName,
        Integer pointsBalance,
        Integer lifetimePoints,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}