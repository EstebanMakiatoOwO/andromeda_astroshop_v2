package com.andromedaastroshop.crudfullstack.crud_fullstack.user.controller;

import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.JwtRespose;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.LoginRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.RegisterRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.UserRespose;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.Role;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.security.JwtAuthFilter;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.security.JwtService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.service.AuthService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.service.UserDetailServiceCustom;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthService.class)
@Import(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private UserDetailServiceCustom userDetailServiceCustom;

    // ─── register ───────────────────────────────────────────────────────────────

    @Test
    void register_deberiaRetornar201() throws Exception {
        RegisterRequest request = new RegisterRequest("Juan", "juan@example.com", "password123", Role.USER);
        UserRespose response = new UserRespose(1L, "Juan", "juan@example.com", Role.USER);

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan@example.com"));
    }

    // ─── login ──────────────────────────────────────────────────────────────────

    @Test
    void login_deberiaRetornar200ConJwt() throws Exception {
        LoginRequest request = new LoginRequest("juan@example.com", "password123");
        JwtRespose response = new JwtRespose("Juan","jwt.token.aqui");

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("jwt.token.aqui"));
    }
}