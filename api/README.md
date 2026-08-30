# Employee API Service

A lightweight, secure Spring Boot REST API for managing employee records and interfacing with external webhook systems.

---

## Tech Stack & Tools Used

- **Language & Runtime**: Java 17+ (compatible up to Java 24)
- **Framework**: Spring Boot 3.2+
  - **Spring Web**: RESTful API controllers and routing
  - **Spring Data JPA**: Persistence and CRUD operations
  - **Spring Security**: HTTP Basic authentication and Role-Based Access Control (RBAC)
  - **Jakarta Validation**: Request payload validation constraints
- **Database**: In-Memory H2 Database (with automated `schema.sql` and `data.sql` seeding)
- **Build Tool**: Gradle (with Spotless code formatting plugin)
- **Testing**: JUnit 5, Mockito, AssertJ, Spring MockMvc, Spring Security Test

---

## What Has Been Implemented

1. **REST Endpoints (`EmployeeController`)**:
   - `GET /api/v1/employee/all` &mdash; Retrieves all employees.
   - `GET /api/v1/employee/{id}` &mdash; Retrieves an employee by UUID (validates UUID format and handles 404 Not Found).
   - `POST /api/v1/employee` &mdash; Validates request body and creates a new employee record (returns 201 Created).

2. **Domain & Data Persistence**:
   - `Employee` domain interface and `EmployeeImpl` JPA entity mapped to an H2 database.
   - `EmployeeRepository` extending `JpaRepository` for data access and email lookup.
   - `EmployeeMapper` converting between `CreateEmployeeRequest` DTOs and `EmployeeImpl` entities.

3. **Input Validation (`CreateEmployeeRequest`)**:
   - Jakarta Bean Validation constraints on fields (names, positive salary, age range 21–70, email format, past/present hire date).

4. **Security & RBAC (`SecurityConfig`)**:
   - HTTP Basic Authentication protecting all `/api/**` endpoints.
   - Role-based permissions: `ROLE_EMPLOYEE_READER` for read operations and `ROLE_EMPLOYEE_WRITER` for write operations.
   - Configurable partner credentials via `PARTNER_API_SECRET` environment variable.

5. **Centralized Exception Handling (`GlobalExceptionHandler`)**:
   - `@RestControllerAdvice` producing consistent JSON error responses for:
     - `400 Bad Request` (Validation errors)
     - `404 Not Found` (Entity not found)
     - `409 Conflict` (Duplicate email or invalid UUID syntax)
     - `500 Internal Server Error` (Unexpected server errors)

6. **Comprehensive Test Suite (49 Tests)**:
   - Full test coverage across Controller, Service, DTO Validation, Mapper, Model, Security, and Exception Handler layers.

---

## Quick Start

### 1. Set Environment Variable
```bash
export PARTNER_API_SECRET=mySecretPassword   # Linux/macOS
$env:PARTNER_API_SECRET="mySecretPassword"   # Windows PowerShell
```

### 2. Build and Run
```bash
./gradlew :api:bootRun
```
The application will start at `http://localhost:8080`.

### 3. Run Tests
```bash
./gradlew test
```
