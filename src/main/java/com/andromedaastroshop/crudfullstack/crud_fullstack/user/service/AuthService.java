package com.andromedaastroshop.crudfullstack.crud_fullstack.user.service;

import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.JwtRespose;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.LoginRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.RegisterRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.UserRespose;

public interface AuthService {
    UserRespose register(RegisterRequest request);
    JwtRespose login(LoginRequest request);
}