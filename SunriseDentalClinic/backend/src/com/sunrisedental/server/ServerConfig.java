package com.sunrisedental.server;

import java.io.File;

/**
 * Server configuration holder for ports and filesystem paths.
 */
public class ServerConfig {
    private final int port;
    private final String dataDir;
    private final String frontendDir;

    public ServerConfig() {
        this.port = Integer.parseInt(System.getProperty("server.port", "8080"));
        
        // Compute relative or resolved dataDir
        String baseDir = System.getProperty("user.dir");
        // If running from SunriseDentalClinic root or backend root
        File directData = new File("data");
        File backendData = new File("backend" + File.separator + "data");

        if (directData.exists() || !backendData.exists()) {
            this.dataDir = directData.getAbsolutePath();
        } else {
            this.dataDir = backendData.getAbsolutePath();
        }

        // Determine frontend location
        File directFrontend = new File("frontend");
        File parentFrontend = new File(".." + File.separator + "frontend");
        File subFrontend = new File("SunriseDentalClinic" + File.separator + "frontend");

        if (directFrontend.exists() && directFrontend.isDirectory()) {
            this.frontendDir = directFrontend.getAbsolutePath();
        } else if (parentFrontend.exists() && parentFrontend.isDirectory()) {
            this.frontendDir = parentFrontend.getAbsolutePath();
        } else if (subFrontend.exists() && subFrontend.isDirectory()) {
            this.frontendDir = subFrontend.getAbsolutePath();
        } else {
            this.frontendDir = new File("frontend").getAbsolutePath();
        }
    }

    public int getPort() {
        return port;
    }

    public String getDataDir() {
        return dataDir;
    }

    public String getFrontendDir() {
        return frontendDir;
    }

    public String getUsersFilePath() {
        return dataDir + File.separator + "users.json";
    }

    public String getAppointmentsFilePath() {
        return dataDir + File.separator + "appointments.json";
    }

    public String getTreatmentsFilePath() {
        return dataDir + File.separator + "treatments.json";
    }

    public String getBillsFilePath() {
        return dataDir + File.separator + "bills.json";
    }
}
