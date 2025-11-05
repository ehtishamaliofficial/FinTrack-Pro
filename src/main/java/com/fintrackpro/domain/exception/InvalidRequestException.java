package com.fintrackpro.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic exception for invalid requests.
 * Can be used throughout the application for validation or bad requests.
 */
@Getter
public class InvalidRequestException extends RuntimeException {

    private final Map<String, String> fieldErrors;
    private final String errorCode;
    private final HttpStatus httpStatus;

    // ──────────────────────────────────────────────
    // Constructors
    // ──────────────────────────────────────────────

    public InvalidRequestException(String message) {
        super(message);
        this.fieldErrors = new HashMap<>();
        this.errorCode = "INVALID_REQUEST";
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public InvalidRequestException(String message, HttpStatus httpStatus) {
        super(message);
        this.fieldErrors = new HashMap<>();
        this.errorCode = "INVALID_REQUEST";
        this.httpStatus = httpStatus;
    }

    public InvalidRequestException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.fieldErrors = new HashMap<>();
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public InvalidRequestException(String message, Map<String, String> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors;
        this.errorCode = "VALIDATION_ERROR";
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public InvalidRequestException(String message, String field, String fieldError) {
        super(message);
        this.fieldErrors = new HashMap<>();
        this.fieldErrors.put(field, fieldError);
        this.errorCode = "VALIDATION_ERROR";
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public InvalidRequestException(String message, String field, String fieldError, HttpStatus httpStatus) {
        super(message);
        this.fieldErrors = new HashMap<>();
        this.fieldErrors.put(field, fieldError);
        this.errorCode = "VALIDATION_ERROR";
        this.httpStatus = httpStatus;
    }

    // ──────────────────────────────────────────────
    // Builder-style method for chaining
    // ──────────────────────────────────────────────
    public InvalidRequestException addFieldError(String field, String error) {
        this.fieldErrors.put(field, error);
        return this;
    }
}
