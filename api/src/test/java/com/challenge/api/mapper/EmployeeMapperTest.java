package com.challenge.api.mapper;

import static org.assertj.core.api.Assertions.*;

import com.challenge.api.dto.CreateEmployeeRequest;
import com.challenge.api.model.impl.EmployeeImpl;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmployeeMapperTest {

    @Test
    @DisplayName("toEmployee → maps all fields from CreateEmployeeRequest to EmployeeImpl")
    void toEmployee_mapsAllFields() {
        Instant hireDate = Instant.parse("2024-03-01T00:00:00Z");
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "Alice", "Williams", 85000, 35, "Manager", "alice.williams@example.com", hireDate);

        EmployeeImpl result = EmployeeMapper.toEmployee(request);

        assertThat(result.getUuid()).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Alice");
        assertThat(result.getLastName()).isEqualTo("Williams");
        assertThat(result.getFullName()).isEqualTo("Alice Williams");
        assertThat(result.getSalary()).isEqualTo(85000);
        assertThat(result.getAge()).isEqualTo(35);
        assertThat(result.getJobTitle()).isEqualTo("Manager");
        assertThat(result.getEmail()).isEqualTo("alice.williams@example.com");
        assertThat(result.getContractHireDate()).isEqualTo(hireDate);
        assertThat(result.getContractTerminationDate()).isNull();
    }

    @Test
    @DisplayName("toEmployee → handles null contractHireDate gracefully")
    void toEmployee_nullHireDate_handlesGracefully() {
        CreateEmployeeRequest request =
                new CreateEmployeeRequest("Bob", "Jones", 60000, 25, "Intern", "bob.jones@example.com", null);

        EmployeeImpl result = EmployeeMapper.toEmployee(request);

        assertThat(result.getContractHireDate()).isNull();
        assertThat(result.getFirstName()).isEqualTo("Bob");
        assertThat(result.getUuid()).isNotNull();
    }
}
