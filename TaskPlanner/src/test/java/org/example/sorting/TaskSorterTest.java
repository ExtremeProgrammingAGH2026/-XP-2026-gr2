package org.example.sorting;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskSorterTest {

    private static HasScheduledTime item(int year, int month, int day, int hour, int minute) {
        Instant instant = LocalDateTime.of(year, month, day, hour, minute, 0)
                .toInstant(ZoneOffset.UTC);
        return () -> instant;
    }

    // --- sorting only by date ---

    @Test
    void shouldSortByDateAscending() {
        HasScheduledTime march10 = item(2024, 3, 10, 12, 0);
        HasScheduledTime march8 = item(2024, 3, 8, 12, 0);
        HasScheduledTime march15 = item(2024, 3, 15, 12, 0);

        List<HasScheduledTime> result = TaskSorter.sort(
                Arrays.asList(march10, march8, march15),
                ZoneOffset.UTC,
                SortOrder.ASC,
                SortOrder.ASC
        );

        assertEquals(march8, result.get(0));
        assertEquals(march10, result.get(1));
        assertEquals(march15, result.get(2));
    }

    @Test
    void shouldSortByDateDescending() {
        HasScheduledTime march10 = item(2024, 3, 10, 12, 0);
        HasScheduledTime march8 = item(2024, 3, 8, 12, 0);
        HasScheduledTime march15 = item(2024, 3, 15, 12, 0);

        List<HasScheduledTime> result = TaskSorter.sort(
                Arrays.asList(march10, march8, march15),
                ZoneOffset.UTC,
                SortOrder.DESC,
                SortOrder.ASC
        );

        assertEquals(march15, result.get(0));
        assertEquals(march10, result.get(1));
        assertEquals(march8, result.get(2));
    }

    // --- sorting only by time of day (all items on the same day) ---

    @Test
    void shouldSortByTimeOfDayAscending() {
        HasScheduledTime at14 = item(2024, 3, 10, 14, 0);
        HasScheduledTime at8 = item(2024, 3, 10, 8, 0);
        HasScheduledTime at20 = item(2024, 3, 10, 20, 0);

        List<HasScheduledTime> result = TaskSorter.sort(
                Arrays.asList(at14, at8, at20),
                ZoneOffset.UTC,
                SortOrder.ASC,
                SortOrder.ASC
        );

        assertEquals(at8, result.get(0));
        assertEquals(at14, result.get(1));
        assertEquals(at20, result.get(2));
    }

    @Test
    void shouldSortByTimeOfDayDescending() {
        HasScheduledTime at14 = item(2024, 3, 10, 14, 0);
        HasScheduledTime at8 = item(2024, 3, 10, 8, 0);
        HasScheduledTime at20 = item(2024, 3, 10, 20, 0);

        List<HasScheduledTime> result = TaskSorter.sort(
                Arrays.asList(at14, at8, at20),
                ZoneOffset.UTC,
                SortOrder.ASC,
                SortOrder.DESC
        );

        assertEquals(at20, result.get(0));
        assertEquals(at14, result.get(1));
        assertEquals(at8, result.get(2));
    }

    // --- sorting by both date and time of day ---

    @Test
    void shouldSortByDateAscendingAndTimeOfDayDescending() {
        HasScheduledTime march10at9 = item(2024, 3, 10, 9, 0);
        HasScheduledTime march10at15 = item(2024, 3, 10, 15, 0);
        HasScheduledTime march8at12 = item(2024, 3, 8, 12, 0);
        HasScheduledTime march12at8 = item(2024, 3, 12, 8, 0);

        List<HasScheduledTime> result = TaskSorter.sort(
                Arrays.asList(march10at9, march10at15, march8at12, march12at8),
                ZoneOffset.UTC,
                SortOrder.ASC,
                SortOrder.DESC
        );

        assertEquals(march8at12, result.get(0));
        assertEquals(march10at15, result.get(1)); // March 10 — later time first (DESC)
        assertEquals(march10at9, result.get(2));  // March 10 — earlier time second (DESC)
        assertEquals(march12at8, result.get(3));
    }

    @Test
    void shouldSortByDateDescendingAndTimeOfDayAscending() {
        HasScheduledTime march10at9 = item(2024, 3, 10, 9, 0);
        HasScheduledTime march10at15 = item(2024, 3, 10, 15, 0);
        HasScheduledTime march8at12 = item(2024, 3, 8, 12, 0);
        HasScheduledTime march12at8 = item(2024, 3, 12, 8, 0);

        List<HasScheduledTime> result = TaskSorter.sort(
                Arrays.asList(march10at9, march10at15, march8at12, march12at8),
                ZoneOffset.UTC,
                SortOrder.DESC,
                SortOrder.ASC
        );

        assertEquals(march12at8, result.get(0));
        assertEquals(march10at9, result.get(1));  // March 10 — earlier time first (ASC)
        assertEquals(march10at15, result.get(2)); // March 10 — later time second (ASC)
        assertEquals(march8at12, result.get(3));
    }

    @Test
    void shouldSortByDateDescendingAndTimeOfDayDescending() {
        HasScheduledTime march10at9 = item(2024, 3, 10, 9, 0);
        HasScheduledTime march10at15 = item(2024, 3, 10, 15, 0);
        HasScheduledTime march8at12 = item(2024, 3, 8, 12, 0);
        HasScheduledTime march12at8 = item(2024, 3, 12, 8, 0);

        List<HasScheduledTime> result = TaskSorter.sort(
                Arrays.asList(march10at9, march10at15, march8at12, march12at8),
                ZoneOffset.UTC,
                SortOrder.DESC,
                SortOrder.DESC
        );

        assertEquals(march12at8, result.get(0));
        assertEquals(march10at15, result.get(1)); // March 10 — later time first (DESC)
        assertEquals(march10at9, result.get(2));  // March 10 — earlier time second (DESC)
        assertEquals(march8at12, result.get(3));
    }

    // --- edge cases ---

    @Test
    void shouldReturnEmptyListWhenInputIsEmpty() {
        List<HasScheduledTime> result = TaskSorter.sort(
                new ArrayList<>(),
                ZoneOffset.UTC,
                SortOrder.ASC,
                SortOrder.ASC
        );

        assertEquals(0, result.size());
    }

    @Test
    void shouldReturnSingleElementList() {
        HasScheduledTime single = item(2024, 3, 10, 12, 0);

        List<HasScheduledTime> result = TaskSorter.sort(
                new ArrayList<>(List.of(single)),
                ZoneOffset.UTC,
                SortOrder.ASC,
                SortOrder.ASC
        );

        assertEquals(1, result.size());
        assertEquals(single, result.get(0));
    }

    @Test
    void shouldNotModifyOriginalList() {
        HasScheduledTime first = item(2024, 3, 10, 12, 0);
        HasScheduledTime second = item(2024, 3, 8, 12, 0);
        List<HasScheduledTime> original = new ArrayList<>(Arrays.asList(first, second));

        TaskSorter.sort(original, ZoneOffset.UTC, SortOrder.ASC, SortOrder.ASC);

        assertEquals(first, original.get(0));
        assertEquals(second, original.get(1));
    }

    @Test
    void shouldPreserveOrderWhenAllTimestampsAreEqual() {
        HasScheduledTime a = item(2024, 3, 10, 12, 0);
        HasScheduledTime b = item(2024, 3, 10, 12, 0);
        HasScheduledTime c = item(2024, 3, 10, 12, 0);

        List<HasScheduledTime> result = TaskSorter.sort(
                Arrays.asList(a, b, c),
                ZoneOffset.UTC,
                SortOrder.ASC,
                SortOrder.ASC
        );

        assertEquals(3, result.size());
        assertEquals(a, result.get(0));
        assertEquals(b, result.get(1));
        assertEquals(c, result.get(2));
    }
}
