package com.challenge.api.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.challenge.api.controller.EmployeeController;
import com.challenge.api.model.impl.EmployeeImpl;
import com.challenge.api.service.EmployeeService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeController.class)
@Import(EmployeeControllerSecurityTest.TestSecurityConfig.class)
@TestPropertySource(properties = "spring.security.user.password=test")
class EmployeeControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain testApi(HttpSecurity http) throws Exception {
            return http.securityMatcher("/api/**")
                    .csrf(AbstractHttpConfigurer::disable)
                    .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.GET, "/api/v1/employee/**")
                            .hasRole("EMPLOYEE_READER")
                            .requestMatchers(HttpMethod.POST, "/api/v1/employee/**")
                            .hasRole("EMPLOYEE_WRITER")
                            .anyRequest()
                            .denyAll())
                    .httpBasic(Customizer.withDefaults())
                    .build();
        }
    }

    private EmployeeImpl buildEmployee() {
        EmployeeImpl emp = new EmployeeImpl();
        emp.setUuid(UUID.fromString("33333333-3333-3333-3333-333333333333"));
        emp.setFirstName("Sec");
        emp.setLastName("User");
        emp.setSalary(50000);
        emp.setAge(25);
        emp.setJobTitle("Tester");
        emp.setEmail("sec.user@example.com");
        emp.setContractHireDate(Instant.parse("2024-01-01T00:00:00Z"));
        return emp;
    }

    @Test
    @DisplayName("Unauthenticated GET /all → 401 Unauthorized")
    void unauthenticated_getAllEmployees_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/employee/all")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Unauthenticated POST / → 401 Unauthorized")
    void unauthenticated_createEmployee_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("EMPLOYEE_READER accessing GET /all → 200 OK")
    @WithMockUser(roles = "EMPLOYEE_READER")
    void reader_getAllEmployees_returns200() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(List.of(buildEmployee()));

        mockMvc.perform(get("/api/v1/employee/all")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("EMPLOYEE_READER attempting POST / → 403 Forbidden")
    @WithMockUser(roles = "EMPLOYEE_READER")
    void reader_createEmployee_returns403() throws Exception {
        String requestJson =
                """
                {
                    "firstName": "Test",
                    "lastName": "User",
                    "salary": 50000,
                    "age": 25,
                    "jobTitle": "Tester",
                    "email": "test@example.com"
                }
                """;

        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("EMPLOYEE_WRITER attempting POST / → 201 Created")
    @WithMockUser(roles = "EMPLOYEE_WRITER")
    void writer_createEmployee_returns201() throws Exception {
        EmployeeImpl saved = buildEmployee();
        when(employeeService.createEmployee(any(EmployeeImpl.class))).thenReturn(saved);

        String requestJson =
                """
                {
                    "firstName": "Sec",
                    "lastName": "User",
                    "salary": 50000,
                    "age": 25,
                    "jobTitle": "Tester",
                    "email": "sec.user@example.com"
                }
                """;

        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());
    }
}
