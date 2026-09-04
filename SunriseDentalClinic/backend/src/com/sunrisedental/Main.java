package com.sunrisedental;

import com.sunrisedental.server.DentalClinicServer;
import com.sunrisedental.server.ServerConfig;

/**
 * Main application entry point for Sunrise Dental Clinic Management System.
 */
public class Main {
    public static void main(String[] args) {
        try {
            ServerConfig config = new ServerConfig();
            DentalClinicServer server = new DentalClinicServer(config);
            server.start();

            // Register shutdown hook for safe exit
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nShutting down Sunrise Dental Clinic server safely...");
                server.stop();
            }));

        } catch (Exception e) {
            System.err.println("Fatal error starting Sunrise Dental Clinic Server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
