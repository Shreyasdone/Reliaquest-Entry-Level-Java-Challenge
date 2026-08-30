package com.challenge.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.Instant;
import java.util.UUID;

/**
 * Every abstraction of an Employee should, at the bare minimum, implement this interface. Consider this a binding
 * contract for the domain model of an Employee.
 */
public interface Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID getUuid();

    /**
     * Set by either the Service or Data layer.
     * @param uuid required non-null
     */
    void setUuid(UUID uuid);

    String getFirstName();

    void setFirstName(String name);

    String getLastName();

    void setLastName(String name);

    String getFullName();

    void setFullName(String name);

    Integer getSalary();

    void setSalary(Integer salary);

    Integer getAge();

    void setAge(Integer age);

    String getJobTitle();

    void setJobTitle(String jobTitle);

    String getEmail();

    @Column(unique = true, nullable = false)
    void setEmail(String email);

    Instant getContractHireDate();

    void setContractHireDate(Instant date);

    /**
     * Nullable.
     * @return null, if Employee has not been terminated.
     */
    Instant getContractTerminationDate();

    void setContractTerminationDate(Instant date);
}
