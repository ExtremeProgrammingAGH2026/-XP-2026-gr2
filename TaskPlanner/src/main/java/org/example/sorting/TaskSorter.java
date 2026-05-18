package org.example.sorting;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Sorts items implementing {@link HasScheduledTime} by date and time of day independently.
 */
public class TaskSorter {

    private TaskSorter() {
    }

    /**
     * Returns a new list sorted first by calendar day (controlled by {@code dayOrder}),
     * then by time of day within each day (controlled by {@code timeOrder}).
     *
     * @param items     items to sort
     * @param zone      zone used to split each {@link java.time.Instant} into date and time
     * @param dayOrder  sort order for the date part
     * @param timeOrder sort order for the time-of-day part within a given day
     * @param <T>       type of item
     * @return new sorted list
     */
    public static <T extends HasScheduledTime> List<T> sort(
            List<T> items,
            ZoneId zone,
            SortOrder dayOrder,
            SortOrder timeOrder
    ) {
        Comparator<T> byDay = Comparator.comparing(
                (T item) -> item.getScheduledTime().atZone(zone).toLocalDate()
        );
        if (dayOrder == SortOrder.DESC) {
            byDay = byDay.reversed();
        }

        Comparator<T> byTime = Comparator.comparing(
                (T item) -> item.getScheduledTime().atZone(zone).toLocalTime()
        );
        if (timeOrder == SortOrder.DESC) {
            byTime = byTime.reversed();
        }

        List<T> result = new ArrayList<>(items);
        result.sort(byDay.thenComparing(byTime));
        return result;
    }
}