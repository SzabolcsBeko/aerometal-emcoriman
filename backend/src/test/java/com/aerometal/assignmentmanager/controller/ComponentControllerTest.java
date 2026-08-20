package com.aerometal.assignmentmanager.controller;

import com.aerometal.assignmentmanager.entity.Component;
import com.aerometal.assignmentmanager.exception.ApiExceptionHandler;
import com.aerometal.assignmentmanager.service.ComponentService;
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

@WebMvcTest(ComponentController.class)
@Import(ApiExceptionHandler.class)
class ComponentControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean ComponentService service;

    private Component component(Long id, String name, String description) {
        Component c = new Component(); c.setId(id); c.setName(name); c.setDescription(description); return c;
    }

    @Test void all_returnsComponents() throws Exception {
        when(service.findAll()).thenReturn(List.of(component(1L,"Engine","Main engine")));
        mockMvc.perform(get("/api/components")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Engine"));
    }

    @Test void one_returnsComponent() throws Exception {
        when(service.findById(1L)).thenReturn(component(1L,"Engine","Main engine"));
        mockMvc.perform(get("/api/components/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Main engine"));
    }

    @Test void create_validComponent_returnsComponent() throws Exception {
        Component input = component(null,"Engine","Main engine");
        when(service.create(any(Component.class))).thenReturn(component(1L,"Engine","Main engine"));
        mockMvc.perform(post("/api/components").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input))).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
        verify(service).create(any(Component.class));
    }

    @Test void create_blankName_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/components").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"   \",\"description\":\"description\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Validation failed"));
        verifyNoInteractions(service);
    }

    @Test void update_returnsUpdatedComponent() throws Exception {
        when(service.update(eq(1L), any(Component.class))).thenReturn(component(1L,"Wing","Updated"));
        mockMvc.perform(put("/api/components/1").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Wing\",\"description\":\"Updated\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Wing"));
    }

    @Test void delete_callsService() throws Exception {
        mockMvc.perform(delete("/api/components/1")).andExpect(status().isOk());
        verify(service).delete(1L);
    }
}
