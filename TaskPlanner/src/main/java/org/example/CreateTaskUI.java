package org.example;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.UUID;

public class CreateTaskUI {

    private static final ZoneId WARSAW = ZoneId.of("Europe/Warsaw");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(WARSAW);

    private final TaskSaveService taskSaveService;
    private final String tasksFilePath;

    public CreateTaskUI(TaskSaveService taskSaveService, String tasksFilePath) {
        this.taskSaveService = taskSaveService;
        this.tasksFilePath = tasksFilePath;
    }

    public Task createTask(Scanner scanner, User currentUser) {
        System.out.println("=== New Task ===");

        System.out.print("Title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Description: ");
        String description = scanner.nextLine().trim();

        ZonedDateTime startDate = promptDate(scanner, "Start date (dd.MM.yyyy HH:mm): ");
        ZonedDateTime endDate = promptDate(scanner, "End date (dd.MM.yyyy HH:mm): ");

        Task task = new Task(
                UUID.randomUUID().toString(),
                title,
                description,
                currentUser.getName(),
                startDate.toInstant(),
                endDate.toInstant()
        );

        taskSaveService.saveTask(task, tasksFilePath, true);
        System.out.println("Task created.");
        return task;
    }

    private ZonedDateTime promptDate(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return ZonedDateTime.parse(input, FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format. Use dd.MM.yyyy HH:mm");
            }
        }
    }
}
