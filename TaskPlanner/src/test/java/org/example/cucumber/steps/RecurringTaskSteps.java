package org.example.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.Task;
import org.example.TestContext;
import org.example.recurring.RecurrencePattern;
import org.example.recurring.RecurringTask;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecurringTaskSteps {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final TestContext ctx;
    private RecurringTask currentRecurringTask;
    private List<Task> expandedOccurrences;

    public RecurringTaskSteps(TestContext ctx) {
        this.ctx = ctx;
    }

    @Given("a weekly recurring task {string} owned by {string} starting {string} ending {string}")
    public void aWeeklyRecurringTask(String title, String owner, String start, String end) {
        Instant startInstant = ctx.parseDateTime(start);
        Instant endInstant = ctx.parseDateTime(end);
        currentRecurringTask = new RecurringTask(
                UUID.randomUUID().toString(), title, "", owner,
                startInstant, endInstant, RecurrencePattern.WEEKLY, null);
    }

    @Given("a weekly recurring task {string} owned by {string} starting {string} ending {string} with recurrence end {string}")
    public void aWeeklyRecurringTaskWithEnd(String title, String owner, String start, String end, String recurrenceEnd) {
        Instant startInstant = ctx.parseDateTime(start);
        Instant endInstant = ctx.parseDateTime(end);
        // plusDays(1) makes the given date inclusive: tasks starting on recurrenceEnd day are included
        Instant recurrenceEndInstant = LocalDate.parse(recurrenceEnd, DATE_FORMAT)
                .plusDays(1).atStartOfDay(TestContext.ZONE).toInstant();
        currentRecurringTask = new RecurringTask(
                UUID.randomUUID().toString(), title, "", owner,
                startInstant, endInstant, RecurrencePattern.WEEKLY, recurrenceEndInstant);
    }

    @Given("a daily recurring task {string} owned by {string} starting {string} ending {string}")
    public void aDailyRecurringTask(String title, String owner, String start, String end) {
        Instant startInstant = ctx.parseDateTime(start);
        Instant endInstant = ctx.parseDateTime(end);
        currentRecurringTask = new RecurringTask(
                UUID.randomUUID().toString(), title, "", owner,
                startInstant, endInstant, RecurrencePattern.DAILY, null);
    }

    @Given("a monthly recurring task {string} owned by {string} starting {string} ending {string}")
    public void aMonthlyRecurringTask(String title, String owner, String start, String end) {
        Instant startInstant = ctx.parseDateTime(start);
        Instant endInstant = ctx.parseDateTime(end);
        currentRecurringTask = new RecurringTask(
                UUID.randomUUID().toString(), title, "", owner,
                startInstant, endInstant, RecurrencePattern.MONTHLY, null);
    }

    @When("the recurring task is expanded for 4 weeks from {string} to {string}")
    public void theRecurringTaskIsExpandedForWeeks(String from, String to) {
        expandRecurringTask(from, to);
    }

    @When("the recurring task is expanded from {string} to {string}")
    public void theRecurringTaskIsExpandedFromTo(String from, String to) {
        expandRecurringTask(from, to);
    }

    private void expandRecurringTask(String from, String to) {
        Instant windowStart = LocalDate.parse(from, DATE_FORMAT).atStartOfDay(TestContext.ZONE).toInstant();
        Instant windowEnd = LocalDate.parse(to, DATE_FORMAT).plusDays(1).atStartOfDay(TestContext.ZONE).toInstant();
        expandedOccurrences = ctx.recurringTaskExpander.expand(currentRecurringTask, windowStart, windowEnd);
    }

    @Then("the expanded task list should contain {int} occurrences")
    public void theExpandedTaskListShouldContainOccurrences(int expectedCount) {
        assertEquals(expectedCount, expandedOccurrences.size(),
                "Expected " + expectedCount + " occurrences but got " + expandedOccurrences.size());
    }

    @Then("all occurrences should have the title {string}")
    public void allOccurrencesShouldHaveTheTitle(String expectedTitle) {
        boolean allMatch = expandedOccurrences.stream().allMatch(t -> t.getTitle().equals(expectedTitle));
        assertTrue(allMatch, "Not all occurrences have the expected title '" + expectedTitle + "'");
    }

    @Then("all occurrence IDs should be unique")
    public void allOccurrenceIdsShouldBeUnique() {
        Set<String> ids = new HashSet<>();
        for (Task occurrence : expandedOccurrences) {
            boolean added = ids.add(occurrence.getId());
            assertTrue(added, "Duplicate occurrence ID found: " + occurrence.getId());
        }
    }
}
