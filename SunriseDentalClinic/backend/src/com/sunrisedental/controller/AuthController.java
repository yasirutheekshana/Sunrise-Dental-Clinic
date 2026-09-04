package com.sunrisedental.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sunrisedental.dto.LoginRequest;
import com.sunrisedental.dto.SignupRequest;
import com.sunrisedental.dto.UserResponse;
import com.sunrisedental.model.User;
import com.sunrisedental.service.AuthService;

import java.io.IOException;

/**
 * Controller managing authentication endpoints: signup, login, logout, and session checks.
 */
public class AuthController extends BaseController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (handleCorsPreflight(exchange)) {
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod().toUpperCase();

        try {
            if (path.endsWith("/signup") && "POST".equals(method)) {
                handleSignup(exchange);
            } else if (path.endsWith("/login") && "POST".equals(method)) {
                handleLogin(exchange);
            } else if (path.endsWith("/logout") && "POST".equals(method)) {
                handleLogout(exchange);
            } else if (path.endsWith("/session") && "GET".equals(method)) {
                handleGetSession(exchange);
            } else {
                sendError(exchange, 404, "Endpoint not found: " + method + " " + path);
            }
        } catch (Exception e) {
            handleException(exchange, e);
        }
    }

    private void handleSignup(HttpExchange exchange) throws IOException {
        SignupRequest req = parseRequestBody(exchange, SignupRequest.class);
        UserResponse response = authService.registerStaff(req);
        sendSuccess(exchange, 201, "Staff account created successfully. Please login to continue.", response);
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        LoginRequest req = parseRequestBody(exchange, LoginRequest.class);
        UserResponse response = authService.login(req);
        sendSuccess(exchange, 200, "Login successful.", response);
    }

    private void handleLogout(HttpExchange exchange) throws IOException {
        String sessionId = exchange.getRequestHeaders().getFirst("X-Session-Id");
        if (sessionId != null) {
            authService.logout(sessionId);
        }
        sendSuccess(exchange, 200, "You have been logged out successfully.");
    }

    private void handleGetSession(HttpExchange exchange) throws IOException {
        User user = requireAuthenticatedUser(exchange, authService);
        sendSuccess(exchange, 200, "Active session verified.", UserResponse.fromUser(user));
    }
}
