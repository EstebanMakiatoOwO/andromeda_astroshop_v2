package user.service.impl;

import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.LoginRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.RegisterRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.Role;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.User;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.repository.UserRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.security.JwtService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Juan", "juan@example.com", "encoded123", Role.USER);
        user.setId(1L);
    }

    @Test
    void register_deberiaGuardarUsuarioCorrectamente() {
        RegisterRequest request = new RegisterRequest("Juan", "juan@example.com", "password123", Role.USER);

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded123");
        when(userRepository.save(any(User.class))).thenReturn(user);

        var response = authService.register(request);

        assertNotNull(response);
        assertEquals("Juan", response.name());
        assertEquals("juan@example.com", response.email());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_deberiaLanzarExcepcionSiEmailYaExiste() {
        RegisterRequest request = new RegisterRequest("Juan", "juan@example.com", "password123", Role.USER);

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_deberiaRetornarJwt() {
        LoginRequest request = new LoginRequest("juan@example.com", "password123");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user.getEmail())).thenReturn("jwt.token.aqui");

        var response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt.token.aqui", response.jwt());
        verify(authenticationManager, times(1)).authenticate(any());
    }
}
