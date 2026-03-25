package com.andromedaastroshop.crudfullstack.crud_fullstack.user.service;

import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.RegisterRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.UserRespose;

public interface UserService {
    UserRespose register(RegisterRequest request);

    UserRespose findById(Long id);
}
