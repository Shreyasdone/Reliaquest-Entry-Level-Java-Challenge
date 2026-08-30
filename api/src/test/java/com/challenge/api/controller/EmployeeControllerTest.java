package com.challenge.api.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.challenge.api.model.impl.EmployeeImpl;
import com.challenge.api.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeImpl buildEmployee() {
        EmployeeImpl emp = new EmployeeImpl();
        emp.setUuid(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        emp.setFirstName("John");
        emp.setLastName("Doe");
        emp.setSalary(75000);
        emp.setAge(30);
        emp.setJobTitle("Engineer");
        emp.setEmail("john.doe@example.com");
        emp.setContractHireDate(Instant.parse("2024-01-15T00:00:00Z"));
        emp.setContractTerminationDate(null);
        return emp;
    }

    @Test
    @DisplayName("GET /all → 200 with list of employees")
    @WithMockUser(roles = "EMPLOYEE_READER")
    void getAllEmployees_returnsOkWithList() throws Exception {
        EmployeeImpl emp = buildEmployee();
        when(employeeService.getAllEmployees()).thenReturn(List.of(emp));

        mockMvc.perform(get("/api/v1/employee/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[0].lastName", is("Doe")))
                .andExpect(jsonPath("$[0].email", is("john.doe@example.com")));

        verify(employeeService).getAllEmployees();
    }

    @Test
    @DisplayName("GET /{id} with valid UUID → 200 with employee")
    @WithMockUser(roles = "EMPLOYEE_READER")
    void getEmployeeByUuid_validUuid_returnsOk() throws Exception {
        EmployeeImpl emp = buildEmployee();
        UUID uuid = emp.getUuid();
        when(employeeService.getEmployeeByUuid(uuid)).thenReturn(emp);

        mockMvc.perform(get("/api/v1/employee/{id}", uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid", is(uuid.toString())))
                .andExpect(jsonPath("$.firstName", is("John")));

        verify(employeeService).getEmployeeByUuid(uuid);
    }

    @Test
    @DisplayName("GET /{id} with non-existent UUID → 404 Not Found")
    @WithMockUser(roles = "EMPLOYEE_READER")
    void getEmployeeByUuid_notFound_returns404() throws Exception {
        UUID uuid = UUID.randomUUID();
        when(employeeService.getEmployeeByUuid(uuid))
                .thenThrow(new EntityNotFoundException("Employee by the uuid " + uuid + " does not exist"));

        mockMvc.perform(get("/api/v1/employee/{id}", uuid))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("does not exist")));
    }

    @Test
    @DisplayName("GET /{id} with invalid UUID format → 409 Conflict")
    @WithMockUser(roles = "EMPLOYEE_READER")
    void getEmployeeByUuid_invalidUuid_returns409() throws Exception {
        mockMvc.perform(get("/api/v1/employee/{id}", "not-a-uuid")).andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST / with valid body → 201 Created")
    @WithMockUser(roles = "EMPLOYEE_WRITER")
    void createEmployee_validBody_returnsCreated() throws Exception {
        EmployeeImpl saved = buildEmployee();
        when(employeeService.createEmployee(any(EmployeeImpl.class))).thenReturn(saved);

        String requestJson =
                """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "salary": 75000,
                    "age": 30,
                    "jobTitle": "Engineer",
                    "email": "john.doe@example.com"
                }
                """;

        mockMvc.perform(post("/api/v1/employee")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));

        verify(employeeService).createEmployee(any(EmployeeImpl.class));
    }

    @Test
    @DisplayName("POST / with blank names, negative salary, invalid email → 400 with validation errors")
    @WithMockUser(roles = "EMPLOYEE_WRITER")
    void createEmployee_invalidBody_returns400() throws Exception {
        String requestJson =
                """
                {
                    "firstName": "",
                    "lastName": "",
                    "salary": null,
                    "age": 15,
                    "jobTitle": "",
                    "email": "not-an-email"
                }
                """;

        mockMvc.perform(post("/api/v1/employee")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors").isMap())
                .andExpect(jsonPath("$.validationErrors.firstName").exists())
                .andExpect(jsonPath("$.validationErrors.lastName").exists())
                .andExpect(jsonPath("$.validationErrors.jobTitle").exists());

        verify(employeeService, never()).createEmployee(any());
    }

    @Test
    @DisplayName("POST / with duplicate email → 409 Conflict")
    @WithMockUser(roles = "EMPLOYEE_WRITER")
    void createEmployee_duplicateEmail_returns409() throws Exception {
        when(employeeService.createEmployee(any(EmployeeImpl.class)))
                .thenThrow(new IllegalArgumentException("Email already in use: john.doe@example.com"));

        String requestJson =
                """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "salary": 75000,
                    "age": 30,
                    "jobTitle": "Engineer",
                    "email": "john.doe@example.com"
                }
                """;

        mockMvc.perform(post("/api/v1/employee")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", containsString("Email already in use")));
    }
}
