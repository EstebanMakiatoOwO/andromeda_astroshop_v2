package com.andromedaastroshop.crudfullstack.crud_fullstack.brands.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBrandRequest(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    String name,

    @Size(max = 200)
    String description,

    @Size(max = 255)
    String logoUrl
) {}