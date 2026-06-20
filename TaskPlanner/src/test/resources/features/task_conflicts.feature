Feature: Task Conflict Detection
  As a user I want to see potential time conflicts between tasks so that I can avoid overlapping duties.
  User Story #26

  Scenario: No conflict when tasks do not overlap in time
    Given the existing tasks are:
      | title   | startDateTime    | endDateTime      | owner |
      | Meeting | 2026-07-01 10:00 | 2026-07-01 11:00 | jan   |
    When checking if a new task from "2026-07-01 11:30" to "2026-07-01 12:30" conflicts
    Then there should be no conflict

  Scenario: Conflict is detected when tasks overlap
    Given the existing tasks are:
      | title   | startDateTime    | endDateTime      | owner |
      | Meeting | 2026-07-01 10:00 | 2026-07-01 12:00 | jan   |
    When checking if a new task from "2026-07-01 11:00" to "2026-07-01 13:00" conflicts
    Then a conflict should be detected

  Scenario: Conflict is detected when new task is fully contained in an existing task
    Given the existing tasks are:
      | title     | startDateTime    | endDateTime      | owner |
      | All day   | 2026-07-01 08:00 | 2026-07-01 18:00 | jan   |
    When checking if a new task from "2026-07-01 10:00" to "2026-07-01 11:00" conflicts
    Then a conflict should be detected

  Scenario: Tasks touching at boundary (end equals start) do not conflict
    Given the existing tasks are:
      | title   | startDateTime    | endDateTime      | owner |
      | Morning | 2026-07-01 08:00 | 2026-07-01 10:00 | jan   |
    When checking if a new task from "2026-07-01 10:00" to "2026-07-01 11:00" conflicts
    Then there should be no conflict

  Scenario: No conflict when no existing tasks are present
    Given there are no existing tasks
    When checking if a new task from "2026-07-01 10:00" to "2026-07-01 11:00" conflicts
    Then there should be no conflict

  Scenario: Multiple existing tasks - conflict with one is enough
    Given the existing tasks are:
      | title   | startDateTime    | endDateTime      | owner |
      | Morning | 2026-07-01 08:00 | 2026-07-01 10:00 | jan   |
      | Lunch   | 2026-07-01 12:00 | 2026-07-01 13:00 | jan   |
    When checking if a new task from "2026-07-01 09:00" to "2026-07-01 09:30" conflicts
    Then a conflict should be detected