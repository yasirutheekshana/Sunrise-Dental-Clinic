package com.sunrisedental.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/**
 * Data transfer object for creating a patient bill.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BillRequest {
    private String appointmentNumber;
    private BigDecimal customConsultationFee;

    public BillRequest() {
    }

    public BillRequest(String appointmentNumber) {
        this.appointmentNumber = appointmentNumber;
    }

    public BillRequest(String appointmentNumber, BigDecimal customConsultationFee) {
        this.appointmentNumber = appointmentNumber;
        this.customConsultationFee = customConsultationFee;
    }

    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(String appointmentNumber) {
        this.appointmentNumber = appointmentNumber;
    }

    public BigDecimal getCustomConsultationFee() {
        return customConsultationFee;
    }

    public void setCustomConsultationFee(BigDecimal customConsultationFee) {
        this.customConsultationFee = customConsultationFee;
    }
}
