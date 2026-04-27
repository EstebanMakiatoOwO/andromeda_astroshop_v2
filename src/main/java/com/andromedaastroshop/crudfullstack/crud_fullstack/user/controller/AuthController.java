package com.andromedaastroshop.crudfullstack.crud_fullstack.user.controller;

import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.JwtRespose;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.LoginRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.RegisterRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.UserRespose;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserRespose> register(@Valid @RequestBody RegisterRequest request) {
        UserRespose newUser = authService.register(request);
        URI location = URI.create("/api/v1/auth/" + newUser.id());
        return ResponseEntity.created(location).body(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtRespose> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
