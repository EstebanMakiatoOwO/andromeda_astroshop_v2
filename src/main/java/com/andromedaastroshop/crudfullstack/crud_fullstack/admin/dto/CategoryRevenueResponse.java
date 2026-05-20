package com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto;

import java.math.BigDecimal;

public record CategoryRevenueResponse(
        String name,
        BigDecimal amount,
        double percent
) {}