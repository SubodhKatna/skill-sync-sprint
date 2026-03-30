package com.skillsync.auth.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", ex.getMessage(), request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), request);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex, HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, "CONFLICT", ex.getMessage(), request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED, "AUTH_INVALID_CREDENTIALS", "Invalid email or password", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ErrorResponse.ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toValidationError)
                .toList();
        return buildError(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                "Validation failed for " + validationErrors.size() + " field(s)",
                request, validationErrors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        List<ErrorResponse.ValidationError> validationErrors = ex.getConstraintViolations()
                .stream()
                .map(v -> new ErrorResponse.ValidationError(v.getPropertyPath().toString(), v.getMessage(), v.getInvalidValue()))
                .toList();
        return buildError(HttpStatus.BAD_REQUEST, "CONSTRAINT_VIOLATION",
                "Constraint violation for " + validationErrors.size() + " value(s)",
                request, validationErrors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String message = "Invalid value '" + ex.getValue() + "' for parameter '" + ex.getName() + "'. Expected type: " + expectedType;
        return buildError(HttpStatus.BAD_REQUEST, "TYPE_MISMATCH", message, request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return buildError(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "Method not allowed: " + ex.getMethod(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableMessage(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST_BODY",
                "Malformed JSON request or invalid data format", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception for {} {}", request.getMethod(), request.getRequestURI(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred. Please contact support if this persists.", request);
    }

    private ErrorResponse.ValidationError toValidationError(FieldError fieldError) {
        return new ErrorResponse.ValidationError(
                fieldError.getField(),
                fieldError.getDefaultMessage(),
                fieldError.getRejectedValue()
        );
    }

    private ResponseEntity<ErrorResponse> buildError(HttpStatus status, String errorCode, String message, HttpServletRequest request) {
        return buildError(status, errorCode, message, request, List.of());
    }

    private ResponseEntity<ErrorResponse> buildError(HttpStatus status, String errorCode, String message,
                                                     HttpServletRequest request, List<ErrorResponse.ValidationError> validationErrors) {
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                errorCode,
                message,
                request.getRequestURI(),
                request.getMethod(),
                LocalDateTime.now(),
                validationErrors
        );
        return ResponseEntity.status(status).body(errorResponse);
    }
}
