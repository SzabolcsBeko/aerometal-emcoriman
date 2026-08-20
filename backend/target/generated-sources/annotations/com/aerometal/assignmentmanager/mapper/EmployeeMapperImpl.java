package com.aerometal.assignmentmanager.mapper;

import com.aerometal.assignmentmanager.dto.EmployeeRequest;
import com.aerometal.assignmentmanager.dto.EmployeeResponse;
import com.aerometal.assignmentmanager.entity.Employee;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-20T22:32:59+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
@Component
public class EmployeeMapperImpl implements EmployeeMapper {

    @Override
    public Employee toEntity(EmployeeRequest request) {
        if ( request == null ) {
            return null;
        }

        Employee employee = new Employee();

        employee.setFirstName( request.firstName() );
        employee.setLastName( request.lastName() );
        employee.setEmail( request.email() );
        employee.setHireDate( request.hireDate() );

        return employee;
    }

    @Override
    public EmployeeResponse toResponse(Employee employee) {
        if ( employee == null ) {
            return null;
        }

        Long id = null;
        String firstName = null;
        String lastName = null;
        String email = null;
        LocalDate hireDate = null;

        id = employee.getId();
        firstName = employee.getFirstName();
        lastName = employee.getLastName();
        email = employee.getEmail();
        hireDate = employee.getHireDate();

        EmployeeResponse employeeResponse = new EmployeeResponse( id, firstName, lastName, email, hireDate );

        return employeeResponse;
    }

    @Override
    public void updateEntity(EmployeeRequest request, Employee employee) {
        if ( request == null ) {
            return;
        }

        employee.setFirstName( request.firstName() );
        employee.setLastName( request.lastName() );
        employee.setEmail( request.email() );
        employee.setHireDate( request.hireDate() );
    }
}
