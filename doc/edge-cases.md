# Przypadki brzegowe — MUST User Stories

**Data weryfikacji:** 2026-06-22
**Środowisko:** Windows 11, Java 11, Maven 3.9.9

Każdy przypadek brzegowy zawiera planowany scenariusz, oczekiwany rezultat oraz wynik
ręcznego testu w aplikacji konsolowej. Przypadki oznaczone `auto` są pokryte testami
automatycznymi (jednostkowymi lub Cucumber) i nie były testowane ręcznie.

## US#1 — Rejestracja konta

| # | Przypadek | Oczekiwany rezultat | Wynik | Uwagi |
|---|-----------|---------------------|-------|-------|
| 1 | Pusta nazwa (empty string) | Błąd walidacji + reprompt | PASS | "Name must not be empty. Try again." |
| 2 | Nazwa = same spacje | Błąd walidacji + reprompt | PASS | jak wyżej |
| 3 | Email bez `@` | Błąd walidacji + reprompt | PASS | "Email format is invalid. Try again." |
| 4 | Email bez domeny ("user@") | Błąd walidacji + reprompt | auto | `RegistrationValidatorTest`, `registration.feature` |
| 5 | Email już zarejestrowany | Błąd: email zajęty | PASS | "Email is already registered" |
| 6 | Email duplikat case-insensitive | Błąd: email zajęty | PASS | "USER@X.COM" vs "user@x.com" wykrywane |
| 7 | Hasło < 8 znaków | Błąd walidacji + reprompt | PASS | "Password must be at least 8 characters long. Try again." |
| 8 | Hasło dokładnie 8 znaków (granica) | Rejestracja OK | PASS | granica włącznie |
| 9 | Null w polu name/email/password | Błąd walidacji | auto | `RegistrationValidatorTest` |
| 10 | Anulowanie rejestracji w trakcie | Powrót do menu | PASS | wpisanie `cancel` w dowolnym polu |
| 11 | Nazwa zawiera separator CSV `;` | Zapis/odczyt zachowuje nazwę | auto | `RegistrationServiceTest`, `registration.feature` (RFC4180 quoting) |

## US#2 — Logowanie

| # | Przypadek | Oczekiwany rezultat | Wynik | Uwagi |
|---|-----------|---------------------|-------|-------|
| 1 | Nieprawidłowe hasło | Błąd logowania | PASS | "Invalid password. X attempt(s) remaining." |
| 2 | Nieistniejący email | Błąd logowania | PASS | komunikat o niepoprawnych danych |
| 3 | Przekroczenie max prób (3 nieudane) | Powrót do ekranu startowego | PASS | "Too many failed attempts. Exiting." |
| 4 | Poprawne logowanie po 2 nieudanych próbach | Logowanie OK | auto | `AuthServiceTest` |
| 5 | Email case-insensitive przy logowaniu | Logowanie OK | PASS | naprawione (was: `equals()` → `equalsIgnoreCase()`) |

## US#22 — Tworzenie zadań jednorazowych

| # | Przypadek | Oczekiwany rezultat | Wynik | Uwagi |
|---|-----------|---------------------|-------|-------|
| 1 | Pusta nazwa zadania | Błąd walidacji + reprompt | auto | `task_creation_validation.feature` |
| 2 | Nazwa = same spacje | Błąd walidacji | auto | jak wyżej |
| 3 | Data końca < data początku | Błąd walidacji + reprompt | PASS | "End date must be after start date." |
| 4 | Data końca = data początku | Błąd walidacji | auto | strict after |
| 5 | Brak opisu (puste) | OK (opcjonalny) | PASS | zadanie tworzone bez opisu |
| 6 | Nieprawidłowy format daty | Błąd parsowania + reprompt | PASS | "Invalid format. Use dd.MM.yyyy HH:mm" |
| 7 | Tytuł zawiera separator CSV `;` | Zapis/odczyt zachowuje tytuł | auto | `TaskReadServiceTest`, `task_management.feature` (RFC4180 quoting) |

## US#8 — Przypisywanie zadań do osoby

| # | Przypadek | Oczekiwany rezultat | Wynik | Uwagi |
|---|-----------|---------------------|-------|-------|
| 1 | Przypisanie do siebie | OK | PASS | wybór pozycji "(you)" z listy |
| 2 | Przypisanie do innego użytkownika | OK | PASS | wybór z listy domowników |
| 3 | Jedyny użytkownik w systemie | Auto-przypisanie | PASS | "Task assigned to: User" (bez pytania) |

## US#14 — Zmiana statusu zadania

| # | Przypadek | Oczekiwany rezultat | Wynik | Uwagi |
|---|-----------|---------------------|-------|-------|
| 1 | NEW → IN_PROGRESS | OK | PASS | "Status changed to IN_PROGRESS." |
| 2 | IN_PROGRESS → DONE | OK | auto | `task_status.feature` |
| 3 | DONE → NEW (cofnięcie) | OK | auto | stan odwracalny |
| 4 | Wielokrotna zmiana statusu | OK | auto | `task_status.feature` |
| 5 | Zmiana statusu gdy brak zadań | Komunikat "No tasks available." | PASS | wyjście z opcji bez zmian |

## US#15 — Lista swoich zadań

| # | Przypadek | Oczekiwany rezultat | Wynik | Uwagi |
|---|-----------|---------------------|-------|-------|
| 1 | Użytkownik bez zadań | "No tasks available" | PASS | pusta lista wyświetlana czytelnie |
| 2 | Mix statusów (NEW, IN_PROGRESS, DONE) | Wszystkie wyświetlone | PASS | każdy ze swoim statusem |
| 3 | Filtrowanie aktywnych (bez DONE) | DONE wykluczone | auto | `TaskListServiceTest` |
| 4 | Sortowanie po dacie | Chronologicznie rosnąco | PASS | również w opcji "Sort my tasks" |

## US#16 — Zadania innych domowników

| # | Przypadek | Oczekiwany rezultat | Wynik | Uwagi |
|---|-----------|---------------------|-------|-------|
| 1 | Jedyny użytkownik w systemie | Brak "innych" | PASS | "No other users in the system." |
| 2 | Inny użytkownik bez zadań | Pusta lista | PASS | "No tasks available" |
| 3 | Wyświetlanie zadań wybranego usera | Poprawne zadania | PASS | Alice widzi zadania Boba |

## US#21 — Zadania cykliczne

| # | Przypadek | Oczekiwany rezultat | Wynik | Uwagi |
|---|-----------|---------------------|-------|-------|
| 1 | DAILY — generowanie wystąpień | 1 wystąpienie/dzień | PASS | naprawiony bug: `expandAll` używa własnego recurrence end |
| 2 | WEEKLY — generowanie | 1 wystąpienie/tydzień | auto | `recurring_tasks.feature` |
| 3 | BIWEEKLY — generowanie | 1 wystąpienie / 2 tygodnie | auto | `recurring_tasks.feature` |
| 4 | MONTHLY — koniec miesiąca (28/29/30/31) | Poprawna obsługa granic | auto | `RecurringTaskExpanderTest` |
| 5 | Recurrence end date < start | Brak wystąpień | auto | `RecurringTaskExpander` zwraca pustą listę |
| 6 | Puste pole end date | Brak limitu (cap +1 rok) | PASS | akceptuje blank |
| 7 | Nieprawidłowy format end date | Błąd parsowania + reprompt | PASS | jak przy zwykłej dacie |

## US#24 — Kalendarz zadań

| # | Przypadek | Oczekiwany rezultat | Wynik | Uwagi |
|---|-----------|---------------------|-------|-------|
| 1 | Brak zadań w miesiącu | Pusta lista | auto | `task_calendar.feature` |
| 2 | Filtrowanie po zakresie (inclusive) | Granice włącznie | auto | `TaskDateFilterServiceTest` |
| 3 | Zadanie startujące na początku zakresu | Uwzględnione | auto | jak wyżej |
| 4 | Zadanie kończące się na końcu zakresu | Uwzględnione | auto | jak wyżej |
| 5 | Filtr "Today" | Tylko zadania z dzisiejszego dnia | PASS | opcja dostępna w menu |

## US#25 — Zadania na dany dzień

| # | Przypadek | Oczekiwany rezultat | Wynik | Uwagi |
|---|-----------|---------------------|-------|-------|
| 1 | Dzień bez zadań | "No tasks available" | PASS | poprawny komunikat |
| 2 | Wiele zadań tego samego dnia | Wszystkie wyświetlone | PASS | posortowane po godzinie |
| 3 | Zadanie wielodniowe (np. 5-dniowe) | Wyświetlone w każdym dniu zakresu | auto | naprawiony bug w `filterByDay()` |
| 4 | Nieprawidłowy format daty | Błąd parsowania + reprompt | PASS | "Invalid date format." |

## US#26 — Konflikty czasowe

| # | Przypadek | Oczekiwany rezultat | Wynik | Uwagi |
|---|-----------|---------------------|-------|-------|
| 1 | Dwa zadania nachodzące się czasowo | Wykryty konflikt | PASS | "WARNING: Task 'X' conflicts with task 'Y'" |
| 2 | Zadania stykające się (end = start) | BRAK konfliktu | PASS | granica nie generuje fałszywego konfliktu |
| 3 | Nowe zadanie w pełni zawarte w istniejącym | Wykryty konflikt | auto | `TaskConflictServiceTest` |
| 4 | Brak istniejących zadań | Brak konfliktu | auto | jak wyżej |
| 5 | Wiele zadań, konflikt z jednym | Wykryty konflikt | auto | jak wyżej |
| 6 | To samo zadanie (ten sam ID) | Ignorowane | auto | nie konflikt z samym sobą |
| 7 | Opcja anulowania po konflikcie | Wybór y/n | PASS | "Task has conflicts. Create anyway? (y/n)" |
| 8 | Nakładające się zadanie innego domownika | BRAK konfliktu | auto | konflikt liczony tylko w obrębie właściciela (`TaskConflictService`, `task_conflicts.feature`) |

## Konfiguracja (edycja w runtime)

| # | Przypadek | Oczekiwany rezultat | Wynik | Uwagi |
|---|-----------|---------------------|-------|-------|
| 1 | Zmiana `dateTimeFormat` w trakcie sesji | Stosowane od razu | auto | `MainMenuTest` (parsowanie/wyświetlanie na żywo) |
| 2 | Zmiana `timeZoneName` w trakcie sesji | Stosowane od razu (filtr dnia) | auto | `MainMenuTest.shouldApplyEditedTimeZoneLiveWhenFilteringByDay` |
| 3 | Zmiana `maxLoginAttempts` / `minPasswordLength` | Stosowane od razu | auto | `LoginUITest`, `RegistrationValidatorTest` (supplier) |
| 4 | Zmiana `tasksFilePath` w trakcie sesji | Odczyt/zapis/status z nowego pliku | auto | `MainMenuTest.shouldReadTasksFromEditedTasksFilePathLive` |
| 5 | Zmiana `usersFilePath` gdy ktoś zalogowany | Wymuszone wylogowanie (sesja unieważniona) | auto | `MainMenuTest.shouldLogOutWhenUsersFilePathIsChanged` |
| 6 | Zmiana pola innego niż `usersFilePath` | Brak wylogowania | auto | `MainMenuTest.shouldNotLogOutWhenANonUsersConfigFieldIsChanged` |
| 7 | Ustawienie `usersFilePath` na nieistniejący plik | Pusta lista użytkowników (brak crashu) | auto | `AuthServiceTest.shouldReturnEmptyListWhenUsersFileDoesNotExist` |

## Podsumowanie

Wszystkie przetestowane przypadki brzegowe (ręcznie + automatycznie) przechodzą poprawnie.

Aplikacja prawidłowo:
- Waliduje dane wejściowe per pole i pozwala na ponowne wprowadzenie po błędzie (reprompt)
- Wspiera anulowanie operacji wpisaniem `cancel` w dowolnym promptzie rejestracji
- Obsługuje case-insensitive email w rejestracji i logowaniu
- Wykrywa konflikty czasowe i daje użytkownikowi wybór y/n
- Obsługuje granicę stykających się zadań bez fałszywego konfliktu
- Ogranicza liczbę prób logowania
- Ekspanduje zadania cykliczne na konkretne wystąpienia przy listowaniu
- Pokazuje zadania wielodniowe w każdym dniu z ich zakresu
- Stosuje zmiany konfiguracji na żywo, a zmianę pliku użytkowników traktuje jako unieważnienie sesji (wymusza wylogowanie)
