package com.andromedaastroshop.crudfullstack.crud_fullstack.user.impl;

import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.UpdateUserRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.Role;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.User;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.repository.UserRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Juan", "juan@example.com", "encoded123", Role.USER);
        user.setId(1L);
    }

    // ─── findAll ────────────────────────────────────────────────────────────────

    @Test
    void findAll_deberiaRetornarListaDeUsuarios() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        var response = userService.findAllUsers();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Juan", response.get(0).name());
    }

    @Test
    void findAll_deberiaRetornarListaVacia() {
        when(userRepository.findAll()).thenReturn(List.of());

        var response = userService.findAllUsers();

        assertTrue(response.isEmpty());
    }

    // ─── findById ───────────────────────────────────────────────────────────────

    @Test
    void findById_deberiaRetornarUsuario() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var response = userService.findById(1L);

        assertNotNull(response);
        assertEquals("Juan", response.name());
    }

    @Test
    void findById_deberiaLanzarExcepcionSiNoExiste() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.findById(99L));
    }

    // ─── updateById ─────────────────────────────────────────────────────────────

    @Test
    void updateById_deberiaActualizarUsuario() {
        UpdateUserRequest request = new UpdateUserRequest("Juan Modificado", "juan@example.com", null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        var response = userService.updateById(1L, request);

        assertNotNull(response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateById_deberiaEncriptarPasswordSiSeProvee() {
        UpdateUserRequest request = new UpdateUserRequest("Juan", "juan@example.com", "nuevaPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("nuevaPassword")).thenReturn("encodedNueva");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.updateById(1L, request);

        verify(passwordEncoder, times(1)).encode("nuevaPassword");
    }

    @Test
    void updateById_deberiaLanzarExcepcionSiNoExiste() {
        UpdateUserRequest request = new UpdateUserRequest("Juan", "juan@example.com", null);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.updateById(99L, request));
    }

    // ─── deleteById ─────────────────────────────────────────────────────────────

    @Test
    void deleteById_deberiaEliminarUsuario() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteById(1L);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteById_deberiaLanzarExcepcionSiNoExiste() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.deleteById(99L));
    }
}
