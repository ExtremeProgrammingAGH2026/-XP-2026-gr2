package org.example;

import org.example.registration.RegistrationService;
import org.example.registration.RegistrationValidator;
import org.example.recurring.RecurringTaskExpander;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Per-scenario shared state injected into Cucumber step definitions via Picocontainer.
 * Placed in org.example to access package-private TaskSaveService.
 */
public class TestContext {

    public static final ZoneId ZONE = ZoneId.of("Europe/Warsaw");

    public final Path tempDir;
    public final Path usersFile;
    public final Path tasksFile;

    public final RegistrationService registrationService;
    public final AuthService authService;
    public final SessionService sessionService;
    public final TaskSaveService taskSaveService;
    public final TaskReadService taskReadService;
    public final TaskListService taskListService;
    public final TaskFilterService taskFilterService;
    public final TaskStatusService taskStatusService;
    public final TaskEditService taskEditService;
    public final TaskConflictService taskConflictService;
    public final TaskOverlapService taskOverlapService;
    public final TaskDateFilterService taskDateFilterService;
    public final RecurringTaskExpander recurringTaskExpander;

    public User lastRegisteredUser;
    public User lastLoggedInUser;
    public Exception lastException;
    public Task lastTask;
    public List<Task> taskPool = new ArrayList<>();
    public List<Task> lastTaskList = new ArrayList<>();

    public TestContext() {
        try {
            tempDir = Files.createTempDirectory("cucumber-task-planner-");
            usersFile = tempDir.resolve("users.csv");
            tasksFile = tempDir.resolve("tasks.csv");
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp directory for Cucumber scenario", e);
        }

        registrationService = new RegistrationService(usersFile.toString(), new RegistrationValidator());
        authService = new AuthService(usersFile.toString());
        sessionService = new SessionService();
        taskSaveService = new TaskSaveService();
        taskReadService = new TaskReadService();
        taskFilterService = new TaskFilterService();
        taskListService = new TaskListService(taskFilterService);
        taskStatusService = new TaskStatusService();
        taskEditService = new TaskEditService(taskStatusService);
        taskOverlapService = new TaskOverlapService();
        taskConflictService = new TaskConflictService(taskOverlapService);
        taskDateFilterService = new TaskDateFilterService(ZONE);
        recurringTaskExpander = new RecurringTaskExpander(ZONE);
    }

    /**
     * Converts a "yyyy-MM-dd HH:mm" string to an Instant in the scenario time zone.
     */
    public Instant parseDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                .atZone(ZONE)
                .toInstant();
    }

    /**
     * Builds an Instant from explicit components in the scenario time zone.
     */
    public Instant at(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute).atZone(ZONE).toInstant();
    }

    /**
     * Saves all tasks in taskPool to tasksFile (overwrites existing file).
     */
    public void persistTaskPool() {
        if (!taskPool.isEmpty()) {
            taskSaveService.saveTasks(taskPool, tasksFile.toString(), false);
        }
    }

    /**
     * Cleans up temporary files created for this scenario.
     */
    public void cleanup() {
        try {
            if (Files.exists(tempDir)) {
                Files.walk(tempDir)
                        .sorted(Comparator.reverseOrder())
                        .forEach(p -> {
                            try {
                                Files.deleteIfExists(p);
                            } catch (IOException ignored) {
                            }
                        });
            }
        } catch (IOException ignored) {
        }
    }
}