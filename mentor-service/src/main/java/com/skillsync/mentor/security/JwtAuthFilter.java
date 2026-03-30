package com.skillsync.mentor.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                String token = header.substring(7);
                SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
                Claims claims = Jwts.parser().verifyWith(key).build()
                        .parseSignedClaims(token).getPayload();
                String email = claims.getSubject();
                String role = claims.get("role", String.class);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        email, null, List.of(new SimpleGrantedAuthority(role)));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getOutputStream().print(
                    "{\"status\":401,\"error\":\"Unauthorized\",\"errorCode\":\"INVALID_TOKEN\"," +
                    "\"message\":\"Invalid or expired token.\",\"validationErrors\":[]}");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
