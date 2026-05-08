package com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


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
        String notes,

        @NotBlank(message = "La calle es obligatoria")
        @Size(max = 200)
        String shippingStreet,

        @NotBlank(message = "La ciudad es obligatoria")
        @Size(max = 100)
        String shippingCity,

        @NotBlank(message = "El estado es obligatorio")
        @Size(max = 100)
        String shippingState,

        @NotBlank(message = "El código postal es obligatorio")
        @Size(max = 20)
        String shippingZipCode,

        @NotBlank(message = "El país es obligatorio")
        @Size(max = 100)
        String shippingCountry
) {}