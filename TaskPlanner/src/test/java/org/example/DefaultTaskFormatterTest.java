package org.example;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultTaskFormatterTest {

    private static final ZoneId ZONE = ZoneId.of("Europe/Warsaw");

    private final TaskFormatter formatter = new DefaultTaskFormatter();

    @Test
    void shouldFormatTaskWithBordersAndAllFields() {
        Task task = new Task("1", "Clean", "Clean room", "Adam", toInstant(2026, 5, 29, 10, 30));

        String result = formatter.format(task);

        String expected = "-------------------------------------------" + System.lineSeparator()
                + "TASK: Clean status: NEW" + System.lineSeparator()
                + "Owned by: Adam" + System.lineSeparator()
                + "Start date: 29.05.2026 10:30" + System.lineSeparator()
                + "Description: Clean room" + System.lineSeparator()
                + "-------------------------------------------" + System.lineSeparator();

        assertEquals(expected, result);
    }

    private static Instant toInstant(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute).atZone(ZONE).toInstant();
    }
}
