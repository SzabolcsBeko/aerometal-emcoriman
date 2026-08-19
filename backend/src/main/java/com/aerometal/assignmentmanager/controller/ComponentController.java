package com.aerometal.assignmentmanager.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aerometal.assignmentmanager.entity.Component;
import com.aerometal.assignmentmanager.service.ComponentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/components")
public class ComponentController {
    private final ComponentService service;

    public ComponentController(ComponentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Component> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Component one(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public Component create(@Valid @RequestBody Component component) {
        return service.create(component);
    }

    @PutMapping("/{id}")
    public Component update(@PathVariable Long id, @Valid @RequestBody Component component) {
        return service.update(id, component);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
