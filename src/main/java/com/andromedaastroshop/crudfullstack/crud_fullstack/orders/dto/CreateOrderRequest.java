package com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(
        @NotEmpty(message = "La orden debe tener al menos un ítem")
        @Valid
        List<OrderItemRequest> items,

        @Email(message = "El email del invitado no es válido")
        @Size(max = 100)
        String guestEmail,

        @Size(max = 50, message = "El nombre del invitado no puede superar los 50 caracteres")
        String guestName,

        @NotNull(message = "El costo de envío es obligatorio")
        @DecimalMin(value = "0.00", message = "El costo de envío no puede ser negativo")
        BigDecimal shippingCost,

        @Size(max = 500, message = "Las notas no pueden superar los 500 caracteres")
        String notes
) {}