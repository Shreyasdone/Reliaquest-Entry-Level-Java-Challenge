package com.challenge.api.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.challenge.api.model.Employee;
import com.challenge.api.model.impl.EmployeeImpl;
import com.challenge.api.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private EmployeeImpl buildEmployee() {
        EmployeeImpl emp = new EmployeeImpl();
        emp.setUuid(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        emp.setFirstName("Jane");
        emp.setLastName("Smith");
        emp.setSalary(90000);
        emp.setAge(28);
        emp.setJobTitle("Analyst");
        emp.setEmail("jane.smith@example.com");
        emp.setContractHireDate(Instant.parse("2023-06-01T00:00:00Z"));
        emp.setContractTerminationDate(null);
        return emp;
    }

    @Test
    @DisplayName("getAllEmployees → returns mapped list of employees")
    void getAllEmployees_returnsMappedList() {
        EmployeeImpl emp = buildEmployee();
        when(employeeRepository.findAll()).thenReturn(List.of(emp));

        List<Employee> result = employeeService.getAllEmployees();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("jane.smith@example.com");
        verify(employeeRepository).findAll();
    }

    @Test
    @DisplayName("getAllEmployees → returns empty list when no employees exist")
    void getAllEmployees_returnsEmptyList() {
        when(employeeRepository.findAll()).thenReturn(List.of());

        List<Employee> result = employeeService.getAllEmployees();

        assertThat(result).isEmpty();
        verify(employeeRepository).findAll();
    }

    @Test
    @DisplayName("getEmployeeByUuid → returns employee when found")
    void getEmployeeByUuid_found_returnsEmployee() {
        EmployeeImpl emp = buildEmployee();
        UUID uuid = emp.getUuid();
        when(employeeRepository.findById(uuid)).thenReturn(Optional.of(emp));

        Employee result = employeeService.getEmployeeByUuid(uuid);

        assertThat(result).isNotNull();
        assertThat(result.getUuid()).isEqualTo(uuid);
        assertThat(result.getFirstName()).isEqualTo("Jane");
        verify(employeeRepository).findById(uuid);
    }

    @Test
    @DisplayName("getEmployeeByUuid → throws EntityNotFoundException when not found")
    void getEmployeeByUuid_notFound_throwsException() {
        UUID uuid = UUID.randomUUID();
        when(employeeRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getEmployeeByUuid(uuid))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("does not exist")
                .hasMessageContaining(uuid.toString());

        verify(employeeRepository).findById(uuid);
    }

    @Test
    @DisplayName("createEmployee → saves and returns employee when email is unique")
    void createEmployee_uniqueEmail_savesAndReturns() {
        EmployeeImpl emp = buildEmployee();
        when(employeeRepository.existsByEmail(emp.getEmail())).thenReturn(false);
        when(employeeRepository.save(emp)).thenReturn(emp);

        Employee result = employeeService.createEmployee(emp);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("jane.smith@example.com");
        verify(employeeRepository).existsByEmail(emp.getEmail());
        verify(employeeRepository).save(emp);
    }

    @Test
    @DisplayName("createEmployee → throws IllegalArgumentException when email already exists")
    void createEmployee_duplicateEmail_throwsException() {
        EmployeeImpl emp = buildEmployee();
        when(employeeRepository.existsByEmail(emp.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> employeeService.createEmployee(emp))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already in use");

        verify(employeeRepository).existsByEmail(emp.getEmail());
        verify(employeeRepository, never()).save(any());
    }
}
