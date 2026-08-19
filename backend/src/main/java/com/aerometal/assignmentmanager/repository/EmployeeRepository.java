package com.aerometal.assignmentmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aerometal.assignmentmanager.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
