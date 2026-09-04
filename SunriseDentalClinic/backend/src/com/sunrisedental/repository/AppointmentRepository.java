package com.sunrisedental.repository;

import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.AppointmentStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository for persisting and querying appointments in appointments.json.
 */
public class AppointmentRepository extends JsonFileRepository<Appointment> {

    public AppointmentRepository(String filePath) {
        super(filePath, Appointment.class);
    }

    public Optional<Appointment> findByAppointmentNumber(String appointmentNumber) {
        if (appointmentNumber == null) return Optional.empty();
        return findAll().stream()
                .filter(a -> a.getAppointmentNumber() != null &&
                        a.getAppointmentNumber().equalsIgnoreCase(appointmentNumber.trim()))
                .findFirst();
    }

    /**
     * Finds any existing non-cancelled appointment for the exact dentist, date, and time.
     */
    public Optional<Appointment> findConflictingAppointment(String dentistName, String date, String time) {
        if (dentistName == null || date == null || time == null) {
            return Optional.empty();
        }
        String cleanDentist = dentistName.trim().toLowerCase();
        String cleanDate = date.trim();
        String cleanTime = time.trim();

        return findAll().stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .filter(a -> a.getDentistName() != null && a.getDentistName().trim().equalsIgnoreCase(cleanDentist))
                .filter(a -> a.getAppointmentDate() != null && a.getAppointmentDate().trim().equals(cleanDate))
                .filter(a -> a.getAppointmentTime() != null && a.getAppointmentTime().trim().equals(cleanTime))
                .findFirst();
    }

    public synchronized boolean update(Appointment updated) {
        if (updated == null || updated.getAppointmentNumber() == null) return false;
        List<Appointment> list = findAll();
        boolean found = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getAppointmentNumber().equalsIgnoreCase(updated.getAppointmentNumber())) {
                list.set(i, updated);
                found = true;
                break;
            }
        }
        if (found) {
            saveAll(list);
        }
        return found;
    }

    public List<Appointment> search(String query, String statusStr) {
        List<Appointment> all = findAll();
        return all.stream()
                .filter(a -> {
                    if (statusStr != null && !statusStr.trim().isEmpty() && !statusStr.equalsIgnoreCase("ALL")) {
                        try {
                            AppointmentStatus filterStatus = AppointmentStatus.valueOf(statusStr.trim().toUpperCase());
                            if (a.getStatus() != filterStatus) return false;
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                    if (query != null && !query.trim().isEmpty()) {
                        String q = query.trim().toLowerCase();
                        boolean matchNumber = a.getAppointmentNumber() != null && a.getAppointmentNumber().toLowerCase().contains(q);
                        boolean matchPatient = a.getPatientName() != null && a.getPatientName().toLowerCase().contains(q);
                        boolean matchDentist = a.getDentistName() != null && a.getDentistName().toLowerCase().contains(q);
                        boolean matchTreatment = a.getTreatmentType() != null && a.getTreatmentType().toLowerCase().contains(q);
                        boolean matchContact = a.getContactNumber() != null && a.getContactNumber().contains(q);
                        return matchNumber || matchPatient || matchDentist || matchTreatment || matchContact;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }
}
