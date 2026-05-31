package org.example;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskConflictServiceTest {

    @Test
    void shouldDetectConflictWhenTasksOverlap() {
        Task first = new Task("1", "Task 1", "Desc", "Adam",
                Instant.parse("2026-06-01T10:00:00Z"),
                Instant.parse("2026-06-01T11:00:00Z"));

        Task second = new Task("2", "Task 2", "Desc", "Adam",
                Instant.parse("2026-06-01T10:30:00Z"),
                Instant.parse("2026-06-01T11:30:00Z"));

        TaskConflictService service = new TaskConflictService();

        assertTrue(service.hasConflict(first, List.of(second)));
    }

    @Test
    void shouldNotDetectConflictWhenTasksDoNotOverlap() {
        Task first = new Task("1", "Task 1", "Desc", "Adam",
                Instant.parse("2026-06-01T10:00:00Z"),
                Instant.parse("2026-06-01T11:00:00Z"));

        Task second = new Task("2", "Task 2", "Desc", "Adam",
                Instant.parse("2026-06-01T11:00:00Z"),
                Instant.parse("2026-06-01T12:00:00Z"));

        TaskConflictService service = new TaskConflictService();

        assertFalse(service.hasConflict(first, List.of(second)));
    }

    @Test
    void shouldDetectConflictWhenTasksHaveSameStartAndEndDate() {
        Task first = new Task("1", "Task 1", "Desc", "Adam",
                Instant.parse("2026-06-01T10:00:00Z"),
                Instant.parse("2026-06-01T11:00:00Z"));

        Task second = new Task("2", "Task 2", "Desc", "Adam",
                Instant.parse("2026-06-01T10:00:00Z"),
                Instant.parse("2026-06-01T11:00:00Z"));

        TaskConflictService service = new TaskConflictService();

        assertTrue(service.hasConflict(first, List.of(second)));
    }

    @Test
    void shouldIgnoreTheSameTaskById() {
        Task first = new Task("1", "Task 1", "Desc", "Adam",
                Instant.parse("2026-06-01T10:00:00Z"),
                Instant.parse("2026-06-01T11:00:00Z"));

        Task sameTask = new Task("1", "Task 1", "Desc", "Adam",
                Instant.parse("2026-06-01T10:30:00Z"),
                Instant.parse("2026-06-01T11:30:00Z"));

        TaskConflictService service = new TaskConflictService();

        assertFalse(service.hasConflict(first, List.of(sameTask)));
    }
}