package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Edge cases for the start/end time validation when creating a task.
 * A task must span a positive amount of time: the end must be strictly after
 * the start (end before start and end equal to start are both rejected).
 */
public class CreateTaskTimeValidationTest {

    private static final ZoneId WARSAW = ZoneId.of("Europe/Warsaw");

    @TempDir
    Path tempDir;

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream output;
    private String tasksFile;
    private User user;

    @BeforeEach
    public void setUp() {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        tasksFile = tempDir.resolve("tasks.csv").toString();
        user = new User("1", "alice@example.com", "Alice", "secret");
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    private CreateTaskUI ui() {
        return new CreateTaskUI(new TaskSaveService(), tasksFile);
    }

    @Test
    public void acceptsEndStrictlyAfterStart() {
        Task task = ui().createTask(
                new Scanner("T\nd\n15.06.2026 10:00\n15.06.2026 11:00\n"), user);

        assertEquals(ZonedDateTime.of(2026, 6, 15, 11, 0, 0, 0, WARSAW).toInstant(), task.getEndDate());
    }

    @Test
    public void rejectsEndBeforeStartThenAcceptsValid() {
        Task task = ui().createTask(
                new Scanner("T\nd\n15.06.2026 10:00\n15.06.2026 09:00\n15.06.2026 11:00\n"), user);

        assertTrue(output.toString().contains("after start date"),
                "end before start must be rejected, output: " + output);
        assertEquals(ZonedDateTime.of(2026, 6, 15, 11, 0, 0, 0, WARSAW).toInstant(), task.getEndDate());
    }

    @Test
    public void rejectsEndEqualToStartThenAcceptsValid() {
        Task task = ui().createTask(
                new Scanner("T\nd\n15.06.2026 10:00\n15.06.2026 10:00\n15.06.2026 11:00\n"), user);

        assertTrue(output.toString().contains("after start date"),
                "end equal to start (zero duration) must be rejected, output: " + output);
        assertEquals(ZonedDateTime.of(2026, 6, 15, 11, 0, 0, 0, WARSAW).toInstant(), task.getEndDate());
    }
}
