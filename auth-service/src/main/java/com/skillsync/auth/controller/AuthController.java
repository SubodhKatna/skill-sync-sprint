package com.skillsync.auth.controller;

import com.skillsync.auth.dto.*;
import com.skillsync.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Registers a new user account using the provided registration data.
     *
     * @param request the registration payload containing user credentials and profile information
     * @return the authentication response containing tokens and authenticated user details
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Authenticate a user using the provided credentials and return the resulting authentication data.
     *
     * @param request the login credentials (e.g., username/email and password)
     * @return an AuthResponse containing authentication tokens and user information
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Refreshes authentication credentials using the provided refresh token request.
     *
     * @param request the refresh token payload containing the refresh token (and any required client context)
     * @return an AuthResponse containing a new access token, optionally a new refresh token, and associated user information
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }
}
