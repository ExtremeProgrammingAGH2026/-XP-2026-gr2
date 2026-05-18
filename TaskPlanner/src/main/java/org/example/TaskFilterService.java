package org.example;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

public class TaskFilterService {

    public List<Task> filterByDateRange(List<Task> tasks, Instant from, Instant to) {
        return tasks.stream()
                .filter(task -> !task.getStartDate().isBefore(from) && !task.getStartDate().isAfter(to))
                .collect(Collectors.toList());
    }

    public List<Task> filterByOwner(List<Task> tasks, String owner) {
        return tasks.stream()
                .filter(task -> task.getOwner().equals(owner))
                .collect(Collectors.toList());
    }

    public List<Task> filterByMonth(List<Task> tasks, int year, int month, ZoneId zone) {
        YearMonth yearMonth = YearMonth.of(year, month);
        Instant from = yearMonth.atDay(1).atStartOfDay(zone).toInstant();
        Instant to = yearMonth.plusMonths(1).atDay(1).atStartOfDay(zone).toInstant();
        return tasks.stream()
                .filter(task -> !task.getStartDate().isBefore(from) && task.getStartDate().isBefore(to))
                .collect(Collectors.toList());
    }
}
