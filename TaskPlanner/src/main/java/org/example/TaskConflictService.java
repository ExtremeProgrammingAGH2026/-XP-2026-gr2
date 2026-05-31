package org.example;

import java.util.List;
import java.util.Objects;

public class TaskConflictService {

    public boolean hasConflict(Task task, List<Task> existingTasks) {
        Objects.requireNonNull(task, "task must not be null");
        Objects.requireNonNull(existingTasks, "existingTasks must not be null");

        return existingTasks.stream()
                .filter(existing -> !existing.getId().equals(task.getId()))
                .anyMatch(existing -> existing.getStartDate().equals(task.getStartDate()));
    }
}