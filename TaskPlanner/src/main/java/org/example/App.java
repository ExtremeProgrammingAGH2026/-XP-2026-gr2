package org.example;

import org.example.registration.RegistrationService;
import org.example.registration.RegistrationValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class App {

    private static final String CONFIG_FILE = "data/config.json";

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("data"));

        AppConfiguration config = normalize(loadConfiguration());
        DateTimeFormats.init(config);
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
        CreateTaskUI createTaskUI = new CreateTaskUI(taskSaveService, authService, tasksFile);
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
            return new AppConfiguration();
        }
    }

    /**
     * Replaces missing or nonsensical config values with safe defaults, so a
     * hand-edited or partial config.json (e.g. minPasswordLength 0, an unknown
     * time zone, or a garbage date pattern) can never crash the app.
     */
    static AppConfiguration normalize(AppConfiguration config) {
        if (config == null) {
            config = new AppConfiguration();
        }
        if (isBlank(config.getUsersFilePath())) {
            config.setUsersFilePath("data/users.csv");
        }
        if (isBlank(config.getTasksFilePath())) {
            config.setTasksFilePath("data/tasks.csv");
        }
        if (config.getMaxLoginAttempts() < 1) {
            config.setMaxLoginAttempts(3);
        }
        if (config.getMinPasswordLength() < 1) {
            config.setMinPasswordLength(8);
        }
        if (!isValidZone(config.getTimeZoneName())) {
            config.setTimeZoneName("Europe/Warsaw");
        }
        if (!isValidPattern(config.getDateTimeFormat())) {
            config.setDateTimeFormat("dd.MM.yyyy HH:mm");
        }
        return config;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static boolean isValidZone(String zoneName) {
        if (zoneName == null) {
            return false;
        }
        try {
            ZoneId.of(zoneName);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private static boolean isValidPattern(String pattern) {
        if (pattern == null) {
            return false;
        }
        try {
            DateTimeFormatter.ofPattern(pattern);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
