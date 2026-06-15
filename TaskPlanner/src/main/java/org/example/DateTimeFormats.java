package org.example;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DateTimeFormats {

    private static final String DEFAULT_PATTERN = "dd.MM.yyyy HH:mm";
    private static final String DEFAULT_ZONE = "Europe/Warsaw";

    /**
     * Canonical, immutable format used to persist dates in the CSV files.
     * Independent of the display config, so changing the display format never
     * rewrites or orphans already-stored data.
     */
    public static final DateTimeFormatter STORAGE_FORMATTER =
            DateTimeFormatter.ofPattern(DEFAULT_PATTERN).withZone(ZoneId.of(DEFAULT_ZONE));

    private static String pattern = DEFAULT_PATTERN;
    private static ZoneId zone = ZoneId.of(DEFAULT_ZONE);
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern).withZone(zone);

    private DateTimeFormats() {
    }

    public static void init(AppConfiguration config) {
        if (config.getDateTimeFormat() != null && !config.getDateTimeFormat().isBlank()) {
            pattern = config.getDateTimeFormat();
        }
        if (config.getTimeZoneName() != null && !config.getTimeZoneName().isBlank()) {
            zone = ZoneId.of(config.getTimeZoneName());
        }
        formatter = DateTimeFormatter.ofPattern(pattern).withZone(zone);
    }

    public static DateTimeFormatter getFormatter() {
        return formatter;
    }

    public static String getPattern() {
        return pattern;
    }

    public static ZoneId getZone() {
        return zone;
    }

    public static void resetToDefaults() {
        pattern = DEFAULT_PATTERN;
        zone = ZoneId.of(DEFAULT_ZONE);
        formatter = DateTimeFormatter.ofPattern(pattern).withZone(zone);
    }
}
