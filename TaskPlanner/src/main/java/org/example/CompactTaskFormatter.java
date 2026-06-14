package org.example;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CompactTaskFormatter implements TaskFormatter {

    private static final ZoneId ZONE = ZoneId.of("Europe/Warsaw");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public String format(Task task) {
        return "[" + task.getStatus().name() + "] "
                + task.getTitle()
                + " (" + task.getOwner() + ") - "
                + DATE_FORMATTER.withZone(ZONE).format(task.getStartDate())
                + System.lineSeparator();
    }
}
