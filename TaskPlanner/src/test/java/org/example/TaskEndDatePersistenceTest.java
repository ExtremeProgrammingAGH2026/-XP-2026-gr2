package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The task end date must be persisted to and read back from the CSV, so the
 * task list shows the end the user actually entered (not a copy of the start).
 * Legacy six-column rows (no end date) stay readable with end defaulting to start.
 */
class TaskEndDatePersistenceTest {

    private static final ZoneId WARSAW = ZoneId.of("Europe/Warsaw");

    @TempDir
    Path tempDir;

    private String csv;

    @BeforeEach
    void setUp() {
        csv = tempDir.resolve("tasks.csv").toString();
    }

    private static Instant at(int y, int mo, int d, int h, int mi) {
        return LocalDateTime.of(y, mo, d, h, mi).atZone(WARSAW).toInstant();
    }

    @Test
    void endDatePersistsAndRoundTrips() {
        Task task = new Task("1", "Meeting", "desc", "Alice", at(2026, 6, 15, 10, 0), at(2026, 6, 15, 12, 0));
        new TaskSaveService().saveTasks(List.of(task), csv, false);

        Task read = new TaskReadService().readTasks(csv).get(0);

        assertEquals(at(2026, 6, 15, 10, 0), read.getStartDate());
        assertEquals(at(2026, 6, 15, 12, 0), read.getEndDate());
    }

    @Test
    void legacySixColumnRowsReadWithEndEqualToStart() throws IOException {
        Files.writeString(Path.of(csv),
                "id;title;description;owner;startDate;status\n1;Meeting;desc;Alice;15.06.2026 10:00;NEW",
                StandardCharsets.UTF_8);

        Task read = new TaskReadService().readTasks(csv).get(0);

        assertEquals(at(2026, 6, 15, 10, 0), read.getStartDate());
        assertEquals(read.getStartDate(), read.getEndDate());
    }
}
