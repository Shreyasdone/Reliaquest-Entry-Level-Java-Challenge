package com.challenge.api.repository;

import com.challenge.api.model.impl.EmployeeImpl;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeImpl, UUID> {
    boolean existsByEmail(String email);
}
