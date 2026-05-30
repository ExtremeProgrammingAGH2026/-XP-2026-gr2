package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class AuthServiceTest {
    private AuthService authService;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    public void setUp() {
        authService = new AuthService(tempDir.resolve("users.csv").toString());
    }
    
    @Test
    public void shouldLoadUsersFromValidCsv() {
        Path csvPath = tempDir.resolve("users.csv");
        
        List<User> expectedUsers = new ArrayList<>();
        StringBuilder csv = new StringBuilder();
        expectedUsers.add(new User("1", "adas123@gmail.com", "Adam", "password123"));
        csv.append("1;adas123@gmail.com;Adam;password123").append(System.lineSeparator());
        expectedUsers.add(new User("2", "ewa.kowalska@example.com", "Ewa", "qwerty"));
        csv.append("2;ewa.kowalska@example.com;Ewa;qwerty").append(System.lineSeparator());
        expectedUsers.add(new User("3", "jan.nowak@example.com", "Jan", "secret"));
        csv.append("3;jan.nowak@example.com;Jan;secret").append(System.lineSeparator());
        expectedUsers.add(new User("4", "ola.z@example.com", "Ola", "pass123"));
        csv.append("4;ola.z@example.com;Ola;pass123").append(System.lineSeparator());
        expectedUsers.add(new User("5", "tomek@example.com", "Tomek", "123456"));
        csv.append("5;tomek@example.com;Tomek;123456");
        writeUtf8(csvPath, csv.toString());
        
        List<User> users = authService.loadUsers();
        assertEquals(5, users.size());
        for (int i = 0; i < expectedUsers.size(); i++) {
            User expected = expectedUsers.get(i);
            User actual = users.get(i);
            assertEquals(expected.getId(), actual.getId());
            assertEquals(expected.getEmail(), actual.getEmail());
            assertEquals(expected.getName(), actual.getName());
            assertEquals(expected.getPassword(), actual.getPassword());
        }
    }
    
    @Test
    public void shouldFindUserAndAuthenticate() {
        Path csvPath = tempDir.resolve("users.csv");

        List<User> expectedUsers = new ArrayList<>();
        StringBuilder csv = new StringBuilder();
        expectedUsers.add(new User("1", "adas123@gmail.com", "Adam", "password123"));
        csv.append("1;adas123@gmail.com;Adam;password123").append(System.lineSeparator());
        expectedUsers.add(new User("2", "ewa.kowalska@example.com", "Ewa", "qwerty"));
        csv.append("2;ewa.kowalska@example.com;Ewa;qwerty").append(System.lineSeparator());
        expectedUsers.add(new User("3", "jan.nowak@example.com", "Jan", "secret"));
        csv.append("3;jan.nowak@example.com;Jan;secret").append(System.lineSeparator());
        expectedUsers.add(new User("4", "ola.z@example.com", "Ola", "pass123"));
        csv.append("4;ola.z@example.com;Ola;pass123").append(System.lineSeparator());
        expectedUsers.add(new User("5", "tomek@example.com", "Tomek", "123456"));
        csv.append("5;tomek@example.com;Tomek;123456");
        writeUtf8(csvPath, csv.toString());
        
        for (User expected : expectedUsers) {
            User actual = authService.authenticateUser(expected.getEmail(), expected.getPassword());
            assertEquals(expected.getId(), actual.getId());
            assertEquals(expected.getEmail(), actual.getEmail());
            assertEquals(expected.getName(), actual.getName());
            assertEquals(expected.getPassword(), actual.getPassword());
        }
    }

    private static void writeUtf8(Path path, String content) {
        try {
            Files.write(path, content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            fail("Failed to write test CSV file: " + e.getMessage());
        }
    }
}