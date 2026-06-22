package org.example.registration;

import org.example.User;

import java.util.List;
import java.util.function.IntSupplier;

/**
 * Validates registration input data provided by the user.
 * User Story: #1 - User Registration
 */
public class RegistrationValidator {

    private static final int DEFAULT_MIN_PASSWORD_LENGTH = 8;
    private static final String EMAIL_REGEX = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";

    private final IntSupplier minPasswordLength;

    public RegistrationValidator() {
        this(DEFAULT_MIN_PASSWORD_LENGTH);
    }

    public RegistrationValidator(int minPasswordLength) {
        this(() -> minPasswordLength);
    }

    public RegistrationValidator(IntSupplier minPasswordLength) {
        this.minPasswordLength = minPasswordLength;
    }

    /**
     * Validates registration fields. Throws {@link RegistrationException} if any field is invalid.
     *
     * @param name     the user's display name
     * @param email    the user's email address
     * @param password the user's chosen password
     */
    public void validate(String name, String email, String password) {
        validateName(name);
        validateEmail(email);
        validatePassword(password);
    }

    public void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new RegistrationException("Name must not be empty");
        }
    }

    public void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RegistrationException("Email must not be empty");
        }
        if (!email.matches(EMAIL_REGEX)) {
            throw new RegistrationException("Email format is invalid");
        }
    }

    public void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new RegistrationException("Password must not be empty");
        }
        int min = minPasswordLength.getAsInt();
        if (password.length() < min) {
            throw new RegistrationException("Password must be at least " + min + " characters long");
        }
    }

    /**
     * Checks whether the given email is already taken by an existing user.
     *
     * @param email         the email to check
     * @param existingUsers list of already registered users
     */
    public void validateEmailNotTaken(String email, List<User> existingUsers) {
        for (User user : existingUsers) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                throw new RegistrationException("Email is already registered");
            }
        }
    }
}
