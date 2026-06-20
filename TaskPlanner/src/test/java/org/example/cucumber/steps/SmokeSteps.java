package org.example.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.example.TestContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SmokeSteps {

    private final TestContext ctx;

    public SmokeSteps(TestContext ctx) {
        this.ctx = ctx;
    }

    @Given("the application context is initialized")
    public void theApplicationContextIsInitialized() {
        assertNotNull(ctx);
    }

    @Then("the registration service should be available")
    public void theRegistrationServiceShouldBeAvailable() {
        assertNotNull(ctx.registrationService);
    }

    @Then("the authentication service should be available")
    public void theAuthenticationServiceShouldBeAvailable() {
        assertNotNull(ctx.authService);
    }

    @Then("the task management services should be available")
    public void theTaskManagementServicesShouldBeAvailable() {
        assertNotNull(ctx.taskSaveService);
        assertNotNull(ctx.taskReadService);
        assertNotNull(ctx.taskListService);
        assertNotNull(ctx.taskStatusService);
        assertNotNull(ctx.taskEditService);
        assertNotNull(ctx.taskConflictService);
    }
}
