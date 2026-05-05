package com.andromedaastroshop.crudfullstack.crud_fullstack.currency.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExchangeRateResponse(
        Long id,
        String fromCurrency,
        String toCurrency,
        BigDecimal rate,
        LocalDateTime updatedAt
) {}