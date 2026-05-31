package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskListServiceTest {

    private TaskListService taskListService;

    @BeforeEach
    void setUp() {
        taskListService = new TaskListService(new TaskFilterService());
    }

    private Task task(String id, String owner, String date, TaskStatus status) {
        Task task = new Task(id, "Title " + id, "Description " + id, owner, Instant.parse(date));
        task.setStatus(status);
        return task;
    }

    @Test
    void shouldReturnOnlyTasksOwnedByGivenOwner() {
        Task mine = task("1", "anna", "2026-06-01T10:00:00Z", TaskStatus.NEW);
        Task other = task("2", "bob", "2026-06-01T10:00:00Z", TaskStatus.NEW);

        List<Task> result = taskListService.getMyTasks(List.of(mine, other), "anna");

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
    }

    @Test
    void shouldReturnMyTasksSortedByStartDateAscending() {
        Task later = task("1", "anna", "2026-06-03T10:00:00Z", TaskStatus.NEW);
        Task earlier = task("2", "anna", "2026-06-01T10:00:00Z", TaskStatus.NEW);

        List<Task> result = taskListService.getMyTasks(List.of(later, earlier), "anna");

        assertEquals("2", result.get(0).getId());
        assertEquals("1", result.get(1).getId());
    }

    @Test
    void shouldReturnEmptyListWhenOwnerHasNoTasks() {
        Task other = task("1", "bob", "2026-06-01T10:00:00Z", TaskStatus.NEW);

        List<Task> result = taskListService.getMyTasks(List.of(other), "anna");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowWhenTasksIsNull() {
        assertThrows(NullPointerException.class, () -> taskListService.getMyTasks(null, "anna"));
    }

    @Test
    void shouldThrowWhenOwnerIsNull() {
        assertThrows(NullPointerException.class, () -> taskListService.getMyTasks(List.of(), null));
    }

    @Test
    void shouldExcludeDoneTasksFromActiveTasks() {
        Task active = task("1", "anna", "2026-06-01T10:00:00Z", TaskStatus.IN_PROGRESS);
        Task done = task("2", "anna", "2026-06-02T10:00:00Z", TaskStatus.DONE);

        List<Task> result = taskListService.getMyActiveTasks(List.of(active, done), "anna");

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
    }

    @Test
    void shouldReturnActiveTasksSortedByStartDate() {
        Task later = task("1", "anna", "2026-06-03T10:00:00Z", TaskStatus.NEW);
        Task earlier = task("2", "anna", "2026-06-01T10:00:00Z", TaskStatus.IN_PROGRESS);

        List<Task> result = taskListService.getMyActiveTasks(List.of(later, earlier), "anna");

        assertEquals("2", result.get(0).getId());
        assertEquals("1", result.get(1).getId());
    }
}
