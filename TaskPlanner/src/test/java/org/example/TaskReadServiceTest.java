package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskReadServiceTest {

    private static final ZoneId WARSAW = ZoneId.of("Europe/Warsaw");
    private static final String HEADER = "id;title;description;owner;startDate;status";

    @TempDir
    Path tempDir;

    private TaskReadService service;

    @BeforeEach
    public void setUp() {
        service = new TaskReadService();
    }

    @Test
    public void shouldReadSingleTaskFromCsv() throws IOException {
        Path csv = tempDir.resolve("tasks.csv");
        write(csv, HEADER + "\n1;Clean;Clean room;Adam;29.05.2026 10:30;NEW");

        List<Task> tasks = service.readTasks(csv.toString());

        assertEquals(1, tasks.size());
        assertEquals("1", tasks.get(0).getId());
        assertEquals("Clean", tasks.get(0).getTitle());
        assertEquals("Clean room", tasks.get(0).getDescription());
        assertEquals("Adam", tasks.get(0).getOwner());
        assertEquals(TaskStatus.NEW, tasks.get(0).getStatus());
    }

    @Test
    public void shouldReadStartDateCorrectly() throws IOException {
        Path csv = tempDir.resolve("tasks.csv");
        write(csv, HEADER + "\n1;Clean;Clean room;Adam;29.05.2026 10:30;NEW");

        Task task = service.readTasks(csv.toString()).get(0);

        assertEquals(LocalDateTime.of(2026, 5, 29, 10, 30).atZone(WARSAW).toInstant(), task.getStartDate());
    }

    @Test
    public void shouldReadMultipleTasksFromCsv() throws IOException {
        Path csv = tempDir.resolve("tasks.csv");
        write(csv, HEADER + "\n1;Clean;Clean room;Adam;29.05.2026 10:30;NEW\n2;Shopping;Buy milk;Ewa;29.05.2026 11:00;DONE");

        List<Task> tasks = service.readTasks(csv.toString());

        assertEquals(2, tasks.size());
        assertEquals("Shopping", tasks.get(1).getTitle());
        assertEquals(TaskStatus.DONE, tasks.get(1).getStatus());
    }

    @Test
    public void shouldReturnEmptyListForHeaderOnlyCsv() throws IOException {
        Path csv = tempDir.resolve("tasks.csv");
        write(csv, HEADER);

        List<Task> tasks = service.readTasks(csv.toString());

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void shouldThrowCsvExceptionForInvalidRowFormat() throws IOException {
        Path csv = tempDir.resolve("tasks.csv");
        write(csv, HEADER + "\n1;Clean;Clean room");

        assertThrows(CsvException.class, () -> service.readTasks(csv.toString()));
    }

    @Test
    public void shouldThrowCsvExceptionForNullPath() {
        assertThrows(CsvException.class, () -> service.readTasks(null));
    }

    @Test
    public void shouldReturnEmptyListForNonExistentFile() {
        String nonExistent = tempDir.resolve("missing.csv").toString();
        List<Task> tasks = service.readTasks(nonExistent);
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void shouldReadAllTaskStatuses() throws IOException {
        Path csv = tempDir.resolve("tasks.csv");
        write(csv, HEADER
                + "\n1;A;desc;Adam;01.06.2026 08:00;NEW"
                + "\n2;B;desc;Adam;01.06.2026 09:00;IN_PROGRESS"
                + "\n3;C;desc;Adam;01.06.2026 10:00;DONE");

        List<Task> tasks = service.readTasks(csv.toString());

        assertEquals(TaskStatus.NEW, tasks.get(0).getStatus());
        assertEquals(TaskStatus.IN_PROGRESS, tasks.get(1).getStatus());
        assertEquals(TaskStatus.DONE, tasks.get(2).getStatus());
    }

    private static void write(Path path, String content) throws IOException {
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }
}
