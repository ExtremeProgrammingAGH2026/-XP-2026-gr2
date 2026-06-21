package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskConflictWarningServiceTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream output;

    @BeforeEach
    void setUp() {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void shouldPrintWarningWhenConflictExists() {
        Task task = new Task("1", "Sprzątanie", "Desc", "Adam",
                Instant.parse("2026-06-01T10:00:00Z"),
                Instant.parse("2026-06-01T11:00:00Z"));

        Task conflicting = new Task("2", "Zakupy", "Desc", "Adam",
                Instant.parse("2026-06-01T10:30:00Z"),
                Instant.parse("2026-06-01T11:30:00Z"));

        TaskConflictWarningService service = new TaskConflictWarningService();

        service.printConflictWarning(task, List.of(conflicting));

        assertEquals(
                "WARNING: Task 'Sprzątanie' conflicts with task 'Zakupy'" + System.lineSeparator(),
                new String(output.toByteArray(), StandardCharsets.UTF_8)
        );
    }

    @Test
    void shouldPrintNothingWhenNoConflictExists() {
        Task task = new Task("1", "Sprzątanie", "Desc", "Adam",
                Instant.parse("2026-06-01T10:00:00Z"),
                Instant.parse("2026-06-01T11:00:00Z"));

        Task other = new Task("2", "Zakupy", "Desc", "Adam",
                Instant.parse("2026-06-01T11:00:00Z"),
                Instant.parse("2026-06-01T12:00:00Z"));

        TaskConflictWarningService service = new TaskConflictWarningService();

        service.printConflictWarning(task, List.of(other));

        assertEquals("", new String(output.toByteArray(), StandardCharsets.UTF_8));
    }

    @Test
    void shouldPrintWarningForEachConflictingTask() {
        Task task = new Task("1", "Sprzątanie", "Desc", "Adam",
                Instant.parse("2026-06-01T10:00:00Z"),
                Instant.parse("2026-06-01T11:00:00Z"));

        Task firstConflict = new Task("2", "Zakupy", "Desc", "Adam",
                Instant.parse("2026-06-01T10:15:00Z"),
                Instant.parse("2026-06-01T10:45:00Z"));

        Task secondConflict = new Task("3", "Gotowanie", "Desc", "Adam",
                Instant.parse("2026-06-01T10:30:00Z"),
                Instant.parse("2026-06-01T11:30:00Z"));

        TaskConflictWarningService service = new TaskConflictWarningService();

        service.printConflictWarning(task, List.of(firstConflict, secondConflict));

        assertEquals(
                "WARNING: Task 'Sprzątanie' conflicts with task 'Zakupy'" + System.lineSeparator()
                        + "WARNING: Task 'Sprzątanie' conflicts with task 'Gotowanie'" + System.lineSeparator(),
                new String(output.toByteArray(), StandardCharsets.UTF_8)
        );
    }
}