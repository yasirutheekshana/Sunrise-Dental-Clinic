package com.sunrisedental.dto;

import java.io.Serializable;

/**
 * Data transfer object for clinic dashboard metrics.
 */
public class DashboardStats implements Serializable {
    private static final long serialVersionUID = 1L;

    private int todayAppointments;
    private int totalAppointments;
    private int scheduledAppointments;
    private int completedAppointments;
    private int cancelledAppointments;
    private String currentDate;

    public DashboardStats() {
    }

    public DashboardStats(int todayAppointments, int totalAppointments, int scheduledAppointments,
                          int completedAppointments, int cancelledAppointments, String currentDate) {
        this.todayAppointments = todayAppointments;
        this.totalAppointments = totalAppointments;
        this.scheduledAppointments = scheduledAppointments;
        this.completedAppointments = completedAppointments;
        this.cancelledAppointments = cancelledAppointments;
        this.currentDate = currentDate;
    }

    public int getTodayAppointments() {
        return todayAppointments;
    }

    public void setTodayAppointments(int todayAppointments) {
        this.todayAppointments = todayAppointments;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(int totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public int getScheduledAppointments() {
        return scheduledAppointments;
    }

    public void setScheduledAppointments(int scheduledAppointments) {
        this.scheduledAppointments = scheduledAppointments;
    }

    public int getCompletedAppointments() {
        return completedAppointments;
    }

    public void setCompletedAppointments(int completedAppointments) {
        this.completedAppointments = completedAppointments;
    }

    public int getCancelledAppointments() {
        return cancelledAppointments;
    }

    public void setCancelledAppointments(int cancelledAppointments) {
        this.cancelledAppointments = cancelledAppointments;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
}
