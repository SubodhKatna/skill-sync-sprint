package com.skillsync.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles a missing-resource condition by converting the exception into an HTTP 404 response.
     *
     * @param ex the ResourceNotFoundException that triggered this handler
     * @return a ResponseEntity containing an ErrorResponse with status 404 and the exception's message
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, ex.getMessage()));
    }

    /**
     * Handle a BadRequestException and map it to an HTTP 400 error response.
     *
     * @param ex the BadRequestException that triggered this handler
     * @return a ResponseEntity containing an ErrorResponse with status 400 and the exception's message
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, ex.getMessage()));
    }

    /**
     * Handles a resource conflict and maps it to an HTTP 409 Conflict response.
     *
     * @param ex the ConflictException whose message is used as the response detail
     * @return a ResponseEntity containing an ErrorResponse with status 409 and the exception message
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(409, ex.getMessage()));
    }

    /**
     * Handle authentication failures caused by invalid credentials.
     *
     * @param ex the caught BadCredentialsException
     * @return a ResponseEntity with HTTP status 401 and an ErrorResponse containing the message "Invalid email or password"
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(401, "Invalid email or password"));
    }

    /**
     * Builds a 400 Bad Request response containing validation error details.
     *
     * @param ex the MethodArgumentNotValidException containing field validation errors
     * @return a ResponseEntity with HTTP status 400 and an ErrorResponse whose message is the field errors formatted as "field: message" pairs separated by ", "
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, errors));
    }

    /**
     * Handles any uncaught exception and maps it to a generic internal server error response.
     *
     * @param ex the uncaught exception (details are not exposed to the client)
     * @return a ResponseEntity containing an ErrorResponse with HTTP status 500 and message "An unexpected error occurred"
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, "An unexpected error occurred"));
    }
}
