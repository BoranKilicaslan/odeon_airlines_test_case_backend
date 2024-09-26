package com.odeon.odeon_airlines.dto;

public class LoginRequest {

    private String username;
    private String password;

    // Getters ve Setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
