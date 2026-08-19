package com.aerometal.assignmentmanager.dto;

import java.time.LocalDate;

public record EmployeeResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        LocalDate hireDate

) {
}
