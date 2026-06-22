Feature: Task Management
  As a user I want to create tasks so that I can manage household duties.
  User Stories #7, #8, #22

  Scenario: Creating a one-time task produces a task with correct data
    When a task is created with title "Clean kitchen", description "Clean all surfaces", owner "jan", starting "2026-07-01 10:00" ending "2026-07-01 11:00"
    Then the task title should be "Clean kitchen"
    And the task description should be "Clean all surfaces"
    And the task owner should be "jan"

  Scenario: A newly created task has NEW status by default
    When a task is created with title "Buy groceries", description "Milk and bread", owner "jan", starting "2026-07-01 09:00" ending "2026-07-01 09:30"
    Then the task status should be "NEW"

  Scenario: A task can be assigned to a specific household member
    When a task is created with title "Vacuum rooms", description "All rooms", owner "anna", starting "2026-07-02 11:00" ending "2026-07-02 12:00"
    Then the task owner should be "anna"

  Scenario: A saved task can be loaded back from storage
    Given a task is saved with title "Dishes", description "Wash all dishes", owner "jan", starting "2026-07-01 08:00" ending "2026-07-01 08:30"
    When all tasks are loaded from storage
    Then the loaded task list should contain 1 task
    And the first loaded task title should be "Dishes"

  Scenario: Multiple tasks can be saved and loaded
    Given a task is saved with title "Dishes", description "Wash dishes", owner "jan", starting "2026-07-01 08:00" ending "2026-07-01 08:30"
    And a task is saved with title "Laundry", description "Wash clothes", owner "anna", starting "2026-07-01 09:00" ending "2026-07-01 10:00"
    When all tasks are loaded from storage
    Then the loaded task list should contain 2 tasks

  Scenario: Task persists its status after save and load
    Given a task is saved with title "Dishes", description "Wash dishes", owner "jan", starting "2026-07-01 08:00" ending "2026-07-01 08:30" with status "DONE"
    When all tasks are loaded from storage
    Then the first loaded task status should be "DONE"

  Scenario: A task can be assigned to the creator themselves
    When a task is created with title "My own task", description "Self-assigned", owner "jan", starting "2026-07-01 10:00" ending "2026-07-01 11:00"
    Then the task owner should be "jan"

  Scenario: A task can be assigned to any registered household member
    When a task is created with title "Task for Bob", description "Assigned by Jan", owner "bob", starting "2026-07-03 14:00" ending "2026-07-03 15:00"
    Then the task owner should be "bob"

  Scenario: Multiple tasks can be assigned to different people
    When a task is created with title "Jan's task", description "", owner "jan", starting "2026-07-01 10:00" ending "2026-07-01 11:00"
    And a task is created with title "Anna's task", description "", owner "anna", starting "2026-07-01 12:00" ending "2026-07-01 13:00"
    Then the task pool should contain 2 tasks with different owners

  Scenario: A task whose title contains the CSV separator survives save and load
    Given a task is saved with title "Buy milk; eggs and bread", description "weekly run", owner "jan", starting "2026-07-01 08:00" ending "2026-07-01 09:00"
    When all tasks are loaded from storage
    Then the loaded task list should contain 1 task
    And the first loaded task title should be "Buy milk; eggs and bread"