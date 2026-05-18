package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskFilterServiceTest {

    private static final ZoneId ZONE = ZoneOffset.UTC;

    private TaskFilterService filterService;
    private List<Task> sampleTasks;

    @BeforeEach
    void setUp() {
        filterService = new TaskFilterService();
        sampleTasks = Arrays.asList(
                new Task("1", "Spotkanie zespołu", "Daily standup", "Anna", toInstant(2026, 5, 5, 9, 0)),
                new Task("2", "Code review", "Przegląd PR #42", "Tomek", toInstant(2026, 5, 15, 14, 0)),
                new Task("3", "Planowanie sprintu", "Sprint 7", "Anna", toInstant(2026, 5, 31, 10, 0)),
                new Task("4", "Retro", "Retrospektywa sprintu 6", "Tomek", toInstant(2026, 6, 1, 11, 0)),
                new Task("5", "Demo", "Prezentacja klientowi", "Kasia", toInstant(2026, 4, 30, 16, 0))
        );
    }

    private static Instant toInstant(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute).toInstant(ZoneOffset.UTC);
    }

    @Test
    void filterByDateRange_returnsTasksWithinRange() {
        Instant from = toInstant(2026, 5, 1, 0, 0);
        Instant to = toInstant(2026, 5, 31, 23, 59);

        List<Task> result = filterService.filterByDateRange(sampleTasks, from, to);

        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(t -> t.getId().equals("1")));
        assertTrue(result.stream().anyMatch(t -> t.getId().equals("2")));
        assertTrue(result.stream().anyMatch(t -> t.getId().equals("3")));
    }

    @Test
    void filterByDateRange_excludesTasksOutsideRange() {
        Instant from = toInstant(2026, 5, 1, 0, 0);
        Instant to = toInstant(2026, 5, 31, 23, 59);

        List<Task> result = filterService.filterByDateRange(sampleTasks, from, to);

        assertFalse(result.stream().anyMatch(t -> t.getId().equals("4")));
        assertFalse(result.stream().anyMatch(t -> t.getId().equals("5")));
    }

    @Test
    void filterByDateRange_returnsEmptyListWhenNoTasksMatch() {
        Instant from = toInstant(2026, 7, 1, 0, 0);
        Instant to = toInstant(2026, 7, 31, 23, 59);

        List<Task> result = filterService.filterByDateRange(sampleTasks, from, to);

        assertTrue(result.isEmpty());
    }

    @Test
    void filterByDateRange_handlesEmptyTaskList() {
        Instant from = toInstant(2026, 5, 1, 0, 0);
        Instant to = toInstant(2026, 5, 31, 23, 59);

        List<Task> result = filterService.filterByDateRange(Collections.emptyList(), from, to);

        assertTrue(result.isEmpty());
    }

    @Test
    void filterByDateRange_includesTasksExactlyOnBoundary() {
        Instant from = toInstant(2026, 5, 5, 9, 0);
        Instant to = toInstant(2026, 5, 15, 14, 0);

        List<Task> result = filterService.filterByDateRange(sampleTasks, from, to);

        assertTrue(result.stream().anyMatch(t -> t.getId().equals("1")));
        assertTrue(result.stream().anyMatch(t -> t.getId().equals("2")));
        assertEquals(2, result.size());
    }

    @Test
    void filterByMonth_returnsMayTasks() {
        List<Task> result = filterService.filterByMonth(sampleTasks, 2026, 5, ZONE);

        assertEquals(3, result.size());
    }

    @Test
    void filterByMonth_returnsEmptyForMonthWithNoTasks() {
        List<Task> result = filterService.filterByMonth(sampleTasks, 2026, 3, ZONE);

        assertTrue(result.isEmpty());
    }

    @Test
    void filterByOwner_returnsTasksForGivenOwner() {
        List<Task> result = filterService.filterByOwner(sampleTasks, "Anna");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getOwner().equals("Anna")));
    }

    @Test
    void filterByOwner_returnsEmptyWhenOwnerHasNoTasks() {
        List<Task> result = filterService.filterByOwner(sampleTasks, "Nieznany");

        assertTrue(result.isEmpty());
    }

    @Test
    void filterByOwner_isCaseSensitive() {
        List<Task> result = filterService.filterByOwner(sampleTasks, "anna");

        assertTrue(result.isEmpty());
    }
}
