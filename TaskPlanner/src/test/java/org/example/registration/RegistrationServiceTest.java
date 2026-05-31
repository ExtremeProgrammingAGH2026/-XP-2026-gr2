package org.example.registration;

import org.example.AuthService;
import org.example.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegistrationServiceTest {

    @TempDir
    Path tempDir;

    private Path csvPath;
    private RegistrationService rs;

    @BeforeEach
    public void setUp() {
        csvPath = tempDir.resolve("user.csv");
        RegistrationValidator rv = new RegistrationValidator();
        rs = new RegistrationService(csvPath.toString(), rv);
    }

    @Test
    public void shouldRegisterNewUserAndReturnUserObject() {
        User user = rs.register("Jan Kowalski", "jan@example.com", "securePass1");

        assertNotNull(user);
        assertEquals("Jan Kowalski", user.getName());
        assertEquals("jan@example.com", user.getEmail());
        assertEquals("securePass1", user.getPassword());
        assertNotNull(user.getId());
    }

    // Save to CSV file
    @Test
    public void shouldSaveNewUserToCsvFile() throws IOException {
        rs.register("Jan Kowalski", "jan@example.com", "securePass1");

        assertTrue(Files.exists(csvPath));
        String content = Files.readString(csvPath, StandardCharsets.UTF_8);
        assertTrue(content.contains("Jan Kowalski"));
        assertTrue(content.contains("jan@example.com"));
        assertTrue(content.contains("securePass1"));
    }

    @Test
    public void shouldSaveMultipleUsersToSameCsvFile() throws IOException {
        rs.register("Jan Kowalski", "jan@example.com", "securePass1");
        rs.register("Anna Nowak", "anna@example.com", "securePass2");

        String content = Files.readString(csvPath, StandardCharsets.UTF_8);
        assertTrue(content.contains("jan@example.com"));
        assertTrue(content.contains("anna@example.com"));
    }

    //Format
    @Test
    public void shouldThrowWhenNameIsEmpty() {
        assertThrows(RegistrationException.class,
                () -> rs.register(" ", "jan@example.com", "securePass1"));
    }

    @Test
    public void shouldThrowWhenEmailIsInvalid() {
        assertThrows(RegistrationException.class,
                () -> rs.register("Jan Kowalski", "not-valid", "securePass1"));
    }

    @Test
    public void shouldThrowWhenPasswordIsTooShort() {
        assertThrows(RegistrationException.class,
                () -> rs.register("Jan Kowalski", "jan@example.com", "123"));
    }

    // Duplication
    @Test
    public void shouldThrowWhenEmailIsAlreadyRegistered() {
        rs.register("Jan Kowalski", "jan@example.com", "securePass1");

        assertThrows(RegistrationException.class,
                () -> rs.register("Jan Kasztan", "jan@example.com", "otherPass1"));
    }

    @Test
    public void shouldLoadSavedUserViaAuthService() {
        rs.register("Jan Kowalski", "jan@example.com", "securePass1");

        AuthService as = new AuthService(csvPath.toString());
        List<User> users = as.loadUsers();

        assertEquals(1, users.size());
        assertEquals("jan@example.com", users.get(0).getEmail());
    }

    @Test
    public void shouldGenerateUniqueIdsForDifferentUsers() {
        User first = rs.register("Jan Kowalski", "jan@example.com", "securePass1");
        User second = rs.register("Anna Nowak", "anna@example.com", "securePass2");

        assertNotEquals(first.getId(), second.getId());
    }
}
