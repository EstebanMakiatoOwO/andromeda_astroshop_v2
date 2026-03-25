package com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto;

import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.Role;

public record RegisterRequest(String name, String email, String password, Role role) {
}
