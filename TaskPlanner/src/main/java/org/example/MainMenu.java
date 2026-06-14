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
    }

    public void run(Scanner scanner, User currentUser) {
        while (true) {
            System.out.println("\n=== Menu ===");
            System.out.println("1. My tasks");
            System.out.println("2. Other users' tasks");
            System.out.println("3. Create task");
            System.out.println("4. Show config");
            System.out.println("5. Save config");
            System.out.println("6. Load Json");
            System.out.println("7. Exit");
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
                    try {
                        ConfigurationLoadService loadService = new ConfigurationLoadService();
                        AppConfiguration loadedConfig = loadService.loadConfiguration(path);
                        appConfiguration.setUsersFilePath(loadedConfig.getUsersFilePath());
                        appConfiguration.setTasksFilePath(loadedConfig.getTasksFilePath());
                        appConfiguration.setMaxLoginAttempts(loadedConfig.getMaxLoginAttempts());
                        appConfiguration.setMinPasswordLength(loadedConfig.getMinPasswordLength());
                        appConfiguration.setTimeZoneName(loadedConfig.getTimeZoneName());
                        appConfiguration.setDateTimeFormat(loadedConfig.getDateTimeFormat());
                        System.out.println("Configuration loaded from " + path);
                    } catch (IOException e) {
                        System.out.println("Failed to load configuration: " + e.getMessage());
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

    private String nullToPlaceholder(String s) { // helper method to print "(none)" for null values
        return s == null ? "(none)" : s;
    }
}
