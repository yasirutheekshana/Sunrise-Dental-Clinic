package com.sunrisedental.service;

import com.sunrisedental.dto.BillRequest;
import com.sunrisedental.exception.NotFoundException;
import com.sunrisedental.exception.ValidationException;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.AppointmentStatus;
import com.sunrisedental.model.Bill;
import com.sunrisedental.model.Treatment;
import com.sunrisedental.repository.AppointmentRepository;
import com.sunrisedental.repository.BillRepository;
import com.sunrisedental.repository.TreatmentRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Optional;

/**
 * Service managing patient billing calculations, receipt generation, and persistence.
 * Uses BigDecimal exclusively for accurate monetary arithmetic.
 */
public class BillingService {
    /**
     * Standard clinic consultation fee (Academic assumed demo value: Rs. 1,500.00).
     */
    public static final BigDecimal DEFAULT_CONSULTATION_FEE = new BigDecimal("1500.00").setScale(2, RoundingMode.HALF_UP);

    private final BillRepository billRepository;
    private final AppointmentRepository appointmentRepository;
    private final TreatmentRepository treatmentRepository;

    public BillingService(BillRepository billRepository,
                          AppointmentRepository appointmentRepository,
                          TreatmentRepository treatmentRepository) {
        this.billRepository = billRepository;
        this.appointmentRepository = appointmentRepository;
        this.treatmentRepository = treatmentRepository;
    }

    public BigDecimal getDefaultConsultationFee() {
        return DEFAULT_CONSULTATION_FEE;
    }

    /**
     * Calculates and creates a persistent bill for a dental appointment.
     */
    public synchronized Bill generateBill(BillRequest req, String staffUsername) {
        if (req == null || req.getAppointmentNumber() == null || req.getAppointmentNumber().trim().isEmpty()) {
            throw new ValidationException("Please provide an appointment number to generate bill.");
        }

        String aptNum = req.getAppointmentNumber().trim();

        // Check if bill already generated for this appointment
        Optional<Bill> existingBill = billRepository.findByAppointmentNumber(aptNum);
        if (existingBill.isPresent()) {
            return existingBill.get();
        }

        Appointment appointment = appointmentRepository.findByAppointmentNumber(aptNum)
                .orElseThrow(() -> new NotFoundException("Cannot generate bill: Appointment '" + aptNum + "' not found."));

        // Retrieve treatment cost
        Treatment treatment = treatmentRepository.findByTreatmentName(appointment.getTreatmentType())
                .orElseGet(() -> new Treatment("TRT-CUSTOM", appointment.getTreatmentType(), new BigDecimal("3000.00")));

        BigDecimal treatmentCost = treatment.getTreatmentCost().setScale(2, RoundingMode.HALF_UP);
        BigDecimal consultationFee = (req.getCustomConsultationFee() != null && req.getCustomConsultationFee().compareTo(BigDecimal.ZERO) >= 0)
                ? req.getCustomConsultationFee().setScale(2, RoundingMode.HALF_UP)
                : DEFAULT_CONSULTATION_FEE;

        // Calculation: Treatment Cost + Consultation Fee = Total
        BigDecimal total = treatmentCost.add(consultationFee).setScale(2, RoundingMode.HALF_UP);

        String billId = billRepository.generateNextBillId();

        Bill bill = new Bill(
                billId,
                appointment.getAppointmentNumber(),
                appointment.getPatientName(),
                appointment.getDentistName(),
                appointment.getTreatmentType(),
                treatmentCost,
                consultationFee,
                total,
                staffUsername != null ? staffUsername : "Staff",
                Instant.now().toString()
        );

        billRepository.add(bill);

        // Transition appointment status to COMPLETED if it was SCHEDULED
        if (appointment.getStatus() == AppointmentStatus.SCHEDULED) {
            appointment.setStatus(AppointmentStatus.COMPLETED);
            appointmentRepository.update(appointment);
        }

        return bill;
    }

    public Bill getBillByAppointmentNumber(String appointmentNumber) {
        if (appointmentNumber == null || appointmentNumber.trim().isEmpty()) {
            throw new ValidationException("Please provide an appointment number.");
        }
        return billRepository.findByAppointmentNumber(appointmentNumber.trim())
                .orElseThrow(() -> new NotFoundException("No bill found for appointment '" + appointmentNumber + "'."));
    }

    public Bill getBillById(String billId) {
        if (billId == null || billId.trim().isEmpty()) {
            throw new ValidationException("Please provide a Bill ID.");
        }
        return billRepository.findByBillId(billId.trim())
                .orElseThrow(() -> new NotFoundException("No bill found with ID '" + billId + "'."));
    }
}
