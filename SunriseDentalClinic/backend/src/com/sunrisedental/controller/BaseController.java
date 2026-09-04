package com.sunrisedental.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sunrisedental.dto.ApiResponse;
import com.sunrisedental.exception.ApplicationException;
import com.sunrisedental.exception.AuthenticationException;
import com.sunrisedental.model.User;
import com.sunrisedental.service.AuthService;
import com.sunrisedental.util.JsonUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Base abstract controller providing uniform HTTP handling, JSON responses,
 * CORS support, parameter extraction, and exception management.
 */
public abstract class BaseController implements HttpHandler {

    protected void sendJsonResponse(HttpExchange exchange, int statusCode, Object body) throws IOException {
        addCorsHeaders(exchange);
        String json = (body instanceof String) ? (String) body : JsonUtil.toJson(body);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
            os.flush();
        }
    }

    protected void sendSuccess(HttpExchange exchange, int statusCode, String message, Object data) throws IOException {
        sendJsonResponse(exchange, statusCode, ApiResponse.ok(message, data));
    }

    protected void sendSuccess(HttpExchange exchange, int statusCode, String message) throws IOException {
        sendJsonResponse(exchange, statusCode, ApiResponse.ok(message));
    }

    protected void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        sendJsonResponse(exchange, statusCode, ApiResponse.error(message));
    }

    protected void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Session-Id");
        exchange.getResponseHeaders().set("Access-Control-Max-Age", "3600");
    }

    protected boolean handleCorsPreflight(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            addCorsHeaders(exchange);
            exchange.sendResponseHeaders(204, -1);
            return true;
        }
        return false;
    }

    protected <T> T parseRequestBody(HttpExchange exchange, Class<T> clazz) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return JsonUtil.fromJson(is, clazz);
        }
    }

    protected String getRawRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[1024];
            int n;
            while ((n = is.read(buf)) != -1) {
                baos.write(buf, 0, n);
            }
            return baos.toString(StandardCharsets.UTF_8);
        }
    }

    protected Map<String, String> getQueryParams(HttpExchange exchange) {
        Map<String, String> params = new HashMap<>();
        String query = exchange.getRequestURI().getRawQuery();
        if (query == null || query.isEmpty()) {
            return params;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            try {
                if (idx > 0) {
                    String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8.name());
                    String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8.name());
                    params.put(key, value);
                } else if (idx < 0) {
                    String key = URLDecoder.decode(pair, StandardCharsets.UTF_8.name());
                    params.put(key, "");
                }
            } catch (Exception ignored) {
            }
        }
        return params;
    }

    protected User requireAuthenticatedUser(HttpExchange exchange, AuthService authService) {
        String sessionId = exchange.getRequestHeaders().getFirst("X-Session-Id");
        if (sessionId == null || sessionId.trim().isEmpty()) {
            // Check cookie
            String cookie = exchange.getRequestHeaders().getFirst("Cookie");
            if (cookie != null) {
                for (String c : cookie.split(";")) {
                    String[] parts = c.trim().split("=");
                    if (parts.length == 2 && "sessionId".equalsIgnoreCase(parts[0].trim())) {
                        sessionId = parts[1].trim();
                        break;
                    }
                }
            }
        }
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new AuthenticationException("Authentication required. Please log in.");
        }
        return authService.authenticateSession(sessionId);
    }

    protected void handleException(HttpExchange exchange, Exception e) {
        try {
            if (e instanceof ApplicationException appEx) {
                sendError(exchange, appEx.getStatusCode(), appEx.getMessage());
            } else {
                e.printStackTrace();
                sendError(exchange, 500, "An unexpected error occurred. Please try again.");
            }
        } catch (IOException ioException) {
            System.err.println("Error writing exception response: " + ioException.getMessage());
        }
    }
}
