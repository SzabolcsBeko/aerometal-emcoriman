package com.aerometal.assignmentmanager.controller;

import com.aerometal.assignmentmanager.entity.AccessRight;
import com.aerometal.assignmentmanager.exception.ApiExceptionHandler;
import com.aerometal.assignmentmanager.service.AccessRightService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccessRightController.class)
@Import(ApiExceptionHandler.class)
class AccessRightControllerTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean AccessRightService service;

    private AccessRight right(Long id,String name,String description) {
        AccessRight r=new AccessRight(); r.setId(id); r.setName(name); r.setDescription(description); return r;
    }

    @Test void all_returnsAccessRights() throws Exception {
        when(service.findAll()).thenReturn(List.of(right(1L,"READ","Read access")));
        mockMvc.perform(get("/api/accessrights")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("READ"));
    }

    @Test void one_returnsAccessRight() throws Exception {
        when(service.findById(1L)).thenReturn(right(1L,"READ","Read access"));
        mockMvc.perform(get("/api/accessrights/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test void create_validAccessRight_returnsResult() throws Exception {
    	String json = """
                {
                  "name":"WRITE",
                  "description": "Write access"
                }
                """;
        when(service.create(any())).thenReturn(right(1L,"WRITE","Write access"));
        mockMvc.perform(post("/api/accessrights").contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("WRITE"));
    }

    @Test void create_blankName_returnsBadRequest() throws Exception {
    	String json = """
                {
                  "name": "",
                  "description": "test"
                }
                """;
        mockMvc.perform(post("/api/accessrights").contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test void update_returnsUpdatedAccessRight() throws Exception {
    	String json = """
                {
                  "name": "ADMIN",
                  "description": "Admin access"
                }
                """;
        when(service.update(eq(1L),any())).thenReturn(right(1L,"ADMIN","Admin access"));
        mockMvc.perform(put("/api/accessrights/1").contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("ADMIN"));
    }

    @Test void delete_callsService() throws Exception {
        mockMvc.perform(delete("/api/accessrights/1")).andExpect(status().isOk());
        verify(service).delete(1L);
    }
}
