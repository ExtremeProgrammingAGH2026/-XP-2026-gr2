package org.example;

import org.example.registration.RegistrationService;
import org.example.registration.RegistrationValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class StartScreenUITest {

    @TempDir
    Path tempDir;

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream output;

    private StartScreenUI startScreen;

    @BeforeEach
    public void setUp() throws IOException {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        Path usersFile = tempDir.resolve("users.csv");
        Files.write(usersFile, "1;alice@example.com;Alice;password123".getBytes(StandardCharsets.UTF_8));

        AuthService authService = new AuthService(usersFile.toString());
        LoginUI loginUI = new LoginUI(authService);
        RegistrationService registrationService = new RegistrationService(usersFile.toString(), new RegistrationValidator());
        RegistrationUI registrationUI = new RegistrationUI(registrationService);

        startScreen = new StartScreenUI(loginUI, registrationUI);
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void shouldDisplayMenuOptions() {
        Scanner scanner = new Scanner("3\n");
        startScreen.run(scanner);

        String out = output.toString();
        assertTrue(out.contains("Login"));
        assertTrue(out.contains("Register"));
        assertTrue(out.contains("Exit"));
    }

    @Test
    public void shouldReturnNullOnExit() {
        Scanner scanner = new Scanner("3\n");
        User user = startScreen.run(scanner);
        assertNull(user);
    }

    @Test
    public void shouldReturnUserOnSuccessfulLogin() {
        Scanner scanner = new Scanner("1\nalice@example.com\npassword123\n");
        User user = startScreen.run(scanner);
        assertNotNull(user);
        assertEquals("Alice", user.getName());
    }

    @Test
    public void shouldReturnUserOnSuccessfulRegistration() {
        Scanner scanner = new Scanner("2\nBob\nbob@example.com\npassword123\n");
        User user = startScreen.run(scanner);
        assertNotNull(user);
        assertEquals("Bob", user.getName());
    }

    @Test
    public void shouldLoopOnInvalidChoice() {
        Scanner scanner = new Scanner("99\n3\n");
        startScreen.run(scanner);
        assertTrue(output.toString().contains("Invalid choice"));
    }

    @Test
    public void shouldLoopAfterFailedLogin() {
        Scanner scanner = new Scanner("1\nbad@example.com\nbadpass\nbad@example.com\nbadpass\nbad@example.com\nbadpass\n3\n");
        User user = startScreen.run(scanner);
        assertNull(user);
    }
}
