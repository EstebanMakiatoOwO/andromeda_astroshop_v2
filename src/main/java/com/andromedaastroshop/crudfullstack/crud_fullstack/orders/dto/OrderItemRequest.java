package com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequest(
        @NotNull(message = "El producto es obligatorio")
        Long productId,

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a cero")
        Integer quantity
) {}
    