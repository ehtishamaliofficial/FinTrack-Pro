package com.fintrackpro.domain.exception;

/**
 * Exception for forbidden operations.
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException() {
        super("Access forbidden");
    }

    public ForbiddenException(String message) {
        super(message);
    }

    public static ForbiddenException forResource(String resource) {
        return new ForbiddenException(
                String.format("You don't have permission to access this %s", resource)
        );
    }
}
