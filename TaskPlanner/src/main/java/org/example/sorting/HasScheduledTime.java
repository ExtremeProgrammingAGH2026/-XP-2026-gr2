package org.example.sorting;

import java.time.Instant;

/**
 * Provides a scheduled time for sorting purposes.
 */
public interface HasScheduledTime {

    Instant getScheduledTime();
}