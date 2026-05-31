package org.example;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskOverlapServiceTest {

    @Test
    void shouldReturnOverlappingTasks() {
        Task task = new Task("1", "Task 1", "Desc", "Adam",
                Instant.parse("2026-06-01T10:00:00Z"),
                Instant.parse("2026-06-01T11:00:00Z"));

        Task overlapping = new Task("2", "Task 2", "Desc", "Adam",
                Instant.parse("2026-06-01T10:30:00Z"),
                Instant.parse("2026-06-01T11:30:00Z"));

        Task notOverlapping = new Task("3", "Task 3", "Desc", "Adam",
                Instant.parse("2026-06-01T12:00:00Z"),
                Instant.parse("2026-06-01T13:00:00Z"));

        TaskOverlapService service = new TaskOverlapService();

        List<Task> result = service.findOverlappingTasks(task, List.of(overlapping, notOverlapping));

        assertEquals(1, result.size());
        assertEquals(overlapping, result.get(0));
    }

    @Test
    void shouldReturnEmptyListWhenNoTasksOverlap() {
        Task task = new Task("1", "Task 1", "Desc", "Adam",
                Instant.parse("2026-06-01T10:00:00Z"),
                Instant.parse("2026-06-01T11:00:00Z"));

        Task other = new Task("2", "Task 2", "Desc", "Adam",
                Instant.parse("2026-06-01T11:00:00Z"),
                Instant.parse("2026-06-01T12:00:00Z"));

        TaskOverlapService service = new TaskOverlapService();

        List<Task> result = service.findOverlappingTasks(task, List.of(other));

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldIgnoreTaskWithSameId() {
        Task task = new Task("1", "Task 1", "Desc", "Adam",
                Instant.parse("2026-06-01T10:00:00Z"),
                Instant.parse("2026-06-01T11:00:00Z"));

        Task sameTask = new Task("1", "Task 1 copy", "Desc", "Adam",
                Instant.parse("2026-06-01T10:30:00Z"),
                Instant.parse("2026-06-01T11:30:00Z"));

        TaskOverlapService service = new TaskOverlapService();

        List<Task> result = service.findOverlappingTasks(task, List.of(sameTask));

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldDetectWhenExistingTaskContainsComparedTask() {
        Task task = new Task("1", "Task 1", "Desc", "Adam",
                Instant.parse("2026-06-01T10:30:00Z"),
                Instant.parse("2026-06-01T11:00:00Z"));

        Task containing = new Task("2", "Task 2", "Desc", "Adam",
                Instant.parse("2026-06-01T10:00:00Z"),
                Instant.parse("2026-06-01T12:00:00Z"));

        TaskOverlapService service = new TaskOverlapService();

        List<Task> result = service.findOverlappingTasks(task, List.of(containing));

        assertEquals(1, result.size());
        assertEquals(containing, result.get(0));
    }
}