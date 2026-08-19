package com.aerometal.assignmentmanager.service;

import com.aerometal.assignmentmanager.entity.Component;
import com.aerometal.assignmentmanager.repository.ComponentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ComponentService {
    private final ComponentRepository repository;

    public ComponentService(ComponentRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Component> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Component findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Component not found: " + id));
    }

    @Transactional
    public Component create(Component component) {
        component.setId(null);
        return repository.save(component);
    }

    @Transactional
    public Component update(Long id, Component component) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Component not found: " + id);
        }
        component.setId(id);
        return repository.save(component);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Component not found: " + id);
        }
        repository.deleteById(id);
    }
}
