package com.aerometal.assignmentmanager.dto;

public record AssignmentResponse(Long id, Long employeeId, String employeeFirstName, String employeeLastName, String employeeEmail, Long componentId, String componentName,
		Long accessRightId, String rightName) {
}
