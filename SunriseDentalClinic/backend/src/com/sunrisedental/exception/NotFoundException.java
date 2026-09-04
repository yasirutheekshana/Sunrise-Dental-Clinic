package com.sunrisedental.exception;

/**
 * Thrown when a requested resource is not found (HTTP 404).
 */
public class NotFoundException extends ApplicationException {
    public NotFoundException(String message) {
        super(message, 404);
    }
}
