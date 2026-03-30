package com.skillsync.session.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {
    private int status;
    private String error;
    private String errorCode;
    private String message;
    private String path;
    private String method;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private List<ValidationError> validationErrors;

    public ErrorResponse() {
    }

    public ErrorResponse(int status, String error, String errorCode, String message, String path, String method,
                         LocalDateTime timestamp, List<ValidationError> validationErrors) {
        this.status = status;
        this.error = error;
        this.errorCode = errorCode;
        this.message = message;
        this.path = path;
        this.method = method;
        this.timestamp = timestamp;
        this.validationErrors = validationErrors;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public static class ValidationError {
        private String field;
        private String message;
        private Object rejectedValue;

        public ValidationError() {
        }

        public ValidationError(String field, String message, Object rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getRejectedValue() {
            return rejectedValue;
        }

        public void setRejectedValue(Object rejectedValue) {
            this.rejectedValue = rejectedValue;
        }
    }
}
