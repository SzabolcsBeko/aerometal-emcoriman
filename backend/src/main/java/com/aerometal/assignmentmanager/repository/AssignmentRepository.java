package com.aerometal.assignmentmanager.repository;

import org.springframework.data.jpa.repository.*;

import com.aerometal.assignmentmanager.entity.Assignment;

import java.util.*;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
	boolean existsByEmployeeIdAndComponentIdAndRightId(Long employeeId, Long componentId, Long rightId);

	@EntityGraph(attributePaths = { "employee", "component", "right" })
	List<Assignment> findAllByOrderByIdAsc();
}
