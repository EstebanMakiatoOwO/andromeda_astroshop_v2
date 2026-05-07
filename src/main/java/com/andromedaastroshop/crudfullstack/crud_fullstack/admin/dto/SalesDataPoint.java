package com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SalesDataPoint(
        LocalDate date,
        BigDecimal amount
) {}