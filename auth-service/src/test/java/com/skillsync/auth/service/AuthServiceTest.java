package com.skillsync.auth.service;

import com.skillsync.auth.dto.AuthResponse;
import com.skillsync.auth.dto.RegisterRequest;
import com.skillsync.auth.entity.RefreshToken;
import com.skillsync.auth.entity.Role;
import com.skillsync.auth.entity.User;
import com.skillsync.auth.exception.BadRequestException;
import com.skillsync.auth.repository.RoleRepository;
import com.skillsync.auth.repository.UserRepository;
import com.skillsync.auth.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerNormalizesEmailAndUsesDefaultRole() {
        RegisterRequest request = new RegisterRequest();
        request.setName("  Learner  ");
        request.setEmail("  Learner@Example.com ");
        request.setPassword("secret");

        Role role = new Role(1L, Role.RoleName.ROLE_LEARNER);
        when(userRepository.existsByEmail("learner@example.com")).thenReturn(false);
        when(roleRepository.findByName(Role.RoleName.ROLE_LEARNER)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("secret")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(7L);
            saved.setRoles(Set.of(role));
            return saved;
        });
        when(jwtUtils.generateToken("learner@example.com", "ROLE_LEARNER")).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(7L)).thenReturn(new RefreshToken(3L, "refresh-token", null, null));

        AuthResponse response = authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        org.mockito.Mockito.verify(userRepository).save(userCaptor.capture());
        assertEquals("learner@example.com", userCaptor.getValue().getEmail());
        assertEquals("Learner", userCaptor.getValue().getName());
        assertEquals("ROLE_LEARNER", response.getRole());
        assertEquals("access-token", response.getAccessToken());
    }

    @Test
    void registerRejectsInvalidRole() {
        RegisterRequest request = new RegisterRequest();
        request.setName("User");
        request.setEmail("user@example.com");
        request.setPassword("secret");
        request.setRole("invalid-role");

        assertThrows(BadRequestException.class, () -> authService.register(request));
    }
}
