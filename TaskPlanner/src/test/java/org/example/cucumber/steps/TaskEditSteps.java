package org.example.cucumber.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.Task;
import org.example.TaskStatus;
import org.example.TestContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TaskEditSteps {

    private final TestContext ctx;

    public TaskEditSteps(TestContext ctx) {
        this.ctx = ctx;
    }

    @When("the task title is changed to {string}")
    public void theTaskTitleIsChangedTo(String newTitle) {
        try {
            ctx.taskEditService.editTitle(ctx.lastTask, newTitle);
            ctx.lastException = null;
        } catch (IllegalArgumentException | NullPointerException e) {
            ctx.lastException = e;
        }
    }

    @When("the task description is changed to {string}")
    public void theTaskDescriptionIsChangedTo(String newDescription) {
        ctx.taskEditService.editDescription(ctx.lastTask, newDescription);
    }

    @When("the task status is updated via edit service to {string}")
    public void theTaskStatusIsUpdatedViaEditServiceTo(String newStatus) {
        ctx.taskEditService.editStatus(ctx.lastTask, TaskStatus.valueOf(newStatus));
    }

    @Then("the task edit should fail with an error")
    public void theTaskEditShouldFailWithAnError() {
        assertNotNull(ctx.lastException, "Expected an error but edit succeeded");
    }
}
