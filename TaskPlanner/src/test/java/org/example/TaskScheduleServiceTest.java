package org.example;

import org.example.recurring.RecurringTask;
import org.example.recurring.RecurrencePattern;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskScheduleServiceTest {

    private static final ZoneId WARSAW = ZoneId.of("Europe/Warsaw");

    @Test
    void expandAllShouldExpandDailyRecurringTaskAcrossItsRecurrenceWindow() {
        TaskScheduleService service = new TaskScheduleService(WARSAW);
        RecurringTask daily = new RecurringTask(
                "r1",
                "Create User List",
                "Create User List",
                "User",
                instant(2026, 6, 23, 13, 2),
                instant(2026, 6, 23, 14, 2),
                RecurrencePattern.DAILY,
                instant(2026, 6, 27, 19, 0)
        );

        List<Task> expanded = service.expandAll(List.of(daily));

        assertEquals(5, expanded.size());
        assertEquals(instant(2026, 6, 23, 13, 2), expanded.get(0).getStartDate());
        assertEquals(instant(2026, 6, 27, 13, 2), expanded.get(4).getStartDate());
    }

    @Test
    void expandAllShouldKeepNonRecurringTaskAsIs() {
        TaskScheduleService service = new TaskScheduleService(WARSAW);
        Task normal = new Task("t1", "Meeting", "Team sync", "Alice",
                instant(2026, 6, 15, 11, 0), instant(2026, 6, 15, 12, 0));

        List<Task> expanded = service.expandAll(List.of(normal));

        assertEquals(1, expanded.size());
        assertEquals("t1", expanded.get(0).getId());
    }

    @Test
    void shouldReturnRecurringOccurrencesForGivenDay() {
        TaskScheduleService service = new TaskScheduleService(WARSAW);
        RecurringTask recurringTask = new RecurringTask(
                "r1",
                "Weekly cleaning",
                "Clean the flat",
                "Alice",
                instant(2026, 6, 15, 8, 0),
                instant(2026, 6, 15, 9, 0),
                RecurrencePattern.WEEKLY,
                instant(2026, 7, 31, 9, 0)
        );
        Task normalTask = new Task("t1", "Meeting", "Team sync", "Alice", instant(2026, 6, 15, 11, 0));

        List<Task> tasks = service.getTasksForDay(List.of(recurringTask, normalTask), LocalDate.of(2026, 6, 15));

        assertEquals(2, tasks.size());
        assertEquals("Weekly cleaning", tasks.get(0).getTitle());
        assertEquals("Meeting", tasks.get(1).getTitle());
        assertTrue(tasks.get(0).getStartDate().isBefore(tasks.get(1).getStartDate()));
    }

    private static Instant instant(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute).atZone(WARSAW).toInstant();
    }
}