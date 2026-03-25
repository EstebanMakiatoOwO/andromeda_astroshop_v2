package com.andromedaastroshop.crudfullstack.crud_fullstack.user.service;

import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.RegisterRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.UserRespose;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.Role;

import java.util.List;

public interface UserService {
    UserRespose register(RegisterRequest request);

    UserRespose findById(Long id);

    UserRespose findByEmail(String email);

    UserRespose findByIdAndRole(Long id, Role role);

    List<UserRespose> findAllByRole(Role role);
}