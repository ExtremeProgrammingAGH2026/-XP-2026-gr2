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

public class OtherUsersTasksUITest {

    @TempDir
    Path tempDir;

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream output;

    private Path usersFile;
    private Path tasksFile;
    private User currentUser;
    private OtherUsersTasksUI ui;

    @BeforeEach
    public void setUp() throws IOException {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        usersFile = tempDir.resolve("users.csv");
        tasksFile = tempDir.resolve("tasks.csv");
        currentUser = new User("1", "alice@example.com", "Alice", "secret");

        write(usersFile, "1;alice@example.com;Alice;secret\n2;bob@example.com;Bob;pass\n3;carol@example.com;Carol;pass2");

        AuthService authService = new AuthService(usersFile.toString());
        TaskReadService taskReadService = new TaskReadService();
        TaskPrintService taskPrintService = new TaskPrintService(new TaskFilterService());
        ui = new OtherUsersTasksUI(authService, taskReadService, taskPrintService, tasksFile.toString());
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void shouldListOtherUsersExcludingCurrentUser() {
        Scanner scanner = new Scanner("1\n");
        ui.show(scanner, currentUser);

        String out = output.toString();
        assertFalse(out.contains("Alice"));
        assertTrue(out.contains("Bob"));
        assertTrue(out.contains("Carol"));
    }

    @Test
    public void shouldPrintTasksForSelectedUser() throws IOException {
        write(tasksFile, "id;title;description;owner;startDate;status\n"
                + "1;Clean;Clean room;Bob;01.06.2026 10:00;NEW\n"
                + "2;Shopping;Buy milk;Carol;01.06.2026 11:00;NEW");

        Scanner scanner = new Scanner("1\n");
        ui.show(scanner, currentUser);

        assertTrue(output.toString().contains("Clean"));
    }

    @Test
    public void shouldShowNoTasksMessageWhenSelectedUserHasNoTasks() throws IOException {
        write(tasksFile, "id;title;description;owner;startDate;status\n"
                + "1;Shopping;Buy milk;Carol;01.06.2026 11:00;NEW");

        Scanner scanner = new Scanner("1\n");
        ui.show(scanner, currentUser);

        assertTrue(output.toString().contains("No tasks available"));
    }

    @Test
    public void shouldHandleInvalidSelectionAndRetry() throws IOException {
        write(tasksFile, "id;title;description;owner;startDate;status");

        Scanner scanner = new Scanner("99\n1\n");
        ui.show(scanner, currentUser);

        assertTrue(output.toString().contains("Invalid choice"));
    }

    @Test
    public void shouldShowMessageWhenNoOtherUsersExist() throws IOException {
        write(usersFile, "1;alice@example.com;Alice;secret");
        AuthService authService = new AuthService(usersFile.toString());
        ui = new OtherUsersTasksUI(authService, new TaskReadService(), new TaskPrintService(new TaskFilterService()), tasksFile.toString());

        Scanner scanner = new Scanner("");
        ui.show(scanner, currentUser);

        assertTrue(output.toString().contains("No other users"));
    }

    private static void write(Path path, String content) throws IOException {
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }
}
