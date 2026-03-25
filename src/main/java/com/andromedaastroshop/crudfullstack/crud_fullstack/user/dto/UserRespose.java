package com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto;

import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.Role;

public record UserRespose(Long id, String name, String email, Role role) {
}
