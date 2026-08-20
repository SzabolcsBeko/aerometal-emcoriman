package com.aerometal.assignmentmanager.exception;

public class ComponentNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ComponentNotFoundException(Long id) {
		super("Component not found: " + id);
	}
	
}