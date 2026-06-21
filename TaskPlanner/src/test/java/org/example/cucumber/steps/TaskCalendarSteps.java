package org.example.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.DataTable;
import org.example.Task;
import org.example.TestContext;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskCalendarSteps {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final TestContext ctx;
    private List<Task> filteredTaskList;

    public TaskCalendarSteps(TestContext ctx) {
        this.ctx = ctx;
    }

    @Given("the following tasks exist in the pool:")
    public void theFollowingTasksExistInThePool(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            Instant start = parseFlexibleDateTime(row.get("startDateTime"));
            Instant end = parseFlexibleDateTime(row.get("endDateTime"));
            Task task = new Task(UUID.randomUUID().toString(), row.get("title"), "", row.get("owner"), start, end);
            ctx.taskPool.add(task);
        }
    }

    private Instant parseFlexibleDateTime(String dateTimeStr) {
        if (dateTimeStr.startsWith("today")) {
            String timeStr = dateTimeStr.replace("today ", "").trim();
            LocalTime time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
            return LocalDate.now(TestContext.ZONE).atTime(time).atZone(TestContext.ZONE).toInstant();
        }
        return ctx.parseDateTime(dateTimeStr);
    }

    @When("tasks are filtered by day {string}")
    public void tasksAreFilteredByDay(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr, DATE_FORMAT);
        filteredTaskList = ctx.taskDateFilterService.filterByDay(ctx.taskPool, date);
    }

    @When("tasks are filtered by today")
    public void tasksAreFilteredByToday() {
        filteredTaskList = ctx.taskDateFilterService.filterByToday(ctx.taskPool);
    }

    @When("tasks are filtered by month {int} of year {int}")
    public void tasksAreFilteredByMonth(int month, int year) {
        filteredTaskList = ctx.taskFilterService.filterByMonth(ctx.taskPool, year, month, TestContext.ZONE);
    }

    @When("tasks are filtered from {string} to {string}")
    public void tasksAreFilteredFromTo(String fromStr, String toStr) {
        Instant from = LocalDate.parse(fromStr, DATE_FORMAT).atStartOfDay(TestContext.ZONE).toInstant();
        Instant to = LocalDate.parse(toStr, DATE_FORMAT).plusDays(1).atStartOfDay(TestContext.ZONE).toInstant();
        filteredTaskList = ctx.taskFilterService.filterByDateRange(ctx.taskPool, from, to);
    }

    @Then("the filtered task list should contain {int} task")
    public void theFilteredTaskListShouldContainTask(int expectedCount) {
        assertEquals(expectedCount, filteredTaskList.size());
    }

    @Then("the filtered task list should contain {int} tasks")
    public void theFilteredTaskListShouldContainTasks(int expectedCount) {
        assertEquals(expectedCount, filteredTaskList.size());
    }

    @Then("the filtered task list should include {string}")
    public void theFilteredTaskListShouldInclude(String expectedTitle) {
        boolean found = filteredTaskList.stream().anyMatch(t -> t.getTitle().equals(expectedTitle));
        assertTrue(found, "Expected filtered task list to include '" + expectedTitle + "'");
    }

    @Then("the filtered task list should be empty")
    public void theFilteredTaskListShouldBeEmpty() {
        assertTrue(filteredTaskList.isEmpty(), "Expected filtered list to be empty but had " + filteredTaskList.size() + " tasks");
    }
}
