package org.example;

import org.example.recurring.RecurringTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

class TaskSaveService {
    private static final String HEADER = String.join(CsvConstants.SEPARATOR_STR,
            "id", "title", "description", "owner", "startDate", "endDate",
            "status", "type", "recurrencePattern", "recurrenceEndDate") + System.lineSeparator();

    public void saveTask(Task task, String path, boolean append) {
        Objects.requireNonNull(task, "task must not be null");
        saveTasks(List.of(task), path, append);
    }

    public void saveTasks(List<Task> tasks, String path, boolean append) {
        Objects.requireNonNull(tasks, "tasks must not be null");
        Objects.requireNonNull(path, "path must not be null");
        if (tasks.isEmpty()) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        boolean fileExists = Files.exists(Path.of(path));
        if (!append || !fileExists) {
            builder.append(HEADER);
        }

        for (Task task : tasks) {
            builder.append(rowFor(task));
        }

        writeFile(path, builder.toString(), append && fileExists);
    }

    private static String rowFor(Task task) {
        Objects.requireNonNull(task, "task must not be null");
        if (task.getId() == null || task.getTitle() == null || task.getDescription() == null
                || task.getOwner() == null || task.getStartDate() == null || task.getEndDate() == null
                || task.getStatus() == null) {
            throw new CsvException("Task fields must not be null");
        }

        String type = "NORMAL";
        String recurrencePattern = "";
        String recurrenceEndDate = "";
        if (task instanceof RecurringTask) {
            RecurringTask recurringTask = (RecurringTask) task;
            type = "RECURRING";
            recurrencePattern = recurringTask.getRecurrencePattern().name();
            if (recurringTask.getRecurrenceEndDate() != null) {
                recurrenceEndDate = DateTimeFormats.STORAGE_FORMATTER.format(recurringTask.getRecurrenceEndDate());
            }
        }

        String startDate = DateTimeFormats.STORAGE_FORMATTER.format(task.getStartDate());
        String endDate = DateTimeFormats.STORAGE_FORMATTER.format(task.getEndDate());
        return String.join(CsvConstants.SEPARATOR_STR,
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getOwner(),
                startDate,
                endDate,
                task.getStatus().name(),
                type,
                recurrencePattern,
                recurrenceEndDate) + System.lineSeparator();
    }

    private static String storageDate(Instant instant) {
        return DateTimeFormats.STORAGE_FORMATTER.format(instant);
    }

    private static void writeFile(String path, String content, boolean append) {
        Path output = Path.of(path);
        try {
            if (append) {
                Files.writeString(output, content, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            } else {
                Files.writeString(output, content, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            throw new CsvException("Failed to save CSV: " + path, e);
        }
    }
}
