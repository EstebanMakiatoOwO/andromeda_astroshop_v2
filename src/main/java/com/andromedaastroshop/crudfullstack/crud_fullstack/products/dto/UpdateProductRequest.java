package com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateProductRequest(
    @Size(max = 32, message = "El SKU no puede tener más de 32 caracteres")
    String sku,

    @NotBlank(message = "El nombre no puede estar vacío")
    String name,

    String shortDescription,
    String longDescription,

    @PositiveOrZero(message = "El stock no puede ser menor a cero")
    Integer stock,

    @DecimalMin(value = "100.00", message = "Precio mínimo de 100.00")
    BigDecimal price
) {}
