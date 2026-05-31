package org.example;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TaskOverlapService {

    public List<Task> findOverlappingTasks(Task task, List<Task> tasks) {
        Objects.requireNonNull(task, "task must not be null");
        Objects.requireNonNull(tasks, "tasks must not be null");

        return tasks.stream()
                .filter(existing -> !existing.getId().equals(task.getId()))
                .filter(existing -> overlaps(task, existing))
                .collect(Collectors.toList());
    }

    public boolean overlaps(Task first, Task second) {
        Objects.requireNonNull(first, "first task must not be null");
        Objects.requireNonNull(second, "second task must not be null");

        return first.getStartDate().isBefore(second.getEndDate())
                && first.getEndDate().isAfter(second.getStartDate());
    }
}