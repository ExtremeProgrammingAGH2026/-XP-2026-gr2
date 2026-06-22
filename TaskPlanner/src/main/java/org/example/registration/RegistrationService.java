package org.example.registration;

import org.example.AuthService;
import org.example.CsvConstants;
import org.example.CsvEscaper;
import org.example.CsvException;
import org.example.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.UUID;

/**
 * Registration of new user accounts and persistence to CSV.
 * User Story: #1 - User Registration
 */
public class RegistrationService {

    private final String filePath;
    private final RegistrationValidator rv;

    /**
     * @param filePath  path to the users CSV file
     * @param rv validator used to check registration input
     */
    public RegistrationService(String filePath, RegistrationValidator rv) {
        this.filePath = filePath;
        this.rv = rv;
    }

    /**
     * Registers a new user account. Validates input, checks for duplicate email,
     * and appends the new user to the CSV file.
     *
     * @param name     the user's display name
     * @param email    the user's email address
     * @param password the user's chosen password
     * @return the newly created {@link User}
     */
    public User register(String name, String email, String password) {
        rv.validate(name, email, password);

        AuthService as = new AuthService(filePath);
        List<User> existingUsers = loadExistingUsers(as);
        rv.validateEmailNotTaken(email, existingUsers);

        String id = UUID.randomUUID().toString();
        User newUser = new User(id, email, name, password);
        appendUserToCsv(newUser);
        return newUser;
    }

    private List<User> loadExistingUsers(AuthService as) {
        Path path = Path.of(filePath);
        if(!Files.exists(path)) {
            return List.of();
        }
        return as.loadUsers();
    }

    private void appendUserToCsv(User user) {
        String row =
                String.join(CsvConstants.SEPARATOR_STR,
                        CsvEscaper.escape(user.getId()),
                        CsvEscaper.escape(user.getEmail()),
                        CsvEscaper.escape(user.getName()),
                        CsvEscaper.escape(user.getPassword()))
                        + System.lineSeparator();
        try {
            Files.writeString(Path.of(filePath), row, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new CsvException("Failed to save user to CSV: " + filePath, e);
        }
    }
}
