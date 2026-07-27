package com.randyshreeves.videostreaming.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    @NotBlank(message = "Username is required.")
    @Size(min = 3, max = 30, message = "Username must be 3-30 characters.")
    private String username;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 30, message = "Password must be 8-30 characters.")
    private String password;

    public LoginRequest() {}

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

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
