package org.example;

import org.example.registration.RegistrationService;
import org.example.registration.RegistrationValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationUITest {

    @TempDir
    Path tempDir;

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream output;

    private RegistrationUI registrationUI;

    @BeforeEach
    public void setUp() {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        String usersFile = tempDir.resolve("users.csv").toString();
        RegistrationService registrationService = new RegistrationService(usersFile, new RegistrationValidator());
        registrationUI = new RegistrationUI(registrationService);
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void shouldReturnUserOnValidInput() {
        Scanner scanner = new Scanner("Alice\nalice@example.com\npassword123\n");
        User user = registrationUI.register(scanner);
        assertNotNull(user);
        assertEquals("Alice", user.getName());
        assertEquals("alice@example.com", user.getEmail());
    }

    @Test
    public void shouldPrintErrorMessageOnInvalidEmail() {
        Scanner scanner = new Scanner("Alice\nbad-email\ncancel\n");
        registrationUI.register(scanner);
        assertTrue(output.toString().contains("Email format is invalid"));
    }

    @Test
    public void shouldPrintSuccessMessageOnSuccess() {
        Scanner scanner = new Scanner("Alice\nalice@example.com\npassword123\n");
        registrationUI.register(scanner);
        assertTrue(output.toString().contains("Welcome"));
    }

    @Test
    public void shouldRepromptOnInvalidEmailAndAcceptValidOneOnRetry() {
        Scanner scanner = new Scanner("Alice\nnot-an-email\nalice@example.com\npassword123\n");
        User user = registrationUI.register(scanner);
        assertNotNull(user);
        assertEquals("alice@example.com", user.getEmail());
    }

    @Test
    public void shouldRepromptOnShortPasswordAndAcceptValidOneOnRetry() {
        Scanner scanner = new Scanner("Alice\nalice@example.com\nshort\npassword123\n");
        User user = registrationUI.register(scanner);
        assertNotNull(user);
        assertEquals("password123", user.getPassword());
    }

    @Test
    public void shouldRepromptOnBlankNameAndAcceptValidOneOnRetry() {
        Scanner scanner = new Scanner("\nAlice\nalice@example.com\npassword123\n");
        User user = registrationUI.register(scanner);
        assertNotNull(user);
        assertEquals("Alice", user.getName());
    }

    @Test
    public void shouldReturnNullWhenUserCancelsAtName() {
        Scanner scanner = new Scanner("cancel\n");
        User user = registrationUI.register(scanner);
        assertNull(user);
    }

    @Test
    public void shouldReturnNullWhenUserCancelsAtEmail() {
        Scanner scanner = new Scanner("Alice\ncancel\n");
        User user = registrationUI.register(scanner);
        assertNull(user);
    }

    @Test
    public void shouldReturnNullWhenUserCancelsAtPassword() {
        Scanner scanner = new Scanner("Alice\nalice@example.com\ncancel\n");
        User user = registrationUI.register(scanner);
        assertNull(user);
    }

    @Test
    public void shouldPrintCancellationMessageWhenUserCancels() {
        Scanner scanner = new Scanner("Alice\ncancel\n");
        registrationUI.register(scanner);
        assertTrue(output.toString().contains("Registration cancelled"));
    }
}
