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

import static org.junit.jupiter.api.Assertions.*;

public class MainMenuTest {

    @TempDir
    Path tempDir;

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream output;

    private Path usersFile;
    private Path tasksFile;
    private User currentUser;
    private MainMenu menu;

    @BeforeEach
    public void setUp() throws IOException {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        usersFile = tempDir.resolve("users.csv");
        tasksFile = tempDir.resolve("tasks.csv");
        currentUser = new User("1", "alice@example.com", "Alice", "secret");

        write(usersFile, "1;alice@example.com;Alice;secret\n2;bob@example.com;Bob;pass");

        AuthService authService = new AuthService(usersFile.toString());
        TaskReadService taskReadService = new TaskReadService();
        TaskPrintService taskPrintService = new TaskPrintService(new TaskFilterService());
        OtherUsersTasksUI otherUsersTasksUI = new OtherUsersTasksUI(
                authService, taskReadService, taskPrintService, tasksFile.toString());
        CreateTaskUI createTaskUI = new CreateTaskUI(new TaskSaveService(), tasksFile.toString());

        AppConfiguration config = new AppConfiguration();
        config.setUsersFilePath(usersFile.toString());
        config.setTasksFilePath(tasksFile.toString());
        config.setMaxLoginAttempts(3);
        config.setMinPasswordLength(8);
        config.setTimeZoneName("Europe/Warsaw");
        config.setDateTimeFormat("dd.MM.yyyy HH:mm");

        menu = new MainMenu(taskReadService, taskPrintService, otherUsersTasksUI, createTaskUI, tasksFile.toString(), config);
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void shouldDisplayMenuOptions() {
        Scanner scanner = new Scanner("8\n");
        menu.run(scanner, currentUser);

        String out = output.toString();
        assertTrue(out.contains("My tasks"));
        assertTrue(out.contains("Other users' tasks"));
        assertTrue(out.contains("Create task"));
        assertTrue(out.contains("Change task status"));
        assertTrue(out.contains("Show config"));
        assertTrue(out.contains("Edit config"));
        assertTrue(out.contains("Save config"));
        assertTrue(out.contains("Exit"));
    }

    @Test
    public void shouldExitOnChoice8() {
        Scanner scanner = new Scanner("8\n");
        menu.run(scanner, currentUser);
        assertTrue(output.toString().contains("Exit") || !output.toString().isEmpty());
    }

    @Test
    public void shouldShowMyTasksOnChoice1() throws IOException {
        write(tasksFile, "id;title;description;owner;startDate;status\n"
                + "1;Clean;Clean room;Alice;01.06.2026 10:00;NEW");

        Scanner scanner = new Scanner("1\nn\n8\n");
        menu.run(scanner, currentUser);

        assertTrue(output.toString().contains("Clean"));
    }

    @Test
    public void shouldShowNoTasksWhenFileDoesNotExist() {
        Scanner scanner = new Scanner("1\nn\n8\n");
        menu.run(scanner, currentUser);

        assertTrue(output.toString().contains("No tasks available"));
    }

    @Test
    public void shouldShowMyTasksFilteredByDateRange() throws IOException {
        write(tasksFile, "id;title;description;owner;startDate;status\n"
                + "1;Morning;Desc;Alice;01.06.2026 08:00;NEW\n"
                + "2;Afternoon;Desc;Alice;01.06.2026 14:00;NEW\n"
                + "3;Evening;Desc;Alice;01.06.2026 20:00;NEW");

        Scanner scanner = new Scanner("1\ny\n01.06.2026 07:00\n01.06.2026 15:00\n8\n");
        menu.run(scanner, currentUser);

        String out = output.toString();
        assertTrue(out.contains("Morning"));
        assertTrue(out.contains("Afternoon"));
        assertFalse(out.contains("Evening"));
    }

    @Test
    public void shouldShowOtherUsersTasksOnChoice2() throws IOException {
        write(tasksFile, "id;title;description;owner;startDate;status\n"
                + "1;Shopping;Buy milk;Bob;01.06.2026 11:00;NEW");

        Scanner scanner = new Scanner("2\n1\n8\n");
        menu.run(scanner, currentUser);

        assertTrue(output.toString().contains("Bob"));
    }

    @Test
    public void shouldHandleInvalidChoiceAndRetry() {
        Scanner scanner = new Scanner("99\n8\n");
        menu.run(scanner, currentUser);

        assertTrue(output.toString().contains("Invalid choice"));
    }

    @Test
    public void shouldChangeTaskStatus() throws IOException {
        write(tasksFile, "id;title;description;owner;startDate;status\n"
                + "1;Clean;Clean room;Alice;01.06.2026 10:00;NEW");

        Scanner scanner = new Scanner("4\n1\n2\n8\n");
        menu.run(scanner, currentUser);

        String out = output.toString();
        assertTrue(out.contains("Status changed to IN_PROGRESS"));

        String csv = Files.readString(tasksFile);
        assertTrue(csv.contains("IN_PROGRESS"));
    }

    private static void write(Path path, String content) throws IOException {
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }
}
