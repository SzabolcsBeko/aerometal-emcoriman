package com.aerometal.assignmentmanager.exception;

public class EmployeeNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmployeeNotFoundException(Long id) {
		super("Employee not found: " + id);
	}
	
	public EmployeeNotFoundException(String id) {
		super("Employee not found: " + id);
	}
}