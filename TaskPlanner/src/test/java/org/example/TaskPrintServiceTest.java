package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskPrintServiceTest {

    private static final ZoneId ZONE = ZoneId.of("Europe/Warsaw");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final String TOP_BORDER = "-------------------------------------------";
    private static final String BOTTOM_BORDER = "-----------------------------------";

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream output;

    private TaskPrintService service;

    @BeforeEach
    void setUp() {
        service = new TaskPrintService();
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void shouldPrintWhenOneTask() {
        Task task = new Task("1", "Clean", "Clean room", "Adam", toInstant(2026, 5, 29, 10, 30));

        service.printTasks(List.of(task));

        assertEquals(expectedFor(List.of(task)), output.toString());
    }

    @Test
    void shouldPrintWhenMultipleTasks() {
        Task first = new Task("1", "Clean", "Clean room", "Adam", toInstant(2026, 5, 29, 10, 30));
        Task second = new Task("2", "Shopping", "Buy milk", "Ewa", toInstant(2026, 5, 29, 11, 0));

        service.printTasks(List.of(first, second));

        assertEquals(expectedFor(List.of(first, second)), output.toString());
    }

    @Test
    void shouldPrintNoTasksAvailableWhenTasksEmpty() {
        service.printTasks(List.of());

        assertEquals("No tasks available" + System.lineSeparator(), output.toString());
    }

    private static String expectedFor(List<Task> tasks) {
        StringBuilder builder = new StringBuilder();
        for (Task task : tasks) {
            builder.append(TOP_BORDER).append(System.lineSeparator());
            builder.append("TASK: ").append(task.getTitle())
                    .append(" status: ").append(task.getStatus().name())
                    .append(System.lineSeparator());
            builder.append("Owned by: ").append(task.getOwner()).append(System.lineSeparator());
            builder.append("Start date: ").append(formatDate(task.getStartDate())).append(System.lineSeparator());
            builder.append("Description: ").append(task.getDescription()).append(System.lineSeparator());
            builder.append(BOTTOM_BORDER).append(System.lineSeparator());
        }
        return builder.toString();
    }

    private static String formatDate(Instant instant) {
        return DATE_FORMATTER.withZone(ZONE).format(instant);
    }

    private static Instant toInstant(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute).atZone(ZONE).toInstant();
    }
}
