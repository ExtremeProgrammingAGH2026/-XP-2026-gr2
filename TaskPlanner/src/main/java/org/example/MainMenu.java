package org.example;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class MainMenu {

    private final TaskReadService taskReadService;
    private final TaskPrintService taskPrintService;
    private final TaskSaveService taskSaveService;
    private final TaskEditService taskEditService;
    private final OtherUsersTasksUI otherUsersTasksUI;
    private final CreateTaskUI createTaskUI;
    private final String tasksFilePath;
    private final ConfigurationSaveService configurationSaveService;
    private final AppConfiguration appConfiguration;

    public MainMenu(TaskReadService taskReadService, TaskPrintService taskPrintService,
                    OtherUsersTasksUI otherUsersTasksUI, CreateTaskUI createTaskUI,
                    String tasksFilePath, AppConfiguration appConfiguration) {
        this(taskReadService, taskPrintService, new TaskSaveService(),
                new TaskEditService(new TaskStatusService()),
                otherUsersTasksUI, createTaskUI, tasksFilePath, appConfiguration);
    }

    public MainMenu(TaskReadService taskReadService, TaskPrintService taskPrintService,
                    TaskSaveService taskSaveService, TaskEditService taskEditService,
                    OtherUsersTasksUI otherUsersTasksUI, CreateTaskUI createTaskUI,
                    String tasksFilePath, AppConfiguration appConfiguration) {
        this.taskReadService = taskReadService;
        this.taskPrintService = taskPrintService;
        this.taskSaveService = taskSaveService;
        this.taskEditService = taskEditService;
        this.otherUsersTasksUI = otherUsersTasksUI;
        this.createTaskUI = createTaskUI;
        this.tasksFilePath = tasksFilePath;
        this.configurationSaveService = new ConfigurationSaveService();
        this.appConfiguration = appConfiguration;
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
            String configPath = "data/config.json";
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    showMyTasks(currentUser);
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

    private void showMyTasks(User currentUser) {
        List<Task> tasks = taskReadService.readTasks(tasksFilePath);
        taskPrintService.printTasksByOwner(tasks, currentUser.getName());
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
            case "3":
                try {
                    appConfiguration.setMaxLoginAttempts(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number.");
                    return;
                }
                break;
            case "4":
                try {
                    appConfiguration.setMinPasswordLength(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number.");
                    return;
                }
                break;
            case "5":
                appConfiguration.setTimeZoneName(value);
                break;
            case "6":
                appConfiguration.setDateTimeFormat(value);
                break;
            default:
                System.out.println("Invalid field.");
                return;
        }
        System.out.println("Configuration updated. Use 'Save config' to persist changes.");
    }

    private String nullToPlaceholder(String s) {
        return s == null ? "(none)" : s;
    }
}
