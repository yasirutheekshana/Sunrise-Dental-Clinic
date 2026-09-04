package com.sunrisedental.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sunrisedental.model.Treatment;
import com.sunrisedental.service.AuthService;
import com.sunrisedental.service.TreatmentService;

import java.io.IOException;
import java.util.List;

/**
 * Controller for retrieving available clinic treatments and academic demo prices.
 */
public class TreatmentController extends BaseController {
    private final TreatmentService treatmentService;
    private final AuthService authService;

    public TreatmentController(TreatmentService treatmentService, AuthService authService) {
        this.treatmentService = treatmentService;
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (handleCorsPreflight(exchange)) {
            return;
        }

        try {
            // Protect endpoint
            requireAuthenticatedUser(exchange, authService);

            String method = exchange.getRequestMethod().toUpperCase();
            if ("GET".equals(method)) {
                List<Treatment> treatments = treatmentService.getAllTreatments();
                sendSuccess(exchange, 200, "Treatments retrieved successfully.", treatments);
            } else {
                sendError(exchange, 405, "Method Not Allowed");
            }
        } catch (Exception e) {
            handleException(exchange, e);
        }
    }
}
