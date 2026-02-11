package com.prashant.ems.service.impl;

import com.prashant.ems.entity.Department;
import com.prashant.ems.exception.ResourceNotFoundException;
import com.prashant.ems.repository.DepartmentRepository;
import com.prashant.ems.service.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Department createDepartment(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found with id " + id
                        ));
    }
}
