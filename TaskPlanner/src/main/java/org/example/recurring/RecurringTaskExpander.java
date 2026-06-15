package org.example.recurring;

import org.example.Task;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Expands a {@link RecurringTask} into a list of concrete {@link Task} occurrences
 * that fall within a requested date window.
 *
 * <p>This class contains no mutable state and all methods are stateless — it is
 * safe to share a single instance across threads.
 */
public class RecurringTaskExpander {

    private static final int MAX_OCCURRENCES = 1000;

    private final ZoneId zone;

    /**
     * Creates an expander that uses the given time zone when computing recurrence dates.
     *
     * @param zone the time zone used for date arithmetic
     */
    public RecurringTaskExpander(ZoneId zone) {
        if (zone == null) {
            throw new IllegalArgumentException("zone must not be null");
        }
        this.zone = zone;
    }

    /**
     * Generates all occurrences of the given recurring task that start within
     * [{@code windowStart}, {@code windowEnd}].
     *
     * @param recurringTask the recurring task template
     * @param windowStart   start of the expansion window (inclusive)
     * @param windowEnd     end of the expansion window (inclusive)
     * @return list of concrete task occurrences, ordered chronologically
     */
    public List<Task> expand(RecurringTask recurringTask, Instant windowStart, Instant windowEnd) {
        if (recurringTask == null) {
            throw new IllegalArgumentException("recurringTask must not be null");
        }
        if (windowStart == null || windowEnd == null) {
            throw new IllegalArgumentException("windowStart and windowEnd must not be null");
        }
        if (windowStart.isAfter(windowEnd)) {
            throw new IllegalArgumentException("windowStart must not be after windowEnd");
        }

        List<Task> occurrences = new ArrayList<>();
        ZonedDateTime current = recurringTask.getStartDate().atZone(zone);
        Instant effectiveEnd = resolveEffectiveEnd(recurringTask, windowEnd);

        int count = 0;
        while (!current.toInstant().isAfter(effectiveEnd)) {
            if (count++ >= MAX_OCCURRENCES) {
                break;
            }
            Instant occurrenceStart = current.toInstant();
            if (!occurrenceStart.isBefore(windowStart)) {
                occurrences.add(buildOccurrence(recurringTask, occurrenceStart));
            }
            current = advance(current, recurringTask.getRecurrencePattern());
        }

        return occurrences;
    }

    private Instant resolveEffectiveEnd(RecurringTask recurringTask, Instant windowEnd) {
        Instant recurrenceEndDate = recurringTask.getRecurrenceEndDate();
        if (recurrenceEndDate == null) {
            return windowEnd;
        }
        return recurrenceEndDate.isBefore(windowEnd) ? recurrenceEndDate : windowEnd;
    }

    private Task buildOccurrence(RecurringTask template, Instant occurrenceStart) {
        java.time.Duration duration = java.time.Duration.between(template.getStartDate(), template.getEndDate());
        Instant occurrenceEnd = occurrenceStart.plus(duration);
        return new Task(
                UUID.randomUUID().toString(),
                template.getTitle(),
                template.getDescription(),
                template.getOwner(),
                occurrenceStart,
                occurrenceEnd
        );
    }

    private ZonedDateTime advance(ZonedDateTime current, RecurrencePattern pattern) {
        switch (pattern) {
            case DAILY:
                return current.plusDays(1);
            case WEEKLY:
                return current.plusWeeks(1);
            case BIWEEKLY:
                return current.plusWeeks(2);
            case MONTHLY:
                return current.plusMonths(1);
            default:
                throw new IllegalArgumentException("Unsupported recurrence pattern: " + pattern);
        }
    }
}