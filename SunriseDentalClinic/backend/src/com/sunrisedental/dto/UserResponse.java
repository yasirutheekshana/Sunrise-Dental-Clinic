package com.sunrisedental.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sunrisedental.model.User;

/**
 * Safe user profile data transfer object returned to client.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String staffId;
    private String token;

    public UserResponse() {
    }

    public UserResponse(User user, String token) {
        if (user != null) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.staffId = user.getStaffId();
        }
        this.token = token;
    }

    public static UserResponse fromUser(User user) {
        return new UserResponse(user, null);
    }

    public static UserResponse fromUserWithToken(User user, String token) {
        return new UserResponse(user, token);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
