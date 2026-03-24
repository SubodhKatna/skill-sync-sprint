package com.skillsync.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    /**
     * Constructs a BadRequestException with the specified detail message.
     *
     * @param message the detail message describing the bad request condition
     */
    public BadRequestException(String message) {
        super(message);
    }
}
