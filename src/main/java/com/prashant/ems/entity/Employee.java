package com.prashant.ems.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Column(unique = true, nullable = false)
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @PositiveOrZero(message = "Salary must be positive or zero")
    private Double salary;

    private LocalDate joiningDate;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}