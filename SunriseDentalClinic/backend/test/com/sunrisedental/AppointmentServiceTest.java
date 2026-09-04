package com.sunrisedental;

import com.sunrisedental.dto.AppointmentRequest;
import com.sunrisedental.exception.ConflictException;
import com.sunrisedental.exception.NotFoundException;
import com.sunrisedental.exception.ValidationException;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.AppointmentStatus;
import com.sunrisedental.repository.AppointmentRepository;
import com.sunrisedental.service.AppointmentService;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.Assert.*;

public class AppointmentServiceTest {
    private AppointmentService appointmentService;
    private AppointmentRepository appointmentRepository;
    private Path tempDir;
    private String futureDate;

    @Before
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("dental_test_apt");
        File aptFile = tempDir.resolve("appointments.json").toFile();
        appointmentRepository = new AppointmentRepository(aptFile.getAbsolutePath());
        appointmentService = new AppointmentService(appointmentRepository);
        futureDate = LocalDate.now().plusDays(3).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @Test
    public void testSuccessfulAppointmentCreation() {
        AppointmentRequest req = new AppointmentRequest(
                "Nimal Fernando",
                "123 Galle Road, Colombo 03",
                "0771234567",
                "Dr. K. Perera",
                "Dental Cleaning",
                futureDate,
                "10:00"
        );

        Appointment apt = appointmentService.createAppointment(req, "admin");
        assertNotNull(apt);
        assertNotNull(apt.getAppointmentNumber());
        assertTrue(apt.getAppointmentNumber().startsWith("APT-"));
        assertEquals("Nimal Fernando", apt.getPatientName());
        assertEquals("0771234567", apt.getContactNumber());
        assertEquals(AppointmentStatus.SCHEDULED, apt.getStatus());
    }

    @Test(expected = ValidationException.class)
    public void testMissingPatientName() {
        AppointmentRequest req = new AppointmentRequest(
                "",
                "123 Galle Road, Colombo 03",
                "0771234567",
                "Dr. K. Perera",
                "Dental Cleaning",
                futureDate,
                "10:00"
        );
        appointmentService.createAppointment(req, "admin");
    }

    @Test(expected = ValidationException.class)
    public void testInvalidContactNumber() {
        AppointmentRequest req = new AppointmentRequest(
                "Nimal Fernando",
                "123 Galle Road",
                "12345", // invalid format
                "Dr. K. Perera",
                "Dental Cleaning",
                futureDate,
                "10:00"
        );
        appointmentService.createAppointment(req, "admin");
    }

    @Test(expected = ValidationException.class)
    public void testPastAppointmentDate() {
        AppointmentRequest req = new AppointmentRequest(
                "Nimal Fernando",
                "123 Galle Road",
                "0771234567",
                "Dr. K. Perera",
                "Dental Cleaning",
                "2020-01-01", // in the past
                "10:00"
        );
        appointmentService.createAppointment(req, "admin");
    }

    @Test
    public void testDentistDoubleBookingConflict() {
        AppointmentRequest req1 = new AppointmentRequest(
                "First Patient",
                "Colombo 05",
                "0771112222",
                "Dr. K. Perera",
                "Dental Cleaning",
                futureDate,
                "11:00"
        );
        appointmentService.createAppointment(req1, "admin");

        // Attempt second booking with same dentist, same date, same time
        AppointmentRequest req2 = new AppointmentRequest(
                "Second Patient",
                "Kandy Road, Colombo",
                "0773334444",
                "Dr. K. Perera",
                "Tooth Filling",
                futureDate,
                "11:00"
        );

        try {
            appointmentService.createAppointment(req2, "admin");
            fail("Expected ConflictException for double-booking was not thrown");
        } catch (ConflictException ex) {
            assertTrue(ex.getMessage().contains("Appointment Conflict"));
            assertTrue(ex.getMessage().contains("already has an appointment"));
        }
    }

    @Test
    public void testAppointmentSearchAndLookup() {
        AppointmentRequest req = new AppointmentRequest(
                "Sunil Shantha",
                "Moratuwa",
                "0719876543",
                "Dr. S. Silva",
                "Tooth Extraction",
                futureDate,
                "14:00"
        );
        Appointment created = appointmentService.createAppointment(req, "admin");

        Appointment found = appointmentService.getAppointmentByNumber(created.getAppointmentNumber());
        assertNotNull(found);
        assertEquals(created.getAppointmentNumber(), found.getAppointmentNumber());
        assertEquals("Sunil Shantha", found.getPatientName());

        List<Appointment> results = appointmentService.getAllAppointments("Sunil", "SCHEDULED");
        assertEquals(1, results.size());
    }

    @Test(expected = NotFoundException.class)
    public void testAppointmentNotFound() {
        appointmentService.getAppointmentByNumber("APT-9999-9999");
    }
}
