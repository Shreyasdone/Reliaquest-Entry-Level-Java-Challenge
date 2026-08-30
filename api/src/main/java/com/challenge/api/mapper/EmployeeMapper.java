package com.challenge.api.mapper;

import com.challenge.api.dto.CreateEmployeeRequest;
import com.challenge.api.model.impl.EmployeeImpl;
import java.util.UUID;

public final class EmployeeMapper {

    private EmployeeMapper() {}

    public static EmployeeImpl toEmployee(CreateEmployeeRequest request) {
        EmployeeImpl employee = new EmployeeImpl();

        employee.setUuid(UUID.randomUUID());
        employee.setFirstName(request.firstName());
        employee.setLastName(request.lastName());
        employee.setFullName(request.firstName() + " " + request.lastName());
        employee.setSalary(request.salary());
        employee.setAge(request.age());
        employee.setJobTitle(request.jobTitle());
        employee.setEmail(request.email());
        employee.setContractHireDate(request.contractHireDate());
        employee.setContractTerminationDate(null);

        return employee;
    }
}
