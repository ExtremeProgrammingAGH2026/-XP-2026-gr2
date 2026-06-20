Feature: Task Editing
  As a user I want to edit a task to correct its content, deadline or assigned person.
  User Story #12

  Background:
    Given a task "Clean kitchen" assigned to "jan" exists in memory

  Scenario: Editing the task title updates it correctly
    When the task title is changed to "Deep clean kitchen"
    Then the task title should be "Deep clean kitchen"

  Scenario: Editing the task description updates it correctly
    When the task description is changed to "Clean oven and microwave"
    Then the task description should be "Clean oven and microwave"

  Scenario: Editing the task status via edit service updates it correctly
    When the task status is updated via edit service to "IN_PROGRESS"
    Then the task status should be "IN_PROGRESS"

  Scenario: Setting a blank title is rejected
    When the task title is changed to "   "
    Then the task edit should fail with an error

  Scenario: Setting an empty title is rejected
    When the task title is changed to ""
    Then the task edit should fail with an error