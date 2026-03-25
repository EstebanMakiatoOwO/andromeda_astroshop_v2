package com.andromedaastroshop.crudfullstack.crud_fullstack.user.service.impl;

import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.RegisterRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.UserRespose;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.Role;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.User;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.repository.UserRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserRespose register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);

        userRepository.save(user);

        return mapToUserRespose(user);
    }

    @Override
    public UserRespose findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToUserRespose(user);
    }

    @Override
    public UserRespose findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return mapToUserRespose(user);
    }

    @Override
    public UserRespose findByIdAndRole(Long id, Role role) {
        User user = userRepository.findByIdAndRole(id, role)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id + " and role " + role));
        return mapToUserRespose(user);
    }

    @Override
    public List<UserRespose> findAllByRole(Role role) {
        return userRepository.findAllByRole(role).stream()
                .map(this::mapToUserRespose)
                .collect(Collectors.toList());
    }

    private UserRespose mapToUserRespose(User user) {
        return new UserRespose(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

}
