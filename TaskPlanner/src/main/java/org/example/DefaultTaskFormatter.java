package org.example;

public class DefaultTaskFormatter implements TaskFormatter {
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
        sb.append("Start date: ").append(DateTimeFormats.getFormatter().format(task.getStartDate()))
                .append(System.lineSeparator());
        sb.append("End date: ").append(DateTimeFormats.getFormatter().format(task.getEndDate()))
                .append(System.lineSeparator());
        sb.append("Description: ").append(task.getDescription()).append(System.lineSeparator());
        sb.append(BOTTOM_BORDER).append(System.lineSeparator());
        return sb.toString();
    }
}
