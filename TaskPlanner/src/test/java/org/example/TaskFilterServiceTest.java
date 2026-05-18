package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskFilterServiceTest {

    private TaskFilterService filterService;
    private List<Task> sampleTasks;

    @BeforeEach
    void setUp() {
        filterService = new TaskFilterService();
        sampleTasks = Arrays.asList(
                new Task("1", "Spotkanie zespołu", "Daily standup", "Anna", LocalDateTime.of(2026, 5, 5, 9, 0)),
                new Task("2", "Code review", "Przegląd PR #42", "Tomek", LocalDateTime.of(2026, 5, 15, 14, 0)),
                new Task("3", "Planowanie sprintu", "Sprint 7", "Anna", LocalDateTime.of(2026, 5, 31, 10, 0)),
                new Task("4", "Retro", "Retrospektywa sprintu 6", "Tomek", LocalDateTime.of(2026, 6, 1, 11, 0)),
                new Task("5", "Demo", "Prezentacja klientowi", "Kasia", LocalDateTime.of(2026, 4, 30, 16, 0))
        );
    }

    @Test
    void filterByDateRange_returnsTasksWithinRange() {
        LocalDateTime from = LocalDateTime.of(2026, 5, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 5, 31, 23, 59);

        List<Task> result = filterService.filterByDateRange(sampleTasks, from, to);

        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(t -> t.getId().equals("1")));
        assertTrue(result.stream().anyMatch(t -> t.getId().equals("2")));
        assertTrue(result.stream().anyMatch(t -> t.getId().equals("3")));
    }

    @Test
    void filterByDateRange_excludesTasksOutsideRange() {
        LocalDateTime from = LocalDateTime.of(2026, 5, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 5, 31, 23, 59);

        List<Task> result = filterService.filterByDateRange(sampleTasks, from, to);

        assertFalse(result.stream().anyMatch(t -> t.getId().equals("4")));
        assertFalse(result.stream().anyMatch(t -> t.getId().equals("5")));
    }

    @Test
    void filterByDateRange_returnsEmptyListWhenNoTasksMatch() {
        LocalDateTime from = LocalDateTime.of(2026, 7, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 7, 31, 23, 59);

        List<Task> result = filterService.filterByDateRange(sampleTasks, from, to);

        assertTrue(result.isEmpty());
    }

    @Test
    void filterByDateRange_handlesEmptyTaskList() {
        LocalDateTime from = LocalDateTime.of(2026, 5, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 5, 31, 23, 59);

        List<Task> result = filterService.filterByDateRange(Collections.emptyList(), from, to);

        assertTrue(result.isEmpty());
    }

    @Test
    void filterByDateRange_includesTasksExactlyOnBoundary() {
        LocalDateTime from = LocalDateTime.of(2026, 5, 5, 9, 0);
        LocalDateTime to = LocalDateTime.of(2026, 5, 15, 14, 0);

        List<Task> result = filterService.filterByDateRange(sampleTasks, from, to);

        assertTrue(result.stream().anyMatch(t -> t.getId().equals("1")));
        assertTrue(result.stream().anyMatch(t -> t.getId().equals("2")));
        assertEquals(2, result.size());
    }

    @Test
    void filterByMonth_returnsMayTasks() {
        List<Task> result = filterService.filterByMonth(sampleTasks, 2026, 5);

        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(t ->
                t.getStartDate().getMonthValue() == 5 && t.getStartDate().getYear() == 2026));
    }

    @Test
    void filterByMonth_returnsEmptyForMonthWithNoTasks() {
        List<Task> result = filterService.filterByMonth(sampleTasks, 2026, 3);

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
