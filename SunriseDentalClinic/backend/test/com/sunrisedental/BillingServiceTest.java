package com.sunrisedental;

import com.sunrisedental.dto.AppointmentRequest;
import com.sunrisedental.dto.BillRequest;
import com.sunrisedental.exception.NotFoundException;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.AppointmentStatus;
import com.sunrisedental.model.Bill;
import com.sunrisedental.repository.AppointmentRepository;
import com.sunrisedental.repository.BillRepository;
import com.sunrisedental.repository.TreatmentRepository;
import com.sunrisedental.service.AppointmentService;
import com.sunrisedental.service.BillingService;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.Assert.*;

public class BillingServiceTest {
    private BillingService billingService;
    private AppointmentService appointmentService;
    private AppointmentRepository appointmentRepository;
    private BillRepository billRepository;
    private TreatmentRepository treatmentRepository;
    private Path tempDir;

    @Before
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("dental_test_bill");
        File aptFile = tempDir.resolve("appointments.json").toFile();
        File billFile = tempDir.resolve("bills.json").toFile();
        File trtFile = tempDir.resolve("treatments.json").toFile();

        appointmentRepository = new AppointmentRepository(aptFile.getAbsolutePath());
        billRepository = new BillRepository(billFile.getAbsolutePath());
        treatmentRepository = new TreatmentRepository(trtFile.getAbsolutePath());

        appointmentService = new AppointmentService(appointmentRepository);
        billingService = new BillingService(billRepository, appointmentRepository, treatmentRepository);
    }

    @Test
    public void testBillCalculationTreatmentPlusConsultation() {
        // Create an appointment with Dental Cleaning 
        AppointmentRequest req = new AppointmentRequest(
                "Kamal Silva",
                "Colombo 07",
                "0775556666",
                "Dr. K. Perera",
                "Dental Cleaning",
                LocalDate.now().plusDays(1).toString(),
                "15:00"
        );
        Appointment apt = appointmentService.createAppointment(req, "admin");

        // Generate Bill
        BillRequest billReq = new BillRequest(apt.getAppointmentNumber());
        Bill bill = billingService.generateBill(billReq, "admin");

        assertNotNull(bill);
        assertEquals(apt.getAppointmentNumber(), bill.getAppointmentNumber());
        assertEquals("Kamal Silva", bill.getPatientName());
        assertEquals("Dental Cleaning", bill.getTreatmentType());

        // Verify Treatment Cost
        assertEquals(new BigDecimal("4000.00"), bill.getTreatmentCost());
        assertEquals(new BigDecimal("1500.00"), bill.getConsultationFee());
        assertEquals(new BigDecimal("5500.00"), bill.getTotal());

        // Verify appointment transitioned to COMPLETED
        Appointment updatedApt = appointmentService.getAppointmentByNumber(apt.getAppointmentNumber());
        assertEquals(AppointmentStatus.COMPLETED, updatedApt.getStatus());
    }

    @Test
    public void testBigDecimalMonetaryPrecision() {
        // Test custom consultation fee with cents
        AppointmentRequest req = new AppointmentRequest(
                "Ruwan Dissanayake",
                "Nugegoda",
                "0778889999",
                "Dr. S. Silva",
                "Dental Consultation", // 1500.00
                LocalDate.now().plusDays(2).toString(),
                "09:30"
        );
        Appointment apt = appointmentService.createAppointment(req, "admin");

        BillRequest billReq = new BillRequest(apt.getAppointmentNumber(), new BigDecimal("1500.50"));
        Bill bill = billingService.generateBill(billReq, "admin");

        assertEquals(new BigDecimal("3000.50"), bill.getTotal());
    }

    @Test(expected = NotFoundException.class)
    public void testBillForNonExistentAppointment() {
        BillRequest billReq = new BillRequest("APT-9999-0000");
        billingService.generateBill(billReq, "admin");
    }
}
