package org.example.recurring;

import org.example.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecurringTaskExpanderTest {

    private RecurringTaskExpander expander;

    @BeforeEach
    void setUp() {
        expander = new RecurringTaskExpander(ZoneOffset.UTC);
    }

    // --- daily recurrence ---

    @Test
    void shouldGenerateDailyOccurrencesWithinWindow() {
        Instant start = instant(2024, 3, 1, 8, 0);
        RecurringTask template = new RecurringTask(
                "r1", "Morning walk", "", "Alice", start, RecurrencePattern.DAILY
        );

        Instant windowStart = instant(2024, 3, 1, 0, 0);
        Instant windowEnd = instant(2024, 3, 5, 23, 59);

        List<Task> occurrences = expander.expand(template, windowStart, windowEnd);

        assertEquals(5, occurrences.size());
        assertEquals(instant(2024, 3, 1, 8, 0), occurrences.get(0).getStartDate());
        assertEquals(instant(2024, 3, 5, 8, 0), occurrences.get(4).getStartDate());
    }

    @Test
    void shouldGenerateWeeklyOccurrencesWithinWindow() {
        Instant start = instant(2024, 3, 4, 10, 0); // Monday
        RecurringTask template = new RecurringTask(
                "r2", "Cleaning", "Weekly cleaning", "Bob", start, RecurrencePattern.WEEKLY
        );

        Instant windowStart = instant(2024, 3, 1, 0, 0);
        Instant windowEnd = instant(2024, 3, 31, 23, 59);

        List<Task> occurrences = expander.expand(template, windowStart, windowEnd);

        assertEquals(4, occurrences.size());
        assertEquals(instant(2024, 3, 4, 10, 0), occurrences.get(0).getStartDate());
        assertEquals(instant(2024, 3, 11, 10, 0), occurrences.get(1).getStartDate());
        assertEquals(instant(2024, 3, 18, 10, 0), occurrences.get(2).getStartDate());
        assertEquals(instant(2024, 3, 25, 10, 0), occurrences.get(3).getStartDate());
    }

    @Test
    void shouldGenerateBiweeklyOccurrencesWithinWindow() {
        Instant start = instant(2024, 3, 1, 9, 0);
        RecurringTask template = new RecurringTask(
                "r3", "Report", "", "Alice", start, RecurrencePattern.BIWEEKLY
        );

        Instant windowStart = instant(2024, 3, 1, 0, 0);
        Instant windowEnd = instant(2024, 4, 30, 23, 59);

        List<Task> occurrences = expander.expand(template, windowStart, windowEnd);

        assertEquals(5, occurrences.size());
        assertEquals(instant(2024, 3, 1, 9, 0), occurrences.get(0).getStartDate());
        assertEquals(instant(2024, 3, 15, 9, 0), occurrences.get(1).getStartDate());
        assertEquals(instant(2024, 3, 29, 9, 0), occurrences.get(2).getStartDate());
        assertEquals(instant(2024, 4, 12, 9, 0), occurrences.get(3).getStartDate());
    }

    @Test
    void shouldGenerateMonthlyOccurrencesWithinWindow() {
        Instant start = instant(2024, 1, 15, 12, 0);
        RecurringTask template = new RecurringTask(
                "r4", "Bill payment", "", "Alice", start, RecurrencePattern.MONTHLY
        );

        Instant windowStart = instant(2024, 1, 1, 0, 0);
        Instant windowEnd = instant(2024, 4, 30, 23, 59);

        List<Task> occurrences = expander.expand(template, windowStart, windowEnd);

        assertEquals(4, occurrences.size());
        assertEquals(instant(2024, 1, 15, 12, 0), occurrences.get(0).getStartDate());
        assertEquals(instant(2024, 4, 15, 12, 0), occurrences.get(3).getStartDate());
    }

    // --- window boundary behaviour ---

    @Test
    void shouldExcludeOccurrencesBeforeWindowStart() {
        Instant start = instant(2024, 3, 1, 8, 0);
        RecurringTask template = new RecurringTask(
                "r5", "Daily", "", "Alice", start, RecurrencePattern.DAILY
        );

        Instant windowStart = instant(2024, 3, 3, 0, 0);
        Instant windowEnd = instant(2024, 3, 5, 23, 59);

        List<Task> occurrences = expander.expand(template, windowStart, windowEnd);

        assertEquals(3, occurrences.size());
        assertEquals(instant(2024, 3, 3, 8, 0), occurrences.get(0).getStartDate());
    }

    @Test
    void shouldReturnEmptyListWhenWindowIsBeforeFirstOccurrence() {
        Instant start = instant(2024, 6, 1, 8, 0);
        RecurringTask template = new RecurringTask(
                "r6", "Future task", "", "Alice", start, RecurrencePattern.WEEKLY
        );

        Instant windowStart = instant(2024, 1, 1, 0, 0);
        Instant windowEnd = instant(2024, 2, 28, 23, 59);

        List<Task> occurrences = expander.expand(template, windowStart, windowEnd);

        assertTrue(occurrences.isEmpty());
    }

    // --- recurrenceEndDate ---

    @Test
    void shouldStopGeneratingOccurrencesAfterRecurrenceEndDate() {
        Instant start = instant(2024, 3, 1, 8, 0);
        Instant recurrenceEnd = instant(2024, 3, 15, 23, 59);
        RecurringTask template = new RecurringTask(
                "r7", "Limited task", "", "Alice", start, null,
                RecurrencePattern.WEEKLY, recurrenceEnd
        );

        Instant windowStart = instant(2024, 3, 1, 0, 0);
        Instant windowEnd = instant(2024, 4, 30, 23, 59);

        List<Task> occurrences = expander.expand(template, windowStart, windowEnd);

        assertEquals(3, occurrences.size());
        assertEquals(instant(2024, 3, 1, 8, 0), occurrences.get(0).getStartDate());
        assertEquals(instant(2024, 3, 8, 8, 0), occurrences.get(1).getStartDate());
    }

    // --- occurrence metadata ---

    @Test
    void shouldCopyTitleAndDescriptionAndOwnerToEachOccurrence() {
        Instant start = instant(2024, 3, 1, 8, 0);
        RecurringTask template = new RecurringTask(
                "r8", "Gym", "Go to gym", "Charlie", start, RecurrencePattern.DAILY
        );

        List<Task> occurrences = expander.expand(
                template,
                instant(2024, 3, 1, 0, 0),
                instant(2024, 3, 2, 23, 59)
        );

        assertEquals(2, occurrences.size());
        for (Task occurrence : occurrences) {
            assertEquals("Gym", occurrence.getTitle());
            assertEquals("Go to gym", occurrence.getDescription());
            assertEquals("Charlie", occurrence.getOwner());
        }
    }

    @Test
    void shouldAssignUniqueIdsToEachOccurrence() {
        Instant start = instant(2024, 3, 1, 8, 0);
        RecurringTask template = new RecurringTask(
                "r9", "Stand-up", "", "Alice", start, RecurrencePattern.DAILY
        );

        List<Task> occurrences = expander.expand(
                template,
                instant(2024, 3, 1, 0, 0),
                instant(2024, 3, 3, 23, 59)
        );

        long distinctIds = occurrences.stream().map(Task::getId).distinct().count();
        assertEquals(occurrences.size(), distinctIds);
    }

    // --- guard clauses ---

    @Test
    void shouldThrowWhenRecurringTaskIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> expander.expand(null, Instant.now(), Instant.now()));
    }

    @Test
    void shouldThrowWhenWindowStartIsNull() {
        RecurringTask template = new RecurringTask(
                "r10", "Task", "", "Alice", Instant.now(), RecurrencePattern.DAILY
        );
        assertThrows(IllegalArgumentException.class,
                () -> expander.expand(template, null, Instant.now()));
    }

    @Test
    void shouldThrowWhenWindowEndIsNull() {
        RecurringTask template = new RecurringTask(
                "r11", "Task", "", "Alice", Instant.now(), RecurrencePattern.DAILY
        );
        assertThrows(IllegalArgumentException.class,
                () -> expander.expand(template, Instant.now(), null));
    }

    @Test
    void shouldThrowWhenWindowStartIsAfterWindowEnd() {
        RecurringTask template = new RecurringTask(
                "r12", "Task", "", "Alice", Instant.now(), RecurrencePattern.DAILY
        );
        Instant windowStart = instant(2024, 3, 10, 0, 0);
        Instant windowEnd = instant(2024, 3, 1, 0, 0);
        assertThrows(IllegalArgumentException.class,
                () -> expander.expand(template, windowStart, windowEnd));
    }

    // --- helper ---

    private static Instant instant(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute, 0).toInstant(ZoneOffset.UTC);
    }
}