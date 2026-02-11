package com.prashant.ems.service.impl;

import com.prashant.ems.dto.EmployeeRequestDto;
import com.prashant.ems.dto.EmployeeResponseDto;
import com.prashant.ems.entity.Department;
import com.prashant.ems.entity.Employee;
import com.prashant.ems.exception.ResourceNotFoundException;
import com.prashant.ems.repository.DepartmentRepository;
import com.prashant.ems.repository.EmployeeRepository;
import com.prashant.ems.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public EmployeeResponseDto createEmployee(EmployeeRequestDto dto) {
        Department dept = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found with id " + dto.getDepartmentId()
                        ));

        Employee employee = Employee.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .salary(dto.getSalary())
                .joiningDate(dto.getJoiningDate())
                .department(dept)
                .build();

        return mapToResponse(employeeRepository.save(employee));
    }

    @Override
    public EmployeeResponseDto getEmployeeById(Long id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found with id " + id
                        ));

        return mapToResponse(emp);
    }

    @Override
    public Page<EmployeeResponseDto> getEmployees(String name,
                                                  Long departmentId,
                                                  int page,
                                                  int size,
                                                  String sortBy,
                                                  String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Employee> employeesPage;

        boolean hasName = name != null && !name.isBlank();
        boolean hasDept = departmentId != null;

        if (hasName && hasDept) {
            employeesPage = employeeRepository
                    .findByNameContainingIgnoreCaseAndDepartmentId(name, departmentId, pageable);
        } else if (hasName) {
            employeesPage = employeeRepository
                    .findByNameContainingIgnoreCase(name, pageable);
        } else if (hasDept) {
            employeesPage = employeeRepository
                    .findByDepartmentId(departmentId, pageable);
        } else {
            employeesPage = employeeRepository.findAll(pageable);
        }

        return employeesPage.map(this::mapToResponse);
    }

    @Override
    public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto dto) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found with id " + id
                        ));

        Department dept = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found with id " + dto.getDepartmentId()
                        ));

        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        existing.setSalary(dto.getSalary());
        existing.setJoiningDate(dto.getJoiningDate());
        existing.setDepartment(dept);

        return mapToResponse(employeeRepository.save(existing));
    }

    @Override
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Employee not found with id " + id
            );
        }

        employeeRepository.deleteById(id);
    }

    private EmployeeResponseDto mapToResponse(Employee emp) {
        return EmployeeResponseDto.builder()
                .id(emp.getId())
                .name(emp.getName())
                .email(emp.getEmail())
                .salary(emp.getSalary())
                .joiningDate(emp.getJoiningDate())
                .departmentId(emp.getDepartment() != null ? emp.getDepartment().getId() : null)
                .departmentName(emp.getDepartment() != null ? emp.getDepartment().getName() : null)
                .build();
    }
}