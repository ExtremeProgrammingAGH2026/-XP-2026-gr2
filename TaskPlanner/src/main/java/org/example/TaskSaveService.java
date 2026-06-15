package org.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;

class TaskSaveService {
    private static final String HEADER = String.join(CsvConstants.SEPARATOR_STR,
            "id", "title", "description", "owner", "startDate", "status") + System.lineSeparator();

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
                || task.getOwner() == null || task.getStartDate() == null || task.getStatus() == null) {
            throw new CsvException("Task fields must not be null");
        }

        String date = DateTimeFormats.getFormatter().format(task.getStartDate());
        return String.join(CsvConstants.SEPARATOR_STR,
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getOwner(),
                date,
                task.getStatus().name()) + System.lineSeparator();
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
