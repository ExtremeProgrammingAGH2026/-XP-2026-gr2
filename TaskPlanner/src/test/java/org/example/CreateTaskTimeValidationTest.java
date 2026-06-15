package org.example;

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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateTaskTimeValidationTest {

    private static final ZoneId WARSAW =
            ZoneId.of("Europe/Warsaw");

    @TempDir
    Path tempDir;

    private final PrintStream originalOut = System.out;

    private ByteArrayOutputStream output;
    private String tasksFile;
    private String usersFile;
    private User user;

    @BeforeEach
    public void setUp() throws IOException {
        DateTimeFormats.resetToDefaults();

        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        tasksFile = tempDir.resolve("tasks.csv").toString();
        usersFile = tempDir.resolve("users.csv").toString();

        Files.writeString(
                Path.of(usersFile),
                "1;alice@example.com;Alice;secret",
                StandardCharsets.UTF_8
        );

        user = new User(
                "1",
                "alice@example.com",
                "Alice",
                "secret"
        );
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
        DateTimeFormats.resetToDefaults();
    }

    private CreateTaskUI ui() {
        return new CreateTaskUI(
                new TaskSaveService(),
                new AuthService(usersFile),
                tasksFile
        );
    }

    @Test
    public void acceptsEndStrictlyAfterStart() {
        Task task = ui().createTask(
                new Scanner(
                        "T\n"
                                + "d\n"
                                + "15.06.2026 10:00\n"
                                + "15.06.2026 11:00\n"
                                + "n\n"
                ),
                user
        );

        assertEquals(
                ZonedDateTime.of(
                        2026,
                        6,
                        15,
                        11,
                        0,
                        0,
                        0,
                        WARSAW
                ).toInstant(),
                task.getEndDate()
        );
    }

    @Test
    public void rejectsEndBeforeStartThenAcceptsValid() {
        Task task = ui().createTask(
                new Scanner(
                        "T\n"
                                + "d\n"
                                + "15.06.2026 10:00\n"
                                + "15.06.2026 09:00\n"
                                + "15.06.2026 11:00\n"
                                + "n\n"
                ),
                user
        );

        assertTrue(
                output.toString().contains("after start date"),
                "End before start must be rejected. Output: " + output
        );

        assertEquals(
                ZonedDateTime.of(
                        2026,
                        6,
                        15,
                        11,
                        0,
                        0,
                        0,
                        WARSAW
                ).toInstant(),
                task.getEndDate()
        );
    }

    @Test
    public void rejectsEndEqualToStartThenAcceptsValid() {
        Task task = ui().createTask(
                new Scanner(
                        "T\n"
                                + "d\n"
                                + "15.06.2026 10:00\n"
                                + "15.06.2026 10:00\n"
                                + "15.06.2026 11:00\n"
                                + "n\n"
                ),
                user
        );

        assertTrue(
                output.toString().contains("after start date"),
                "End equal to start must be rejected. Output: " + output
        );

        assertEquals(
                ZonedDateTime.of(
                        2026,
                        6,
                        15,
                        11,
                        0,
                        0,
                        0,
                        WARSAW
                ).toInstant(),
                task.getEndDate()
        );
    }
}