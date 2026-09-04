package com.sunrisedental.service;

import com.sunrisedental.dto.AppointmentRequest;
import com.sunrisedental.dto.DashboardStats;
import com.sunrisedental.exception.ConflictException;
import com.sunrisedental.exception.NotFoundException;
import com.sunrisedental.exception.ValidationException;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.AppointmentStatus;
import com.sunrisedental.repository.AppointmentRepository;
import com.sunrisedental.util.AppointmentNumberGenerator;
import com.sunrisedental.util.ValidationUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Service managing appointment creation, double-booking validation, status transitions, and queries.
 */
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * Registers a new patient appointment after performing thorough validations
     * and double-booking conflict checks.
     */
    public synchronized Appointment createAppointment(AppointmentRequest req, String staffUsername) {
        if (req == null) {
            throw new ValidationException("Appointment details cannot be empty.");
        }

        ValidationUtil.validatePatientName(req.getPatientName());
        ValidationUtil.validateAddress(req.getAddress());
        String contactNumber = ValidationUtil.cleanAndValidateContactNumber(req.getContactNumber());
        ValidationUtil.validateDentist(req.getDentistName());
        ValidationUtil.validateTreatmentType(req.getTreatmentType());
        ValidationUtil.validateAppointmentDate(req.getAppointmentDate());
        ValidationUtil.validateAppointmentTime(req.getAppointmentTime());

        String dentist = req.getDentistName().trim();
        String date = req.getAppointmentDate().trim();
        String time = req.getAppointmentTime().trim();

        // 13. Double Booking Prevention
        Optional<Appointment> conflict = appointmentRepository.findConflictingAppointment(dentist, date, time);
        if (conflict.isPresent()) {
            throw new ConflictException("Appointment Conflict: " + dentist +
                    " already has an appointment at " + time + " on " + date +
                    ". Please select another time.");
        }

        List<Appointment> allExisting = appointmentRepository.findAll();
        String appointmentNumber = AppointmentNumberGenerator.generateNext(allExisting);

        Appointment appointment = new Appointment(
                appointmentNumber,
                req.getPatientName().trim(),
                req.getAddress().trim(),
                contactNumber,
                dentist,
                req.getTreatmentType().trim(),
                date,
                time,
                AppointmentStatus.SCHEDULED,
                staffUsername != null ? staffUsername : "Staff",
                Instant.now().toString()
        );

        appointmentRepository.add(appointment);
        return appointment;
    }

    public List<Appointment> getAllAppointments(String query, String status) {
        return appointmentRepository.search(query, status);
    }

    public Appointment getAppointmentByNumber(String appointmentNumber) {
        if (appointmentNumber == null || appointmentNumber.trim().isEmpty()) {
            throw new ValidationException("Please provide an appointment number.");
        }
        return appointmentRepository.findByAppointmentNumber(appointmentNumber.trim())
                .orElseThrow(() -> new NotFoundException("No appointment found for '" + appointmentNumber.trim() + "'. Please check the appointment number."));
    }

    public synchronized Appointment updateStatus(String appointmentNumber, AppointmentStatus newStatus) {
        if (newStatus == null) {
            throw new ValidationException("Status cannot be null.");
        }
        Appointment apt = getAppointmentByNumber(appointmentNumber);
        apt.setStatus(newStatus);
        boolean updated = appointmentRepository.update(apt);
        if (!updated) {
            throw new NotFoundException("Failed to update appointment '" + appointmentNumber + "'.");
        }
        return apt;
    }

    public String getNextAppointmentNumber() {
        return AppointmentNumberGenerator.generateNext(appointmentRepository.findAll());
    }

    public DashboardStats getDashboardStats() {
        List<Appointment> all = appointmentRepository.findAll();
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        int todayCount = 0;
        int scheduledCount = 0;
        int completedCount = 0;
        int cancelledCount = 0;

        for (Appointment a : all) {
            if (today.equals(a.getAppointmentDate())) {
                todayCount++;
            }
            if (a.getStatus() == AppointmentStatus.SCHEDULED) {
                scheduledCount++;
            } else if (a.getStatus() == AppointmentStatus.COMPLETED) {
                completedCount++;
            } else if (a.getStatus() == AppointmentStatus.CANCELLED) {
                cancelledCount++;
            }
        }

        return new DashboardStats(
                todayCount,
                all.size(),
                scheduledCount,
                completedCount,
                cancelledCount,
                today
        );
    }
}
