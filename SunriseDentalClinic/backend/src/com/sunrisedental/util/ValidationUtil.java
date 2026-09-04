package com.sunrisedental.util;

import com.sunrisedental.exception.ValidationException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Centralized validation utility for inputs and domain constraints across the system.
 */
public final class ValidationUtil {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_.-]{3,30}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z.\\s'-]{2,100}$");
    private static final Pattern STAFF_ID_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{3,20}$");
    private static final Pattern SRI_LANKAN_PHONE_PATTERN = Pattern.compile("^(?:\\+94|0)[1-9][0-9]{8}$");
    private static final Pattern TIME_PATTERN = Pattern.compile("^(?:[01]?[0-9]|2[0-3]):[0-5][0-9]$");

    private ValidationUtil() {
    }

    public static void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Please enter your username.");
        }
        String clean = username.trim();
        if (!USERNAME_PATTERN.matcher(clean).matches()) {
            throw new ValidationException("Username must be between 3 and 30 characters (letters, numbers, underscores, dashes, dots).");
        }
    }

    public static void validateStaffSignup(String username, String firstName, String lastName,
                                          String staffId, String password, String confirmPassword) {
        validateUsername(username);

        if (firstName == null || firstName.trim().isEmpty()) {
            throw new ValidationException("Please enter your first name.");
        }
        if (!NAME_PATTERN.matcher(firstName.trim()).matches()) {
            throw new ValidationException("First name should contain only alphabetic characters.");
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            throw new ValidationException("Please enter your last name.");
        }
        if (!NAME_PATTERN.matcher(lastName.trim()).matches()) {
            throw new ValidationException("Last name should contain only alphabetic characters.");
        }

        if (staffId == null || staffId.trim().isEmpty()) {
            throw new ValidationException("Please enter your Staff ID.");
        }
        if (!STAFF_ID_PATTERN.matcher(staffId.trim()).matches()) {
            throw new ValidationException("Staff ID format is invalid (e.g. STF001).");
        }

        if (password == null || password.isEmpty()) {
            throw new ValidationException("Please enter your password.");
        }
        if (password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long.");
        }

        if (confirmPassword == null || !password.equals(confirmPassword)) {
            throw new ValidationException("Passwords do not match.");
        }
    }

    public static void validatePatientName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Please enter the patient's name.");
        }
        String clean = name.trim();
        if (clean.length() < 2 || clean.length() > 100 || !NAME_PATTERN.matcher(clean).matches()) {
            throw new ValidationException("Patient name must be between 2 and 100 alphabetic characters.");
        }
    }

    public static void validateAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new ValidationException("Please enter the patient's address.");
        }
        if (address.trim().length() < 3) {
            throw new ValidationException("Please enter a valid address (minimum 3 characters).");
        }
    }

    public static String cleanAndValidateContactNumber(String contactNumber) {
        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            throw new ValidationException("Please enter a valid contact number.");
        }
        // Strip spaces, parentheses, and dashes
        String sanitized = contactNumber.trim().replaceAll("[\\s()-]", "");
        if (!SRI_LANKAN_PHONE_PATTERN.matcher(sanitized).matches()) {
            throw new ValidationException("Invalid contact number. Please enter a valid 10-digit Sri Lankan phone number (e.g., 0771234567 or +94771234567).");
        }
        return sanitized;
    }

    public static void validateDentist(String dentistName) {
        if (dentistName == null || dentistName.trim().isEmpty()) {
            throw new ValidationException("Please select a dentist.");
        }
    }

    public static void validateTreatmentType(String treatmentType) {
        if (treatmentType == null || treatmentType.trim().isEmpty()) {
            throw new ValidationException("Please select a treatment.");
        }
    }

    public static void validateAppointmentDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new ValidationException("Please select an appointment date.");
        }
        try {
            LocalDate date = LocalDate.parse(dateStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate today = LocalDate.now();
            if (date.isBefore(today)) {
                throw new ValidationException("Appointment date cannot be in the past. Please select today or a future date.");
            }
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid appointment date format. Please use YYYY-MM-DD.");
        }
    }

    public static void validateAppointmentTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            throw new ValidationException("Please select an appointment time.");
        }
        String clean = timeStr.trim();
        if (!TIME_PATTERN.matcher(clean).matches()) {
            throw new ValidationException("Invalid appointment time format. Please use HH:mm (e.g. 10:00).");
        }
        try {
            LocalTime.parse(clean, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid appointment time value.");
        }
    }
}
