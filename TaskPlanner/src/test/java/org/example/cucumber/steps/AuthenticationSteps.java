package org.example.cucumber.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.TestContext;
import org.example.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthenticationSteps {

    private final TestContext ctx;

    public AuthenticationSteps(TestContext ctx) {
        this.ctx = ctx;
    }

    @When("the user logs in with email {string} and password {string}")
    public void theUserLogsInWith(String email, String password) {
        ctx.lastLoggedInUser = ctx.authService.authenticateUser(email, password);
    }

    @Then("the login should succeed")
    public void theLoginShouldSucceed() {
        assertNotNull(ctx.lastLoggedInUser, "Expected login to succeed but authentication returned null");
    }

    @Then("the login should fail")
    public void theLoginShouldFail() {
        assertNull(ctx.lastLoggedInUser, "Expected login to fail but authentication returned a user");
    }

    @Then("the authenticated user name should be {string}")
    public void theAuthenticatedUserNameShouldBe(String expectedName) {
        assertNotNull(ctx.lastLoggedInUser);
        assertEquals(expectedName, ctx.lastLoggedInUser.getName());
    }

    @And("the session is started for the authenticated user")
    public void theSessionIsStartedForTheAuthenticatedUser() {
        assertNotNull(ctx.lastLoggedInUser, "Cannot start session: no authenticated user");
        ctx.sessionService.login(ctx.lastLoggedInUser);
    }

    @Then("the session should be active")
    public void theSessionShouldBeActive() {
        assertTrue(ctx.sessionService.isLoggedIn(), "Expected session to be active");
    }

    @Then("the current session user should be {string}")
    public void theCurrentSessionUserShouldBe(String expectedName) {
        User currentUser = ctx.sessionService.getCurrentUser();
        assertNotNull(currentUser, "No user in session");
        assertEquals(expectedName, currentUser.getName());
    }

    @And("the user logs out")
    public void theUserLogsOut() {
        ctx.sessionService.logout();
    }

    @Then("the session should not be active")
    public void theSessionShouldNotBeActive() {
        assertFalse(ctx.sessionService.isLoggedIn(), "Expected session to be inactive after logout");
    }
}
