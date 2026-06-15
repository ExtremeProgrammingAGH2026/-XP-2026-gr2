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

public class CreateRecurringTaskUI {

    private final TaskSaveService taskSaveService;
    private final TaskReadService taskReadService;
    private final TaskConflictService taskConflictService;
    private final TaskConflictWarningService taskConflictWarningService;
    private final TaskScheduleService taskScheduleService;
    private final String tasksFilePath;

    public CreateRecurringTaskUI(TaskSaveService taskSaveService, String tasksFilePath) {
        this(taskSaveService, new TaskReadService(), new TaskConflictService(),
                new TaskConflictWarningService(),
                new TaskScheduleService(DateTimeFormats.getZone()), tasksFilePath);
    }

    public CreateRecurringTaskUI(TaskSaveService taskSaveService, TaskReadService taskReadService,
                                 TaskConflictService taskConflictService,
                                 TaskConflictWarningService taskConflictWarningService,
                                 TaskScheduleService taskScheduleService,
                                 String tasksFilePath) {
        this.taskSaveService = taskSaveService;
        this.taskReadService = taskReadService;
        this.taskConflictService = taskConflictService;
        this.taskConflictWarningService = taskConflictWarningService;
        this.taskScheduleService = taskScheduleService;
        this.tasksFilePath = tasksFilePath;
    }

    public Task createRecurringTask(Scanner scanner, User currentUser) {
        System.out.println("=== New Recurring Task ===");

        System.out.print("Title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Description: ");
        String description = scanner.nextLine().trim();

        ZonedDateTime startDate = promptDate(scanner, "Start date (" + DateTimeFormats.getPattern() + "): ");
        ZonedDateTime endDate = promptEndDate(scanner, startDate);
        RecurrencePattern recurrencePattern = promptRecurrencePattern(scanner);
        Instant recurrenceEndDate = promptOptionalDate(scanner,
                "Recurrence end date (blank = no limit, " + DateTimeFormats.getPattern() + "): ");

        RecurringTask task = new RecurringTask(
                UUID.randomUUID().toString(),
                title,
                description,
                currentUser.getName(),
                startDate.toInstant(),
                endDate.toInstant(),
                recurrencePattern,
                recurrenceEndDate
        );

        List<Task> existingTasks = taskReadService.readTasks(tasksFilePath);
        if (hasConflict(task, existingTasks)) {
            printRecurringConflictWarnings(task, existingTasks);
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

    private boolean hasConflict(RecurringTask recurringTask, List<Task> existingTasks) {
        Instant from = recurringTask.getStartDate();
        Instant to = recurringTask.getRecurrenceEndDate();
        if (to == null) {
            to = from.plusSeconds(60L * 60L * 24L * 365L);
        }

        List<Task> occurrences = taskScheduleService.expandForWindow(List.of(recurringTask), from, to);
        for (Task occurrence : occurrences) {
            if (taskConflictService.hasConflict(occurrence, existingTasks)) {
                return true;
            }
        }
        return false;
    }

    private void printRecurringConflictWarnings(RecurringTask recurringTask, List<Task> existingTasks) {
        Instant from = recurringTask.getStartDate();
        Instant to = recurringTask.getRecurrenceEndDate();
        if (to == null) {
            to = from.plusSeconds(60L * 60L * 24L * 365L);
        }

        List<Task> occurrences = taskScheduleService.expandForWindow(List.of(recurringTask), from, to);
        for (Task occurrence : occurrences) {
            if (taskConflictService.hasConflict(occurrence, existingTasks)) {
                taskConflictWarningService.printConflictWarning(occurrence, existingTasks);
            }
        }
    }

    private ZonedDateTime promptEndDate(Scanner scanner, ZonedDateTime startDate) {
        while (true) {
            ZonedDateTime endDate = promptDate(scanner, "End date (" + DateTimeFormats.getPattern() + "): ");
            if (endDate.isBefore(startDate)) {
                System.out.println("End date cannot be before start date.");
                continue;
            }
            return endDate;
        }
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

    private RecurrencePattern promptRecurrencePattern(Scanner scanner) {
        while (true) {
            System.out.println("Select recurrence pattern:");
            System.out.println("1. DAILY");
            System.out.println("2. WEEKLY");
            System.out.println("3. BIWEEKLY");
            System.out.println("4. MONTHLY");
            System.out.print("Choice: ");
            switch (scanner.nextLine().trim()) {
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
}