package com.prashant.ems.controller;

import com.prashant.ems.dto.EmployeeRequestDto;
import com.prashant.ems.dto.EmployeeResponseDto;
import com.prashant.ems.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponseDto createEmployee(@Valid @RequestBody EmployeeRequestDto dto) {
        return employeeService.createEmployee(dto);
    }

    @GetMapping("/{id}")
    public EmployeeResponseDto getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }

    @GetMapping
    public Page<EmployeeResponseDto> getEmployees(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return employeeService.getEmployees(name, departmentId, page, size, sortBy, sortDir);
    }

    @PutMapping("/{id}")
    public EmployeeResponseDto updateEmployee(@PathVariable Long id,
                                              @Valid @RequestBody EmployeeRequestDto dto) {
        return employeeService.updateEmployee(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }
}

