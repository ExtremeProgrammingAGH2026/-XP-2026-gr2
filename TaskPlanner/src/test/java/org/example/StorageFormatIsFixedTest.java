package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The CSV storage date format must stay fixed and independent of the display
 * config, so changing the display format can never corrupt or orphan saved data.
 */
class StorageFormatIsFixedTest {

    private static final ZoneId WARSAW = ZoneId.of("Europe/Warsaw");

    @TempDir
    Path tempDir;

    @AfterEach
    void resetFormats() {
        DateTimeFormats.resetToDefaults();
    }

    private static Instant at(int y, int mo, int d, int h, int mi) {
        return LocalDateTime.of(y, mo, d, h, mi).atZone(WARSAW).toInstant();
    }

    @Test
    void csvKeepsFixedDateFormatRegardlessOfDisplayConfig() throws Exception {
        AppConfiguration cfg = new AppConfiguration();
        cfg.setDateTimeFormat("yyyy-MM-dd HH:mm");
        cfg.setTimeZoneName("Europe/Warsaw");
        DateTimeFormats.init(cfg); // display switched to ISO

        String csv = tempDir.resolve("tasks.csv").toString();
        Task task = new Task("1", "Clean", "room", "Alice", at(2026, 6, 15, 14, 30));
        new TaskSaveService().saveTasks(List.of(task), csv, false);

        String content = Files.readString(Path.of(csv), StandardCharsets.UTF_8);
        assertTrue(content.contains("15.06.2026 14:30"),
                "storage must stay dd.MM.yyyy HH:mm, was: " + content);
        assertFalse(content.contains("2026-06-15 14:30"),
                "storage must not follow the display config");

        List<Task> read = new TaskReadService().readTasks(csv);
        assertEquals(1, read.size());
        assertEquals(at(2026, 6, 15, 14, 30), read.get(0).getStartDate());
    }
}
