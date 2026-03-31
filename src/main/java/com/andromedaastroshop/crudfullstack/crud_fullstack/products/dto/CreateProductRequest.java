package com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateProductRequest(@NotBlank String name, String shortDescription, String longDescription, @PositiveOrZero(message = "El stock no puede ser menor a cero") Integer stock, @DecimalMin(value = "100.00", message = "Precio minimo de 100.00")BigDecimal price) {
}
