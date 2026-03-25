package com.andromedaastroshop.crudfullstack.crud_fullstack.user.service;

import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.LoginRequest;

public interface AuthService {
    String login(LoginRequest request);
}
