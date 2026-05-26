package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TaskEditServiceTest {

    private TaskEditService service;
    private Task task;

    @BeforeEach
    void setUp() {
        service = new TaskEditService(new TaskStatusService());
        task = new Task("1", "Odkurzyć", "Odkurzyć salon", "Adam", Instant.now());
    }

    // --- editTitle ---

    @Test
    void shouldEditTitle() {
        service.editTitle(task, "Umyć okna");

        assertEquals("Umyć okna", task.getTitle());
    }

    @Test
    void shouldNotModifyOtherFieldsWhenEditingTitle() {
        service.editTitle(task, "Umyć okna");

        assertEquals("Odkurzyć salon", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals("Adam", task.getOwner());
    }

    @Test
    void shouldThrowWhenEditTitleWithNull() {
        assertThrows(NullPointerException.class, () -> service.editTitle(task, null));
    }

    @Test
    void shouldThrowWhenEditTitleWithBlankString() {
        assertThrows(IllegalArgumentException.class, () -> service.editTitle(task, "   "));
    }

    @Test
    void shouldThrowWhenEditTitleWithEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> service.editTitle(task, ""));
    }

    // --- editDescription ---

    @Test
    void shouldEditDescription() {
        service.editDescription(task, "Odkurzyć salon i sypialnie");

        assertEquals("Odkurzyć salon i sypialnie", task.getDescription());
    }

    @Test
    void shouldAllowBlankDescription() {
        service.editDescription(task, "");

        assertEquals("", task.getDescription());
    }

    @Test
    void shouldNotModifyOtherFieldsWhenEditingDescription() {
        service.editDescription(task, "Nowy opis");

        assertEquals("Odkurzyć", task.getTitle());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals("Adam", task.getOwner());
    }

    @Test
    void shouldThrowWhenEditDescriptionWithNull() {
        assertThrows(NullPointerException.class, () -> service.editDescription(task, null));
    }

    // --- editStatus ---

    @Test
    void shouldEditStatus() {
        service.editStatus(task, TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    void shouldNotModifyOtherFieldsWhenEditingStatus() {
        service.editStatus(task, TaskStatus.DONE);

        assertEquals("Odkurzyć", task.getTitle());
        assertEquals("Odkurzyć salon", task.getDescription());
        assertEquals("Adam", task.getOwner());
    }

    @Test
    void shouldThrowWhenEditStatusWithNull() {
        assertThrows(NullPointerException.class, () -> service.editStatus(task, null));
    }

    // --- null task ---

    @Test
    void shouldThrowWhenTaskIsNullOnEditTitle() {
        assertThrows(NullPointerException.class, () -> service.editTitle(null, "Tytuł"));
    }

    @Test
    void shouldThrowWhenTaskIsNullOnEditDescription() {
        assertThrows(NullPointerException.class, () -> service.editDescription(null, "Opis"));
    }

    @Test
    void shouldThrowWhenTaskIsNullOnEditStatus() {
        assertThrows(NullPointerException.class, () -> service.editStatus(null, TaskStatus.DONE));
    }
}
