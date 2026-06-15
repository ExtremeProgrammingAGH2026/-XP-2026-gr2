package org.example;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class MainMenu {

    private final TaskReadService taskReadService;
    private final TaskPrintService taskPrintService;
    private final TaskSaveService taskSaveService;
    private final TaskEditService taskEditService;
    private final TaskScheduleService taskScheduleService;
    private final OtherUsersTasksUI otherUsersTasksUI;
    private final CreateTaskUI createTaskUI;
    private final String tasksFilePath;
    private final ConfigurationSaveService configurationSaveService;
    private final AppConfiguration appConfiguration;
    private final String configPath;

    public MainMenu(TaskReadService taskReadService, TaskPrintService taskPrintService,
                    OtherUsersTasksUI otherUsersTasksUI, CreateTaskUI createTaskUI,
                    String tasksFilePath, AppConfiguration appConfiguration) {
        this(taskReadService, taskPrintService, otherUsersTasksUI, createTaskUI,
                tasksFilePath, appConfiguration, "data/config.json");
    }

    public MainMenu(TaskReadService taskReadService, TaskPrintService taskPrintService,
                    OtherUsersTasksUI otherUsersTasksUI, CreateTaskUI createTaskUI,
                    String tasksFilePath, AppConfiguration appConfiguration, String configPath) {
        this(taskReadService, taskPrintService, new TaskSaveService(),
                new TaskEditService(new TaskStatusService()),
                otherUsersTasksUI, createTaskUI, tasksFilePath, appConfiguration, configPath);
    }

    public MainMenu(TaskReadService taskReadService, TaskPrintService taskPrintService,
                    TaskSaveService taskSaveService, TaskEditService taskEditService,
                    OtherUsersTasksUI otherUsersTasksUI, CreateTaskUI createTaskUI,
                    String tasksFilePath, AppConfiguration appConfiguration, String configPath) {
        this.taskReadService = taskReadService;
        this.taskPrintService = taskPrintService;
        this.taskSaveService = taskSaveService;
        this.taskEditService = taskEditService;
        ZoneId zone = resolveZone(appConfiguration);
        this.taskScheduleService = new TaskScheduleService(zone);
        this.otherUsersTasksUI = otherUsersTasksUI;
        this.createTaskUI = createTaskUI;
        this.tasksFilePath = tasksFilePath;
        this.configurationSaveService = new ConfigurationSaveService();
        this.appConfiguration = appConfiguration;
        this.configPath = configPath;
    }

    public void run(Scanner scanner, User currentUser) {
        while (true) {
            System.out.println("\n=== Menu ===");
            System.out.println("1. My tasks");
            System.out.println("2. Other users' tasks");
            System.out.println("3. Create task");
            System.out.println("4. Change task status");
            System.out.println("5. Show config");
            System.out.println("6. Edit config");
            System.out.println("7. Save config");
            System.out.println("8. Exit");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    showMyTasks(scanner, currentUser);
                    break;
                case "2":
                    otherUsersTasksUI.show(scanner, currentUser);
                    break;
                case "3":
                    createTaskUI.createTask(scanner, currentUser);
                    break;
                case "4":
                    changeTaskStatus(scanner, currentUser);
                    break;
                case "5":
                    showConfig();
                    break;
                case "6":
                    editConfig(scanner);
                    break;
                case "7":
                    try {
                        configurationSaveService.saveConfiguration(appConfiguration, configPath);
                        System.out.println("Configuration saved to " + configPath);
                    } catch (IOException e) {
                        System.out.println("Failed to save configuration: " + e.getMessage());
                    }
                    break;
                case "8":
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void showMyTasks(Scanner scanner, User currentUser) {
        List<Task> tasks = taskReadService.readTasks(tasksFilePath);
        System.out.print("Filter by date? (y/n): ");
        String answer = scanner.nextLine().trim();
        if (answer.equalsIgnoreCase("y")) {
            showMyTasksByDay(scanner, tasks, currentUser);
        } else {
            taskPrintService.printTasksByOwner(tasks, currentUser.getName());
        }
    }

    private void showMyTasksByDay(Scanner scanner, List<Task> tasks, User currentUser) {
        LocalDate day = promptDayChoice(scanner);
        if (day == null) {
            return;
        }

        List<Task> dayTasks = taskScheduleService.getTasksForDay(tasks, day).stream()
                .filter(t -> t.getOwner().equals(currentUser.getName()))
                .collect(java.util.stream.Collectors.toList());
        taskPrintService.printTasksSortedByDate(dayTasks);
    }

    private LocalDate promptDayChoice(Scanner scanner) {
        while (true) {
            System.out.println("Date filter:");
            System.out.println("1. Today");
            System.out.println("2. Other day");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();
            if ("1".equals(choice)) {
                return LocalDate.now(resolveZone(appConfiguration));
            }
            if ("2".equals(choice)) {
                return promptDay(scanner);
            }
            System.out.println("Invalid choice. Try again.");
        }
    }

    private LocalDate promptDay(Scanner scanner) {
        while (true) {
            System.out.print("Day (dd.MM.yyyy): ");
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format.");
            }
        }
    }

    private void changeTaskStatus(Scanner scanner, User currentUser) {
        List<Task> allTasks = taskReadService.readTasks(tasksFilePath);
        List<Task> myTasks = allTasks.stream()
                .filter(t -> t.getOwner().equals(currentUser.getName()))
                .collect(java.util.stream.Collectors.toList());

        if (myTasks.isEmpty()) {
            System.out.println("No tasks available.");
            return;
        }

        System.out.println("=== Change Task Status ===");
        for (int i = 0; i < myTasks.size(); i++) {
            Task t = myTasks.get(i);
            System.out.println((i + 1) + ". [" + t.getStatus() + "] " + t.getTitle());
        }

        System.out.print("Select task (1-" + myTasks.size() + "): ");
        String taskInput = scanner.nextLine().trim();
        int taskIndex;
        try {
            taskIndex = Integer.parseInt(taskInput) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice.");
            return;
        }
        if (taskIndex < 0 || taskIndex >= myTasks.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Task selected = myTasks.get(taskIndex);
        TaskStatus[] statuses = TaskStatus.values();
        System.out.println("Select new status:");
        for (int i = 0; i < statuses.length; i++) {
            System.out.println((i + 1) + ". " + statuses[i]);
        }

        System.out.print("Choice: ");
        String statusInput = scanner.nextLine().trim();
        int statusIndex;
        try {
            statusIndex = Integer.parseInt(statusInput) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice.");
            return;
        }
        if (statusIndex < 0 || statusIndex >= statuses.length) {
            System.out.println("Invalid choice.");
            return;
        }

        taskEditService.editStatus(selected, statuses[statusIndex]);
        taskSaveService.saveTasks(allTasks, tasksFilePath, false);
        System.out.println("Status changed to " + selected.getStatus() + ".");
    }

    private void showConfig() {
        System.out.println("Current configuration:");
        System.out.println("1. usersFilePath: " + nullToPlaceholder(appConfiguration.getUsersFilePath()));
        System.out.println("2. tasksFilePath: " + nullToPlaceholder(appConfiguration.getTasksFilePath()));
        System.out.println("3. maxLoginAttempts: " + appConfiguration.getMaxLoginAttempts());
        System.out.println("4. minPasswordLength: " + appConfiguration.getMinPasswordLength());
        System.out.println("5. timeZoneName: " + nullToPlaceholder(appConfiguration.getTimeZoneName()));
        System.out.println("6. dateTimeFormat: " + nullToPlaceholder(appConfiguration.getDateTimeFormat()));
    }

    private void editConfig(Scanner scanner) {
        showConfig();
        System.out.print("Select field to edit (1-6): ");
        String field = scanner.nextLine().trim();
        System.out.print("New value: ");
        String value = scanner.nextLine().trim();

        switch (field) {
            case "1":
                appConfiguration.setUsersFilePath(value);
                break;
            case "2":
                appConfiguration.setTasksFilePath(value);
                break;
            case "3": {
                Integer attempts = parsePositiveInt(value, "maximum login attempts");
                if (attempts == null) {
                    return;
                }
                appConfiguration.setMaxLoginAttempts(attempts);
                break;
            }
            case "4": {
                Integer minLength = parsePositiveInt(value, "minimum password length");
                if (minLength == null) {
                    return;
                }
                appConfiguration.setMinPasswordLength(minLength);
                break;
            }
            case "5":
                try {
                    ZoneId.of(value);
                    appConfiguration.setTimeZoneName(value);
                } catch (Exception e) {
                    System.out.println("Invalid timezone. Example: Europe/Warsaw, UTC, US/Eastern");
                    return;
                }
                break;
            case "6":
                if (!isValidDatePattern(value)) {
                    System.out.println("Invalid date format pattern. Must include year, month, day, hour and minute.");
                    System.out.println("Example: dd.MM.yyyy HH:mm");
                    return;
                }
                appConfiguration.setDateTimeFormat(value);
                break;
            default:
                System.out.println("Invalid field.");
                return;
        }
        DateTimeFormats.init(appConfiguration);
        try {
            configurationSaveService.saveConfiguration(appConfiguration, configPath);
            System.out.println("Configuration updated and saved to " + configPath);
        } catch (IOException e) {
            System.out.println("Configuration updated but failed to save: " + e.getMessage());
        }
    }

    private Integer parsePositiveInt(String value, String label) {
        int parsed;
        try {
            parsed = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return null;
        }
        if (parsed < 1) {
            System.out.println("Invalid value: " + label + " must be at least 1.");
            return null;
        }
        return parsed;
    }

    private boolean isValidDatePattern(String pattern) {
        try {
            DateTimeFormatter testFormatter = DateTimeFormatter.ofPattern(pattern)
                    .withZone(DateTimeFormats.getZone());
            java.time.Instant sample = java.time.Instant.parse("2026-06-15T10:30:00Z");
            String formatted = testFormatter.format(sample);
            ZonedDateTime.parse(formatted, testFormatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String nullToPlaceholder(String s) {
        return s == null ? "(none)" : s;
    }

    private static ZoneId resolveZone(AppConfiguration appConfiguration) {
        if (appConfiguration != null && appConfiguration.getTimeZoneName() != null
                && !appConfiguration.getTimeZoneName().isBlank()) {
            return ZoneId.of(appConfiguration.getTimeZoneName());
        }
        return DateTimeFormats.getZone();
    }
}
