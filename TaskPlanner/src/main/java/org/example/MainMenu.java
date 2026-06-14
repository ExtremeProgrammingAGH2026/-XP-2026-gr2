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
            System.out.println("4. Save config");
            System.out.println("5. Exit");
            System.out.print("Choice: ");

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
                    try {
                        String path = "config.json";
                        configurationSaveService.saveConfiguration(appConfiguration, path);
                        System.out.println("Configuration saved to " + path);
                    } catch (IOException e) {
                        System.out.println("Failed to save configuration: " + e.getMessage());
                    }
                    break;
                case "5":
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
}
