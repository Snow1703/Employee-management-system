package com.prashant.ems.service;

import com.prashant.ems.entity.Department;

import java.util.List;

public interface DepartmentService {

    Department createDepartment(Department department);

    List<Department> getAllDepartments();

    Department getDepartmentById(Long id);
}
