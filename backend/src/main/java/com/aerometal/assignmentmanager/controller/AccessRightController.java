package com.aerometal.assignmentmanager.controller;

import com.aerometal.assignmentmanager.entity.AccessRight;
import com.aerometal.assignmentmanager.service.AccessRightService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/accessrights")
public class AccessRightController {
    private final AccessRightService service;

    public AccessRightController(AccessRightService service) {
        this.service = service;
    }

    @GetMapping
    public List<AccessRight> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public AccessRight one(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public AccessRight create(@Valid @RequestBody AccessRight accessRight) {
        return service.create(accessRight);
    }

    @PutMapping("/{id}")
    public AccessRight update(@PathVariable Long id, @Valid @RequestBody AccessRight accessRight) {
        return service.update(id, accessRight);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}