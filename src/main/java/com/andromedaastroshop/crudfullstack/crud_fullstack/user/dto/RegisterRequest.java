package com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(max = 32) String name,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 64) String password
) {
}
