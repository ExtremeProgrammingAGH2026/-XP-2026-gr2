package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class LoginUITest {

    private LoginUI loginUI;
    private AuthService authService;

    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() throws IOException {
        Path csvPath = tempDir.resolve("users.csv");
        Files.write(csvPath, "1;alice@example.com;Alice;secret123\n2;bob@example.com;Bob;pass456".getBytes(StandardCharsets.UTF_8));
        authService = new AuthService(csvPath.toString());
        loginUI = new LoginUI(authService);
    }

    @Test
    public void shouldReturnUserOnValidCredentials() {
        Scanner scanner = new Scanner("alice@example.com\nsecret123\n");
        User user = loginUI.login(scanner);
        assertNotNull(user);
        assertEquals("Alice", user.getName());
    }

    @Test
    public void shouldReturnNullAfterMaxFailedAttempts() {
        Scanner scanner = new Scanner("alice@example.com\nbadpass\nbadpass\nbadpass\n");
        User user = loginUI.login(scanner);
        assertNull(user);
    }

    @Test
    public void shouldSucceedOnSecondAttempt() {
        Scanner scanner = new Scanner("bob@example.com\nwrongpass\npass456\n");
        User user = loginUI.login(scanner);
        assertNotNull(user);
        assertEquals("Bob", user.getName());
    }
}
