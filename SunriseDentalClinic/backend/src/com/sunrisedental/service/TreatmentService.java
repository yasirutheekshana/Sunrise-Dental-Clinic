package com.sunrisedental.service;

import com.sunrisedental.exception.NotFoundException;
import com.sunrisedental.model.Treatment;
import com.sunrisedental.repository.TreatmentRepository;

import java.util.List;

/**
 * Service providing access to dental treatments and procedure pricing.
 */
public class TreatmentService {
    private final TreatmentRepository treatmentRepository;

    public TreatmentService(TreatmentRepository treatmentRepository) {
        this.treatmentRepository = treatmentRepository;
    }

    public List<Treatment> getAllTreatments() {
        return treatmentRepository.findAll();
    }

    public Treatment getTreatmentByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new NotFoundException("Treatment name cannot be empty.");
        }
        return treatmentRepository.findByTreatmentName(name.trim())
                .orElseThrow(() -> new NotFoundException("Treatment '" + name + "' not found."));
    }

    public Treatment getTreatmentById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new NotFoundException("Treatment ID cannot be empty.");
        }
        return treatmentRepository.findByTreatmentId(id.trim())
                .orElseThrow(() -> new NotFoundException("Treatment with ID '" + id + "' not found."));
    }
}
