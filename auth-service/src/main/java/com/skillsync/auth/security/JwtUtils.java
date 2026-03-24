package com.skillsync.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Derives the HMAC signing key used for JWT creation and verification from the configured secret.
     *
     * @return a SecretKey suitable for HMAC signing derived from the configured JWT secret
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Create a signed JWT containing the given email as the token subject and the given role as a `"role"` claim.
     *
     * @param email the user's email to set as the JWT subject
     * @param role  the user's role to include as the `"role"` claim
     * @return      the signed, compact JWT string with issued-at and expiration set based on configuration
     */
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Verifies a signed JWT and extracts its subject as the user's email.
     *
     * @param token the compact serialized signed JWT
     * @return the subject (email) from the token payload
     */
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Validate a JWT's signature and structure using the configured signing key.
     *
     * Parses and verifies the provided JWT; expiration and signature must be valid for the token to be considered valid.
     *
     * @param token the compact serialized JWT string to validate
     * @return `true` if the token is valid and its signature is verified, `false` otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
