package com.aerometal.assignmentmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aerometal.assignmentmanager.entity.AccessRight;

public interface AccessRightRepository extends JpaRepository<AccessRight, Long> {
}
