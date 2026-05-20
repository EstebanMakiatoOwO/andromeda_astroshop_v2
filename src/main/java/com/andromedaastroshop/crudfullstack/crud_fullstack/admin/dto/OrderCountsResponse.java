package com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto;

public record OrderCountsResponse(
        long pending,
        long paid,
        long shipped,
        long cancelled,
        long refunded,
        long total
) {}