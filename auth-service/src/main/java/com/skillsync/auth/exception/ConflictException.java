package com.skillsync.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {
    /**
     * Constructs a ConflictException with the specified detail message.
     *
     * @param message the detail message describing the conflict
     */
    public ConflictException(String message) {
        super(message);
    }
}
