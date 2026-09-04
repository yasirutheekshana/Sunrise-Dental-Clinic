package com.sunrisedental.repository;

import com.sunrisedental.model.Treatment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing dental treatments and assumed academic costs in treatments.json.
 */
public class TreatmentRepository extends JsonFileRepository<Treatment> {

    public TreatmentRepository(String filePath) {
        super(filePath, Treatment.class);
        seedDefaultsIfEmpty();
    }

    public synchronized void seedDefaultsIfEmpty() {
        List<Treatment> existing = findAll();
        if (existing.isEmpty()) {
            List<Treatment> defaults = new ArrayList<>();
            defaults.add(new Treatment("TRT-001", "Dental Consultation", new BigDecimal("1500.00")));
            defaults.add(new Treatment("TRT-002", "Dental Cleaning", new BigDecimal("4000.00")));
            defaults.add(new Treatment("TRT-003", "Tooth Filling", new BigDecimal("5000.00")));
            defaults.add(new Treatment("TRT-004", "Tooth Extraction", new BigDecimal("6000.00")));
            defaults.add(new Treatment("TRT-005", "Root Canal Treatment", new BigDecimal("15000.00")));
            defaults.add(new Treatment("TRT-006", "Dental X-Ray", new BigDecimal("3000.00")));
            saveAll(defaults);
        }
    }

    public Optional<Treatment> findByTreatmentName(String name) {
        if (name == null) return Optional.empty();
        return findAll().stream()
                .filter(t -> t.getTreatmentName() != null && t.getTreatmentName().equalsIgnoreCase(name.trim()))
                .findFirst();
    }

    public Optional<Treatment> findByTreatmentId(String id) {
        if (id == null) return Optional.empty();
        return findAll().stream()
                .filter(t -> t.getTreatmentId() != null && t.getTreatmentId().equalsIgnoreCase(id.trim()))
                .findFirst();
    }
}
