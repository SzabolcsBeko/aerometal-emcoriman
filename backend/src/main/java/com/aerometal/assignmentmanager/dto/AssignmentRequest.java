package com.aerometal.assignmentmanager.dto;

import jakarta.validation.constraints.NotNull;

public record AssignmentRequest(@NotNull Long employeeId, @NotNull Long componentId, @NotNull Long accessRightId) {
}
