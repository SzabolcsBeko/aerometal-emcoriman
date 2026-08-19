package com.aerometal.assignmentmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aerometal.assignmentmanager.entity.Component;

public interface ComponentRepository extends JpaRepository<Component, Long> {
}
