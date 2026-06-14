package org.example;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TaskPrintService {

    private static final Comparator<Task> BY_START_DATE = Comparator.comparing(Task::getStartDate);
    private static final ZoneId ZONE = ZoneId.of("Europe/Warsaw");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final String TOP_BORDER = "-------------------------------------------";
    private static final String BOTTOM_BORDER = "-------------------------------------------";

    private final TaskFilterService filterService;

    public TaskPrintService(TaskFilterService filterService) {
        this.filterService = Objects.requireNonNull(filterService, "filterService must not be null");
    }

    public void printTasks(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            System.out.println("No tasks available");
            return;
        }

        for (Task task : tasks) {
            printTask(task);
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

    private void printTask(Task task) {
        System.out.println(TOP_BORDER);
        System.out.println("TASK: " + task.getTitle() + " status: " + task.getStatus().name());
        System.out.println("Owned by: " + task.getOwner());
        System.out.println("Start date: " + DATE_FORMATTER.withZone(ZONE).format(task.getStartDate()));
        System.out.println("Description: " + task.getDescription());
        System.out.println(BOTTOM_BORDER);
    }
}
