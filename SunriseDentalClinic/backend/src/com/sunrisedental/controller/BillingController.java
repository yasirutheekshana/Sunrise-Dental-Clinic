package com.sunrisedental.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sunrisedental.dto.BillRequest;
import com.sunrisedental.model.Bill;
import com.sunrisedental.model.User;
import com.sunrisedental.service.AuthService;
import com.sunrisedental.service.BillingService;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Controller handling bill calculation, invoice generation, and receipt retrieval.
 */
public class BillingController extends BaseController {
    private final BillingService billingService;
    private final AuthService authService;

    public BillingController(BillingService billingService, AuthService authService) {
        this.billingService = billingService;
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (handleCorsPreflight(exchange)) {
            return;
        }

        try {
            User staffUser = requireAuthenticatedUser(exchange, authService);

            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod().toUpperCase();

            if (path.equals("/api/bills") || path.equals("/api/bills/")) {
                if ("POST".equals(method)) {
                    handleGenerateBill(exchange, staffUser);
                } else {
                    sendError(exchange, 405, "Method Not Allowed");
                }
            } else if (path.equals("/api/bills/fee")) {
                if ("GET".equals(method)) {
                    sendSuccess(exchange, 200, "Consultation fee retrieved.",
                            Map.of("consultationFee", billingService.getDefaultConsultationFee()));
                } else {
                    sendError(exchange, 405, "Method Not Allowed");
                }
            } else if (path.startsWith("/api/bills/")) {
                String appointmentNumber = path.substring("/api/bills/".length());
                appointmentNumber = URLDecoder.decode(appointmentNumber, StandardCharsets.UTF_8);

                if ("GET".equals(method)) {
                    handleGetBill(exchange, appointmentNumber);
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

    private void handleGenerateBill(HttpExchange exchange, User staffUser) throws IOException {
        BillRequest req = parseRequestBody(exchange, BillRequest.class);
        Bill bill = billingService.generateBill(req, staffUser.getFullName().isEmpty() ? staffUser.getUsername() : staffUser.getFullName());
        sendSuccess(exchange, 201, "Bill calculated and generated successfully.", bill);
    }

    private void handleGetBill(HttpExchange exchange, String appointmentNumber) throws IOException {
        Bill bill = billingService.getBillByAppointmentNumber(appointmentNumber);
        sendSuccess(exchange, 200, "Bill retrieved.", bill);
    }
}
