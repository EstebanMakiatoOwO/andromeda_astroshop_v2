package com.andromedaastroshop.crudfullstack.crud_fullstack.user.controller;

import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception.ResourceNotFoundException;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.UpdateUserRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.UserRespose;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.Role;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.security.JwtAuthFilter;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.security.JwtService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.service.UserDetailServiceCustom;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private UserDetailServiceCustom userDetailServiceCustom;

    private final UserRespose userResponse = new UserRespose(1L, "Juan", "juan@example.com", Role.USER);

    // ─── GET /users ─────────────────────────────────────────────────────────────

    @Test
    void getAllUsers_deberiaRetornar200() throws Exception {
        when(userService.findAllUsers()).thenReturn(List.of(userResponse));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Juan"));
    }

    // ─── GET /users/{id} ────────────────────────────────────────────────────────

    @Test
    void getUser_deberiaRetornar200() throws Exception {
        when(userService.findById(1L)).thenReturn(userResponse);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan@example.com"));
    }

    @Test
    void getUser_deberiaRetornar404SiNoExiste() throws Exception {
        when(userService.findById(99L)).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/users/99")).andExpect(status().isNotFound());
    }

    // ─── GET /users/email/{email} ────────────────────────────────────────────────

    @Test
    void getUserByEmail_deberiaRetornar200() throws Exception {
        when(userService.findByEmail("juan@example.com")).thenReturn(userResponse);

        mockMvc.perform(get("/api/v1/users/email/juan@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juan@example.com"));
    }

    // ─── GET /users/role/{role} ──────────────────────────────────────────────────

    @Test
    void getAllUsersByRole_deberiaRetornar200() throws Exception {
        when(userService.findAllByRole(Role.USER)).thenReturn(List.of(userResponse));

        mockMvc.perform(get("/api/v1/users/role/USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].role").value("USER"));
    }

    // ─── PUT /users/{id} ────────────────────────────────────────────────────────

    @Test
    void updateUser_deberiaRetornar200() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest("Juan Modificado", "juan@example.com", null);
        UserRespose updated = new UserRespose(1L, "Juan Modificado", "juan@example.com", Role.USER);

        when(userService.updateById(eq(1L), any(UpdateUserRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan Modificado"));
    }

    // ─── DELETE /users/{id} ─────────────────────────────────────────────────────

    @Test
    void deleteUser_deberiaRetornar204() throws Exception {
        when(userService.deleteById(1L)).thenReturn("User deleted successfully");

        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isNoContent());
    }
}