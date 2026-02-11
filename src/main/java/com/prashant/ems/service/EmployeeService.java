package com.prashant.ems.service;

import com.prashant.ems.dto.EmployeeRequestDto;
import com.prashant.ems.dto.EmployeeResponseDto;
import org.springframework.data.domain.Page;

public interface EmployeeService {

    EmployeeResponseDto createEmployee(EmployeeRequestDto dto);

    EmployeeResponseDto getEmployeeById(Long id);

    Page<EmployeeResponseDto> getEmployees(String name,
                                           Long departmentId,
                                           int page,
                                           int size,
                                           String sortBy,
                                           String sortDir);

    EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto dto);

    void deleteEmployee(Long id);
}
