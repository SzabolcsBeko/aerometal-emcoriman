package com.aerometal.assignmentmanager.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "access_assignment", uniqueConstraints = @UniqueConstraint(name = "uk_assignment_employee_component_right", columnNames = {
		"employee_id", "component_id", "access_right_id" }))
public class Assignment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "component_id", nullable = false)
	private Component component;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "access_right_id", nullable = false)
	private AccessRight right;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

	public AccessRight getRight() {
		return right;
	}

	public void setRight(AccessRight right) {
		this.right = right;
	}
}
