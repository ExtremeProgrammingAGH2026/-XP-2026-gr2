package org.example;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskConflictServiceTest {

    @Test
    void shouldDetectConflictWhenTasksHaveSameStartDate() {
        Task first = new Task("1", "Task 1", "Desc", "Adam",
                Instant.parse("2026-06-01T10:00:00Z"));
        Task second = new Task("2", "Task 2", "Desc", "Adam",
                Instant.parse("2026-06-01T10:00:00Z"));

        TaskConflictService service = new TaskConflictService();

        boolean result = service.hasConflict(first, List.of(second));

        assertTrue(result);
    }

    @Test
    void shouldNotDetectConflictWhenTasksHaveDifferentStartDate() {
        Task first = new Task("1", "Task 1", "Desc", "Adam",
                Instant.parse("2026-06-01T10:00:00Z"));
        Task second = new Task("2", "Task 2", "Desc", "Adam",
                Instant.parse("2026-06-01T11:00:00Z"));

        TaskConflictService service = new TaskConflictService();

        boolean result = service.hasConflict(first, List.of(second));

        assertFalse(result);
    }

    @Test
    void shouldIgnoreTheSameTaskById() {
        Task first = new Task("1", "Task 1", "Desc", "Adam",
                Instant.parse("2026-06-01T10:00:00Z"));
        Task sameTask = new Task("1", "Task 1", "Desc", "Adam",
                Instant.parse("2026-06-01T10:00:00Z"));

        TaskConflictService service = new TaskConflictService();

        boolean result = service.hasConflict(first, List.of(sameTask));

        assertFalse(result);
    }
}