package com.andromedaastroshop.crudfullstack.crud_fullstack.reviews.dto;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long userId,
        String userName,
        Long productId,
        String productName,
        Integer rating,
        String comment,
        String adminReply,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}