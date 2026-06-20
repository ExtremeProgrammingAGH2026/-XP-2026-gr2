package org.example.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.AuthService;
import org.example.TestContext;
import org.example.User;
import org.example.registration.RegistrationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

public class RegistrationSteps {

    private final TestContext ctx;

    public RegistrationSteps(TestContext ctx) {
        this.ctx = ctx;
    }

    @Given("a user is already registered with name {string}, email {string} and password {string}")
    public void aUserIsAlreadyRegisteredWith(String name, String email, String password) {
        ctx.registrationService.register(name, email, password);
    }

    @When("a user registers with name {string}, email {string} and password {string}")
    public void aUserRegistersWith(String name, String email, String password) {
        try {
            ctx.lastRegisteredUser = ctx.registrationService.register(name, email, password);
            ctx.lastException = null;
        } catch (RegistrationException e) {
            ctx.lastException = e;
            ctx.lastRegisteredUser = null;
        }
    }

    @Then("the registration should succeed")
    public void theRegistrationShouldSucceed() {
        assertNull(ctx.lastException, "Expected registration to succeed but got: " + ctx.lastException);
        assertNotNull(ctx.lastRegisteredUser);
    }

    @Then("the registration should fail")
    public void theRegistrationShouldFail() {
        assertNotNull(ctx.lastException, "Expected registration to fail but it succeeded");
    }

    @Then("the registered user name should be {string}")
    public void theRegisteredUserNameShouldBe(String expectedName) {
        assertNotNull(ctx.lastRegisteredUser);
        assertEquals(expectedName, ctx.lastRegisteredUser.getName());
    }

    @Then("the registered user email should be {string}")
    public void theRegisteredUserEmailShouldBe(String expectedEmail) {
        assertNotNull(ctx.lastRegisteredUser);
        assertEquals(expectedEmail, ctx.lastRegisteredUser.getEmail());
    }

    @Then("the registered user should have a generated ID")
    public void theRegisteredUserShouldHaveAGeneratedId() {
        assertNotNull(ctx.lastRegisteredUser);
        assertNotNull(ctx.lastRegisteredUser.getId());
        if (ctx.lastRegisteredUser.getId().isBlank()) {
            fail("Registered user ID should not be blank");
        }
    }

    @When("the user list is loaded from storage")
    public void theUserListIsLoadedFromStorage() {
        List<User> users = ctx.authService.loadUsers();
        ctx.lastTaskList.clear();
        ctx.lastRegisteredUser = users.isEmpty() ? null : users.get(0);
    }

    @Then("the loaded user list should contain {int} user")
    public void theLoadedUserListShouldContainUser(int expectedCount) {
        List<User> users = ctx.authService.loadUsers();
        assertEquals(expectedCount, users.size());
    }
}