package com.challenge.api.service;

import com.challenge.api.model.Employee;
import com.challenge.api.model.impl.EmployeeImpl;
import com.challenge.api.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employeeRepository.findAll());
    }

    public Employee getEmployeeByUuid(UUID uuid) {
        return employeeRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Employee by the uuid %s does not exist", uuid)));
    }

    public Employee createEmployee(EmployeeImpl requestBody) {
        return employeeRepository.save(requestBody);
    }
}
