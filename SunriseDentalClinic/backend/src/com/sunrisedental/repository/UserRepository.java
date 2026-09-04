package com.sunrisedental.repository;

import com.sunrisedental.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing clinic staff User persistence in users.json.
 */
public class UserRepository extends JsonFileRepository<User> {

    public UserRepository(String filePath) {
        super(filePath, User.class);
    }

    public Optional<User> findByUsername(String username) {
        if (username == null) return Optional.empty();
        return findAll().stream()
                .filter(u -> u.getUsername() != null && u.getUsername().equalsIgnoreCase(username.trim()))
                .findFirst();
    }

    public Optional<User> findByStaffId(String staffId) {
        if (staffId == null) return Optional.empty();
        return findAll().stream()
                .filter(u -> u.getStaffId() != null && u.getStaffId().equalsIgnoreCase(staffId.trim()))
                .findFirst();
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    public boolean existsByStaffId(String staffId) {
        return findByStaffId(staffId).isPresent();
    }
}
