package org.example.cucumber.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.Task;
import org.example.TaskStatus;
import org.example.TestContext;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TaskManagementSteps {

    private final TestContext ctx;

    public TaskManagementSteps(TestContext ctx) {
        this.ctx = ctx;
    }

    @When("a task is created with title {string}, description {string}, owner {string}, starting {string} ending {string}")
    public void aTaskIsCreated(String title, String description, String owner, String start, String end) {
        Instant startInstant = ctx.parseDateTime(start);
        Instant endInstant = ctx.parseDateTime(end);
        ctx.lastTask = new Task(UUID.randomUUID().toString(), title, description, owner, startInstant, endInstant);
        ctx.taskPool.add(ctx.lastTask);
    }

    @Given("a task is saved with title {string}, description {string}, owner {string}, starting {string} ending {string}")
    public void aTaskIsSavedWith(String title, String description, String owner, String start, String end) {
        Instant startInstant = ctx.parseDateTime(start);
        Instant endInstant = ctx.parseDateTime(end);
        Task task = new Task(UUID.randomUUID().toString(), title, description, owner, startInstant, endInstant);
        ctx.saveTask(task);
    }

    @Given("a task is saved with title {string}, description {string}, owner {string}, starting {string} ending {string} with status {string}")
    public void aTaskIsSavedWithStatus(String title, String description, String owner,
                                       String start, String end, String status) {
        Instant startInstant = ctx.parseDateTime(start);
        Instant endInstant = ctx.parseDateTime(end);
        Task task = new Task(UUID.randomUUID().toString(), title, description, owner, startInstant, endInstant);
        task.setStatus(TaskStatus.valueOf(status));
        ctx.saveTask(task);
    }

    @When("all tasks are loaded from storage")
    public void allTasksAreLoadedFromStorage() {
        ctx.lastTaskList = ctx.taskReadService.readTasks(ctx.tasksFile.toString());
    }

    @Then("the task title should be {string}")
    public void theTaskTitleShouldBe(String expectedTitle) {
        assertNotNull(ctx.lastTask, "No task in context");
        assertEquals(expectedTitle, ctx.lastTask.getTitle());
    }

    @Then("the task description should be {string}")
    public void theTaskDescriptionShouldBe(String expectedDescription) {
        assertNotNull(ctx.lastTask, "No task in context");
        assertEquals(expectedDescription, ctx.lastTask.getDescription());
    }

    @Then("the task owner should be {string}")
    public void theTaskOwnerShouldBe(String expectedOwner) {
        assertNotNull(ctx.lastTask, "No task in context");
        assertEquals(expectedOwner, ctx.lastTask.getOwner());
    }

    @Then("the task status should be {string}")
    public void theTaskStatusShouldBe(String expectedStatus) {
        assertNotNull(ctx.lastTask, "No task in context");
        assertEquals(TaskStatus.valueOf(expectedStatus), ctx.lastTask.getStatus());
    }

    @Then("the loaded task list should contain {int} task")
    public void theLoadedTaskListShouldContainTask(int expectedCount) {
        assertEquals(expectedCount, ctx.lastTaskList.size());
    }

    @Then("the loaded task list should contain {int} tasks")
    public void theLoadedTaskListShouldContainTasks(int expectedCount) {
        assertEquals(expectedCount, ctx.lastTaskList.size());
    }

    @Then("the first loaded task title should be {string}")
    public void theFirstLoadedTaskTitleShouldBe(String expectedTitle) {
        assertNotNull(ctx.lastTaskList, "Task list is null");
        if (ctx.lastTaskList.isEmpty()) {
            throw new AssertionError("Task list is empty");
        }
        assertEquals(expectedTitle, ctx.lastTaskList.get(0).getTitle());
    }

    @Then("the first loaded task status should be {string}")
    public void theFirstLoadedTaskStatusShouldBe(String expectedStatus) {
        assertNotNull(ctx.lastTaskList, "Task list is null");
        if (ctx.lastTaskList.isEmpty()) {
            throw new AssertionError("Task list is empty");
        }
        assertEquals(TaskStatus.valueOf(expectedStatus), ctx.lastTaskList.get(0).getStatus());
    }

    @Then("the task pool should contain {int} tasks with different owners")
    public void theTaskPoolShouldContainTasksWithDifferentOwners(int expectedCount) {
        assertEquals(expectedCount, ctx.taskPool.size());
        long distinctOwners = ctx.taskPool.stream()
                .map(Task::getOwner)
                .distinct()
                .count();
        assertEquals(expectedCount, distinctOwners, "Expected all tasks to have different owners");
    }
}
