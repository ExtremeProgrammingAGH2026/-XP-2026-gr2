package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateTaskAssignmentTest {

    private static final ZoneId WARSAW =
            ZoneId.of("Europe/Warsaw");

    @TempDir
    Path tempDir;

    private Path usersFile;
    private Path tasksFile;
    private User currentUser;
    private CreateTaskUI createTaskUI;

    @BeforeEach
    void setUp() throws IOException {
        usersFile = tempDir.resolve("users.csv");
        tasksFile = tempDir.resolve("tasks.csv");

        Files.writeString(
                usersFile,
                "1;alice@example.com;Alice;password123\n"
                        + "2;bob@example.com;Bob;password456",
                StandardCharsets.UTF_8
        );

        currentUser = new User(
                "1",
                "alice@example.com",
                "Alice",
                "password123"
        );

        createTaskUI = new CreateTaskUI(
                new TaskSaveService(),
                new AuthService(usersFile.toString()),
                tasksFile.toString()
        );
    }

    @Test
    void shouldAssignTaskToSelectedUser() throws IOException {
        Scanner scanner = new Scanner(
                "Prepare report\n"
                        + "Quarterly report\n"
                        + "2\n"
                        + "15.06.2026 10:00\n"
                        + "15.06.2026 11:00\n"
                        + "n\n"
        );

        Task task = createTaskUI.createTask(
                scanner,
                currentUser
        );

        assertNotNull(task);
        assertEquals("Bob", task.getOwner());

        assertEquals(
                LocalDateTime.of(
                        2026,
                        6,
                        15,
                        10,
                        0
                ).atZone(WARSAW).toInstant(),
                task.getStartDate()
        );

        assertTrue(
                Files.readString(tasksFile).contains(";Bob;")
        );
    }

    @Test
    void shouldIgnoreConflictsBelongingToAnotherUser() throws IOException {
        Files.writeString(
                tasksFile,
                "id;title;description;owner;startDate;endDate;"
                        + "status;type;recurrencePattern;recurrenceEndDate"
                        + System.lineSeparator()
                        + "existing;Alice task;Description;Alice;"
                        + "15.06.2026 10:00;15.06.2026 11:00;"
                        + "NEW;NORMAL;;"
                        + System.lineSeparator(),
                StandardCharsets.UTF_8
        );

        Scanner scanner = new Scanner(
                "Bob task\n"
                        + "Description\n"
                        + "2\n"
                        + "15.06.2026 10:00\n"
                        + "15.06.2026 11:00\n"
                        + "n\n"
        );

        Task task = createTaskUI.createTask(scanner, currentUser);

        assertNotNull(task);
        assertEquals("Bob", task.getOwner());
    }
}