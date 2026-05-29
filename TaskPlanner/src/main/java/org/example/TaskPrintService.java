package org.example;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TaskPrintService {

    private static final ZoneId ZONE = ZoneId.of("Europe/Warsaw");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final String TOP_BORDER = "-------------------------------------------";
    private static final String BOTTOM_BORDER = "-------------------------------------------";

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
        List<Task> sorted = tasks.stream()
                .sorted(Comparator.comparing(Task::getStartDate))
                .collect(Collectors.toList());
        printTasks(sorted);
    }

    public void printTasksByDateRange(List<Task> tasks, Instant from, Instant to) {
        TaskFilterService filterService = new TaskFilterService();
        List<Task> filtered = filterService.filterByDateRange(tasks, from, to).stream()
                .sorted(Comparator.comparing(Task::getStartDate))
                .collect(Collectors.toList());
        printTasks(filtered);
    }

    public void printTasksByOwner(List<Task> tasks, String owner) {
        TaskFilterService filterService = new TaskFilterService();
        List<Task> filtered = filterService.filterByOwner(tasks, owner).stream()
                .sorted(Comparator.comparing(Task::getStartDate))
                .collect(Collectors.toList());
        printTasks(filtered);
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
