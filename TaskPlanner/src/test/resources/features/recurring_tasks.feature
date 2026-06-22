Feature: Recurring Tasks
  As a user I want to set up a recurring task so that I do not have to add it manually every time.
  User Story #21

  Scenario: A weekly recurring task generates one occurrence per week
    Given a weekly recurring task "Weekly cleaning" owned by "jan" starting "2026-07-06 10:00" ending "2026-07-06 11:00"
    When the recurring task is expanded for 4 weeks from "2026-07-06" to "2026-07-27"
    Then the expanded task list should contain 4 occurrences
    And all occurrences should have the title "Weekly cleaning"

  Scenario: A daily recurring task generates one occurrence per day
    Given a daily recurring task "Morning exercise" owned by "jan" starting "2026-07-01 07:00" ending "2026-07-01 07:30"
    When the recurring task is expanded from "2026-07-01" to "2026-07-03"
    Then the expanded task list should contain 3 occurrences

  Scenario: Recurring task expansion respects the recurrence end date
    Given a weekly recurring task "Weekly cleaning" owned by "jan" starting "2026-07-06 10:00" ending "2026-07-06 11:00" with recurrence end "2026-07-20"
    When the recurring task is expanded for 4 weeks from "2026-07-06" to "2026-07-27"
    Then the expanded task list should contain 3 occurrences

  Scenario: A monthly recurring task generates one occurrence per month
    Given a monthly recurring task "Pay bills" owned by "jan" starting "2026-07-01 09:00" ending "2026-07-01 10:00"
    When the recurring task is expanded from "2026-07-01" to "2026-09-30"
    Then the expanded task list should contain 3 occurrences

  Scenario: Recurring task occurrences have unique IDs
    Given a weekly recurring task "Weekly cleaning" owned by "jan" starting "2026-07-06 10:00" ending "2026-07-06 11:00"
    When the recurring task is expanded for 4 weeks from "2026-07-06" to "2026-07-27"
    Then all occurrence IDs should be unique

  Scenario: A biweekly recurring task generates one occurrence every two weeks
    Given a biweekly recurring task "Biweekly review" owned by "jan" starting "2026-07-06 10:00" ending "2026-07-06 11:00"
    When the recurring task is expanded from "2026-07-06" to "2026-08-16"
    Then the expanded task list should contain 3 occurrences

  Scenario: Daily recurring task with recurrence end produces an occurrence for every day in range
    Given a daily recurring task "Create User List" owned by "User" starting "2026-06-23 13:02" ending "2026-06-23 14:02" with recurrence end "2026-06-27 19:00"
    When all tasks are expanded for listing
    Then the expanded task list should contain 5 occurrences
    And all occurrences should have the title "Create User List"
