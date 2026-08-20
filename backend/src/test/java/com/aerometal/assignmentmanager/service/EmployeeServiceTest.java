package com.aerometal.assignmentmanager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.aerometal.assignmentmanager.dto.EmployeeRequest;
import com.aerometal.assignmentmanager.dto.EmployeeResponse;
import com.aerometal.assignmentmanager.entity.Employee;
import com.aerometal.assignmentmanager.mapper.EmployeeMapper;
import com.aerometal.assignmentmanager.repository.EmployeeRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

	@Mock
	private EmployeeRepository employeeRepository;

	@Mock
	private EmployeeMapper employeeMapper;

	@InjectMocks
	private EmployeeService employeeService;

	private Employee employee;

	private EmployeeRequest employeeRequest;

	private EmployeeResponse employeeResponse;

	@BeforeEach
	void setUp() {
		employee = new Employee();
		employee.setId(1L);
		employee.setFirstName("John");
		employee.setLastName("Smith");
		employee.setEmail("john.smith@example.com");

		final LocalDate dt = LocalDate.parse("2025-10-10",DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		employeeRequest = new EmployeeRequest("John", "Smith", "john.smith@example.com", dt);

		employeeResponse = new EmployeeResponse(1L, "John", "Smith", "john.smith@example.com", dt);
	}

	@Test
	void shouldFindAllEmployees() {

		when(employeeRepository.findAll()).thenReturn(List.of(employee));

		when(employeeMapper.toResponse(employee)).thenReturn(employeeResponse);

		List<EmployeeResponse> result = employeeService.findAll();

		assertThat(result).hasSize(1);

		assertThat(result.get(0).id()).isEqualTo(1L);

		assertThat(result.get(0).firstName()).isEqualTo("John");

		assertThat(result.get(0).lastName()).isEqualTo("Smith");

		assertThat(result.get(0).email()).isEqualTo("john.smith@example.com");

		verify(employeeRepository).findAll();

		verify(employeeMapper).toResponse(employee);

		verifyNoMoreInteractions(employeeRepository);
	}

	@Test
	void shouldReturnEmptyListWhenNoEmployeesExist() {

		when(employeeRepository.findAll()).thenReturn(List.of());

		List<EmployeeResponse> result = employeeService.findAll();

		assertThat(result).isEmpty();

		verify(employeeRepository).findAll();

		verifyNoInteractions(employeeMapper);
	}

	@Test
	void shouldFindEmployeeById() {

		when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

		when(employeeMapper.toResponse(employee)).thenReturn(employeeResponse);

		EmployeeResponse result = employeeService.findResponseById(1L);

		assertThat(result).isNotNull();

		assertThat(result.id()).isEqualTo(1L);

		assertThat(result.firstName()).isEqualTo("John");

		assertThat(result.lastName()).isEqualTo("Smith");

		assertThat(result.email()).isEqualTo("john.smith@example.com");

		verify(employeeRepository).findById(1L);

		verify(employeeMapper).toResponse(employee);
	}

	@Test
	void shouldThrowExceptionWhenEmployeeDoesNotExist() {

		when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> employeeService.findById(999L)).isInstanceOf(EntityNotFoundException.class)
				.hasMessageContaining("999");

		verify(employeeRepository).findById(999L);

		verifyNoInteractions(employeeMapper);
	}

	/*@Test
	void shouldReturnEmptyWhenEmployeeDoesNotExist() {

		when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

		Optional<Employee> result = Optional.of(employeeService.findById(999L));

		assertThat(result).isEmpty();
	}*/

	/*Test pattern
	 * 
	 * @Test
	void findById_whenEmployeeDoesNotExist_shouldThrowEmployeeNotFoundException() {
    	Long employeeId = 99L;

	    when(employeeRepository.findById(employeeId))
	        .thenReturn(Optional.empty());
	
	    EmployeeNotFoundException exception = assertThrows(
	        EmployeeNotFoundException.class,
	        () -> employeeService.findById(employeeId)
	    );
	
	    assertEquals(
	        "Employee not found with id: 99",
	        exception.getMessage()
	    );
	
	    verify(employeeRepository).findById(employeeId);
	    verifyNoInteractions(employeeMapper);
	}
	 */
	@Test
	void shouldCreateEmployee() {

		/*
		 * when(employeeRepository.save(employee)).thenReturn(employee);
		 * 
		 * EmployeeResponse saved = employeeService.create(toRequestDto(employee));
		 * 
		 * assertThat(saved.getId()).isEqualTo(1L);
		 * assertThat(saved.getEmail()).isEqualTo("john.smith@example.com");
		 * 
		 * verify(employeeRepository).save(employee);
		 */

		Employee employeeWithoutId = new Employee();

		employeeWithoutId.setFirstName("John");
		employeeWithoutId.setLastName("Smith");
		employeeWithoutId.setEmail("john.smith@example.com");

		when(employeeMapper.toEntity(employeeRequest)).thenReturn(employeeWithoutId);

		when(employeeRepository.save(employeeWithoutId)).thenReturn(employee);

		when(employeeMapper.toResponse(employee)).thenReturn(employeeResponse);

		EmployeeResponse result = employeeService.create(employeeRequest);

		assertThat(result).isNotNull();

		assertThat(result.id()).isEqualTo(1L);

		assertThat(result.firstName()).isEqualTo("John");

		assertThat(result.lastName()).isEqualTo("Smith");

		assertThat(result.email()).isEqualTo("john.smith@example.com");

		verify(employeeMapper).toEntity(employeeRequest);

		verify(employeeRepository).save(employeeWithoutId);

		verify(employeeMapper).toResponse(employee);
	}

	@Test
	void shouldUpdateEmployee() {

		EmployeeRequest updateRequest = new EmployeeRequest("Jonathan", "Smith", "jonathan.smith@example.com",
				LocalDate.parse("2025-10-10", DateTimeFormatter.ofPattern("yyyy-MM-dd")));

		Employee updatedEmployee = new Employee();

		updatedEmployee.setId(1L);
		updatedEmployee.setFirstName("Jonathan");
		updatedEmployee.setLastName("Smith");
		updatedEmployee.setEmail("jonathan.smith@example.com");

		EmployeeResponse updatedResponse = new EmployeeResponse(1L, "Jonathan", "Smith", "jonathan.smith@example.com",
				LocalDate.parse("2025-10-10", DateTimeFormatter.ofPattern("yyyy-MM-dd")));

		when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

		doAnswer(invocation -> {

			EmployeeRequest request = invocation.getArgument(0);

			Employee target = invocation.getArgument(1);

			target.setFirstName(request.firstName());

			target.setLastName(request.lastName());

			target.setEmail(request.email());

			return null;

		}).when(employeeMapper).updateEntity(eq(updateRequest), eq(employee));

		when(employeeRepository.save(employee)).thenReturn(updatedEmployee);

		when(employeeMapper.toResponse(updatedEmployee)).thenReturn(updatedResponse);

		EmployeeResponse result = employeeService.update(1L, updateRequest);

		assertThat(result.id()).isEqualTo(1L);

		assertThat(result.firstName()).isEqualTo("Jonathan");

		assertThat(result.email()).isEqualTo("jonathan.smith@example.com");

		verify(employeeRepository).findById(1L);

		verify(employeeMapper).updateEntity(updateRequest, employee);

		verify(employeeRepository).save(employee);

		verify(employeeMapper).toResponse(updatedEmployee);
	}

	@Test
	void shouldThrowExceptionWhenUpdatingUnknownEmployee() {

		EmployeeRequest request = new EmployeeRequest("John", "Smith", "john@example.com",
				LocalDate.parse("2025-10-10", DateTimeFormatter.ofPattern("yyyy-MM-dd")));


		when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> employeeService.update(999L, request)).isInstanceOf(EntityNotFoundException.class)
				.hasMessageContaining("999");

		verify(employeeRepository).findById(999L);

		verify(employeeRepository, never()).save(any(Employee.class));

		verifyNoInteractions(employeeMapper);
	}

	@Test
	void shouldDeleteEmployee() {

		/*
		 * employeeService.delete(1L);
		 * 
		 * verify(employeeRepository).deleteById(1L);
		 */

		when(employeeRepository.existsById(1L)).thenReturn(true);

		employeeService.delete(1L);

		verify(employeeRepository).existsById(1L);

		verify(employeeRepository).deleteById(1L);

	}

	@Test
	void shouldThrowExceptionWhenDeletingUnknownEmployee() {

		when(employeeRepository.existsById(999L)).thenReturn(false);

		assertThatThrownBy(() -> employeeService.delete(999L)).isInstanceOf(EntityNotFoundException.class)
				.hasMessageContaining("999");

		verify(employeeRepository).existsById(999L);

		verify(employeeRepository, never()).deleteById(anyLong());
	}
}