package com.challenge.api.controller.exception;

import static org.assertj.core.api.Assertions.*;

import jakarta.persistence.EntityNotFoundException;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("handleEntityNotFoundException → 404 with error message and timestamp")
    @SuppressWarnings("unchecked")
    void handleEntityNotFoundException_returns404WithDetails() {
        EntityNotFoundException ex = new EntityNotFoundException("Employee not found");

        ResponseEntity<Map<String, Object>> response = handler.handleEntityNotFoundException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(404);
        assertThat(body.get("error")).isEqualTo("Not Found");
        assertThat(body.get("message")).isEqualTo("Employee not found");
        assertThat(body.get("timestamp")).isNotNull();
    }

    @Test
    @DisplayName("handleIllegalArgumentException → 409 with error message and timestamp")
    @SuppressWarnings("unchecked")
    void handleIllegalArgumentException_returns409WithDetails() {
        IllegalArgumentException ex = new IllegalArgumentException("Email already in use");

        ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgumentException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(409);
        assertThat(body.get("error")).isEqualTo("Conflict");
        assertThat(body.get("message")).isEqualTo("Email already in use");
        assertThat(body.get("timestamp")).isNotNull();
    }

    @Test
    @DisplayName("handleDataIntegrityViolationException → 409 Conflict")
    @SuppressWarnings("unchecked")
    void handleDataIntegrityViolationException_returns409() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Unique constraint violated");

        ResponseEntity<Map<String, Object>> response = handler.handleDataIntegrityViolationException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(409);
        assertThat(body.get("error")).isEqualTo("Conflict");
        assertThat(body.get("message")).isEqualTo("A database constraint violation occurred (e.g., duplicate email).");
        assertThat(body.get("timestamp")).isNotNull();
    }

    @Test
    @DisplayName("handleValidationExceptions → 400 with detailed field errors map")
    @SuppressWarnings("unchecked")
    void handleValidationExceptions_returns400WithFieldErrors() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "firstName", "First Name can't be empty"));
        bindingResult.addError(new FieldError("target", "email", "Invalid Email Format"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(400);
        assertThat(body.get("error")).isEqualTo("Bad Request");
        assertThat(body.get("timestamp")).isNotNull();

        @SuppressWarnings("unchecked")
        Map<String, String> fieldErrors = (Map<String, String>) body.get("validationErrors");
        assertThat(fieldErrors).containsEntry("firstName", "First Name can't be empty");
        assertThat(fieldErrors).containsEntry("email", "Invalid Email Format");
    }
}
