package org.example;

import java.util.Objects;

/**
 * Handles editing of task fields.
 */
public class TaskEditService {

    private final TaskStatusService taskStatusService;

    public TaskEditService(TaskStatusService taskStatusService) {
        this.taskStatusService = taskStatusService;
    }

    /**
     * Changes the title of the given task.
     *
     * @param task     task to update
     * @param newTitle new title; must not be null or blank
     */
    public void editTitle(Task task, String newTitle) {
        Objects.requireNonNull(task, "task must not be null");
        Objects.requireNonNull(newTitle, "newTitle must not be null");
        if (newTitle.isBlank()) {
            throw new IllegalArgumentException("newTitle must not be blank");
        }
        task.setTitle(newTitle);
    }

    /**
     * Changes the description of the given task.
     *
     * @param task           task to update
     * @param newDescription new description; must not be null
     */
    public void editDescription(Task task, String newDescription) {
        Objects.requireNonNull(task, "task must not be null");
        Objects.requireNonNull(newDescription, "newDescription must not be null");
        task.setDescription(newDescription);
    }

    /**
     * Changes the status of the given task.
     *
     * @param task      task to update
     * @param newStatus desired status
     */
    public void editStatus(Task task, TaskStatus newStatus) {
        Objects.requireNonNull(task, "task must not be null");
        taskStatusService.changeStatus(task, newStatus);
    }
}
