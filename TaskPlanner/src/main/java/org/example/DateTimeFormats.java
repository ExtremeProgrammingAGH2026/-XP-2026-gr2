package org.example;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DateTimeFormats {

    public static final ZoneId ZONE = ZoneId.of("Europe/Warsaw");
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZONE);
    public static final String PATTERN = "dd.MM.yyyy HH:mm";

    private DateTimeFormats() {
    }
}
