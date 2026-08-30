package com.challenge.api.repository;

import com.challenge.api.model.impl.EmployeeImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeImpl, UUID> {
}
