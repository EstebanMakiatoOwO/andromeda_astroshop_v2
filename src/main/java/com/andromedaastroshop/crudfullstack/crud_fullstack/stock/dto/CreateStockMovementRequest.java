package com.andromedaastroshop.crudfullstack.crud_fullstack.stock.dto;

import com.andromedaastroshop.crudfullstack.crud_fullstack.stock.model.MovementType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateStockMovementRequest(
        @NotNull(message = "El producto es obligatorio")
        Long productId,

        @NotNull(message = "El tipo de movimiento es obligatorio")
        MovementType type,

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a cero")
        Integer quantity,

        BigDecimal unitCost,

        String reason,

        Long referenceId,

        @Size(max = 50)
        String referenceType,

        @Size(max = 500, message = "Las notas no pueden tener más de 500 caracteres")
        String notes
) {}