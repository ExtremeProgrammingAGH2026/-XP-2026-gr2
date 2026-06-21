Feature: Task Creation Validation
  As a user I want the system to reject invalid task data so that my calendar stays consistent.
  User Stories #22, #26

  Scenario: Task with end date before start date is invalid
    When a task is created with start "2026-07-01 10:00" and end "2026-07-01 09:00"
    Then the task creation should be rejected

  Scenario: Task with end date equal to start date is invalid
    When a task is created with start "2026-07-01 10:00" and end "2026-07-01 10:00"
    Then the task creation should be rejected

  Scenario: Task with end date after start date is valid
    When a task is created with start "2026-07-01 10:00" and end "2026-07-01 11:00"
    Then the task creation should be accepted

  Scenario: Task with empty description is valid
    When a task is created with title "Test" and description ""
    Then the task creation should be accepted

  Scenario: Task with blank title is invalid
    When a task is created with title "   " and description "desc"
    Then the task creation should be rejected
