package com.aerometal.assignmentmanager.service;

import com.aerometal.assignmentmanager.entity.AccessRight;
import com.aerometal.assignmentmanager.exception.AccessRightNotFoundException;
import com.aerometal.assignmentmanager.repository.AccessRightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccessRightService {
    private final AccessRightRepository repository;

    public AccessRightService(AccessRightRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<AccessRight> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public AccessRight findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AccessRightNotFoundException(id));
    }

    @Transactional
    public AccessRight create(AccessRight accessRight) {
        accessRight.setId(null);
        return repository.save(accessRight);
    }

    @Transactional
    public AccessRight update(Long id, AccessRight accessRight) {
        if (!repository.existsById(id)) {
            throw new AccessRightNotFoundException(id);
        }
        accessRight.setId(id);
        return repository.save(accessRight);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new AccessRightNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
