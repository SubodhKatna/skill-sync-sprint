package com.skillsync.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    /**
     * Constructs a ResourceNotFoundException with the specified detail message.
     *
     * This exception is annotated with {@code @ResponseStatus(HttpStatus.NOT_FOUND)} and will be translated to an HTTP 404 response when thrown.
     *
     * @param message the detail message describing the missing resource
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
