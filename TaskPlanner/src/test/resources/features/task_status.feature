Feature: Task Status Management
  As a user I want to change the status of a task to show progress.
  User Story #14

  Background:
    Given a task "Clean kitchen" assigned to "jan" exists in memory

  Scenario: New task starts with status NEW
    Then the task status should be "NEW"

  Scenario: Status can be changed to IN_PROGRESS
    When the task status is changed to "IN_PROGRESS"
    Then the task status should be "IN_PROGRESS"

  Scenario: Status can be changed to DONE
    When the task status is changed to "DONE"
    Then the task status should be "DONE"

  Scenario: Status can be reverted from DONE back to NEW
    Given the task status is changed to "DONE"
    When the task status is changed to "NEW"
    Then the task status should be "NEW"

  Scenario: Status transitions through all states
    When the task status is changed to "IN_PROGRESS"
    And the task status is changed to "DONE"
    Then the task status should be "DONE"