package org.example;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TaskPrintService {

    private static final Comparator<Task> BY_START_DATE = Comparator.comparing(Task::getStartDate);

    private final TaskFilterService filterService;
    private final TaskFormatter formatter;

    public TaskPrintService(TaskFilterService filterService) {
        this(filterService, new DefaultTaskFormatter());
    }

    public TaskPrintService(TaskFilterService filterService, TaskFormatter formatter) {
        this.filterService = Objects.requireNonNull(filterService, "filterService must not be null");
        this.formatter = Objects.requireNonNull(formatter, "formatter must not be null");
    }

    public void printTasks(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            System.out.println("No tasks available");
            return;
        }

        for (Task task : tasks) {
            System.out.print(formatter.format(task));
        }
    }

    public void printTasksSortedByDate(List<Task> tasks) {
        printTasks(sortByDate(tasks));
    }

    public void printTasksByDateRange(List<Task> tasks, Instant from, Instant to) {
        printTasks(sortByDate(filterService.filterByDateRange(tasks, from, to)));
    }

    public void printTasksByOwner(List<Task> tasks, String owner) {
        printTasks(sortByDate(filterService.filterByOwner(tasks, owner)));
    }

    private List<Task> sortByDate(List<Task> tasks) {
        return tasks.stream()
                .sorted(BY_START_DATE)
                .collect(Collectors.toList());
    }

}
