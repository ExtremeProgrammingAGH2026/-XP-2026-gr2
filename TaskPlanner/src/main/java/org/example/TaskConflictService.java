package org.example;

import java.util.List;
import java.util.Objects;

public class TaskConflictService {

    private final TaskOverlapService taskOverlapService;

    public TaskConflictService() {
        this(new TaskOverlapService());
    }

    public TaskConflictService(TaskOverlapService taskOverlapService) {
        this.taskOverlapService = Objects.requireNonNull(taskOverlapService);
    }

    public boolean hasConflict(Task task, List<Task> existingTasks) {
        return !taskOverlapService.findOverlappingTasks(task, existingTasks).isEmpty();
    }
}