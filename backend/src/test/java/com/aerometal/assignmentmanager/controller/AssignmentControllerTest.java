package com.aerometal.assignmentmanager.controller;

import com.aerometal.assignmentmanager.dto.AssignmentRequest;
import com.aerometal.assignmentmanager.dto.AssignmentResponse;
import com.aerometal.assignmentmanager.exception.ApiExceptionHandler;
import com.aerometal.assignmentmanager.service.AssignmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AssignmentController.class)
@Import(ApiExceptionHandler.class)
class AssignmentControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean AssignmentService service;

    private final AssignmentResponse response = new AssignmentResponse(1L,10L,"John","Smith","john@example.com",20L,"Engine",30L,"READ");

    @Test void all_returnsAssignments() throws Exception {
        when(service.findAll()).thenReturn(List.of(response));
        mockMvc.perform(get("/api/assignments")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employeeFirstName").value("John"))
                .andExpect(jsonPath("$[0].componentName").value("Engine"))
                .andExpect(jsonPath("$[0].rightName").value("READ"));
    }

    @Test void create_validRequest_returnsAssignment() throws Exception {
        AssignmentRequest request = new AssignmentRequest(10L,20L,30L);
        when(service.create(request)).thenReturn(response);
        mockMvc.perform(post("/api/assignments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
        verify(service).create(request);
    }

    @Test void create_missingIds_returnsBadRequest() throws Exception {
    	String json = """
                {
                  "employeeId": null,
                  "componentId": 20,
                  "accessRightId": null
                }
                """;
        mockMvc.perform(post("/api/assignments").contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Validation failed"));
        verifyNoInteractions(service);
    }

    @Test void delete_callsService() throws Exception {
        mockMvc.perform(delete("/api/assignments/1")).andExpect(status().isOk());
        verify(service).delete(1L);
    }

    @Test void export_returnsExcelFile() throws Exception {
        byte[] bytes = {1,2,3,4};
        when(service.exportExcel()).thenReturn(bytes);
        mockMvc.perform(get("/api/assignments/export"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition","attachment; filename=access-assignments.xlsx"))
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(content().bytes(bytes));
        verify(service).exportExcel();
    }
}
