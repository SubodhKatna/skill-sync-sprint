package com.skillsync.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.skillsync.user.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class AuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse body = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "UNAUTHORIZED",
                "Authentication required. Please provide a valid Bearer token.",
                request.getRequestURI(),
                request.getMethod(),
                LocalDateTime.now(),
                List.of()
        );
        mapper.writeValue(response.getOutputStream(), body);
    }
}
