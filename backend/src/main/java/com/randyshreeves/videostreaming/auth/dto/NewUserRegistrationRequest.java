package com.randyshreeves.videostreaming.auth.dto;

import com.randyshreeves.videostreaming.auth.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class NewUserRegistrationRequest {

    @NotBlank(
            message = "Username is required.",
            groups = ValidationGroups.Required.class
    )
    @Size(
            min = 3,
            max = 30,
            message = "Username must be 3-30 characters.",
            groups = ValidationGroups.Length.class
    )
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z0-9_]*$",
            message = "Username must start with a letter and only contain letters, numbers, and underscores.",
            groups = ValidationGroups.Format.class
    )
    private String username;

    @NotBlank(
            message = "Password is required.",
            groups = ValidationGroups.Required.class
    )
    @Size(
            min = 8,
            max = 30,
            message = "Password must be 8-30 characters.",
            groups = ValidationGroups.Length.class
    )
    private String password;

    public NewUserRegistrationRequest() {}

    public NewUserRegistrationRequest(String username, String password) {
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
