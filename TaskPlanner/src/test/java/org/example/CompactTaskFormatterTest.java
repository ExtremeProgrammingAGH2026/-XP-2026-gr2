package org.example;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompactTaskFormatterTest {

    private static final ZoneId ZONE = ZoneId.of("Europe/Warsaw");

    private final TaskFormatter formatter = new CompactTaskFormatter();

    @Test
    void shouldFormatTaskAsCompactSingleLine() {
        Task task = new Task("1", "Clean", "Clean room", "Adam", toInstant(2026, 5, 29, 10, 30));

        String result = formatter.format(task);

        assertEquals("[NEW] Clean (Adam) 29.05.2026 10:30 - 29.05.2026 10:30" + System.lineSeparator(), result);
    }

    @Test
    void shouldFormatTaskWithDoneStatus() {
        Task task = new Task("2", "Shopping", "Buy milk", "Ewa", toInstant(2026, 5, 29, 11, 0));
        task.setStatus(TaskStatus.DONE);

        String result = formatter.format(task);

        assertEquals("[DONE] Shopping (Ewa) 29.05.2026 11:00 - 29.05.2026 11:00" + System.lineSeparator(), result);
    }

    private static Instant toInstant(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute).atZone(ZONE).toInstant();
    }
}
