package org.example;

import org.example.registration.RegistrationService;
import org.example.registration.RegistrationValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class App {

    private static final String USERS_FILE = "data/users.csv";
    private static final String TASKS_FILE = "data/tasks.csv";

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("data"));
        if (!Files.exists(Path.of(USERS_FILE))) {
            Files.createFile(Path.of(USERS_FILE));
        }

        Scanner scanner = new Scanner(System.in);

        AuthService authService = new AuthService(USERS_FILE);
        LoginUI loginUI = new LoginUI(authService);
        RegistrationUI registrationUI = new RegistrationUI(new RegistrationService(USERS_FILE, new RegistrationValidator()));
        StartScreenUI startScreen = new StartScreenUI(loginUI, registrationUI);

        TaskSaveService taskSaveService = new TaskSaveService();
        TaskReadService taskReadService = new TaskReadService();
        TaskPrintService taskPrintService = new TaskPrintService();
        OtherUsersTasksUI otherUsersTasksUI = new OtherUsersTasksUI(authService, taskReadService, taskPrintService, TASKS_FILE);
        CreateTaskUI createTaskUI = new CreateTaskUI(taskSaveService, TASKS_FILE);
        MainMenu mainMenu = new MainMenu(taskReadService, taskPrintService, otherUsersTasksUI, createTaskUI, TASKS_FILE);

        User user = startScreen.run(scanner);
        if (user != null) {
            mainMenu.run(scanner, user);
        }
    }
}
