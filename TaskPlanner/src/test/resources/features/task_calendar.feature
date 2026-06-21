Feature: Task Calendar and Date Filtering
  As a user I want to browse the task calendar and see tasks assigned to a given day.
  User Stories #24, #25

  Background:
    Given the following tasks exist in the pool:
      | title          | owner | startDateTime    | endDateTime      |
      | Monday meeting | jan   | 2026-07-06 09:00 | 2026-07-06 10:00 |
      | Tuesday lunch  | jan   | 2026-07-07 12:00 | 2026-07-07 13:00 |
      | Wednesday task | anna  | 2026-07-08 14:00 | 2026-07-08 15:00 |
      | July evening   | jan   | 2026-07-15 20:00 | 2026-07-15 21:00 |

  Scenario: Filtering tasks by a specific day returns only tasks on that day
    When tasks are filtered by day "2026-07-06"
    Then the filtered task list should contain 1 task
    And the filtered task list should include "Monday meeting"

  Scenario: Filtering tasks by a day with no tasks returns an empty list
    When tasks are filtered by day "2026-07-10"
    Then the filtered task list should be empty

  Scenario: Filtering tasks by month returns all tasks in that month
    When tasks are filtered by month 7 of year 2026
    Then the filtered task list should contain 4 tasks

  Scenario: Filtering tasks by date range returns tasks within that range
    When tasks are filtered from "2026-07-06" to "2026-07-08"
    Then the filtered task list should contain 3 tasks

  Scenario: Filtering tasks by date range excludes tasks outside the range
    When tasks are filtered from "2026-07-10" to "2026-07-14"
    Then the filtered task list should be empty

  Scenario: Multi-day task appears when filtering by a middle day
    Given the following tasks exist in the pool:
      | title         | owner | startDateTime    | endDateTime      |
      | Long project  | jan   | 2026-08-01 09:00 | 2026-08-05 17:00 |
    When tasks are filtered by day "2026-08-03"
    Then the filtered task list should contain 1 task
    And the filtered task list should include "Long project"