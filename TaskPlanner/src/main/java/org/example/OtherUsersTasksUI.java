package org.example;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class OtherUsersTasksUI {

    private final AuthService authService;
    private final TaskReadService taskReadService;
    private final TaskPrintService taskPrintService;
    private final String tasksFilePath;

    public OtherUsersTasksUI(AuthService authService, TaskReadService taskReadService,
                             TaskPrintService taskPrintService, String tasksFilePath) {
        this.authService = authService;
        this.taskReadService = taskReadService;
        this.taskPrintService = taskPrintService;
        this.tasksFilePath = tasksFilePath;
    }

    public void show(Scanner scanner, User currentUser) {
        List<User> others = authService.loadUsers().stream()
                .filter(u -> !u.getId().equals(currentUser.getId()))
                .collect(Collectors.toList());

        if (others.isEmpty()) {
            System.out.println("No other users in the system.");
            return;
        }

        System.out.println("=== Other users ===");
        for (int i = 0; i < others.size(); i++) {
            System.out.println((i + 1) + ". " + others.get(i).getName());
        }

        User selected = promptSelection(scanner, others);
        List<Task> tasks = taskReadService.readTasks(tasksFilePath);
        taskPrintService.printTasksByOwner(tasks, selected.getName());
    }

    private User promptSelection(Scanner scanner, List<User> users) {
        while (true) {
            System.out.print("Select user (1-" + users.size() + "): ");
            String input = scanner.nextLine().trim();
            try {
                int index = Integer.parseInt(input) - 1;
                if (index >= 0 && index < users.size()) {
                    return users.get(index);
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Invalid choice. Try again.");
        }
    }
}
