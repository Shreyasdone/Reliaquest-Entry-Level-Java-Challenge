package com.challenge.api.security;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    @DisplayName("PasswordEncoder bean encodes and verifies passwords correctly")
    void passwordEncoder_encodesAndVerifies() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String rawPassword = "testSecret123";

        String encoded = encoder.encode(rawPassword);

        assertThat(encoded).isNotEqualTo(rawPassword);
        assertThat(encoder.matches(rawPassword, encoded)).isTrue();
        assertThat(encoder.matches("wrongPassword", encoded)).isFalse();
    }

    @Test
    @DisplayName("PasswordEncoder produces delegating-format hash (bcrypt prefix)")
    void passwordEncoder_producesDelegatingFormat() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        String encoded = encoder.encode("somePassword");

        assertThat(encoded).startsWith("{bcrypt}");
    }

    @Test
    @DisplayName("UserDetailsService loads 'employees-r-us' user with correct roles")
    void userDetailsService_loadsUserWithCorrectRoles() {
        PasswordEncoder mockEncoder = mock(PasswordEncoder.class);
        when(mockEncoder.encode(any())).thenReturn("{bcrypt}$2a$10$testEncodedPassword");

        UserDetailsService uds = securityConfig.users(mockEncoder);
        UserDetails user = uds.loadUserByUsername("employees-r-us");

        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("employees-r-us");
        assertThat(user.getAuthorities())
                .extracting(auth -> auth.getAuthority())
                .contains("ROLE_EMPLOYEE_READER", "ROLE_EMPLOYEE_WRITER");
    }

    @Test
    @DisplayName("UserDetailsService encodes password via the injected encoder")
    void userDetailsService_passwordIsEncoded() {
        String encodedPassword = "{bcrypt}$2a$10$testEncodedPassword";
        PasswordEncoder mockEncoder = mock(PasswordEncoder.class);
        when(mockEncoder.encode(any())).thenReturn(encodedPassword);

        UserDetailsService uds = securityConfig.users(mockEncoder);
        UserDetails user = uds.loadUserByUsername("employees-r-us");

        assertThat(user.getPassword()).isEqualTo(encodedPassword);
    }
}
