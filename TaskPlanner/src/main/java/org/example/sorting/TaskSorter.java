package org.example.sorting;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
        int n = items.size();

        // Precompute keys: O(n) zone conversions instead of O(n log n) during comparisons
        LocalDate[] dates = new LocalDate[n];
        LocalTime[] times = new LocalTime[n];
        for (int i = 0; i < n; i++) {
            ZonedDateTime zdt = items.get(i).getScheduledTime().atZone(zone);
            dates[i] = zdt.toLocalDate();
            times[i] = zdt.toLocalTime();
        }

        int dayFactor = dayOrder == SortOrder.ASC ? 1 : -1;
        int timeFactor = timeOrder == SortOrder.ASC ? 1 : -1;

        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) indices[i] = i;

        // Comparator touches only pre-built arrays — no allocations in the hot path
        Arrays.sort(indices, (i, j) -> {
            int cmp = dayFactor * dates[i].compareTo(dates[j]);
            if (cmp != 0) return cmp;
            return timeFactor * times[i].compareTo(times[j]);
        });

        List<T> result = new ArrayList<>(n);
        for (int idx : indices) result.add(items.get(idx));
        return result;
    }
}