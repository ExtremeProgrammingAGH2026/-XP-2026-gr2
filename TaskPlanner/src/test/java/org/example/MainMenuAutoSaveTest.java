package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MainMenuAutoSaveTest {

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
        CreateTaskUI create = new CreateTaskUI(new TaskSaveService(), tasksFile);
        return new MainMenu(read, print, other, create, tasksFile, config, configPath);
    }

    @Test
    public void editingConfigPersistsImmediatelyWithoutSeparateSave() throws IOException {
        AppConfiguration config = defaultConfig();
        String configPath = tempDir.resolve("config.json").toString();
        MainMenu menu = menuWith(config, configPath);

        // 6 = Edit config, field 6 = dateTimeFormat, 8 = Exit (no "Save config" step)
        menu.run(new Scanner("6\n6\nyyyy-MM-dd HH:mm\n8\n"), user);

        assertEquals("yyyy-MM-dd HH:mm", config.getDateTimeFormat());
        assertTrue(Files.exists(Path.of(configPath)), "edit should persist config.json immediately");
        assertTrue(Files.readString(Path.of(configPath), StandardCharsets.UTF_8).contains("yyyy-MM-dd HH:mm"));
    }
}
