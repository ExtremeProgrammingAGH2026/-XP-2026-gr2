package org.example.cucumber.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.Task;
import org.example.TestContext;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskCreationValidationSteps {

    private final TestContext ctx;
    private boolean validationPassed;

    public TaskCreationValidationSteps(TestContext ctx) {
        this.ctx = ctx;
    }

    @When("a task is created with start {string} and end {string}")
    public void aTaskIsCreatedWithStartAndEnd(String start, String end) {
        Instant startInstant = ctx.parseDateTime(start);
        Instant endInstant = ctx.parseDateTime(end);
        validationPassed = endInstant.isAfter(startInstant);
    }

    @When("a task is created with title {string} and description {string}")
    public void aTaskIsCreatedWithTitleAndDescription(String title, String description) {
        validationPassed = title != null && !title.isBlank();
        if (validationPassed) {
            ctx.lastTask = new Task(UUID.randomUUID().toString(), title, description, "owner",
                    Instant.now(), Instant.now().plusSeconds(3600));
        }
    }

    @Then("the task creation should be rejected")
    public void theTaskCreationShouldBeRejected() {
        assertFalse(validationPassed, "Expected task creation to be rejected but it was accepted");
    }

    @Then("the task creation should be accepted")
    public void theTaskCreationShouldBeAccepted() {
        assertTrue(validationPassed, "Expected task creation to be accepted but it was rejected");
    }
}
