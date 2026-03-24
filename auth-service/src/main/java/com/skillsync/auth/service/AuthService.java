package com.skillsync.auth.service;

import com.skillsync.auth.dto.*;
import com.skillsync.auth.entity.RefreshToken;
import com.skillsync.auth.entity.Role;
import com.skillsync.auth.entity.User;
import com.skillsync.auth.exception.BadRequestException;
import com.skillsync.auth.exception.ConflictException;
import com.skillsync.auth.exception.ResourceNotFoundException;
import com.skillsync.auth.repository.RoleRepository;
import com.skillsync.auth.repository.UserRepository;
import com.skillsync.auth.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Locale;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenService refreshTokenService;

    /**
     * Create a new user account, assign or create the requested role, persist the user, and issue access and refresh tokens.
     *
     * @param request registration data; email and name are trimmed (email is lowercased); `role` may be null or blank to use the default role
     * @return an AuthResponse containing the access token, the refresh token value, the user's email, and the resolved role name
     * @throws ConflictException if a user with the normalized email already exists
     * @throws BadRequestException if the provided role string cannot be resolved to a valid role
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role.RoleName roleName = resolveRoleName(request.getRole());

        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(roleName);
                    return roleRepository.save(newRole);
                });

        user.setRoles(Collections.singleton(role));
        user = userRepository.save(user);

        String token = jwtUtils.generateToken(user.getEmail(), roleName.name());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(token, refreshToken.getToken(), user.getEmail(), roleName.name());
    }

    /**
     * Authenticate the provided credentials and issue a new access token and refresh token.
     *
     * @param request the login request containing the user's email and password
     * @return an AuthResponse containing the access JWT, the refresh token value, the user's email, and the resolved role name
     * @throws ResourceNotFoundException if no user exists with the normalized email
     */
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        String roleName = user.getRoles().stream()
                .findFirst()
                .map(role -> role.getName().name())
                .orElse("ROLE_LEARNER");

        String token = jwtUtils.generateToken(user.getEmail(), roleName);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(token, refreshToken.getToken(), user.getEmail(), roleName);
    }

    /**
     * Issues a new access JWT for the user associated with the provided refresh token.
     *
     * @param request the request containing the refresh token string
     * @return an AuthResponse containing the newly generated access token, the existing refresh token value, the user's email, and the user's role name
     * @throws ResourceNotFoundException if no refresh token is found for the provided value
     */
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        refreshToken = refreshTokenService.verifyExpiration(refreshToken);
        User user = refreshToken.getUser();

        String roleName = user.getRoles().stream()
                .findFirst()
                .map(role -> role.getName().name())
                .orElse("ROLE_LEARNER");

        String newAccessToken = jwtUtils.generateToken(user.getEmail(), roleName);

        return new AuthResponse(newAccessToken, refreshToken.getToken(), user.getEmail(), roleName);
    }

    /**
     * Resolve a raw role string into a Role.RoleName enum value.
     *
     * <p>Null or blank inputs map to ROLE_LEARNER. The input is case-insensitive and may omit the
     * "ROLE_" prefix (e.g., "admin" or "ROLE_ADMIN" both resolve to ROLE_ADMIN).
     *
     * @param rawRole the raw role string to normalize and convert
     * @return the resolved Role.RoleName
     * @throws BadRequestException if the normalized role name does not match any Role.RoleName
     */
    private Role.RoleName resolveRoleName(String rawRole) {
        if (rawRole == null || rawRole.isBlank()) {
            return Role.RoleName.ROLE_LEARNER;
        }

        String normalizedRole = rawRole.trim().toUpperCase(Locale.ROOT);
        if (!normalizedRole.startsWith("ROLE_")) {
            normalizedRole = "ROLE_" + normalizedRole;
        }

        try {
            return Role.RoleName.valueOf(normalizedRole);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid role: " + rawRole);
        }
    }
}
