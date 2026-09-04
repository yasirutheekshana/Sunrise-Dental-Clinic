package com.sunrisedental.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Model representing a patient dental bill and printable receipt record.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Bill implements Serializable {
    private static final long serialVersionUID = 1L;

    private String billId;
    private String appointmentNumber;
    private String patientName;
    private String dentistName;
    private String treatmentType;
    private BigDecimal treatmentCost;
    private BigDecimal consultationFee;
    private BigDecimal total;
    private String generatedBy;
    private String generatedAt;

    public Bill() {
    }

    public Bill(String billId, String appointmentNumber, String patientName, String dentistName,
                String treatmentType, BigDecimal treatmentCost, BigDecimal consultationFee,
                BigDecimal total, String generatedBy, String generatedAt) {
        this.billId = billId;
        this.appointmentNumber = appointmentNumber;
        this.patientName = patientName;
        this.dentistName = dentistName;
        this.treatmentType = treatmentType;
        this.treatmentCost = treatmentCost;
        this.consultationFee = consultationFee;
        this.total = total;
        this.generatedBy = generatedBy;
        this.generatedAt = generatedAt;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(String appointmentNumber) {
        this.appointmentNumber = appointmentNumber;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDentistName() {
        return dentistName;
    }

    public void setDentistName(String dentistName) {
        this.dentistName = dentistName;
    }

    public String getTreatmentType() {
        return treatmentType;
    }

    public void setTreatmentType(String treatmentType) {
        this.treatmentType = treatmentType;
    }

    public BigDecimal getTreatmentCost() {
        return treatmentCost;
    }

    public void setTreatmentCost(BigDecimal treatmentCost) {
        this.treatmentCost = treatmentCost;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bill bill)) return false;
        return Objects.equals(billId, bill.billId) || Objects.equals(appointmentNumber, bill.appointmentNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(billId, appointmentNumber);
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billId='" + billId + '\'' +
                ", appointmentNumber='" + appointmentNumber + '\'' +
                ", patientName='" + patientName + '\'' +
                ", treatmentCost=" + treatmentCost +
                ", consultationFee=" + consultationFee +
                ", total=" + total +
                '}';
    }
}
