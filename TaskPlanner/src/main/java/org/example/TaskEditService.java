package org.example;

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
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Changes the description of the given task.
     *
     * @param task           task to update
     * @param newDescription new description; must not be null
     */
    public void editDescription(Task task, String newDescription) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Changes the status of the given task.
     *
     * @param task      task to update
     * @param newStatus desired status
     */
    public void editStatus(Task task, TaskStatus newStatus) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
