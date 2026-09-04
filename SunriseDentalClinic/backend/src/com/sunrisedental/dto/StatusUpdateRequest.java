package com.sunrisedental.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sunrisedental.model.AppointmentStatus;

/**
 * DTO for updating an appointment's status (e.g. COMPLETED or CANCELLED).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusUpdateRequest {
    private AppointmentStatus status;

    public StatusUpdateRequest() {
    }

    public StatusUpdateRequest(AppointmentStatus status) {
        this.status = status;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }
}
