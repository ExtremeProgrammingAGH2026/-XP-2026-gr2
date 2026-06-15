package org.example;

import org.example.registration.RegistrationService;
import org.example.registration.RegistrationValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class App {

    private static final String CONFIG_FILE = "data/config.json";

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("data"));

        AppConfiguration config = loadConfiguration();
        String usersFile = config.getUsersFilePath();
        String tasksFile = config.getTasksFilePath();

        if (!Files.exists(Path.of(usersFile))) {
            Files.createFile(Path.of(usersFile));
        }

        Scanner scanner = new Scanner(System.in);

        AuthService authService = new AuthService(usersFile);
        LoginUI loginUI = new LoginUI(authService, config.getMaxLoginAttempts());
        RegistrationValidator registrationValidator = new RegistrationValidator(config.getMinPasswordLength());
        RegistrationUI registrationUI = new RegistrationUI(new RegistrationService(usersFile, registrationValidator));
        StartScreenUI startScreen = new StartScreenUI(loginUI, registrationUI);

        TaskSaveService taskSaveService = new TaskSaveService();
        TaskReadService taskReadService = new TaskReadService();
        TaskFilterService taskFilterService = new TaskFilterService();
        TaskPrintService taskPrintService = new TaskPrintService(taskFilterService);
        OtherUsersTasksUI otherUsersTasksUI = new OtherUsersTasksUI(authService, taskReadService, taskPrintService, tasksFile);
        CreateTaskUI createTaskUI = new CreateTaskUI(taskSaveService, tasksFile);
        MainMenu mainMenu = new MainMenu(taskReadService, taskPrintService, otherUsersTasksUI, createTaskUI, tasksFile, config);

        User user = startScreen.run(scanner);
        if (user != null) {
            mainMenu.run(scanner, user);
        }
    }

    private static AppConfiguration loadConfiguration() {
        try {
            return new ConfigurationLoadService().loadConfiguration(CONFIG_FILE);
        } catch (IOException e) {
            AppConfiguration defaults = new AppConfiguration();
            defaults.setUsersFilePath("data/users.csv");
            defaults.setTasksFilePath("data/tasks.csv");
            defaults.setMaxLoginAttempts(3);
            defaults.setMinPasswordLength(8);
            defaults.setTimeZoneName("Europe/Warsaw");
            defaults.setDateTimeFormat("dd.MM.yyyy HH:mm");
            return defaults;
        }
    }
}
