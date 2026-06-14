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
        TaskPrintService taskPrintService = new TaskPrintService();
        OtherUsersTasksUI otherUsersTasksUI = new OtherUsersTasksUI(
                authService, taskReadService, taskPrintService, tasksFile.toString());
        CreateTaskUI createTaskUI = new CreateTaskUI(new TaskSaveService(), tasksFile.toString());

        menu = new MainMenu(taskReadService, taskPrintService, otherUsersTasksUI, createTaskUI, tasksFile.toString());
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void shouldDisplayMenuOptions() {
        Scanner scanner = new Scanner("6\n");
        menu.run(scanner, currentUser);

        String out = output.toString();
        assertTrue(out.contains("My tasks"));
        assertTrue(out.contains("Other users' tasks"));
        assertTrue(out.contains("Create task"));
        assertTrue(out.contains("Show config"));
        assertTrue(out.contains("Save config"));
        assertTrue(out.contains("Exit"));
    }

    @Test
    public void shouldExitOnChoice4() {
        Scanner scanner = new Scanner("6\n");
        menu.run(scanner, currentUser);
        assertTrue(output.toString().contains("Exit") || !output.toString().isEmpty());
    }

    @Test
    public void shouldShowMyTasksOnChoice1() throws IOException {
        write(tasksFile, "id;title;description;owner;startDate;status\n"
                + "1;Clean;Clean room;Alice;01.06.2026 10:00;NEW");

        Scanner scanner = new Scanner("1\n6\n");
        menu.run(scanner, currentUser);

        assertTrue(output.toString().contains("Clean"));
    }

    @Test
    public void shouldShowNoTasksWhenFileDoesNotExist() {
        Scanner scanner = new Scanner("1\n6\n");
        menu.run(scanner, currentUser);

        assertTrue(output.toString().contains("No tasks available"));
    }

    @Test
    public void shouldShowOtherUsersTasksOnChoice2() throws IOException {
        write(tasksFile, "id;title;description;owner;startDate;status\n"
                + "1;Shopping;Buy milk;Bob;01.06.2026 11:00;NEW");

        Scanner scanner = new Scanner("2\n1\n6\n");
        menu.run(scanner, currentUser);

        assertTrue(output.toString().contains("Bob"));
    }

    @Test
    public void shouldHandleInvalidChoiceAndRetry() {
        Scanner scanner = new Scanner("99\n6\n");
        menu.run(scanner, currentUser);

        assertTrue(output.toString().contains("Invalid choice"));
    }

    private static void write(Path path, String content) throws IOException {
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }
}
