package com.challenge.api.dto;

import static org.assertj.core.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateEmployeeRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    private CreateEmployeeRequest validRequest() {
        return new CreateEmployeeRequest(
                "Alice", "Williams", 85000, 30, "Engineer", "alice@example.com", Instant.parse("2024-01-01T00:00:00Z"));
    }

    @Test
    @DisplayName("Record accessors return correct values")
    void accessors_returnCorrectValues() {
        Instant hireDate = Instant.parse("2024-06-15T00:00:00Z");
        CreateEmployeeRequest req =
                new CreateEmployeeRequest("Bob", "Jones", 60000, 25, "Intern", "bob@test.com", hireDate);

        assertThat(req.firstName()).isEqualTo("Bob");
        assertThat(req.lastName()).isEqualTo("Jones");
        assertThat(req.salary()).isEqualTo(60000);
        assertThat(req.age()).isEqualTo(25);
        assertThat(req.jobTitle()).isEqualTo("Intern");
        assertThat(req.email()).isEqualTo("bob@test.com");
        assertThat(req.contractHireDate()).isEqualTo(hireDate);
    }

    @Test
    @DisplayName("Valid request → no constraint violations")
    void validRequest_noViolations() {
        Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(validRequest());

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Blank firstName → constraint violation")
    void blankFirstName_violation() {
        CreateEmployeeRequest req =
                new CreateEmployeeRequest("", "Williams", 85000, 30, "Engineer", "alice@example.com", null);

        Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(req);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("firstName"));
    }

    @Test
    @DisplayName("Blank lastName → constraint violation")
    void blankLastName_violation() {
        CreateEmployeeRequest req =
                new CreateEmployeeRequest("Alice", "", 85000, 30, "Engineer", "alice@example.com", null);

        Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(req);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("lastName"));
    }

    @Test
    @DisplayName("Null salary → constraint violation")
    void nullSalary_violation() {
        CreateEmployeeRequest req =
                new CreateEmployeeRequest("Alice", "Williams", null, 30, "Engineer", "alice@example.com", null);

        Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(req);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("salary"));
    }

    @Test
    @DisplayName("Age below minimum (20) → constraint violation")
    void ageBelowMin_violation() {
        CreateEmployeeRequest req =
                new CreateEmployeeRequest("Alice", "Williams", 85000, 20, "Engineer", "alice@example.com", null);

        Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(req);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("age")
                        && v.getMessage().contains("at least 21"));
    }

    @Test
    @DisplayName("Age above maximum (71) → constraint violation")
    void ageAboveMax_violation() {
        CreateEmployeeRequest req =
                new CreateEmployeeRequest("Alice", "Williams", 85000, 71, "Engineer", "alice@example.com", null);

        Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(req);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("age")
                        && v.getMessage().contains("more than 70"));
    }

    @Test
    @DisplayName("Age at minimum boundary (21) → no violation")
    void ageAtMin_noViolation() {
        CreateEmployeeRequest req =
                new CreateEmployeeRequest("Alice", "Williams", 85000, 21, "Engineer", "alice@example.com", null);

        Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(req);

        assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("age"));
    }

    @Test
    @DisplayName("Age at maximum boundary (70) → no violation")
    void ageAtMax_noViolation() {
        CreateEmployeeRequest req =
                new CreateEmployeeRequest("Alice", "Williams", 85000, 70, "Engineer", "alice@example.com", null);

        Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(req);

        assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("age"));
    }

    @Test
    @DisplayName("Blank jobTitle → constraint violation")
    void blankJobTitle_violation() {
        CreateEmployeeRequest req =
                new CreateEmployeeRequest("Alice", "Williams", 85000, 30, "", "alice@example.com", null);

        Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(req);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("jobTitle"));
    }

    @Test
    @DisplayName("Invalid email format → constraint violation")
    void invalidEmail_violation() {
        CreateEmployeeRequest req =
                new CreateEmployeeRequest("Alice", "Williams", 85000, 30, "Engineer", "not-an-email", null);

        Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(req);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    @DisplayName("Blank email → constraint violation")
    void blankEmail_violation() {
        CreateEmployeeRequest req = new CreateEmployeeRequest("Alice", "Williams", 85000, 30, "Engineer", "", null);

        Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(req);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    @DisplayName("Future contractHireDate → constraint violation")
    void futureHireDate_violation() {
        Instant futureDate = Instant.now().plusSeconds(86400 * 365);
        CreateEmployeeRequest req =
                new CreateEmployeeRequest("Alice", "Williams", 85000, 30, "Engineer", "alice@example.com", futureDate);

        Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(req);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("contractHireDate"));
    }

    @Test
    @DisplayName("Null age → constraint violation")
    void nullAge_violation() {
        CreateEmployeeRequest req =
                new CreateEmployeeRequest("Alice", "Williams", 85000, null, "Engineer", "alice@example.com", null);

        Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(req);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("age"));
    }
}
