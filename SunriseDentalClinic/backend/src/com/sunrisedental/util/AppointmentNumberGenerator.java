package com.sunrisedental.util;

import com.sunrisedental.model.Appointment;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generates sequential, standardized appointment numbers in the format:
 * APT-YYYY-XXXX (e.g. APT-2026-0001).
 */
public final class AppointmentNumberGenerator {
    private static final Pattern APPOINTMENT_PATTERN = Pattern.compile("^APT-(\\d{4})-(\\d{4})$");

    private AppointmentNumberGenerator() {
    }

    /**
     * Inspects existing appointments for the current year and generates the next sequential number.
     *
     * @param existingAppointments List of existing appointments
     * @return Next formatted appointment number string
     */
    public static synchronized String generateNext(List<Appointment> existingAppointments) {
        int currentYear = LocalDate.now().getYear();
        int maxSequence = 0;

        if (existingAppointments != null) {
            for (Appointment apt : existingAppointments) {
                if (apt == null || apt.getAppointmentNumber() == null) continue;
                Matcher matcher = APPOINTMENT_PATTERN.matcher(apt.getAppointmentNumber());
                if (matcher.matches()) {
                    int year = Integer.parseInt(matcher.group(1));
                    int seq = Integer.parseInt(matcher.group(2));
                    if (year == currentYear && seq > maxSequence) {
                        maxSequence = seq;
                    }
                }
            }
        }

        int nextSequence = maxSequence + 1;
        return String.format("APT-%04d-%04d", currentYear, nextSequence);
    }
}
