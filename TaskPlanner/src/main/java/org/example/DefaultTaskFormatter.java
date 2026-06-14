package org.example;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DefaultTaskFormatter implements TaskFormatter {

    private static final ZoneId ZONE = ZoneId.of("Europe/Warsaw");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final String TOP_BORDER = "-------------------------------------------";
    private static final String BOTTOM_BORDER = "-------------------------------------------";

    @Override
    public String format(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(TOP_BORDER).append(System.lineSeparator());
        sb.append("TASK: ").append(task.getTitle())
                .append(" status: ").append(task.getStatus().name())
                .append(System.lineSeparator());
        sb.append("Owned by: ").append(task.getOwner()).append(System.lineSeparator());
        sb.append("Start date: ").append(DATE_FORMATTER.withZone(ZONE).format(task.getStartDate()))
                .append(System.lineSeparator());
        sb.append("Description: ").append(task.getDescription()).append(System.lineSeparator());
        sb.append(BOTTOM_BORDER).append(System.lineSeparator());
        return sb.toString();
    }
}
