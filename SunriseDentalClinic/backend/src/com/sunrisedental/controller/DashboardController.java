package com.sunrisedental.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sunrisedental.dto.DashboardStats;
import com.sunrisedental.service.AppointmentService;
import com.sunrisedental.service.AuthService;

import java.io.IOException;

/**
 * Controller for dashboard metrics and summary statistics.
 */
public class DashboardController extends BaseController {
    private final AppointmentService appointmentService;
    private final AuthService authService;

    public DashboardController(AppointmentService appointmentService, AuthService authService) {
        this.appointmentService = appointmentService;
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (handleCorsPreflight(exchange)) {
            return;
        }

        try {
            requireAuthenticatedUser(exchange, authService);

            String method = exchange.getRequestMethod().toUpperCase();
            if ("GET".equals(method)) {
                DashboardStats stats = appointmentService.getDashboardStats();
                sendSuccess(exchange, 200, "Dashboard statistics retrieved.", stats);
            } else {
                sendError(exchange, 405, "Method Not Allowed");
            }
        } catch (Exception e) {
            handleException(exchange, e);
        }
    }
}
