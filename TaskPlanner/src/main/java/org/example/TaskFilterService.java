package org.example;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TaskFilterService {

    public List<Task> filterByDateRange(List<Task> tasks, LocalDateTime from, LocalDateTime to) {
        return tasks.stream()
                .filter(task -> !task.getStartDate().isBefore(from) && !task.getStartDate().isAfter(to))
                .collect(Collectors.toList());
    }

    public List<Task> filterByOwner(List<Task> tasks, String owner) {
        return tasks.stream()
                .filter(task -> task.getOwner().equals(owner))
                .collect(Collectors.toList());
    }

    public List<Task> filterByMonth(List<Task> tasks, int year, int month) {
        return tasks.stream()
                .filter(task -> task.getStartDate().getYear() == year
                        && task.getStartDate().getMonthValue() == month)
                .collect(Collectors.toList());
    }
}
