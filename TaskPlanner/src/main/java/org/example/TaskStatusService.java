package org.example;

import java.util.Objects;

/**
 * Handles task status transitions.
 */
public class TaskStatusService {

    /**
     * Changes the status of the given task.
     *
     * @param task      task to update
     * @param newStatus desired status
     * @throws NullPointerException if task or newStatus is null
     */
    public void changeStatus(Task task, TaskStatus newStatus) {
        Objects.requireNonNull(task, "task must not be null");
        Objects.requireNonNull(newStatus, "newStatus must not be null");
        task.setStatus(newStatus);
    }
}
