package org.example;

public class CompactTaskFormatter implements TaskFormatter {

    @Override
    public String format(Task task) {
        return "[" + task.getStatus().name() + "] "
                + task.getTitle()
                + " (" + task.getOwner() + ") "
                + DateTimeFormats.getFormatter().format(task.getStartDate())
                + " - " + DateTimeFormats.getFormatter().format(task.getEndDate())
                + System.lineSeparator();
    }
}
