# Weryfikacja User Stories — Raport

**Data:** 2026-06-22  
**Wynik testów:** 242 testy jednostkowe + ~75 scenariuszy Cucumber, 0 failures, 0 errors  
**Status:** BUILD SUCCESS

## Mapowanie feature files → user stories

| User Story | Feature file | Scenariusze | Status |
|---|---|---|---|
| US#1 - Rejestracja konta | `registration.feature` | 8 scenariuszy | PASS |
| US#2 - Logowanie | `authentication.feature` | 5 scenariuszy | PASS |
| US#22 - Tworzenie zadań jednorazowych | `task_management.feature` | 6 scenariuszy | PASS |
| US#8 - Przypisywanie zadań | `task_management.feature` | 1 scenariusz (assignment) | PASS |
| US#14 - Zmiana statusu | `task_status.feature` | 5 scenariuszy | PASS |
| US#15 - Lista swoich zadań | `task_list.feature` | 5 scenariuszy | PASS |
| US#16 - Zadania innych | `task_list.feature` | 1 scenariusz (other user) | PASS |
| US#21 - Zadania cykliczne | `recurring_tasks.feature` | 5 scenariuszy | PASS |
| US#24 - Kalendarz zadań | `task_calendar.feature` | 5 scenariuszy | PASS |
| US#25 - Zadania na dany dzień | `task_calendar.feature` | 2 scenariusze (by day) | PASS |
| US#26 - Konflikty czasowe | `task_conflicts.feature` | 6 scenariuszy | PASS |

## Podsumowanie

Wszystkie 11 wymaganych MUST user stories przechodzą testy akceptacyjne (Cucumber BDD) oraz testy jednostkowe.

### Znalezione i naprawione problemy

| Problem | Przyczyna | Fix |
|---|---|---|
| `TaskConflictWarningServiceTest` — 2 failures | Kodowanie Cp1252 na Windows nie obsługuje polskich znaków (ą) w PrintStream | Wymuszenie UTF-8 w PrintStream i odczyt ByteArrayOutputStream |
| Login case-insensitive nie działał | `AuthService.authenticateUser()` używał `equals()` zamiast `equalsIgnoreCase()` | Zmiana na `equalsIgnoreCase()` |
| Multi-day task nie pojawiał się w filtrze dnia | `TaskDateFilterService.filterByDay()` sprawdzał tylko datę startu | Sprawdzanie czy dzień mieści się w zakresie start-end |
| Zadania cykliczne tworzyły tylko 1 wystąpienie na liście | `MainMenu` / `OtherUsersTasksUI` drukowały szablon bez ekspansji | `TaskScheduleService.expandAll()` + podpięcie w obu UI |
| Konflikt fałszywie wykrywany między zadaniami różnych domowników | `TaskConflictService.hasConflict()` nie filtrował po właścicielu | Filtrowanie istniejących zadań po `getOwner()` przed sprawdzeniem nakładania |
| Crash przy listowaniu, gdy data końca cykliczności < startu | `expandAll` wołał `expand(from, to)` z `from > to` → `IllegalArgumentException` | Guard pomijający źle skonfigurowane zadanie (brak wystąpień) |
| Tytuł zadania / nazwa użytkownika z `;` rozbijały plik CSV | Writer używał `String.join(";")` bez cytowania, reader (commons-csv) oczekiwał RFC4180 | Wspólny `CsvEscaper` cytujący pola ze separatorem/cudzysłowem/nową linią |
| Brak opcji wylogowania | `MainMenu` miał tylko Exit | Dodano Logout (powrót do ekranu startowego), `run()` zwraca `boolean` |
| Rejestracja przerywała się po jednym błędnym polu | `RegistrationUI` walidowało wszystko naraz i wracało do menu | Walidacja per pole z repromptem + `cancel` do anulowania |
| Edycja configu nie działała w trakcie sesji (tylko po restarcie) | Serwisy „zaszywały" wartości configu przy starcie w `App.main` | Konsumenci czytają config na żywo: `LoginUI`/`RegistrationValidator` przez `IntSupplier`, ścieżki plików przez `Supplier<String>` / live z `AppConfiguration`, strefa budowana per użycie |
| Zmiana ścieżki użytkowników na nieistniejący plik powodowała crash | `AuthService.loadUsers()` rzucał `CsvException` gdy plik nie istniał | Zwraca pustą listę gdy plik nie istnieje (jak `TaskReadService`) |

### Testy jednostkowe pokrywające edge cases

- Rejestracja: walidacja email, hasła, duplikatów (RegistrationValidatorTest)
- Logowanie: AuthServiceTest
- Tworzenie zadań: CreateTaskUITest, CreateTaskTimeValidationTest
- Przypisywanie: CreateTaskAssignmentTest, UserSelectionServiceTest
- Statusy: TaskStatusServiceTest
- Lista zadań: TaskListServiceTest, TaskFilterServiceTest
- Zadania cykliczne: RecurringTaskExpanderTest, CyclicalTaskTest
- Kalendarz: TaskDateFilterServiceTest, TaskScheduleServiceTest
- Konflikty: TaskConflictServiceTest, TaskOverlapServiceTest, TaskConflictWarningServiceTest
