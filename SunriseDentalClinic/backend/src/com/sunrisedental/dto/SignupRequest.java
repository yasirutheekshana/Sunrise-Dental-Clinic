package com.sunrisedental.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Data transfer object for staff registration.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignupRequest {
    private String username;
    private String firstName;
    private String lastName;
    private String staffId;
    private String password;
    private String confirmPassword;

    public SignupRequest() {
    }

    public SignupRequest(String username, String firstName, String lastName, String staffId, String password, String confirmPassword) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.staffId = staffId;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
