package com.challenge.api.model.impl;

import com.challenge.api.model.Employee;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
public class EmployeeImpl implements Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    private String firstName;
    private String lastName;
    private Integer salary;
    private Integer age;
    private String jobTitle;
    private String email;

    @CreationTimestamp
    private Instant contractHireDate;

    private Instant contractTerminationDate;

    @Override
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public void setFullName(String name) {
        // No-op: Full name is derived from parts and cannot drift
    }
}
