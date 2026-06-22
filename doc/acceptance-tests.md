    # Testy akceptacyjne (Cucumber) — MUST User Stories

**Data weryfikacji:** 2026-06-22
**Framework:** Cucumber 7.22.2 + JUnit Platform 5
**Wynik:** wszystkie scenariusze PASS, BUILD SUCCESS

Każda tabela poniżej zawiera scenariusze BDD z odpowiedniego `.feature` pokrywające
daną user story. Scenariusze są wykonywane automatycznie przez `mvn test` przez
CucumberTestEngine.

## US#1 — Rejestracja konta (`registration.feature`)

| # | Scenariusz | Wynik |
|---|------------|-------|
| 1 | Successful registration returns a user with correct data | PASS |
| 2 | Registration fails when email is invalid | PASS |
| 3 | Registration fails when password is too short | PASS |
| 4 | Registration fails when name is blank | PASS |
| 5 | Registration fails when email is already taken | PASS |
| 6 | Registered user is persisted and can be loaded from storage | PASS |
| 7 | Multiple users can be registered with different emails | PASS |
| 8 | Registration fails when email is duplicate with different case | PASS |
| 9 | Registration succeeds with password exactly 8 characters long | PASS |
| 10 | Registration fails with password 7 characters long | PASS |
| 11 | Registration fails when email has no domain | PASS |
| 12 | Registration fails when name is empty string | PASS |
| 13 | A user whose name contains the CSV separator survives save and load | PASS |

## US#2 — Logowanie (`authentication.feature`)

| # | Scenariusz | Wynik |
|---|------------|-------|
| 1 | Successful login returns the correct user | PASS |
| 2 | Login with wrong password fails | PASS |
| 3 | Login with unknown email fails | PASS |
| 4 | Session tracks the logged-in user | PASS |
| 5 | Logout clears the session | PASS |
| 6 | Login succeeds with email in different case | PASS |

## US#22 — Tworzenie zadań jednorazowych (`task_management.feature`, `task_creation_validation.feature`)

| # | Scenariusz | Wynik |
|---|------------|-------|
| 1 | Creating a one-time task produces a task with correct data | PASS |
| 2 | A newly created task has NEW status by default | PASS |
| 3 | A task can be assigned to a specific household member | PASS |
| 4 | A saved task can be loaded back from storage | PASS |
| 5 | Multiple tasks can be saved and loaded | PASS |
| 6 | Task persists its status after save and load | PASS |
| 7 | Task with end date before start date is invalid | PASS |
| 8 | Task with end date equal to start date is invalid | PASS |
| 9 | Task with end date after start date is valid | PASS |
| 10 | Task with empty description is valid | PASS |
| 11 | Task with blank title is invalid | PASS |
| 12 | A task whose title contains the CSV separator survives save and load | PASS |

## US#8 — Przypisywanie zadań (`task_management.feature`)

| # | Scenariusz | Wynik |
|---|------------|-------|
| 1 | A task can be assigned to the creator themselves | PASS |
| 2 | A task can be assigned to any registered household member | PASS |
| 3 | Multiple tasks can be assigned to different people | PASS |

## US#14 — Zmiana statusu zadania (`task_status.feature`)

| # | Scenariusz | Wynik |
|---|------------|-------|
| 1 | New task starts with status NEW | PASS |
| 2 | Status can be changed to IN_PROGRESS | PASS |
| 3 | Status can be changed to DONE | PASS |
| 4 | Status can be reverted from DONE back to NEW | PASS |
| 5 | Status transitions through all states | PASS |

## US#15 — Lista swoich zadań (`task_list.feature`)

| # | Scenariusz | Wynik |
|---|------------|-------|
| 1 | User sees only their own tasks | PASS |
| 2 | Another user's tasks are accessible via filter | PASS |
| 3 | Active task list excludes DONE tasks | PASS |
| 4 | Task list is sorted by start date ascending | PASS |
| 5 | User with no tasks receives an empty list | PASS |

## US#16 — Zadania innych domowników (`task_list.feature`)

| # | Scenariusz | Wynik |
|---|------------|-------|
| 1 | Viewing another user who has no tasks returns empty list | PASS |
| 2 | Each user sees only their own tasks when multiple users have tasks | PASS |
| 3 | User can view specific other user's tasks among multiple users | PASS |

## US#21 — Zadania cykliczne (`recurring_tasks.feature`)

| # | Scenariusz | Wynik |
|---|------------|-------|
| 1 | A weekly recurring task generates one occurrence per week | PASS |
| 2 | A daily recurring task generates one occurrence per day | PASS |
| 3 | Recurring task expansion respects the recurrence end date | PASS |
| 4 | A monthly recurring task generates one occurrence per month | PASS |
| 5 | Recurring task occurrences have unique IDs | PASS |
| 6 | A biweekly recurring task generates one occurrence every two weeks | PASS |
| 7 | Daily recurring task with recurrence end produces an occurrence for every day in range | PASS |

## US#24 — Kalendarz zadań (`task_calendar.feature`)

| # | Scenariusz | Wynik |
|---|------------|-------|
| 1 | Filtering tasks by a specific day returns only tasks on that day | PASS |
| 2 | Filtering tasks by a day with no tasks returns an empty list | PASS |
| 3 | Filtering tasks by month returns all tasks in that month | PASS |
| 4 | Filtering tasks by date range returns tasks within that range | PASS |
| 5 | Filtering tasks by date range excludes tasks outside the range | PASS |
| 6 | Multi-day task appears when filtering by a middle day | PASS |
| 7 | Filtering by today returns tasks scheduled for today | PASS |

## US#25 — Zadania na dany dzień (`task_calendar.feature`)

| # | Scenariusz | Wynik |
|---|------------|-------|
| 1 | Filtering tasks by a specific day returns only tasks on that day | PASS |
| 2 | Filtering tasks by a day with no tasks returns an empty list | PASS |
| 3 | Filtering by today returns tasks scheduled for today | PASS |

## US#26 — Konflikty czasowe (`task_conflicts.feature`)

| # | Scenariusz | Wynik |
|---|------------|-------|
| 1 | No conflict when tasks do not overlap in time | PASS |
| 2 | Conflict is detected when tasks overlap | PASS |
| 3 | Conflict is detected when new task is fully contained in an existing task | PASS |
| 4 | Tasks touching at boundary (end equals start) do not conflict | PASS |
| 5 | No conflict when no existing tasks are present | PASS |
| 6 | Multiple existing tasks - conflict with one is enough | PASS |
| 7 | No conflict when the overlapping task belongs to another household member | PASS |
| 8 | Conflict is detected only with the owner's own overlapping task | PASS |

## Scenariusze pomocnicze

### Task editing (`task_edit.feature`) — US#12

| # | Scenariusz | Wynik |
|---|------------|-------|
| 1 | Editing the task title updates it correctly | PASS |
| 2 | Editing the task description updates it correctly | PASS |
| 3 | Editing the task status via edit service updates it correctly | PASS |
| 4 | Setting a blank title is rejected | PASS |
| 5 | Setting an empty title is rejected | PASS |

### Smoke (`smoke.feature`)

| # | Scenariusz | Wynik |
|---|------------|-------|
| 1 | Core services are operational | PASS |

## Podsumowanie

Wszystkie 11 wymaganych MUST user stories mają pokrycie testami akceptacyjnymi Cucumber.
Łącznie wykonywanych jest ~67 scenariuszy BDD. Scenariusze pokrywają:

- Pozytywne ścieżki (happy path) każdej user story
- Walidację błędnych danych wejściowych
- Granice (granica długości hasła, granica stykających się zadań)
- Persystencję (zapis/odczyt z CSV, w tym pola zawierające separator `;`)
- Trzy poziomy filtrowania (dzień / miesiąc / zakres)
- Wszystkie wzorce powtarzania zadań cyklicznych
- Konflikty czasowe w 8 wariantach (w tym rozdzielność per właściciel)
