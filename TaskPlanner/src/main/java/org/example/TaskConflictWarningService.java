package org.example;

import java.util.List;
import java.util.Objects;

public class TaskConflictWarningService {

    private final TaskOverlapService taskOverlapService;

    public TaskConflictWarningService() {
        this(new TaskOverlapService());
    }

    public TaskConflictWarningService(TaskOverlapService taskOverlapService) {
        this.taskOverlapService = Objects.requireNonNull(taskOverlapService, "taskOverlapService must not be null");
    }

    public void printConflictWarning(Task task, List<Task> existingTasks) {
        List<Task> overlappingTasks = taskOverlapService.findOverlappingTasks(task, existingTasks);

        for (Task overlappingTask : overlappingTasks) {
            System.out.println("WARNING: Task '" + task.getTitle()
                    + "' conflicts with task '" + overlappingTask.getTitle() + "'");
        }
    }
}