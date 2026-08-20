package com.aerometal.assignmentmanager.service;

import com.aerometal.assignmentmanager.dto.AssignmentRequest;
import com.aerometal.assignmentmanager.dto.AssignmentResponse;
import com.aerometal.assignmentmanager.entity.AccessRight;
import com.aerometal.assignmentmanager.entity.Assignment;
import com.aerometal.assignmentmanager.entity.Component;
import com.aerometal.assignmentmanager.entity.Employee;
import com.aerometal.assignmentmanager.repository.AccessRightRepository;
import com.aerometal.assignmentmanager.repository.AssignmentRepository;
import com.aerometal.assignmentmanager.repository.ComponentRepository;
import com.aerometal.assignmentmanager.repository.EmployeeRepository;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceTest {

    @Mock
    private AssignmentRepository assignments;

    @Mock
    private EmployeeRepository employees;

    @Mock
    private ComponentRepository components;

    @Mock
    private AccessRightRepository accessRights;

    @InjectMocks
    private AssignmentService assignmentService;

    private Employee employee;
    private Component component;
    private AccessRight accessRight;
    private Assignment assignment;

    @BeforeEach
    void setUp() {

        employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Smith");
        employee.setEmail("john.smith@example.com");

        component = new Component();
        component.setId(10L);
        component.setName("Engine");

        accessRight = new AccessRight();
        accessRight.setId(20L);
        accessRight.setName("READ");

        assignment = new Assignment();
        assignment.setId(100L);
        assignment.setEmployee(employee);
        assignment.setComponent(component);
        assignment.setRight(accessRight);
    }

    // =========================================================
    // findAll()
    // =========================================================

    @Test
    void findAll_shouldReturnAssignmentResponses() {

        when(assignments.findAllByOrderByIdAsc())
                .thenReturn(List.of(assignment));

        List<AssignmentResponse> result =
                assignmentService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());

        AssignmentResponse response = result.get(0);

        assertEquals(100L, response.id());

        assertEquals(1L, response.employeeId());
        assertEquals("John", response.employeeFirstName());
        assertEquals("Smith", response.employeeLastName());
        assertEquals(
                "john.smith@example.com",
                response.employeeEmail()
        );

        assertEquals(10L, response.componentId());
        assertEquals("Engine", response.componentName());

        assertEquals(20L, response.accessRightId());
        assertEquals("READ", response.rightName());

        verify(assignments).findAllByOrderByIdAsc();
    }

    @Test
    void findAll_whenNoAssignmentsExist_shouldReturnEmptyList() {

        when(assignments.findAllByOrderByIdAsc())
                .thenReturn(List.of());

        List<AssignmentResponse> result =
                assignmentService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(assignments).findAllByOrderByIdAsc();
    }

    // =========================================================
    // create()
    // =========================================================

    @Test
    void create_whenValidRequest_shouldCreateAssignment() {

        AssignmentRequest request =
                new AssignmentRequest(
                        1L,
                        10L,
                        20L
                );

        when(assignments
                .existsByEmployeeIdAndComponentIdAndRightId(
                        1L, 10L, 20L))
                .thenReturn(false);

        when(employees.findById(1L))
                .thenReturn(Optional.of(employee));

        when(components.findById(10L))
                .thenReturn(Optional.of(component));

        when(accessRights.findById(20L))
                .thenReturn(Optional.of(accessRight));

        when(assignments.save(any(Assignment.class)))
                .thenAnswer(invocation -> {
                    Assignment saved =
                            invocation.getArgument(0);

                    saved.setId(100L);

                    return saved;
                });

        AssignmentResponse result =
                assignmentService.create(request);

        assertNotNull(result);

        assertEquals(100L, result.id());

        assertEquals(1L, result.employeeId());
        assertEquals("John", result.employeeFirstName());
        assertEquals("Smith", result.employeeLastName());

        assertEquals(10L, result.componentId());
        assertEquals("Engine", result.componentName());

        assertEquals(20L, result.accessRightId());
        assertEquals("READ", result.rightName());

        verify(assignments)
                .existsByEmployeeIdAndComponentIdAndRightId(
                        1L, 10L, 20L);

        verify(employees).findById(1L);
        verify(components).findById(10L);
        verify(accessRights).findById(20L);

        verify(assignments).save(any(Assignment.class));
    }

    @Test
    void create_shouldSaveAssignmentWithCorrectEntities() {

        AssignmentRequest request =
                new AssignmentRequest(
                        1L,
                        10L,
                        20L
                );

        when(assignments
                .existsByEmployeeIdAndComponentIdAndRightId(
                        1L, 10L, 20L))
                .thenReturn(false);

        when(employees.findById(1L))
                .thenReturn(Optional.of(employee));

        when(components.findById(10L))
                .thenReturn(Optional.of(component));

        when(accessRights.findById(20L))
                .thenReturn(Optional.of(accessRight));

        when(assignments.save(any(Assignment.class)))
                .thenAnswer(invocation -> {
                    Assignment saved =
                            invocation.getArgument(0);

                    saved.setId(100L);

                    return saved;
                });

        assignmentService.create(request);

        ArgumentCaptor<Assignment> captor =
                ArgumentCaptor.forClass(Assignment.class);

        verify(assignments).save(captor.capture());

        Assignment savedAssignment =
                captor.getValue();

        assertSame(
                employee,
                savedAssignment.getEmployee()
        );

        assertSame(
                component,
                savedAssignment.getComponent()
        );

        assertSame(
                accessRight,
                savedAssignment.getRight()
        );
    }

    // =========================================================
    // Duplicate assignment
    // =========================================================

    @Test
    void create_whenAssignmentAlreadyExists_shouldThrowException() {

        AssignmentRequest request =
                new AssignmentRequest(
                        1L,
                        10L,
                        20L
                );

        when(assignments
                .existsByEmployeeIdAndComponentIdAndRightId(
                        1L, 10L, 20L))
                .thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> assignmentService.create(request)
                );

        assertEquals(
                "This employee/component/right assignment already exists.",
                exception.getMessage()
        );

        verify(assignments)
                .existsByEmployeeIdAndComponentIdAndRightId(
                        1L, 10L, 20L);

        verifyNoInteractions(employees);
        verifyNoInteractions(components);
        verifyNoInteractions(accessRights);

        verify(assignments, never())
                .save(any());
    }

    // =========================================================
    // Employee not found
    // =========================================================

    @Test
    void create_whenEmployeeDoesNotExist_shouldThrowException() {

        AssignmentRequest request =
                new AssignmentRequest(
                        99L,
                        10L,
                        20L
                );

        when(assignments
                .existsByEmployeeIdAndComponentIdAndRightId(
                        99L, 10L, 20L))
                .thenReturn(false);

        when(employees.findById(99L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> assignmentService.create(request)
                );

        assertEquals(
                "Employee not found",
                exception.getMessage()
        );

        verify(employees).findById(99L);

        verifyNoInteractions(components);
        verifyNoInteractions(accessRights);

        verify(assignments, never())
                .save(any());
    }

    // =========================================================
    // Component not found
    // =========================================================

    @Test
    void create_whenComponentDoesNotExist_shouldThrowException() {

        AssignmentRequest request =
                new AssignmentRequest(
                        1L,
                        99L,
                        20L
                );

        when(assignments
                .existsByEmployeeIdAndComponentIdAndRightId(
                        1L, 99L, 20L))
                .thenReturn(false);

        when(employees.findById(1L))
                .thenReturn(Optional.of(employee));

        when(components.findById(99L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> assignmentService.create(request)
                );

        assertEquals(
                "Component not found",
                exception.getMessage()
        );

        verify(employees).findById(1L);
        verify(components).findById(99L);

        verifyNoInteractions(accessRights);

        verify(assignments, never())
                .save(any());
    }

    // =========================================================
    // AccessRight not found
    // =========================================================

    @Test
    void create_whenAccessRightDoesNotExist_shouldThrowException() {

        AssignmentRequest request =
                new AssignmentRequest(
                        1L,
                        10L,
                        99L
                );

        when(assignments
                .existsByEmployeeIdAndComponentIdAndRightId(
                        1L, 10L, 99L))
                .thenReturn(false);

        when(employees.findById(1L))
                .thenReturn(Optional.of(employee));

        when(components.findById(10L))
                .thenReturn(Optional.of(component));

        when(accessRights.findById(99L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> assignmentService.create(request)
                );

        assertEquals(
                "Right not found",
                exception.getMessage()
        );

        verify(employees).findById(1L);
        verify(components).findById(10L);
        verify(accessRights).findById(99L);

        verify(assignments, never())
                .save(any());
    }

    // =========================================================
    // delete()
    // =========================================================

    @Test
    void delete_shouldDeleteAssignmentById() {

        Long id = 100L;

        assignmentService.delete(id);

        verify(assignments).deleteById(id);
    }

    // =========================================================
    // exportExcel()
    // =========================================================

    @Test
    void exportExcel_shouldReturnExcelFile() {

        when(assignments.findAllByOrderByIdAsc())
                .thenReturn(List.of(assignment));

        byte[] result =
                assignmentService.exportExcel();

        assertNotNull(result);
        assertTrue(result.length > 0);

        verify(assignments).findAllByOrderByIdAsc();
    }

    @Test
    void exportExcel_shouldContainAssignmentsSheet()
            throws IOException {

        when(assignments.findAllByOrderByIdAsc())
                .thenReturn(List.of(assignment));

        byte[] excel =
                assignmentService.exportExcel();

        try (
                Workbook workbook =
                        new XSSFWorkbook(
                                new ByteArrayInputStream(excel))
        ) {

            Sheet sheet =
                    workbook.getSheet("Assignments");

            assertNotNull(sheet);
        }
    }

    @Test
    void exportExcel_shouldContainCorrectAssignmentData()
            throws IOException {

        when(assignments.findAllByOrderByIdAsc())
                .thenReturn(List.of(assignment));

        byte[] excel =
                assignmentService.exportExcel();

        try (
                Workbook workbook =
                        new XSSFWorkbook(
                                new ByteArrayInputStream(excel))
        ) {

            Sheet sheet =
                    workbook.getSheet("Assignments");

            assertNotNull(sheet);

            Row row =
                    sheet.getRow(1);

            assertNotNull(row);

            assertEquals(
                    100L,
                    (long) row.getCell(0)
                            .getNumericCellValue()
            );

            assertEquals(
                    "Engine",
                    row.getCell(1)
                            .getStringCellValue()
            );

            assertEquals(
                    "John",
                    row.getCell(2)
                            .getStringCellValue()
            );

            assertEquals(
                    "Smith",
                    row.getCell(3)
                            .getStringCellValue()
            );

            assertEquals(
                    "john.smith@example.com",
                    row.getCell(4)
                            .getStringCellValue()
            );

            assertEquals(
                    "READ",
                    row.getCell(5)
                            .getStringCellValue()
            );
        }
    }
    
    @Test
    void exportExcel_shouldContainCorrectHeadersAndAssignmentData()
            throws IOException {

        // Arrange
        when(assignments.findAllByOrderByIdAsc())
                .thenReturn(List.of(assignment));

        // Act
        byte[] excelBytes = assignmentService.exportExcel();

        // Assert
        assertNotNull(excelBytes);
        assertTrue(excelBytes.length > 0);

        try (Workbook workbook =
                     new XSSFWorkbook(
                             new ByteArrayInputStream(excelBytes))) {

            Sheet sheet = workbook.getSheet("Assignments");

            assertNotNull(sheet);

            // ---------------------------------------------
            // Verify header
            // ---------------------------------------------

            Row header = sheet.getRow(0);

            assertNotNull(header);

            assertAll(
                    () -> assertEquals(
                            "ID",
                            header.getCell(0).getStringCellValue()
                    ),
                    () -> assertEquals(
                            "Component",
                            header.getCell(1).getStringCellValue()
                    ),
                    () -> assertEquals(
                            "Employee First Name",
                            header.getCell(2).getStringCellValue()
                    ),
                    () -> assertEquals(
                            "Employee Last Name",
                            header.getCell(3).getStringCellValue()
                    ),
                    () -> assertEquals(
                            "Employee Email",
                            header.getCell(4).getStringCellValue()
                    ),
                    () -> assertEquals(
                            "Right",
                            header.getCell(5).getStringCellValue()
                    )
            );

            // ---------------------------------------------
            // Verify first assignment
            // ---------------------------------------------

            Row row = sheet.getRow(1);

            assertNotNull(row);

            assertAll(
                    () -> assertEquals(
                            100L,
                            (long) row.getCell(0)
                                    .getNumericCellValue()
                    ),
                    () -> assertEquals(
                            "Engine",
                            row.getCell(1)
                                    .getStringCellValue()
                    ),
                    () -> assertEquals(
                            "John",
                            row.getCell(2)
                                    .getStringCellValue()
                    ),
                    () -> assertEquals(
                            "Smith",
                            row.getCell(3)
                                    .getStringCellValue()
                    ),
                    () -> assertEquals(
                            "john.smith@example.com",
                            row.getCell(4)
                                    .getStringCellValue()
                    ),
                    () -> assertEquals(
                            "READ",
                            row.getCell(5)
                                    .getStringCellValue()
                    )
            );
        }

        verify(assignments).findAllByOrderByIdAsc();
    }
}
