package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class TaskStatusServiceTest {

    private TaskStatusService service;
    private Task task;

    @BeforeEach
    void setUp() {
        service = new TaskStatusService();
        task = new Task("1", "Odkurzyć", "", "Adam", Instant.now());
    }

    @Test
    void shouldHaveStatusNewByDefault() {
        assertEquals(TaskStatus.NEW, task.getStatus());
    }

    @Test
    void shouldChangeStatusFromNewToInProgress() {
        service.changeStatus(task, TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    void shouldChangeStatusFromInProgressToDone() {
        task.setStatus(TaskStatus.IN_PROGRESS);

        service.changeStatus(task, TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, task.getStatus());
    }

    @Test
    void shouldChangeStatusFromNewToDone() {
        service.changeStatus(task, TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, task.getStatus());
    }

    @Test
    void shouldChangeStatusFromDoneToNew() {
        task.setStatus(TaskStatus.DONE);

        service.changeStatus(task, TaskStatus.NEW);

        assertEquals(TaskStatus.NEW, task.getStatus());
    }

    @Test
    void shouldChangeStatusOnTheExactTaskInstance() {
        Task other = new Task("2", "Inne", "", "Adam", Instant.now());

        service.changeStatus(task, TaskStatus.IN_PROGRESS);

        assertSame(TaskStatus.IN_PROGRESS, task.getStatus());
        assertEquals(TaskStatus.NEW, other.getStatus());
    }

    @Test
    void shouldAllowSettingTheSameStatusAgain() {
        service.changeStatus(task, TaskStatus.NEW);

        assertEquals(TaskStatus.NEW, task.getStatus());
    }
}