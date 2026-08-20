package com.aerometal.assignmentmanager.controller;

import com.aerometal.assignmentmanager.dto.EmployeeRequest;
import com.aerometal.assignmentmanager.dto.EmployeeResponse;
import com.aerometal.assignmentmanager.exception.ApiExceptionHandler;
import com.aerometal.assignmentmanager.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@Import(ApiExceptionHandler.class)
class EmployeeControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean EmployeeService service;

    private final EmployeeResponse response = new EmployeeResponse(1L, "John", "Smith", "john@example.com", LocalDate.of(2024, 1, 10));

    @Test void all_returnsEmployees() throws Exception {
        when(service.findAll()).thenReturn(List.of(response));
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"));
        verify(service).findAll();
    }

    @Test void one_returnsEmployee() throws Exception {
        when(service.findResponseById(1L)).thenReturn(response);
        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.lastName").value("Smith"));
    }

    @Test void create_validRequest_returnsCreatedEmployee() throws Exception {
        EmployeeRequest request = new EmployeeRequest("John", "Smith", "john@example.com", LocalDate.of(2024,1,10));
        when(service.create(request)).thenReturn(response);
        mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
        verify(service).create(request);
    }

    @Test void create_invalidRequest_returnsBadRequestAndDoesNotCallService() throws Exception {
    	String json = """
                {
                  "firstName": "",
                  "lastName": "",
                  "email": "not-an-email",
                  "hireDate": null
                }
                """;
        mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Validation failed"));
        verifyNoInteractions(service);
    }

    @Test void update_validRequest_returnsUpdatedEmployee() throws Exception {
        EmployeeRequest request = new EmployeeRequest("Jane", "Smith", "jane@example.com", LocalDate.of(2024,2,10));
        EmployeeResponse updated = new EmployeeResponse(1L,"Jane","Smith","jane@example.com",LocalDate.of(2024,2,10));
        when(service.update(eq(1L), eq(request))).thenReturn(updated);
        mockMvc.perform(put("/api/employees/1").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test void delete_callsService() throws Exception {
        mockMvc.perform(delete("/api/employees/1")).andExpect(status().isOk());
        verify(service).delete(1L);
    }

    @Test void serviceIllegalArgument_returnsBadRequest() throws Exception {
        when(service.findResponseById(99L)).thenThrow(new IllegalArgumentException("Employee not found: 99"));
        mockMvc.perform(get("/api/employees/99"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Employee not found: 99"));
    }
}
