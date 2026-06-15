package org.example;

import java.nio.file.Files;
import java.nio.file.Path;
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
            if (row.size() != 6 && row.size() != 7) {
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

        ZonedDateTime startDate = parseDate(row.get(4));

        // 7 columns: id;title;description;owner;startDate;endDate;status
        // 6 columns (legacy): no endDate -> default the end to the start
        ZonedDateTime endDate;
        String statusStr;
        if (row.size() == 7) {
            endDate = parseDate(row.get(5));
            statusStr = row.get(6);
        } else {
            endDate = startDate;
            statusStr = row.get(5);
        }

        TaskStatus status;
        try {
            status = TaskStatus.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            throw new CsvException("Invalid task status: " + statusStr, e);
        }

        Task task = new Task(id, title, description, owner, startDate.toInstant(), endDate.toInstant());
        task.setStatus(status);
        return task;
    }

    private ZonedDateTime parseDate(String dateStr) {
        try {
            return ZonedDateTime.parse(dateStr, DateTimeFormats.STORAGE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new CsvException("Invalid date format in task row: " + dateStr, e);
        }
    }
}
