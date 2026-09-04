package com.sunrisedental.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Model representing a dental treatment procedure and its assumed academic fee.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Treatment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String treatmentId;
    private String treatmentName;
    private BigDecimal treatmentCost;

    public Treatment() {
    }

    public Treatment(String treatmentId, String treatmentName, BigDecimal treatmentCost) {
        this.treatmentId = treatmentId;
        this.treatmentName = treatmentName;
        this.treatmentCost = treatmentCost;
    }

    public String getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(String treatmentId) {
        this.treatmentId = treatmentId;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public BigDecimal getTreatmentCost() {
        return treatmentCost;
    }

    public void setTreatmentCost(BigDecimal treatmentCost) {
        this.treatmentCost = treatmentCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Treatment treatment)) return false;
        return Objects.equals(treatmentId, treatment.treatmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(treatmentId);
    }

    @Override
    public String toString() {
        return "Treatment{" +
                "treatmentId='" + treatmentId + '\'' +
                ", treatmentName='" + treatmentName + '\'' +
                ", treatmentCost=" + treatmentCost +
                '}';
    }
}
