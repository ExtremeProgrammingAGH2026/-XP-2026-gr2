package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserSelectionServiceTest {

    private UserSelectionService selectionService;
    private List<User> allUsers;
    private List<Task> allTasks;

    @BeforeEach
    void setUp() {
        selectionService = new UserSelectionService();
        allUsers = Arrays.asList(
                new User("1", "anna@dom.pl", "Anna Kowalska", "pass123"),
                new User("2", "tomek@dom.pl", "Tomek Nowak", "pass456"),
                new User("3", "kasia@dom.pl", "Kasia Wiśniewska", "pass789")
        );
        allTasks = Arrays.asList(
                new Task("t1", "Zmywanie", "Zmyć naczynia", "Anna Kowalska", toInstant(2026, 6, 1, 9, 0)),
                new Task("t2", "Odkurzanie", "Odkurzyć salon", "Anna Kowalska", toInstant(2026, 6, 2, 10, 0)),
                new Task("t3", "Pranie", "Wyprać ubrania", "Tomek Nowak", toInstant(2026, 6, 1, 11, 0)),
                new Task("t4", "Zakupy", "Kupić mleko", "Kasia Wiśniewska", toInstant(2026, 6, 3, 14, 0))
        );
    }

    private static Instant toInstant(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute).toInstant(ZoneOffset.UTC);
    }

    @Test
    void getSelectableUsers_returnsAllUsers() {
        List<User> selectable = selectionService.getSelectableUsers(allUsers);

        assertEquals(3, selectable.size());
    }

    @Test
    void getSelectableUsers_returnsEmptyForEmptyList() {
        List<User> selectable = selectionService.getSelectableUsers(Collections.emptyList());

        assertTrue(selectable.isEmpty());
    }

    @Test
    void selectUserByName_returnsMatchingUser() {
        User result = selectionService.selectUserByName(allUsers, "Tomek Nowak");

        assertNotNull(result);
        assertEquals("2", result.getId());
        assertEquals("Tomek Nowak", result.getName());
    }

    @Test
    void selectUserByName_returnsNullWhenNotFound() {
        User result = selectionService.selectUserByName(allUsers, "Nieznany");

        assertNull(result);
    }

    @Test
    void selectUserByName_isCaseSensitive() {
        User result = selectionService.selectUserByName(allUsers, "tomek nowak");

        assertNull(result);
    }

    @Test
    void selectUserById_returnsMatchingUser() {
        User result = selectionService.selectUserById(allUsers, "3");

        assertNotNull(result);
        assertEquals("Kasia Wiśniewska", result.getName());
    }

    @Test
    void selectUserById_returnsNullWhenNotFound() {
        User result = selectionService.selectUserById(allUsers, "999");

        assertNull(result);
    }

    @Test
    void getTasksForUser_returnsOnlyThatUsersTasksByName() {
        List<Task> result = selectionService.getTasksForUser(allTasks, allUsers.get(0));

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getOwner().equals("Anna Kowalska")));
    }

    @Test
    void getTasksForUser_returnsEmptyWhenUserHasNoTasks() {
        User userWithNoTasks = new User("99", "nowy@dom.pl", "Nowy User", "pass");

        List<Task> result = selectionService.getTasksForUser(allTasks, userWithNoTasks);

        assertTrue(result.isEmpty());
    }

    @Test
    void getTasksForUser_returnsSingleTask() {
        List<Task> result = selectionService.getTasksForUser(allTasks, allUsers.get(2)); // Kasia

        assertEquals(1, result.size());
        assertEquals("Zakupy", result.get(0).getTitle());
    }

    @Test
    void getTasksForUser_handlesEmptyTaskList() {
        List<Task> result = selectionService.getTasksForUser(Collections.emptyList(), allUsers.get(0));

        assertTrue(result.isEmpty());
    }
}
