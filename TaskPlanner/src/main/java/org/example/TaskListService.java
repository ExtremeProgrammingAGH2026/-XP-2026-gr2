package org.example;

import java.util.List;

public class TaskListService {

    private final TaskFilterService filterService;

    public TaskListService(TaskFilterService filterService) {
        this.filterService = filterService;
    }

    public List<Task> getMyTasks(List<Task> tasks, String owner) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<Task> getMyActiveTasks(List<Task> tasks, String owner) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
