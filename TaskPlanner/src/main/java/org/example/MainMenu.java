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
                    String tasksFilePath) {
        this.taskReadService = taskReadService;
        this.taskPrintService = taskPrintService;
        this.otherUsersTasksUI = otherUsersTasksUI;
        this.createTaskUI = createTaskUI;
        this.tasksFilePath = tasksFilePath;
        // initialize configuration save service and default configuration
        this.configurationSaveService = new ConfigurationSaveService();
        this.appConfiguration = new AppConfiguration();
        this.appConfiguration.setUsersFilePath("data/users.csv");
        this.appConfiguration.setTasksFilePath(tasksFilePath);
        this.appConfiguration.setMaxLoginAttempts(3);
        this.appConfiguration.setMinPasswordLength(8);
        this.appConfiguration.setTimeZoneName("Europe/Warsaw");
        this.appConfiguration.setDateTimeFormat("dd.MM.yyyy HH:mm");

        // Try to load configuration from file on startup; if loading fails, keep defaults
        String configPath = "data/config.json";
        try {
            ConfigurationLoadService loadService = new ConfigurationLoadService();
            AppConfiguration loaded = loadService.loadConfiguration(configPath);
            if (loaded != null) {
                // copy values (allow nulls to overwrite if present in file)
                this.appConfiguration.setUsersFilePath(loaded.getUsersFilePath());
                this.appConfiguration.setTasksFilePath(loaded.getTasksFilePath());
                this.appConfiguration.setMaxLoginAttempts(loaded.getMaxLoginAttempts());
                this.appConfiguration.setMinPasswordLength(loaded.getMinPasswordLength());
                this.appConfiguration.setTimeZoneName(loaded.getTimeZoneName());
                this.appConfiguration.setDateTimeFormat(loaded.getDateTimeFormat());
            }
        } catch (IOException e) {
            // ignore - keep defaults when config file is missing or unreadable
        }
    }

    public void run(Scanner scanner, User currentUser) {
        while (true) {
            System.out.println("\n=== Menu ===");
            System.out.println("1. My tasks");
            System.out.println("2. Other users' tasks");
            System.out.println("3. Create task");
            System.out.println("4. Show config");
            System.out.println("5. Save config");
            System.out.println("6. Exit");
            System.out.print("Choice: ");
            String path = "data/config.json";
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
                    // Print current configuration
                    System.out.println("Current configuration:");
                    System.out.println("usersFilePath: " + nullToPlaceholder(appConfiguration.getUsersFilePath()));
                    System.out.println("tasksFilePath: " + nullToPlaceholder(appConfiguration.getTasksFilePath()));
                    System.out.println("maxLoginAttempts: " + appConfiguration.getMaxLoginAttempts());
                    System.out.println("minPasswordLength: " + appConfiguration.getMinPasswordLength());
                    System.out.println("timeZoneName: " + nullToPlaceholder(appConfiguration.getTimeZoneName()));
                    System.out.println("dateTimeFormat: " + nullToPlaceholder(appConfiguration.getDateTimeFormat()));
                    break;
                case "5":
                    try {
                        configurationSaveService.saveConfiguration(appConfiguration, path);
                        System.out.println("Configuration saved to " + path);
                    } catch (IOException e) {
                        System.out.println("Failed to save configuration: " + e.getMessage());
                    }
                    break;
                case "6":
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

    private String nullToPlaceholder(String s) { // helper method to print "(none)" for null values
        return s == null ? "(none)" : s;
    }
}
