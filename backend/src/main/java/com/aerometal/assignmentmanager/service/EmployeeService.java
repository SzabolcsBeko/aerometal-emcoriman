package com.aerometal.assignmentmanager.service;

import com.aerometal.assignmentmanager.dto.EmployeeRequest;
import com.aerometal.assignmentmanager.dto.EmployeeResponse;
import com.aerometal.assignmentmanager.entity.Employee;
import com.aerometal.assignmentmanager.mapper.EmployeeMapper;
import com.aerometal.assignmentmanager.repository.EmployeeRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeService {
	
    private final EmployeeRepository repository;
    private final EmployeeMapper employeeMapper;

    
    public EmployeeService(EmployeeRepository repository,
    		EmployeeMapper employeeMapper) {
        this.repository = repository;
        this.employeeMapper = employeeMapper;
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> findAll() {
        //return repository.findAll().stream().map(this::toResponse).toList();
    	return repository.findAll().stream().map(employeeMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse findResponseById(Long id) {
        //return toResponse(findById(id));
    	Employee employee = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Employee not found: " + id));
    	
    	return employeeMapper.toResponse(employee);
    }

    @Transactional(readOnly = true)
    public Employee findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + id));
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        //Employee employee = new Employee();
    	Employee employee = employeeMapper.toEntity(request);
        //apply(employee, request);
    	Employee saved = repository.save(employee);
        //return toResponse(repository.save(employee));
    	return employeeMapper.toResponse(saved);
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        /*Employee employee = findById(id);
        apply(employee, request);
        return toResponse(repository.save(employee));*/
    	 Employee employee = repository.findById(id)
                 .orElseThrow(() ->
                         new EntityNotFoundException(
                                 "Employee not found: " + id));

         employeeMapper.updateEntity(request, employee);

         Employee saved = repository.save(employee);

         return employeeMapper.toResponse(saved);
    	
    }

    @Transactional
    public void delete(Long id) {
        /*Employee employee = findById(id);
        repository.delete(employee);*/
    	if (!repository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Employee not found: " + id);
        }

        repository.deleteById(id);
    }

    private void apply(Employee employee, EmployeeRequest request) {
        employee.setFirstName(request.firstName().trim());
        employee.setLastName(request.lastName().trim());
        employee.setEmail(request.email().trim());
        employee.setHireDate(request.hireDate());
    }

    private EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getHireDate()
        );
    }
}
