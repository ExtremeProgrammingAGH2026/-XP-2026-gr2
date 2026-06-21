Feature: Task List
  As a user I want to see my own tasks and other household members' tasks.
  User Stories #15, #16

  Scenario: User sees only their own tasks
    Given the following tasks exist:
      | title       | owner | startDateTime    | endDateTime      |
      | Cook dinner | jan   | 2026-07-01 18:00 | 2026-07-01 19:00 |
      | Buy flowers | anna  | 2026-07-01 10:00 | 2026-07-01 10:30 |
    When "jan" requests their task list
    Then the task list should contain 1 task
    And the task list should include "Cook dinner"

  Scenario: Another user's tasks are accessible via filter
    Given the following tasks exist:
      | title       | owner | startDateTime    | endDateTime      |
      | Cook dinner | jan   | 2026-07-01 18:00 | 2026-07-01 19:00 |
      | Buy flowers | anna  | 2026-07-01 10:00 | 2026-07-01 10:30 |
    When "anna" requests their task list
    Then the task list should contain 1 task
    And the task list should include "Buy flowers"

  Scenario: Active task list excludes DONE tasks
    Given the following tasks exist:
      | title           | owner | startDateTime    | endDateTime      | status |
      | Cook dinner     | jan   | 2026-07-01 18:00 | 2026-07-01 19:00 | NEW    |
      | Already done    | jan   | 2026-07-01 10:00 | 2026-07-01 10:30 | DONE   |
    When "jan" requests their active task list
    Then the task list should contain 1 task
    And the task list should include "Cook dinner"

  Scenario: Task list is sorted by start date ascending
    Given the following tasks exist:
      | title         | owner | startDateTime    | endDateTime      |
      | Evening task  | jan   | 2026-07-01 20:00 | 2026-07-01 21:00 |
      | Morning task  | jan   | 2026-07-01 08:00 | 2026-07-01 09:00 |
      | Midday task   | jan   | 2026-07-01 12:00 | 2026-07-01 13:00 |
    When "jan" requests their task list
    Then the task list should contain 3 tasks
    And the first task in the list should be "Morning task"
    And the last task in the list should be "Evening task"

  Scenario: User with no tasks receives an empty list
    Given the following tasks exist:
      | title       | owner | startDateTime    | endDateTime      |
      | Buy flowers | anna  | 2026-07-01 10:00 | 2026-07-01 10:30 |
    When "jan" requests their task list
    Then the task list should be empty

  Scenario: Viewing another user who has no tasks returns empty list
    Given the following tasks exist:
      | title       | owner | startDateTime    | endDateTime      |
      | Cook dinner | jan   | 2026-07-01 18:00 | 2026-07-01 19:00 |
    When "anna" requests their task list
    Then the task list should be empty

  Scenario: Each user sees only their own tasks when multiple users have tasks
    Given the following tasks exist:
      | title        | owner | startDateTime    | endDateTime      |
      | Jan task 1   | jan   | 2026-07-01 08:00 | 2026-07-01 09:00 |
      | Jan task 2   | jan   | 2026-07-01 10:00 | 2026-07-01 11:00 |
      | Anna task    | anna  | 2026-07-01 12:00 | 2026-07-01 13:00 |
      | Bob task     | bob   | 2026-07-01 14:00 | 2026-07-01 15:00 |
    When "jan" requests their task list
    Then the task list should contain 2 tasks
    And the task list should include "Jan task 1"
    And the task list should include "Jan task 2"

  Scenario: User can view specific other user's tasks among multiple users
    Given the following tasks exist:
      | title        | owner | startDateTime    | endDateTime      |
      | Jan task     | jan   | 2026-07-01 08:00 | 2026-07-01 09:00 |
      | Anna task    | anna  | 2026-07-01 12:00 | 2026-07-01 13:00 |
      | Bob task     | bob   | 2026-07-01 14:00 | 2026-07-01 15:00 |
    When "bob" requests their task list
    Then the task list should contain 1 task
    And the task list should include "Bob task"