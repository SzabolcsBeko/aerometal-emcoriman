package com.aerometal.assignmentmanager.exception;

public class AccessRightNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AccessRightNotFoundException(Long id) {
		super("Access right not found: " + id);
	}
}