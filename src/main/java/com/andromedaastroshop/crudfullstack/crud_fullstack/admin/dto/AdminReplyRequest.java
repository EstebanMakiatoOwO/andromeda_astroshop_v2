package com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminReplyRequest(@NotBlank String reply) {}