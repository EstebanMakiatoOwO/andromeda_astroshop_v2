package com.andromedaastroshop.crudfullstack.crud_fullstack.currency.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateExchangeRateRequest(
        @NotNull(message = "La tasa de cambio es obligatoria")
        @DecimalMin(value = "0.000001", message = "La tasa de cambio debe ser mayor a cero")
        BigDecimal rate
) {}