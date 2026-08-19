package com.aerometal.assignmentmanager.service;

import com.aerometal.assignmentmanager.dto.*;
import com.aerometal.assignmentmanager.entity.*;
import com.aerometal.assignmentmanager.repository.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.*;
import java.util.*;

@Service
public class AssignmentService {
	private final AssignmentRepository assignments;
	private final EmployeeRepository employees;
	private final ComponentRepository components;
	private final AccessRightRepository accessRights;

	public AssignmentService(AssignmentRepository assignments, EmployeeRepository employees,
			ComponentRepository components, AccessRightRepository accessRights) {
		this.assignments = assignments;
		this.employees = employees;
		this.components = components;
		this.accessRights = accessRights;
	}

	@Transactional(readOnly = true)
	public List<AssignmentResponse> findAll() {
		return assignments.findAllByOrderByIdAsc().stream().map(this::toDto).toList();
	}

	@Transactional
	public AssignmentResponse create(AssignmentRequest r) {
		if (assignments.existsByEmployeeIdAndComponentIdAndRightId(r.employeeId(), r.componentId(), r.accessRightId()))
			throw new IllegalArgumentException("This employee/component/right assignment already exists.");
		Employee e = employees.findById(r.employeeId())
				.orElseThrow(() -> new IllegalArgumentException("Employee not found"));
		Component c = components.findById(r.componentId())
				.orElseThrow(() -> new IllegalArgumentException("Component not found"));
		AccessRight right = accessRights.findById(r.accessRightId())
				.orElseThrow(() -> new IllegalArgumentException("Right not found"));
		Assignment a = new Assignment();
		a.setEmployee(e);
		a.setComponent(c);
		a.setRight(right);
		return toDto(assignments.save(a));
	}

	@Transactional
	public void delete(Long id) {
		assignments.deleteById(id);
	}

	@Transactional(readOnly = true)
	public byte[] exportExcel() {
		List<AssignmentResponse> rows = findAll();
		try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = wb.createSheet("Assignments");
			Row h = sheet.createRow(0);
			String[] heads = { "ID", "Employee", "Component", "Right" };
			for (int i = 0; i < heads.length; i++)
				h.createCell(i).setCellValue(heads[i]);
			int rn = 1;
			for (AssignmentResponse a : rows) {
				Row row = sheet.createRow(rn++);
				row.createCell(0).setCellValue(a.id());
				row.createCell(1).setCellValue(a.componentName());
				row.createCell(2).setCellValue(a.employeeFirstName());
				row.createCell(3).setCellValue(a.employeeLastName());
				row.createCell(4).setCellValue(a.employeeEmail());
				row.createCell(5).setCellValue(a.rightName());
			}
			for (int i = 0; i < heads.length; i++)
				sheet.autoSizeColumn(i);
			wb.write(out);
			return out.toByteArray();
		} catch (IOException ex) {
			throw new IllegalStateException("Could not create Excel export", ex);
		}
	}

	private AssignmentResponse toDto(Assignment a) {
		return new AssignmentResponse(a.getId(), a.getEmployee().getId(), a.getEmployee().getFirstName(),a.getEmployee().getLastName(), a.getEmployee().getEmail(),
				a.getComponent().getId(), a.getComponent().getName(), a.getRight().getId(), a.getRight().getName());
	}
}
