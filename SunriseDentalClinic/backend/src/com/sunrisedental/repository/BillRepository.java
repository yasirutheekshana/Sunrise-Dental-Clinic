package com.sunrisedental.repository;

import com.sunrisedental.model.Bill;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Repository for persisting and retrieving clinic billing records in bills.json.
 */
public class BillRepository extends JsonFileRepository<Bill> {
    private static final Pattern BILL_ID_PATTERN = Pattern.compile("^BIL-(\\d{4})-(\\d{4})$");

    public BillRepository(String filePath) {
        super(filePath, Bill.class);
    }

    public Optional<Bill> findByAppointmentNumber(String appointmentNumber) {
        if (appointmentNumber == null) return Optional.empty();
        return findAll().stream()
                .filter(b -> b.getAppointmentNumber() != null &&
                        b.getAppointmentNumber().equalsIgnoreCase(appointmentNumber.trim()))
                .findFirst();
    }

    public Optional<Bill> findByBillId(String billId) {
        if (billId == null) return Optional.empty();
        return findAll().stream()
                .filter(b -> b.getBillId() != null && b.getBillId().equalsIgnoreCase(billId.trim()))
                .findFirst();
    }

    public synchronized String generateNextBillId() {
        int currentYear = LocalDate.now().getYear();
        int maxSeq = 0;
        List<Bill> all = findAll();
        for (Bill b : all) {
            if (b.getBillId() != null) {
                Matcher m = BILL_ID_PATTERN.matcher(b.getBillId());
                if (m.matches()) {
                    int year = Integer.parseInt(m.group(1));
                    int seq = Integer.parseInt(m.group(2));
                    if (year == currentYear && seq > maxSeq) {
                        maxSeq = seq;
                    }
                }
            }
        }
        return String.format("BIL-%04d-%04d", currentYear, maxSeq + 1);
    }
}
