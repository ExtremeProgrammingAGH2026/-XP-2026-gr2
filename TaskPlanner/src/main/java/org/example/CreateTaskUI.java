package org.example;

import org.example.recurring.RecurringTask;
import org.example.recurring.RecurrencePattern;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class CreateTaskUI {

    private final TaskSaveService taskSaveService;
    private final TaskReadService taskReadService;
    private final TaskConflictService taskConflictService;
    private final TaskConflictWarningService taskConflictWarningService;
    private final String tasksFilePath;

    public CreateTaskUI(TaskSaveService taskSaveService, String tasksFilePath) {
        this(taskSaveService, new TaskReadService(), new TaskConflictService(),
                new TaskConflictWarningService(), tasksFilePath);
    }

    public CreateTaskUI(TaskSaveService taskSaveService, TaskReadService taskReadService,
                        TaskConflictService taskConflictService,
                        TaskConflictWarningService taskConflictWarningService,
                        String tasksFilePath) {
        this.taskSaveService = taskSaveService;
        this.taskReadService = taskReadService;
        this.taskConflictService = taskConflictService;
        this.taskConflictWarningService = taskConflictWarningService;
        this.tasksFilePath = tasksFilePath;
    }

    public Task createTask(Scanner scanner, User currentUser) {
        System.out.println("=== New Task ===");

        System.out.print("Title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Description: ");
        String description = scanner.nextLine().trim();

        ZonedDateTime startDate = promptDate(scanner, "Start date (" + DateTimeFormats.getPattern() + "): ");
        ZonedDateTime endDate = promptEndDate(scanner, startDate);
        boolean recurring = promptYesNo(scanner, "Recurring task? (y/n): ");

        Task task;
        if (recurring) {
            RecurrencePattern recurrencePattern = promptRecurrencePattern(scanner);
            Instant recurrenceEndDate = promptOptionalDate(scanner,
                "Recurrence end date (blank = no limit, " + DateTimeFormats.getPattern() + "): ");
            task = new RecurringTask(
                UUID.randomUUID().toString(),
                title,
                description,
                currentUser.getName(),
                startDate.toInstant(),
                endDate.toInstant(),
                recurrencePattern,
                recurrenceEndDate
            );
        } else {
            task = new Task(
                UUID.randomUUID().toString(),
                title,
                description,
                currentUser.getName(),
                startDate.toInstant(),
                endDate.toInstant()
            );
        }

        List<Task> existingTasks = taskReadService.readTasks(tasksFilePath);
        if (taskConflictService.hasConflict(task, existingTasks)) {
            taskConflictWarningService.printConflictWarning(task, existingTasks);
            System.out.print("Task has conflicts. Create anyway? (y/n): ");
            String answer = scanner.nextLine().trim();
            if (!answer.equalsIgnoreCase("y")) {
                System.out.println("Task creation cancelled.");
                return null;
            }
        }

        taskSaveService.saveTask(task, tasksFilePath, true);
        System.out.println("Task created.");
        return task;
    }

    private ZonedDateTime promptEndDate(Scanner scanner, ZonedDateTime startDate) {
        while (true) {
            ZonedDateTime endDate = promptDate(scanner, "End date (" + DateTimeFormats.getPattern() + "): ");
            if (endIsAfterStart(startDate, endDate)) {
                return endDate;
            }
            System.out.println("End date must be after start date.");
        }
    }

    private static boolean endIsAfterStart(ZonedDateTime startDate, ZonedDateTime endDate) {
        return endDate.isAfter(startDate);
    }

    private ZonedDateTime promptDate(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return ZonedDateTime.parse(input, DateTimeFormats.getFormatter());
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format. Use " + DateTimeFormats.getPattern());
            }
        }
    }

    private boolean promptYesNo(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("y")) {
                return true;
            }
            if (input.equalsIgnoreCase("n")) {
                return false;
            }
            System.out.println("Please answer y or n.");
        }
    }

    private RecurrencePattern promptRecurrencePattern(Scanner scanner) {
        while (true) {
            System.out.println("Select recurrence pattern:");
            System.out.println("1. DAILY");
            System.out.println("2. WEEKLY");
            System.out.println("3. BIWEEKLY");
            System.out.println("4. MONTHLY");
            System.out.print("Choice: ");
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    return RecurrencePattern.DAILY;
                case "2":
                    return RecurrencePattern.WEEKLY;
                case "3":
                    return RecurrencePattern.BIWEEKLY;
                case "4":
                    return RecurrencePattern.MONTHLY;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private Instant promptOptionalDate(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return null;
            }
            try {
                LocalDateTime dateTime = LocalDateTime.parse(input, DateTimeFormats.getFormatter());
                ZoneId zone = DateTimeFormats.getZone();
                return dateTime.atZone(zone).toInstant();
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format. Use " + DateTimeFormats.getPattern());
            }
        }
    }
}
