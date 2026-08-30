package com.challenge.api.model;

import static org.assertj.core.api.Assertions.*;

import com.challenge.api.model.impl.EmployeeImpl;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmployeeImplTest {

    @Test
    @DisplayName("No-arg constructor creates instance with null fields")
    void noArgConstructor_createsEmptyInstance() {
        EmployeeImpl emp = new EmployeeImpl();

        assertThat(emp.getUuid()).isNull();
        assertThat(emp.getFirstName()).isNull();
        assertThat(emp.getLastName()).isNull();
        assertThat(emp.getSalary()).isNull();
        assertThat(emp.getAge()).isNull();
        assertThat(emp.getJobTitle()).isNull();
        assertThat(emp.getEmail()).isNull();
        assertThat(emp.getContractHireDate()).isNull();
        assertThat(emp.getContractTerminationDate()).isNull();
    }

    @Test
    @DisplayName("All setters and getters work correctly")
    void settersAndGetters_workCorrectly() {
        EmployeeImpl emp = new EmployeeImpl();
        UUID uuid = UUID.randomUUID();
        Instant hireDate = Instant.parse("2024-01-01T00:00:00Z");
        Instant termDate = Instant.parse("2025-01-01T00:00:00Z");

        emp.setUuid(uuid);
        emp.setFirstName("Alice");
        emp.setLastName("Brown");
        emp.setSalary(100000);
        emp.setAge(40);
        emp.setJobTitle("Director");
        emp.setEmail("alice.brown@example.com");
        emp.setContractHireDate(hireDate);
        emp.setContractTerminationDate(termDate);

        assertThat(emp.getUuid()).isEqualTo(uuid);
        assertThat(emp.getFirstName()).isEqualTo("Alice");
        assertThat(emp.getLastName()).isEqualTo("Brown");
        assertThat(emp.getSalary()).isEqualTo(100000);
        assertThat(emp.getAge()).isEqualTo(40);
        assertThat(emp.getJobTitle()).isEqualTo("Director");
        assertThat(emp.getEmail()).isEqualTo("alice.brown@example.com");
        assertThat(emp.getContractHireDate()).isEqualTo(hireDate);
        assertThat(emp.getContractTerminationDate()).isEqualTo(termDate);
    }

    @Test
    @DisplayName("getFullName → combines firstName and lastName")
    void getFullName_combinesNames() {
        EmployeeImpl emp = new EmployeeImpl();
        emp.setFirstName("John");
        emp.setLastName("Doe");

        String fullName = emp.getFullName();

        assertThat(fullName).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("setFullName → is a no-op (full name is derived)")
    void setFullName_isNoOp() {
        EmployeeImpl emp = new EmployeeImpl();
        emp.setFirstName("John");
        emp.setLastName("Doe");

        emp.setFullName("Should Be Ignored");

        assertThat(emp.getFullName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("setContractTerminationDate with null → nullable field")
    void contractTerminationDate_nullable() {
        EmployeeImpl emp = new EmployeeImpl();
        emp.setContractTerminationDate(Instant.now());

        emp.setContractTerminationDate(null);

        assertThat(emp.getContractTerminationDate()).isNull();
    }
}
