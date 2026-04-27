package com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductRequest(
    @Size(max = 32, message = "El SKU no puede tener más de 32 caracteres")
    String sku,

    @Size(max = 50, message = "El barcode no puede tener más de 50 caracteres")
    String barcode,

    @NotBlank(message = "El nombre no puede estar vacío")
    String name,

    String shortDescription,
    String longDescription,

    @PositiveOrZero(message = "El stock no puede ser menor a cero")
    Integer stock,

    @PositiveOrZero(message = "El umbral de alerta no puede ser menor a cero")
    Integer stockAlertThreshold,

    @DecimalMin(value = "0.00", message = "El costo no puede ser negativo")
    BigDecimal costPrice,

    @DecimalMin(value = "100.00", message = "Precio mínimo de 100.00")
    BigDecimal price,

    @NotNull(message = "El estado del producto es obligatorio")
    Boolean isActive,

    List<Long> categoryIds
) {}
