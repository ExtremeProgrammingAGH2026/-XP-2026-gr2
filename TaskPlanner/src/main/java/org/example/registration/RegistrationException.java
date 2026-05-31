package org.example.registration;

/**
 * Exception thrown when registration data provided by the user is invalid.
 * User Story: #1 - User Registration
 */
public class RegistrationException extends RuntimeException {
    /**
     * @param message description of the validation error
     */
    public RegistrationException(String message) {
        super(message);
    }
}