package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MainMenuValidationTest {

    @TempDir
    Path tempDir;

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream output;
    private String tasksFile;
    private User user;

    @BeforeEach
    public void setUp() {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        tasksFile = tempDir.resolve("tasks.csv").toString();
        user = new User("1", "alice@example.com", "Alice", "secret");
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
        DateTimeFormats.resetToDefaults();
    }

    private AppConfiguration defaultConfig() {
        AppConfiguration c = new AppConfiguration();
        c.setUsersFilePath("data/users.csv");
        c.setTasksFilePath(tasksFile);
        c.setMaxLoginAttempts(3);
        c.setMinPasswordLength(8);
        c.setTimeZoneName("Europe/Warsaw");
        c.setDateTimeFormat("dd.MM.yyyy HH:mm");
        return c;
    }

    private MainMenu menuWith(AppConfiguration config, String configPath) {
        TaskReadService read = new TaskReadService();
        TaskPrintService print = new TaskPrintService(new TaskFilterService());
        AuthService auth = new AuthService(tempDir.resolve("users.csv").toString());
        OtherUsersTasksUI other = new OtherUsersTasksUI(auth, read, print, tasksFile);
        CreateTaskUI create = new CreateTaskUI(
                new TaskSaveService(),
                auth,
                tasksFile
        );
        return new MainMenu(read, print, other, create, tasksFile, config, configPath);
    }

    @Test
    public void minPasswordLengthBelowOneIsRejected() {
        AppConfiguration config = defaultConfig();
        String configPath = tempDir.resolve("config.json").toString();
        MainMenu menu = menuWith(config, configPath);

        // 6 = Edit config, field 4 = minPasswordLength, value 0, 9 = Exit
        menu.run(new Scanner("6\n4\n0\n9\n"), user);

        assertEquals(8, config.getMinPasswordLength(), "0 is nonsensical and must be rejected");
        assertFalse(Files.exists(Path.of(configPath)), "rejected edit must not persist");
        assertTrue(output.toString().contains("at least 1"));
    }

    @Test
    public void maxLoginAttemptsBelowOneIsRejected() {
        AppConfiguration config = defaultConfig();
        String configPath = tempDir.resolve("config2.json").toString();
        MainMenu menu = menuWith(config, configPath);

        // 6 = Edit config, field 3 = maxLoginAttempts, value 0, 9 = Exit
        menu.run(new Scanner("6\n3\n0\n9\n"), user);

        assertEquals(3, config.getMaxLoginAttempts());
        assertTrue(output.toString().contains("at least 1"));
    }
}
