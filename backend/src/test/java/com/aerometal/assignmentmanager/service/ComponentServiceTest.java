package com.aerometal.assignmentmanager.service;

import com.aerometal.assignmentmanager.entity.Component;
import com.aerometal.assignmentmanager.exception.ComponentNotFoundException;
import com.aerometal.assignmentmanager.repository.ComponentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComponentServiceTest {

    @Mock
    private ComponentRepository repository;

    @InjectMocks
    private ComponentService componentService;

    private Component component;

    @BeforeEach
    void setUp() {
        component = new Component();
        component.setId(1L);

        // Set your actual Component fields here if needed:
        // component.setName("Engine");
        // component.setDescription("Aircraft engine");
    }

    // ---------------------------------------------------------
    // findAll()
    // ---------------------------------------------------------

    @Test
    void findAll_shouldReturnAllComponents() {

        Component component2 = new Component();
        component2.setId(2L);

        List<Component> components =
                List.of(component, component2);

        when(repository.findAll())
                .thenReturn(components);

        List<Component> result =
                componentService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(component, result.get(0));
        assertEquals(component2, result.get(1));

        verify(repository).findAll();
    }

    @Test
    void findAll_whenNoComponentsExist_shouldReturnEmptyList() {

        when(repository.findAll())
                .thenReturn(List.of());

        List<Component> result =
                componentService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repository).findAll();
    }

    // ---------------------------------------------------------
    // findById()
    // ---------------------------------------------------------

    @Test
    void findById_whenComponentExists_shouldReturnComponent() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(component));

        Component result =
                componentService.findById(1L);

        assertNotNull(result);
        assertEquals(component, result);
        assertEquals(1L, result.getId());

        verify(repository).findById(1L);
    }

    @Test
    void findById_whenComponentDoesNotExist_shouldThrowException() {

        Long id = 99L;

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        ComponentNotFoundException exception =
                assertThrows(
                		ComponentNotFoundException.class,
                        () -> componentService.findById(id)
                );

        assertTrue(
                exception.getMessage().contains("99")
        );

        verify(repository).findById(id);
    }

    // ---------------------------------------------------------
    // create()
    // ---------------------------------------------------------

    @Test
    void create_shouldSetIdToNullAndSaveComponent() {

        Component newComponent = new Component();
        newComponent.setId(100L);

        when(repository.save(newComponent))
                .thenAnswer(invocation -> {
                    Component saved =
                            invocation.getArgument(0);
                    saved.setId(1L);
                    return saved;
                });

        Component result =
                componentService.create(newComponent);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(repository).save(newComponent);
    }

    @Test
    void create_shouldClearExistingIdBeforeSaving() {

        Component newComponent = new Component();
        newComponent.setId(999L);

        when(repository.save(any(Component.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        componentService.create(newComponent);

        assertNull(newComponent.getId());

        verify(repository).save(newComponent);
    }

    // ---------------------------------------------------------
    // update()
    // ---------------------------------------------------------

    @Test
    void update_whenComponentExists_shouldUpdateComponent() {

        Long id = 1L;

        Component updatedComponent = new Component();

        when(repository.existsById(id))
                .thenReturn(true);

        when(repository.save(updatedComponent))
                .thenReturn(updatedComponent);

        Component result =
                componentService.update(id, updatedComponent);

        assertNotNull(result);
        assertEquals(id, result.getId());

        verify(repository).existsById(id);
        verify(repository).save(updatedComponent);
    }

    @Test
    void update_shouldSetComponentIdToRequestedId() {

        Long id = 10L;

        Component updatedComponent = new Component();
        updatedComponent.setId(999L);

        when(repository.existsById(id))
                .thenReturn(true);

        when(repository.save(updatedComponent))
                .thenReturn(updatedComponent);

        componentService.update(id, updatedComponent);

        assertEquals(id, updatedComponent.getId());

        verify(repository).existsById(id);
        verify(repository).save(updatedComponent);
    }

    @Test
    void update_whenComponentDoesNotExist_shouldThrowException() {

        Long id = 99L;

        Component updatedComponent = new Component();

        when(repository.existsById(id))
                .thenReturn(false);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> componentService.update(
                                id,
                                updatedComponent)
                );

        assertEquals(
                "Component not found: 99",
                exception.getMessage()
        );

        verify(repository).existsById(id);

        // Important: nothing should be saved
        verify(repository, never())
                .save(any(Component.class));
    }

    // ---------------------------------------------------------
    // delete()
    // ---------------------------------------------------------

    @Test
    void delete_whenComponentExists_shouldDeleteComponent() {

        Long id = 1L;

        when(repository.existsById(id))
                .thenReturn(true);

        componentService.delete(id);

        verify(repository).existsById(id);
        verify(repository).deleteById(id);
    }

    @Test
    void delete_whenComponentDoesNotExist_shouldThrowException() {

        Long id = 99L;

        when(repository.existsById(id))
                .thenReturn(false);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> componentService.delete(id)
                );

        assertEquals(
                "Component not found: 99",
                exception.getMessage()
        );

        verify(repository).existsById(id);

        // Very important:
        // deleteById() must NOT be called.
        verify(repository, never())
                .deleteById(anyLong());
    }
}