package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

import org.example.recurring.RecurringTask;

public class CreateTaskUITest {

    private static final ZoneId WARSAW = ZoneId.of("Europe/Warsaw");

    @TempDir
    Path tempDir;

    private String tasksFile;
    private User currentUser;
    private CreateTaskUI createTaskUI;

    @BeforeEach
    public void setUp() {
        tasksFile = tempDir.resolve("tasks.csv").toString();
        currentUser = new User("1", "alice@example.com", "Alice", "secret");
        createTaskUI = new CreateTaskUI(new TaskSaveService(), tasksFile);
    }

    @Test
    public void shouldCreateTaskWithCorrectTitle() {
        Scanner scanner = new Scanner("Buy milk\nGet 2 liters\n01.06.2026 10:00\n01.06.2026 11:00\nn\n");
        Task task = createTaskUI.createTask(scanner, currentUser);
        assertEquals("Buy milk", task.getTitle());
    }

    @Test
    public void shouldCreateTaskWithCorrectDescription() {
        Scanner scanner = new Scanner("Buy milk\nGet 2 liters\n01.06.2026 10:00\n01.06.2026 11:00\nn\n");
        Task task = createTaskUI.createTask(scanner, currentUser);
        assertEquals("Get 2 liters", task.getDescription());
    }

    @Test
    public void shouldSetCurrentUserAsOwner() {
        Scanner scanner = new Scanner("Clean room\nVacuum and mop\n02.06.2026 09:00\n02.06.2026 10:00\nn\n");
        Task task = createTaskUI.createTask(scanner, currentUser);
        assertEquals("Alice", task.getOwner());
    }

    @Test
    public void shouldParseStartDateCorrectly() {
        Scanner scanner = new Scanner("Buy milk\nGet 2 liters\n15.06.2026 14:30\n15.06.2026 15:00\nn\n");
        Task task = createTaskUI.createTask(scanner, currentUser);
        ZonedDateTime expected = ZonedDateTime.of(2026, 6, 15, 14, 30, 0, 0, WARSAW);
        assertEquals(expected.toInstant(), task.getStartDate());
    }

    @Test
    public void shouldParseEndDateCorrectly() {
        Scanner scanner = new Scanner("Buy milk\nGet 2 liters\n15.06.2026 14:30\n15.06.2026 16:00\nn\n");
        Task task = createTaskUI.createTask(scanner, currentUser);
        ZonedDateTime expected = ZonedDateTime.of(2026, 6, 15, 16, 0, 0, 0, WARSAW);
        assertEquals(expected.toInstant(), task.getEndDate());
    }

    @Test
    public void shouldCreateTaskWithStatusNew() {
        Scanner scanner = new Scanner("Buy milk\nGet 2 liters\n01.06.2026 10:00\n01.06.2026 11:00\nn\n");
        Task task = createTaskUI.createTask(scanner, currentUser);
        assertEquals(TaskStatus.NEW, task.getStatus());
    }

    @Test
    public void shouldSaveTaskToFile() throws IOException {
        Scanner scanner = new Scanner("Buy milk\nGet 2 liters\n01.06.2026 10:00\n01.06.2026 11:00\nn\n");
        createTaskUI.createTask(scanner, currentUser);
        String content = Files.readString(Path.of(tasksFile), StandardCharsets.UTF_8);
        assertTrue(content.contains("Buy milk"));
        assertTrue(content.contains("Alice"));
    }

    @Test
    public void shouldRetryOnInvalidDateFormat() {
        Scanner scanner = new Scanner("Buy milk\nGet 2 liters\nnot-a-date\n01.06.2026 10:00\n01.06.2026 11:00\nn\n");
        Task task = createTaskUI.createTask(scanner, currentUser);
        assertNotNull(task);
        assertEquals("Buy milk", task.getTitle());
    }

    @Test
    public void shouldCreateRecurringTaskWhenUserChoosesYes() {
        Scanner scanner = new Scanner(
                "Pay rent\nMonthly payment\n01.06.2026 10:00\n01.06.2026 11:00\ny\n4\n\n");

        Task task = createTaskUI.createTask(scanner, currentUser);

        assertTrue(task instanceof RecurringTask);
    }
}
