// filepath: /Users/prashant/Downloads/ems/src/main/java/com/prashant/ems/dto/EmployeeResponseDto.java
package com.prashant.ems.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class EmployeeResponseDto {
    private Long id;
    private String name;
    private String email;
    private Double salary;
    private LocalDate joiningDate;
    private Long departmentId;
    private String departmentName;
}