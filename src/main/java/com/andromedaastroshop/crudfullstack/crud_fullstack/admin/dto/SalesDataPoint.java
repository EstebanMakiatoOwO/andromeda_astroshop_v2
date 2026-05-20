package com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto;

import java.math.BigDecimal;

public record SalesDataPoint(
        String date,
        BigDecimal amount
) {}