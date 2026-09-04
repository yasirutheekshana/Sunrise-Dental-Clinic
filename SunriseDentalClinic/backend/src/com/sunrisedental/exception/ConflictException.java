package com.sunrisedental.exception;

/**
 * Thrown when an entity conflict occurs, such as double bookings or duplicate usernames (HTTP 409).
 */
public class ConflictException extends ApplicationException {
    public ConflictException(String message) {
        super(message, 409);
    }
}
