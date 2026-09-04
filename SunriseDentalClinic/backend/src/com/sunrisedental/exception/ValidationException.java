package com.sunrisedental.exception;

/**
 * Thrown when input validation fails (HTTP 400).
 */
public class ValidationException extends ApplicationException {
    public ValidationException(String message) {
        super(message, 400);
    }
}
