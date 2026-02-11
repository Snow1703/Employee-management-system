package com.prashant.ems.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeRequestDto {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @PositiveOrZero(message = "Salary must be positive or zero")
    @NotNull(message = "Salary is required")
    private Double salary;

    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;

    @NotNull(message = "Department id is required")
    private Long departmentId;
}