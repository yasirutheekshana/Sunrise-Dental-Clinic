package com.sunrisedental.service;

import com.sunrisedental.dto.LoginRequest;
import com.sunrisedental.dto.SignupRequest;
import com.sunrisedental.dto.UserResponse;
import com.sunrisedental.exception.AuthenticationException;
import com.sunrisedental.exception.ConflictException;
import com.sunrisedental.exception.ValidationException;
import com.sunrisedental.model.User;
import com.sunrisedental.repository.UserRepository;
import com.sunrisedental.session.SessionManager;
import com.sunrisedental.util.PasswordUtil;
import com.sunrisedental.util.ValidationUtil;

import java.time.Instant;
import java.util.UUID;

/**
 * Service encapsulating authentication, registration, password hashing, and session management.
 */
public class AuthService {
    private final UserRepository userRepository;
    private final SessionManager sessionManager;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.sessionManager = SessionManager.getInstance();
        seedDefaultAdmin();
    }

    /**
     * Seeds default development administrator account if no users exist.
     */
    public synchronized void seedDefaultAdmin() {
        if (userRepository.findAll().isEmpty()) {
            User admin = new User(
                    UUID.randomUUID().toString(),
                    "admin",
                    "System",
                    "Administrator",
                    "STF001",
                    PasswordUtil.hash("Admin@123"),
                    Instant.now().toString()
            );
            userRepository.add(admin);
            System.out.println("Initialized default development account (admin / Admin@123)");
        }
    }

    /**
     * Registers a new staff member account.
     */
    public synchronized UserResponse registerStaff(SignupRequest req) {
        if (req == null) {
            throw new ValidationException("Registration request cannot be empty.");
        }

        ValidationUtil.validateStaffSignup(
                req.getUsername(),
                req.getFirstName(),
                req.getLastName(),
                req.getStaffId(),
                req.getPassword(),
                req.getConfirmPassword()
        );

        String username = req.getUsername().trim();
        String staffId = req.getStaffId().trim();

        if (userRepository.existsByUsername(username)) {
            throw new ConflictException("Username '" + username + "' already exists. Please choose a different username.");
        }

        if (userRepository.existsByStaffId(staffId)) {
            throw new ConflictException("Staff ID '" + staffId + "' already exists. Please check your Staff ID.");
        }

        String passwordHash = PasswordUtil.hash(req.getPassword());
        User newUser = new User(
                UUID.randomUUID().toString(),
                username,
                req.getFirstName().trim(),
                req.getLastName().trim(),
                staffId,
                passwordHash,
                Instant.now().toString()
        );

        userRepository.add(newUser);
        return UserResponse.fromUser(newUser);
    }

    /**
     * Authenticates clinic staff credentials and initiates a session.
     */
    public UserResponse login(LoginRequest req) {
        if (req == null) {
            throw new ValidationException("Login credentials cannot be empty.");
        }
        if (req.getUsername() == null || req.getUsername().trim().isEmpty()) {
            throw new ValidationException("Please enter your username.");
        }
        if (req.getPassword() == null || req.getPassword().isEmpty()) {
            throw new ValidationException("Please enter your password.");
        }

        User user = userRepository.findByUsername(req.getUsername().trim())
                .orElseThrow(() -> new AuthenticationException("Invalid username or password."));

        if (!PasswordUtil.verify(req.getPassword(), user.getPasswordHash())) {
            throw new AuthenticationException("Invalid username or password.");
        }

        String token = sessionManager.createSession(user);
        return UserResponse.fromUserWithToken(user, token);
    }

    /**
     * Terminates an active session token.
     */
    public void logout(String sessionId) {
        sessionManager.invalidateSession(sessionId);
    }

    /**
     * Verifies that the provided session ID belongs to an active staff member.
     */
    public User authenticateSession(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new AuthenticationException("Authentication required. Please log in.");
        }
        User user = sessionManager.getUser(sessionId);
        if (user == null) {
            throw new AuthenticationException("Session expired or invalid. Please log in again.");
        }
        return user;
    }
}
