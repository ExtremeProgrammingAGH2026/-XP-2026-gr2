package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskDateFilterServiceTest {

    private TaskDateFilterService filterService;

    @BeforeEach
    void setUp() {
        filterService = new TaskDateFilterService(ZoneOffset.UTC);
    }

    // --- filterByDay ---

    @Test
    void shouldReturnTasksStartingOnGivenDay() {
        Task taskOnTarget = task("1", instant(2024, 3, 10, 9, 0));
        Task taskOnOtherDay = task("2", instant(2024, 3, 11, 9, 0));
        Task taskOnAnotherDay = task("3", instant(2024, 3, 9, 23, 0));

        List<Task> result = filterService.filterByDay(
                Arrays.asList(taskOnTarget, taskOnOtherDay, taskOnAnotherDay),
                LocalDate.of(2024, 3, 10)
        );

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
    }

    @Test
    void shouldReturnAllTasksWhenAllStartOnGivenDay() {
        Task taskMorning = task("1", instant(2024, 5, 20, 7, 0));
        Task taskNoon = task("2", instant(2024, 5, 20, 12, 0));
        Task taskEvening = task("3", instant(2024, 5, 20, 20, 30));

        List<Task> result = filterService.filterByDay(
                Arrays.asList(taskMorning, taskNoon, taskEvening),
                LocalDate.of(2024, 5, 20)
        );

        assertEquals(3, result.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoTasksMatchGivenDay() {
        Task taskA = task("1", instant(2024, 3, 8, 10, 0));
        Task taskB = task("2", instant(2024, 3, 12, 10, 0));

        List<Task> result = filterService.filterByDay(
                Arrays.asList(taskA, taskB),
                LocalDate.of(2024, 3, 10)
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenInputIsEmpty() {
        List<Task> result = filterService.filterByDay(
                Collections.emptyList(),
                LocalDate.of(2024, 3, 10)
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldHandleMidnightBoundaryCorrectly() {
        Task atMidnight = task("1", instant(2024, 3, 10, 0, 0));
        Task justBeforeMidnight = task("2", instant(2024, 3, 9, 23, 59));

        List<Task> result = filterService.filterByDay(
                Arrays.asList(atMidnight, justBeforeMidnight),
                LocalDate.of(2024, 3, 10)
        );

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
    }

    // --- filterByToday ---

    @Test
    void shouldReturnOnlyTasksScheduledForToday() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        Instant todayInstant = today.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant yesterdayInstant = today.minusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        Task todayTask = task("1", todayInstant);
        Task yesterdayTask = task("2", yesterdayInstant);

        List<Task> result = filterService.filterByToday(Arrays.asList(todayTask, yesterdayTask));

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
    }

    // --- filterByWeek ---

    @Test
    void shouldReturnTasksWithinWeekRange() {
        Task monday = task("1", instant(2024, 3, 4, 10, 0));
        Task wednesday = task("2", instant(2024, 3, 6, 10, 0));
        Task sunday = task("3", instant(2024, 3, 10, 10, 0));
        Task nextMonday = task("4", instant(2024, 3, 11, 10, 0));

        List<Task> result = filterService.filterByWeek(
                Arrays.asList(monday, wednesday, sunday, nextMonday),
                LocalDate.of(2024, 3, 4),
                LocalDate.of(2024, 3, 10)
        );

        assertEquals(3, result.size());
    }

    @Test
    void shouldIncludeTasksOnWeekBoundaryDays() {
        Task onWeekStart = task("1", instant(2024, 3, 4, 0, 0));
        Task onWeekEnd = task("2", instant(2024, 3, 10, 23, 59));

        List<Task> result = filterService.filterByWeek(
                Arrays.asList(onWeekStart, onWeekEnd),
                LocalDate.of(2024, 3, 4),
                LocalDate.of(2024, 3, 10)
        );

        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoTasksInWeek() {
        Task beforeWeek = task("1", instant(2024, 3, 3, 10, 0));
        Task afterWeek = task("2", instant(2024, 3, 11, 10, 0));

        List<Task> result = filterService.filterByWeek(
                Arrays.asList(beforeWeek, afterWeek),
                LocalDate.of(2024, 3, 4),
                LocalDate.of(2024, 3, 10)
        );

        assertTrue(result.isEmpty());
    }

    // --- guard clauses ---

    @Test
    void shouldThrowWhenTaskListIsNull() {
        assertThrows(NullPointerException.class,
                () -> filterService.filterByDay(null, LocalDate.now()));
    }

    @Test
    void shouldThrowWhenDateIsNull() {
        assertThrows(NullPointerException.class,
                () -> filterService.filterByDay(Collections.emptyList(), null));
    }

    @Test
    void shouldThrowWhenWeekStartIsAfterWeekEnd() {
        assertThrows(IllegalArgumentException.class,
                () -> filterService.filterByWeek(
                        Collections.emptyList(),
                        LocalDate.of(2024, 3, 10),
                        LocalDate.of(2024, 3, 4)
                ));
    }

    // --- helpers ---

    private Task task(String id, Instant startDate) {
        return new Task(id, "title-" + id, "desc", "owner", startDate);
    }

    private Instant instant(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute, 0).toInstant(ZoneOffset.UTC);
    }
}