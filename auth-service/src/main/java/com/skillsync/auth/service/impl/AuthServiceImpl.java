package com.skillsync.auth.service.impl;

import com.skillsync.auth.client.UserServiceClient;
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
import com.skillsync.auth.service.AuthService;
import com.skillsync.auth.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Locale;

@Service
public class AuthServiceImpl implements AuthService {

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

    @Autowired
    private UserServiceClient userServiceClient;

    @Override
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
        userServiceClient.createProfile(user);

        String token = jwtUtils.generateToken(user.getEmail(), roleName.name());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(user.getId(), token, refreshToken.getToken(), user.getEmail(), roleName.name());
    }

    @Override
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

        return new AuthResponse(user.getId(), token, refreshToken.getToken(), user.getEmail(), roleName);
    }

    @Override
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

        return new AuthResponse(user.getId(), newAccessToken, refreshToken.getToken(), user.getEmail(), roleName);
    }

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
