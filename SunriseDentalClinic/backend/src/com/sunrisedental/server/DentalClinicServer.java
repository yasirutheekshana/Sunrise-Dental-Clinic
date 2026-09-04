package com.sunrisedental.server;

import com.sun.net.httpserver.HttpServer;
import com.sunrisedental.controller.*;
import com.sunrisedental.repository.*;
import com.sunrisedental.service.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Main Pure Java HTTP Server integrating controllers, services, repositories,
 * and static frontend delivery using com.sun.net.httpserver.HttpServer.
 */
public class DentalClinicServer {
    private final ServerConfig config;
    private HttpServer server;

    private UserRepository userRepository;
    private AppointmentRepository appointmentRepository;
    private TreatmentRepository treatmentRepository;
    private BillRepository billRepository;

    private AuthService authService;
    private AppointmentService appointmentService;
    private TreatmentService treatmentService;
    private BillingService billingService;

    public DentalClinicServer(ServerConfig config) {
        this.config = config;
        initializeLayers();
    }

    private void initializeLayers() {
        // 1. Repositories
        this.userRepository = new UserRepository(config.getUsersFilePath());
        this.appointmentRepository = new AppointmentRepository(config.getAppointmentsFilePath());
        this.treatmentRepository = new TreatmentRepository(config.getTreatmentsFilePath());
        this.billRepository = new BillRepository(config.getBillsFilePath());

        // 2. Services
        this.authService = new AuthService(userRepository);
        this.appointmentService = new AppointmentService(appointmentRepository);
        this.treatmentService = new TreatmentService(treatmentRepository);
        this.billingService = new BillingService(billRepository, appointmentRepository, treatmentRepository);
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(config.getPort()), 0);

        // Register API Controllers
        AuthController authController = new AuthController(authService);
        AppointmentController appointmentController = new AppointmentController(appointmentService, authService);
        TreatmentController treatmentController = new TreatmentController(treatmentService, authService);
        BillingController billingController = new BillingController(billingService, authService);
        DashboardController dashboardController = new DashboardController(appointmentService, authService);

        server.createContext("/api/auth", authController);
        server.createContext("/api/appointments", appointmentController);
        server.createContext("/api/treatments", treatmentController);
        server.createContext("/api/bills", billingController);
        server.createContext("/api/dashboard", dashboardController);

        // Register Static File Handler for Frontend
        server.createContext("/", new StaticFileHandler(config.getFrontendDir()));

        // Multi-threaded executor
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();

        System.out.println("========================================");
        System.out.println(" Sunrise Dental Clinic Management System");
        System.out.println("========================================");
        System.out.println("Server started successfully.");
        System.out.println();
        System.out.println("Application URL:");
        System.out.println("http://localhost:" + config.getPort());
        System.out.println();
        System.out.println("Data Directory: " + config.getDataDir());
        System.out.println("Frontend Directory: " + config.getFrontendDir());
        System.out.println();
        System.out.println("Default Demo Credentials:");
        System.out.println("Username: admin");
        System.out.println("Password: Admin@123");
        System.out.println();
        System.out.println("Application ready.");
        System.out.println("Press Ctrl+C to stop the server.");
        System.out.println("========================================");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Dental Clinic Server stopped.");
        }
    }

    public ServerConfig getConfig() {
        return config;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public AppointmentService getAppointmentService() {
        return appointmentService;
    }

    public TreatmentService getTreatmentService() {
        return treatmentService;
    }

    public BillingService getBillingService() {
        return billingService;
    }
}
