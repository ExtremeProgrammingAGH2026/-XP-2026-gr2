package org.example;

public class CompactTaskFormatter implements TaskFormatter {

    @Override
    public String format(Task task) {
        return "[" + task.getStatus().name() + "] "
                + task.getTitle()
                + " (" + task.getOwner() + ") "
                + DateTimeFormats.FORMATTER.format(task.getStartDate())
                + " - " + DateTimeFormats.FORMATTER.format(task.getEndDate())
                + System.lineSeparator();
    }
}
