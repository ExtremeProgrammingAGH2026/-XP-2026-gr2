package org.example;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class MainMenu {

    private final TaskReadService taskReadService;
    private final TaskPrintService taskPrintService;
    private final OtherUsersTasksUI otherUsersTasksUI;
    private final CreateTaskUI createTaskUI;
    private final String tasksFilePath;
    private final ConfigurationSaveService configurationSaveService;
    private final AppConfiguration appConfiguration;

    public MainMenu(TaskReadService taskReadService, TaskPrintService taskPrintService,
                    OtherUsersTasksUI otherUsersTasksUI, CreateTaskUI createTaskUI,
                    String tasksFilePath, AppConfiguration appConfiguration) {
        this.taskReadService = taskReadService;
        this.taskPrintService = taskPrintService;
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
            System.out.println("4. Show config");
            System.out.println("5. Edit config");
            System.out.println("6. Save config");
            System.out.println("7. Exit");
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
                    showConfig();
                    break;
                case "5":
                    editConfig(scanner);
                    break;
                case "6":
                    try {
                        configurationSaveService.saveConfiguration(appConfiguration, configPath);
                        System.out.println("Configuration saved to " + configPath);
                    } catch (IOException e) {
                        System.out.println("Failed to save configuration: " + e.getMessage());
                    }
                    break;
                case "7":
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
