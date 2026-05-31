package org.example.registration;

import org.example.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RegistrationValidatorTest {

    private RegistrationValidator rv;

    @BeforeEach
    public void setUp() {
        rv = new RegistrationValidator();
    }

    @Test
    public void shouldPassValidationForCorrectData() {
        assertDoesNotThrow(() -> rv.validate("Jan Kowalski", "jan@example.com", "securePass1"));
    }

    // Name
    @Test
    public void shouldThrowWhenNameIsNull() {
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> rv.validate(null, "jan@example.com", "securePass1"));
        assertEquals("Name must not be empty", exception.getMessage());
    }

    @Test
    public void shouldThrowWhenNameIsBlank() {
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> rv.validate(" ", "jan@example.com", "securePass1"));
        assertEquals("Name must not be empty", exception.getMessage());
    }

    // Email
    @Test
    public void shouldThrowWhenEmailIsNull() {
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> rv.validate("Jan Kowalski", null, "securePass1"));
        assertEquals("Email must not be empty", exception.getMessage());
    }

    @Test
    public void shouldThrowWhenEmailHasInvalidFormat() {
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> rv.validate("Jan", "not-valid", "securePass1"));
        assertEquals("Email format is invalid", exception.getMessage());
    }

    @Test
    public void shouldThrowWhenEmailHasNoAtSign() {
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> rv.validate("Jan", "janexample.com", "securePass1"));
        assertEquals("Email format is invalid", exception.getMessage());
    }

    @Test
    public void shouldPassWhenEmailIsNotTaken() {
        List<User> existing = List.of(new User("1", "other@example.com", "Other", "pass"));
        assertDoesNotThrow(() -> rv.validateEmailNotTaken("jan@example.com", existing));
    }

    @Test
    public void shouldThrowWhenEmailIsAlreadyTaken() {
        List<User> existing = List.of(new User("1", "jan@example.com", "Jan", "pass"));
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> rv.validateEmailNotTaken("jan@example.com", existing));
        assertEquals("Email is already registered", exception.getMessage());
    }

    @Test
    public void shouldThrowWhenEmailIsTakenWithDifferentCase() {
        List<User> existing = List.of(new User("1", "Jan@Example.com", "Jan", "pass"));
        assertThrows(RegistrationException.class,
                () -> rv.validateEmailNotTaken("jan@example.com", existing));
    }

    // Password
    @Test
    public void shouldPassWhenPasswordIsExactlyMinimumLength() {
        assertDoesNotThrow(() -> rv.validate("Jan", "jan@example.com", "12345678"));
    }

    @Test
    public void shouldThrowWhenPasswordIsTooShort() {
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> rv.validate("Jan", "jan@example.com", "123"));
        assertEquals("Password must be at least 8 characters long", exception.getMessage());
    }

    @Test
    public void shouldThrowWhenPasswordIsNull() {
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> rv.validate("Jan", "jan@example.com", null));
        assertEquals("Password must not be empty", exception.getMessage());
    }
}
