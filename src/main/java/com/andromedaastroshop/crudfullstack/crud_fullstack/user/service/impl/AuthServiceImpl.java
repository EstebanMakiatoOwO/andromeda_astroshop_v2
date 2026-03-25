package com.andromedaastroshop.crudfullstack.crud_fullstack.user.service.impl;

import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.JwtRespose;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.LoginRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.RegisterRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.UserRespose;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.Role;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.User;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.repository.UserRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.security.JwtService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public UserRespose register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password())); // <--- Aquí se encripta la contraseña
        // Asignar un rol por defecto si no se proporciona, o usar el del request
        user.setRole(request.role() != null ? request.role() : Role.USER);

        User savedUser = userRepository.save(user);

        return mapToUserRespose(savedUser);
    }

    @Override
    public JwtRespose login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        // Si la autenticación es exitosa, buscamos el usuario para generar el token
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found after authentication")); // Esto no debería pasar
        return new JwtRespose(user.getName(), jwtService.generateToken(user.getEmail()));
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