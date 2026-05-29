package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskSaveServiceTest {

    private static final ZoneId ZONE = ZoneId.of("Europe/Warsaw");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final String HEADER = "id;title;description;owner;startDate;status" + System.lineSeparator();

    @TempDir
    Path tempDir;

    private TaskSaveService service;

    @BeforeEach
    void setUp() {
        service = new TaskSaveService();
    }

    @Test
    void shouldsaveTaskToNewCSVFile() throws IOException {
        Path csvPath = tempDir.resolve("tasks.csv");
        Task task = new Task("1", "Clean", "Clean room", "Adam", toInstant(2026, 5, 29, 10, 30));

        service.saveTask(task, csvPath.toString(), false);

        assertTrue(Files.exists(csvPath));
        assertEquals(HEADER + rowFor(task), Files.readString(csvPath));
    }

    @Test
    void shouldsaveMultipleTasksToNewCSVFile() throws IOException {
        Path csvPath = tempDir.resolve("tasks.csv");
        Task first = new Task("1", "Clean", "Clean room", "Adam", toInstant(2026, 5, 29, 10, 30));
        Task second = new Task("2", "Shopping", "Buy milk", "Ewa", toInstant(2026, 5, 29, 11, 0));

        service.saveTasks(Arrays.asList(first, second), csvPath.toString(), false);

        assertTrue(Files.exists(csvPath));
        assertEquals(HEADER + rowFor(first) + rowFor(second), Files.readString(csvPath));
    }

    @Test
    void shouldappendTaskToExistingCSVFile() throws IOException {
        Path csvPath = tempDir.resolve("tasks.csv");
        Task existing = new Task("1", "Clean", "Clean room", "Adam", toInstant(2026, 5, 29, 10, 30));
        Files.writeString(csvPath, HEADER + rowFor(existing));
        Task task = new Task("2", "Shopping", "Buy milk", "Ewa", toInstant(2026, 5, 29, 11, 0));

        service.saveTask(task, csvPath.toString(), true);

        assertEquals(HEADER + rowFor(existing) + rowFor(task), Files.readString(csvPath));
    }

    @Test
    void shoulldAppendMultipleTasksToExistingCSVFile() throws IOException {
        Path csvPath = tempDir.resolve("tasks.csv");
        Task existing = new Task("1", "Clean", "Clean room", "Adam", toInstant(2026, 5, 29, 10, 30));
        Files.writeString(csvPath, HEADER + rowFor(existing));
        Task second = new Task("2", "Shopping", "Buy milk", "Ewa", toInstant(2026, 5, 29, 11, 0));
        Task third = new Task("3", "Laundry", "Wash clothes", "Ola", toInstant(2026, 5, 29, 12, 0));

        service.saveTasks(List.of(second, third), csvPath.toString(), true);

        assertEquals(HEADER + rowFor(existing) + rowFor(second) + rowFor(third), Files.readString(csvPath));
    }

    @Test
    void shouldsaveTaskWithEmptyFieldsToCSVFile() throws IOException {
        Path csvPath = tempDir.resolve("tasks.csv");
        Task task = new Task("1", "", "", "", toInstant(2026, 5, 29, 10, 30));

        service.saveTask(task, csvPath.toString(), false);

        assertTrue(Files.exists(csvPath));
        assertEquals(HEADER + rowFor(task), Files.readString(csvPath));
    }

    @Test
    void shouldThrowExceptionWhenSavingToInvalidFile() {
        Path invalidPath = tempDir.resolve("missing").resolve("tasks.csv");
        Task task = new Task("1", "Clean", "Clean room", "Adam", toInstant(2026, 5, 29, 10, 30));

        assertThrows(CsvException.class, () -> service.saveTask(task, invalidPath.toString(), false));
    }

    @Test
    void shouldHandleSpecialCharactersInTaskFields() throws IOException {
        Path csvPath = tempDir.resolve("tasks.csv");
        Task task = new Task("1", "Zażółć", "gęślą jaźń", "Łukasz", toInstant(2026, 5, 29, 10, 30));

        service.saveTask(task, csvPath.toString(), false);

        assertEquals(HEADER + rowFor(task), Files.readString(csvPath));
    }

    private static Instant toInstant(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute).atZone(ZONE).toInstant();
    }

    private static String rowFor(Task task) {
        String date = DATE_FORMATTER.withZone(ZONE).format(task.getStartDate());
        return String.join(";",
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getOwner(),
                date,
                task.getStatus().name()) + System.lineSeparator();
    }
}
