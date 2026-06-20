package org.example.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.DataTable;
import org.example.Task;
import org.example.TestContext;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskConflictSteps {

    private final TestContext ctx;
    private boolean conflictResult;

    public TaskConflictSteps(TestContext ctx) {
        this.ctx = ctx;
    }

    @Given("the existing tasks are:")
    public void theExistingTasksAre(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            Instant start = ctx.parseDateTime(row.get("startDateTime"));
            Instant end = ctx.parseDateTime(row.get("endDateTime"));
            Task task = new Task(UUID.randomUUID().toString(), row.get("title"), "", row.get("owner"), start, end);
            ctx.taskPool.add(task);
        }
    }

    @Given("there are no existing tasks")
    public void thereAreNoExistingTasks() {
        ctx.taskPool.clear();
    }

    @When("checking if a new task from {string} to {string} conflicts")
    public void checkingIfANewTaskConflicts(String startStr, String endStr) {
        Instant start = ctx.parseDateTime(startStr);
        Instant end = ctx.parseDateTime(endStr);
        Task candidate = new Task(UUID.randomUUID().toString(), "Candidate task", "", "jan", start, end);
        conflictResult = ctx.taskConflictService.hasConflict(candidate, ctx.taskPool);
    }

    @Then("there should be no conflict")
    public void thereShouldBeNoConflict() {
        assertFalse(conflictResult, "Expected no conflict but one was detected");
    }

    @Then("a conflict should be detected")
    public void aConflictShouldBeDetected() {
        assertTrue(conflictResult, "Expected a conflict to be detected but none was found");
    }
}
