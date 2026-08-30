package com.challenge.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.Instant;

public record CreateEmployeeRequest(
        @NotBlank(message = "First Name can't be empty") String firstName,
        @NotBlank(message = "Last Name can't be empty") String lastName,
        @NotNull Integer salary,
        @NotNull @Min(value = 21, message = "Age must be at least 21")
                @Max(value = 70, message = "Age can't be more than 70")
                Integer age,
        @NotBlank(message = "Job Title can't be empty") String jobTitle,
        @NotBlank(message = "Email ID can't be empty") @Email(message = "Invalid Email Format") String email,
        @PastOrPresent(message = "Creation date cannot be in the future") Instant contractHireDate) {}
