package com.prashant.ems.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    /**
     * Expected values: ADMIN, HR, EMPLOYEE
     * Will be mapped to ROLE_ADMIN, ROLE_HR, ROLE_EMPLOYEE internally.
     */
    @NotBlank(message = "Role is required")
    private String role;
}

