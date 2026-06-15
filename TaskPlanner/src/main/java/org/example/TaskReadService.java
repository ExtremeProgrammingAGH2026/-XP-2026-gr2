package org.example;

import org.example.recurring.RecurringTask;
import org.example.recurring.RecurrencePattern;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class TaskReadService {

    private static final String HEADER_ID = "id";

    public List<Task> readTasks(String path) {
        if (path == null) {
            throw new CsvException("Path cannot be null");
        }
        if (!Files.exists(Path.of(path))) {
            return new ArrayList<>();
        }
        CSVService csvService = new CSVService();
        List<List<String>> rows = csvService.readCsv(path, CsvConstants.SEPARATOR);

        List<Task> tasks = new ArrayList<>();
        for (List<String> row : rows) {
            if (row.isEmpty() || row.get(0).equals(HEADER_ID)) {
                continue;
            }
            if (row.size() != 6 && row.size() != 10) {
                throw new CsvException("Invalid task row format: " + row);
            }
            tasks.add(parseRow(row));
        }
        return tasks;
    }

    private Task parseRow(List<String> row) {
        String id = row.get(0);
        String title = row.get(1);
        String description = row.get(2);
        String owner = row.get(3);
        String dateStr = row.get(4);
        String statusStr;
        String endDateStr;
        String type;
        String recurrencePatternStr;
        String recurrenceEndDateStr;

        if (row.size() == 6) {
            endDateStr = row.get(4);
            statusStr = row.get(5);
            type = "NORMAL";
            recurrencePatternStr = "";
            recurrenceEndDateStr = "";
        } else {
            endDateStr = row.get(5);
            statusStr = row.get(6);
            type = row.get(7);
            recurrencePatternStr = row.get(8);
            recurrenceEndDateStr = row.get(9);
        }

        ZonedDateTime startDate = parseDate(dateStr);
        Instant endDate = parseDate(endDateStr).toInstant();

        TaskStatus status;
        try {
            status = TaskStatus.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            throw new CsvException("Invalid task status: " + statusStr, e);
        }

        Task task = parseTask(id, title, description, owner, startDate.toInstant(), endDate,
                type, recurrencePatternStr, recurrenceEndDateStr);
        task.setStatus(status);
        return task;
    }

    private Task parseTask(String id, String title, String description, String owner,
                           Instant startDate, Instant endDate, String type,
                           String recurrencePatternStr, String recurrenceEndDateStr) {
        if ("RECURRING".equalsIgnoreCase(type)) {
            RecurrencePattern recurrencePattern;
            try {
                recurrencePattern = RecurrencePattern.valueOf(recurrencePatternStr);
            } catch (IllegalArgumentException e) {
                throw new CsvException("Invalid recurrence pattern: " + recurrencePatternStr, e);
            }

            Instant recurrenceEndDate = recurrenceEndDateStr == null || recurrenceEndDateStr.isBlank()
                    ? null
                    : parseDate(recurrenceEndDateStr).toInstant();
            return new RecurringTask(id, title, description, owner, startDate, endDate,
                    recurrencePattern, recurrenceEndDate);
        }

        return new Task(id, title, description, owner, startDate, endDate);
    }

    private ZonedDateTime parseDate(String dateStr) {
        try {
            return ZonedDateTime.parse(dateStr, DateTimeFormats.STORAGE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new CsvException("Invalid date format in task row: " + dateStr, e);
        }
    }
}
