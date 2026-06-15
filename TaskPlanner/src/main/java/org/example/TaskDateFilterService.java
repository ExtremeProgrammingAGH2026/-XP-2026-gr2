package org.example;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Provides date-based filtering operations on a collection of tasks.
 *
 * <p>Filtering is done in the given {@link ZoneId} so that the user's local
 * calendar day is respected correctly regardless of the UTC offset stored in
 * each {@link Task}.
 */
public class TaskDateFilterService {

    private final ZoneId zone;

    /**
     * Creates a filter service that uses the given time zone for date calculations.
     *
     * @param zone the time zone used when converting instants to calendar dates
     */
    public TaskDateFilterService(ZoneId zone) {
        this.zone = Objects.requireNonNull(zone, "zone must not be null");
    }

    /**
     * Returns all tasks whose start date falls on the given calendar day.
     *
     * @param tasks tasks to filter; must not be {@code null}
     * @param date  the calendar day to match against
     * @return a new list containing only tasks that start on {@code date}
     */
    public List<Task> filterByDay(List<Task> tasks, LocalDate date) {
        Objects.requireNonNull(tasks, "tasks must not be null");
        Objects.requireNonNull(date, "date must not be null");

        return tasks.stream()
                .filter(task -> toLocalDate(task).equals(date))
                .collect(Collectors.toList());
    }

    /**
     * Returns all tasks whose start date falls on today's calendar day.
     *
     * @param tasks tasks to filter; must not be {@code null}
     * @return a new list containing only tasks starting today
     */
    public List<Task> filterByToday(List<Task> tasks) {
        Objects.requireNonNull(tasks, "tasks must not be null");
        return filterByDay(tasks, LocalDate.now(zone));
    }

    /**
     * Returns all tasks whose start date falls within the given calendar week.
     *
     * @param tasks     tasks to filter; must not be {@code null}
     * @param weekStart the first day of the week (inclusive)
     * @param weekEnd   the last day of the week (inclusive)
     * @return a new list containing only tasks within the week
     */
    public List<Task> filterByWeek(List<Task> tasks, LocalDate weekStart, LocalDate weekEnd) {
        Objects.requireNonNull(tasks, "tasks must not be null");
        Objects.requireNonNull(weekStart, "weekStart must not be null");
        Objects.requireNonNull(weekEnd, "weekEnd must not be null");
        if (weekStart.isAfter(weekEnd)) {
            throw new IllegalArgumentException("weekStart must not be after weekEnd");
        }

        return tasks.stream()
                .filter(task -> {
                    LocalDate taskDate = toLocalDate(task);
                    return !taskDate.isBefore(weekStart) && !taskDate.isAfter(weekEnd);
                })
                .collect(Collectors.toList());
    }

    private LocalDate toLocalDate(Task task) {
        ZonedDateTime zdt = task.getStartDate().atZone(zone);
        return zdt.toLocalDate();
    }
}