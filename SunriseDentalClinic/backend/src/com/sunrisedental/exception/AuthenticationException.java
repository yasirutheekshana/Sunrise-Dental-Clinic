package com.sunrisedental.exception;

/**
 * Thrown when credentials are invalid or session is unauthenticated (HTTP 401).
 */
public class AuthenticationException extends ApplicationException {
    public AuthenticationException(String message) {
        super(message, 401);
    }
}
