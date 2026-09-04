package com.sunrisedental.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sunrisedental.dto.AppointmentRequest;
import com.sunrisedental.dto.StatusUpdateRequest;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.AppointmentStatus;
import com.sunrisedental.model.User;
import com.sunrisedental.service.AppointmentService;
import com.sunrisedental.service.AuthService;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Controller handling appointment booking, listings, searches, and status updates.
 */
public class AppointmentController extends BaseController {
    private final AppointmentService appointmentService;
    private final AuthService authService;

    public AppointmentController(AppointmentService appointmentService, AuthService authService) {
        this.appointmentService = appointmentService;
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (handleCorsPreflight(exchange)) {
            return;
        }

        try {
            // Protect all appointment endpoints
            User staffUser = requireAuthenticatedUser(exchange, authService);

            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod().toUpperCase();

            if (path.equals("/api/appointments") || path.equals("/api/appointments/")) {
                if ("GET".equals(method)) {
                    handleListAppointments(exchange);
                } else if ("POST".equals(method)) {
                    handleCreateAppointment(exchange, staffUser);
                } else {
                    sendError(exchange, 405, "Method Not Allowed");
                }
            } else if (path.equals("/api/appointments/next-number")) {
                if ("GET".equals(method)) {
                    String nextNumber = appointmentService.getNextAppointmentNumber();
                    sendSuccess(exchange, 200, "Next appointment number generated.", Map.of("nextAppointmentNumber", nextNumber));
                } else {
                    sendError(exchange, 405, "Method Not Allowed");
                }
            } else if (path.startsWith("/api/appointments/")) {
                String subPath = path.substring("/api/appointments/".length());
                subPath = URLDecoder.decode(subPath, StandardCharsets.UTF_8);

                if (subPath.endsWith("/status") && "PUT".equals(method)) {
                    String appointmentNumber = subPath.substring(0, subPath.length() - "/status".length());
                    handleUpdateStatus(exchange, appointmentNumber);
                } else if ("GET".equals(method)) {
                    handleGetAppointment(exchange, subPath);
                } else {
                    sendError(exchange, 405, "Method Not Allowed");
                }
            } else {
                sendError(exchange, 404, "Endpoint Not Found: " + method + " " + path);
            }
        } catch (Exception e) {
            handleException(exchange, e);
        }
    }

    private void handleListAppointments(HttpExchange exchange) throws IOException {
        Map<String, String> query = getQueryParams(exchange);
        String search = query.get("search");
        String status = query.get("status");

        List<Appointment> list = appointmentService.getAllAppointments(search, status);
        sendSuccess(exchange, 200, "Appointments retrieved successfully.", list);
    }

    private void handleCreateAppointment(HttpExchange exchange, User staffUser) throws IOException {
        AppointmentRequest req = parseRequestBody(exchange, AppointmentRequest.class);
        Appointment created = appointmentService.createAppointment(req, staffUser.getUsername());
        sendSuccess(exchange, 201, "Appointment created successfully.", created);
    }

    private void handleGetAppointment(HttpExchange exchange, String appointmentNumber) throws IOException {
        Appointment appointment = appointmentService.getAppointmentByNumber(appointmentNumber);
        sendSuccess(exchange, 200, "Appointment found.", appointment);
    }

    private void handleUpdateStatus(HttpExchange exchange, String appointmentNumber) throws IOException {
        StatusUpdateRequest req = parseRequestBody(exchange, StatusUpdateRequest.class);
        if (req == null || req.getStatus() == null) {
            sendError(exchange, 400, "Please provide a valid status (e.g. COMPLETED or CANCELLED).");
            return;
        }
        Appointment updated = appointmentService.updateStatus(appointmentNumber, req.getStatus());
        sendSuccess(exchange, 200, "Appointment status updated to " + req.getStatus() + ".", updated);
    }
}
