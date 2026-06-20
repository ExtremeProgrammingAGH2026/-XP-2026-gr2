package org.example.cucumber.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.DataTable;
import org.example.Task;
import org.example.TaskStatus;
import org.example.TestContext;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskListSteps {

    private final TestContext ctx;

    public TaskListSteps(TestContext ctx) {
        this.ctx = ctx;
    }

    @Given("the following tasks exist:")
    public void theFollowingTasksExist(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            Instant start = ctx.parseDateTime(row.get("startDateTime"));
            Instant end = ctx.parseDateTime(row.get("endDateTime"));
            Task task = new Task(UUID.randomUUID().toString(), row.get("title"), "", row.get("owner"), start, end);
            String status = row.get("status");
            if (status != null && !status.isBlank()) {
                task.setStatus(TaskStatus.valueOf(status));
            }
            ctx.taskPool.add(task);
        }
    }

    @When("{string} requests their task list")
    public void userRequestsTheirTaskList(String owner) {
        ctx.lastTaskList = ctx.taskListService.getMyTasks(ctx.taskPool, owner);
    }

    @When("{string} requests their active task list")
    public void userRequestsTheirActiveTaskList(String owner) {
        ctx.lastTaskList = ctx.taskListService.getMyActiveTasks(ctx.taskPool, owner);
    }

    @Then("the task list should contain {int} task")
    public void theTaskListShouldContainTask(int expectedCount) {
        assertEquals(expectedCount, ctx.lastTaskList.size());
    }

    @Then("the task list should contain {int} tasks")
    public void theTaskListShouldContainTasks(int expectedCount) {
        assertEquals(expectedCount, ctx.lastTaskList.size());
    }

    @Then("the task list should include {string}")
    public void theTaskListShouldInclude(String expectedTitle) {
        boolean found = ctx.lastTaskList.stream().anyMatch(t -> t.getTitle().equals(expectedTitle));
        assertTrue(found, "Expected task list to include '" + expectedTitle + "' but it did not");
    }

    @Then("the task list should be empty")
    public void theTaskListShouldBeEmpty() {
        assertTrue(ctx.lastTaskList.isEmpty(), "Expected task list to be empty but had " + ctx.lastTaskList.size() + " tasks");
    }

    @Then("the first task in the list should be {string}")
    public void theFirstTaskInTheListShouldBe(String expectedTitle) {
        if (ctx.lastTaskList.isEmpty()) {
            throw new AssertionError("Task list is empty");
        }
        assertEquals(expectedTitle, ctx.lastTaskList.get(0).getTitle());
    }

    @Then("the last task in the list should be {string}")
    public void theLastTaskInTheListShouldBe(String expectedTitle) {
        if (ctx.lastTaskList.isEmpty()) {
            throw new AssertionError("Task list is empty");
        }
        assertEquals(expectedTitle, ctx.lastTaskList.get(ctx.lastTaskList.size() - 1).getTitle());
    }
}
