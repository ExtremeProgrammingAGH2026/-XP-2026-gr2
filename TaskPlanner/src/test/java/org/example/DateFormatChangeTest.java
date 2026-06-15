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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * After the date format is changed, task input in the NEW format is accepted and
 * input in the OLD format is rejected. Covers the data-format requirement at the
 * unit level (the acceptance layer for the full login flow is tracked separately).
 */
public class DateFormatChangeTest {

    private static final ZoneId WARSAW = ZoneId.of("Europe/Warsaw");

    @TempDir
    Path tempDir;

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream output;

    @BeforeEach
    public void setUp() {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        // switch the display/input format to ISO
        AppConfiguration cfg = new AppConfiguration();
        cfg.setDateTimeFormat("yyyy-MM-dd HH:mm");
        cfg.setTimeZoneName("Europe/Warsaw");
        DateTimeFormats.init(cfg);
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
        DateTimeFormats.resetToDefaults();
    }

    @Test
    public void newFormatIsAcceptedAndOldFormatIsRejected() throws IOException {
        String tasksFile = tempDir.resolve("tasks.csv").toString();
        String usersFile = tempDir.resolve("users.csv").toString();

        Files.writeString(
                Path.of(usersFile),
                "1;alice@example.com;Alice;secret",
                StandardCharsets.UTF_8
        );

        CreateTaskUI ui = new CreateTaskUI(
                new TaskSaveService(),
                new AuthService(usersFile),
                tasksFile
        );

        User user = new User(
                "1",
                "alice@example.com",
                "Alice",
                "secret"
        );

        // start: old format first (must be rejected), then new format (accepted); end: new format
        Scanner scanner = new Scanner("Buy milk\ndesc\n15.06.2026 10:00\n2026-06-15 10:00\n2026-06-15 11:00\nn\n");
        Task task = ui.createTask(scanner, user);

        assertNotNull(task, "task should be created once a valid new-format date is given");
        assertEquals(ZonedDateTime.of(2026, 6, 15, 10, 0, 0, 0, WARSAW).toInstant(), task.getStartDate());
        assertTrue(output.toString().contains("Invalid format"),
                "a date in the old format must be rejected after the format change");
    }
}
