package org.example;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TaskListService {

    private final TaskFilterService filterService;

    public TaskListService(TaskFilterService filterService) {
        this.filterService = filterService;
    }

    public List<Task> getMyTasks(List<Task> tasks, String owner) {
        Objects.requireNonNull(tasks, "tasks must not be null");
        Objects.requireNonNull(owner, "owner must not be null");
        return filterService.filterByOwner(tasks, owner).stream()
                .sorted(Comparator.comparing(Task::getStartDate))
                .collect(Collectors.toList());
    }

    public List<Task> getMyActiveTasks(List<Task> tasks, String owner) {
        return getMyTasks(tasks, owner).stream()
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .collect(Collectors.toList());
    }
}
