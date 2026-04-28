package com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto;

import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull(message = "El estado es obligatorio")
        OrderStatus status
) {}