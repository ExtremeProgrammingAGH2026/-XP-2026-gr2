package org.example;

import org.example.recurring.RecurringTask;
import org.example.recurring.RecurringTaskExpander;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TaskScheduleService {

    private static final Comparator<Task> BY_START_DATE = Comparator.comparing(Task::getStartDate);

    private final ZoneId zone;
    private final RecurringTaskExpander recurringTaskExpander;

    public TaskScheduleService(ZoneId zone) {
        this.zone = Objects.requireNonNull(zone, "zone must not be null");
        this.recurringTaskExpander = new RecurringTaskExpander(zone);
    }

    public List<Task> getTasksForDay(List<Task> tasks, LocalDate date) {
        Objects.requireNonNull(tasks, "tasks must not be null");
        Objects.requireNonNull(date, "date must not be null");

        Instant from = date.atStartOfDay(zone).toInstant();
        Instant to = date.plusDays(1).atStartOfDay(zone).toInstant().minusNanos(1);
        return expandForWindow(tasks, from, to).stream()
                .filter(task -> !task.getStartDate().isAfter(to) && !task.getStartDate().isBefore(from))
                .sorted(BY_START_DATE)
                .collect(Collectors.toList());
    }

    public List<Task> expandForWindow(List<Task> tasks, Instant from, Instant to) {
        Objects.requireNonNull(tasks, "tasks must not be null");
        Objects.requireNonNull(from, "from must not be null");
        Objects.requireNonNull(to, "to must not be null");
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from must not be after to");
        }

        List<Task> result = new ArrayList<>();
        for (Task task : tasks) {
            if (task instanceof RecurringTask) {
                result.addAll(recurringTaskExpander.expand((RecurringTask) task, from, to));
            } else if (!task.getStartDate().isAfter(to) && !task.getEndDate().isBefore(from)) {
                result.add(task);
            }
        }
        return result.stream().sorted(BY_START_DATE).collect(Collectors.toList());
    }
}