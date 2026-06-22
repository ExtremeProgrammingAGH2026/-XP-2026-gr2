package org.example;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TaskConflictService {

    private final TaskOverlapService taskOverlapService;

    public TaskConflictService() {
        this(new TaskOverlapService());
    }

    public TaskConflictService(TaskOverlapService taskOverlapService) {
        this.taskOverlapService = Objects.requireNonNull(taskOverlapService);
    }

    /**
     * Returns whether the given task overlaps in time with any existing task
     * belonging to the same owner. Tasks owned by other household members are
     * never considered a conflict, because their schedules are independent.
     */
    public boolean hasConflict(Task task, List<Task> existingTasks) {
        Objects.requireNonNull(task, "task must not be null");
        Objects.requireNonNull(existingTasks, "existingTasks must not be null");
        List<Task> sameOwnerTasks = existingTasks.stream()
                .filter(existing -> Objects.equals(existing.getOwner(), task.getOwner()))
                .collect(Collectors.toList());
        return !taskOverlapService.findOverlappingTasks(task, sameOwnerTasks).isEmpty();
    }
}