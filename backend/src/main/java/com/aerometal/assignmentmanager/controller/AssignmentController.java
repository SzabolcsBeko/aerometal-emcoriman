package com.aerometal.assignmentmanager.controller;

import com.aerometal.assignmentmanager.dto.*;
import com.aerometal.assignmentmanager.service.AssignmentService;

import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {
	private final AssignmentService service;

	public AssignmentController(AssignmentService service) {
		this.service = service;
	}

	@GetMapping
	public List<AssignmentResponse> all() {
		return service.findAll();
	}

	@PostMapping
	public AssignmentResponse create(@Valid @RequestBody AssignmentRequest request) {
		return service.create(request);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		service.delete(id);
	}

	@GetMapping("/export")
	public ResponseEntity<byte[]> export() {
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=access-assignments.xlsx")
				.contentType(
						MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.body(service.exportExcel());
	}
}
