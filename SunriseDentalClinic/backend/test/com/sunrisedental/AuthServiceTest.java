package com.sunrisedental;

import com.sunrisedental.dto.LoginRequest;
import com.sunrisedental.dto.SignupRequest;
import com.sunrisedental.dto.UserResponse;
import com.sunrisedental.exception.AuthenticationException;
import com.sunrisedental.exception.ConflictException;
import com.sunrisedental.exception.ValidationException;
import com.sunrisedental.repository.UserRepository;
import com.sunrisedental.service.AuthService;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class AuthServiceTest {
    private AuthService authService;
    private UserRepository userRepository;
    private Path tempDir;

    @Before
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("dental_test_auth");
        File usersFile = tempDir.resolve("users.json").toFile();
        userRepository = new UserRepository(usersFile.getAbsolutePath());
        authService = new AuthService(userRepository);
    }

    @Test
    public void testSuccessfulSignupAndLogin() {
        SignupRequest signup = new SignupRequest(
                "drperera", "Kasun", "Perera", "STF101", "Secret123", "Secret123"
        );
        UserResponse response = authService.registerStaff(signup);
        assertNotNull(response);
        assertEquals("drperera", response.getUsername());
        assertEquals("STF101", response.getStaffId());

        // Test login with created user
        LoginRequest login = new LoginRequest("drperera", "Secret123");
        UserResponse loginResponse = authService.login(login);
        assertNotNull(loginResponse);
        assertNotNull(loginResponse.getToken());
        assertEquals("drperera", loginResponse.getUsername());
    }

    @Test(expected = AuthenticationException.class)
    public void testInvalidLoginPassword() {
        LoginRequest login = new LoginRequest("admin", "WrongPassword!");
        authService.login(login);
    }

    @Test(expected = ConflictException.class)
    public void testDuplicateUsername() {
        SignupRequest signup1 = new SignupRequest(
                "nurse_amal", "Amal", "Silva", "STF201", "Nurse@123", "Nurse@123"
        );
        authService.registerStaff(signup1);

        SignupRequest signup2 = new SignupRequest(
                "nurse_amal", "Amal", "Different", "STF202", "Nurse@123", "Nurse@123"
        );
        authService.registerStaff(signup2);
    }

    @Test(expected = ConflictException.class)
    public void testDuplicateStaffId() {
        SignupRequest signup1 = new SignupRequest(
                "user_one", "User", "One", "STF999", "Pass@123", "Pass@123"
        );
        authService.registerStaff(signup1);

        SignupRequest signup2 = new SignupRequest(
                "user_two", "User", "Two", "STF999", "Pass@123", "Pass@123"
        );
        authService.registerStaff(signup2);
    }

    @Test(expected = ValidationException.class)
    public void testPasswordMismatch() {
        SignupRequest signup = new SignupRequest(
                "johndoe", "John", "Doe", "STF303", "Password123", "MismatchPassword"
        );
        authService.registerStaff(signup);
    }
}
