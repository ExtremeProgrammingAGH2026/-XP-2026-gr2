package org.example.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.example.Task;
import org.example.TaskStatus;
import org.example.TestContext;

import java.time.Instant;
import java.util.UUID;

public class TaskStatusSteps {

    private final TestContext ctx;

    public TaskStatusSteps(TestContext ctx) {
        this.ctx = ctx;
    }

    @Given("a task {string} assigned to {string} exists in memory")
    public void aTaskAssignedToExistsInMemory(String title, String owner) {
        Instant now = ctx.at(2026, 7, 1, 10, 0);
        ctx.lastTask = new Task(UUID.randomUUID().toString(), title, "", owner, now, now.plusSeconds(3600));
        ctx.taskPool.add(ctx.lastTask);
    }

    @When("the task status is changed to {string}")
    public void theTaskStatusIsChangedTo(String newStatus) {
        ctx.taskStatusService.changeStatus(ctx.lastTask, TaskStatus.valueOf(newStatus));
    }
}
