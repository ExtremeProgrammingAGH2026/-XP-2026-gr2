# Wyniki testowania przypadków brzegowych

**Data:** 2026-06-21  
**Środowisko:** Windows 11, Java 11, Maven 3.9.9

## US#1 - Rejestracja konta

| # | Przypadek | Wynik | Uwagi |
|---|-----------|-------|-------|
| 1 | Pusta nazwa (spacje) | PASS | "Registration failed: Name must not be empty" |
| 2 | Email bez `@` | PASS | "Registration failed: Email format is invalid" |
| 3 | Email już zarejestrowany | PASS | "Registration failed: Email is already registered" |
| 4 | Duplikat case-insensitive (USER@X.COM vs user@x.com) | PASS | Poprawnie wykrywa duplikat |
| 5 | Hasło < 8 znaków | PASS | "Registration failed: Password must be at least 8 characters long" |
| 6 | Hasło = 8 znaków (granica) | PASS | Rejestracja pomyślna |

## US#2 - Logowanie

| # | Przypadek | Wynik | Uwagi |
|---|-----------|-------|-------|
| 1 | Nieprawidłowe hasło | PASS | "Invalid password. X attempt(s) remaining." |
| 2 | Przekroczenie 3 prób | PASS | "Too many failed attempts. Exiting." → powrót do start |
| 3 | Poprawne logowanie | PASS | "Logged in as: User" |

## US#22 - Tworzenie zadań jednorazowych

| # | Przypadek | Wynik | Uwagi |
|---|-----------|-------|-------|
| 1 | Data końca < data początku | PASS | "End date must be after start date." → ponowne pytanie |
| 2 | Poprawne daty | PASS | "Task assigned to X and created." |
| 3 | Brak opisu (opcjonalny) | PASS | Zadanie tworzone bez opisu |
| 4 | Nieprawidłowy format daty | PASS | "Invalid format. Use dd.MM.yyyy HH:mm" → ponowne pytanie |

## US#8 - Przypisywanie zadań

| # | Przypadek | Wynik | Uwagi |
|---|-----------|-------|-------|
| 1 | Jedyny user → auto-przypisanie | PASS | "Task assigned to: User" (bez pytania) |
| 2 | Wielu userów → wybór z listy | PASS | Pokazuje listę, przypisuje wybranego |
| 3 | Przypisanie do innego użytkownika | PASS | Wybór "1" (Alice) z pozycji Boba → OK |

## US#14 - Zmiana statusu

| # | Przypadek | Wynik | Uwagi |
|---|-----------|-------|-------|
| 1 | NEW → IN_PROGRESS | PASS | "Status changed to IN_PROGRESS." |
| 2 | Weryfikacja po zmianie | PASS | Wyświetla "status: IN_PROGRESS" |

## US#15 - Lista swoich zadań

| # | Przypadek | Wynik | Uwagi |
|---|-----------|-------|-------|
| 1 | Użytkownik z zadaniami | PASS | Wyświetla wszystkie z pełnymi szczegółami |
| 2 | Filtrowanie po dacie (dzień z zadaniami) | PASS | Poprawnie filtruje |
| 3 | Filtrowanie po dacie (dzień bez zadań) | PASS | "No tasks available" |

## US#16 - Zadania innych domowników

| # | Przypadek | Wynik | Uwagi |
|---|-----------|-------|-------|
| 1 | Wyświetlanie zadań innego usera | PASS | Alice widzi zadania Boba |
| 2 | Lista innych userów do wyboru | PASS | Pokazuje tylko "innych" (nie siebie) |

## US#21 - Zadania cykliczne

| # | Przypadek | Wynik | Uwagi |
|---|-----------|-------|-------|
| 1 | Tworzenie WEEKLY recurring | PASS | Wybór wzorca + opcjonalna data końca |
| 2 | Puste pole end date (no limit) | PASS | Akceptuje blank |
| 3 | Nieprawidłowy format end date | PASS | "Invalid format. Use dd.MM.yyyy HH:mm" → ponowne pytanie |

## US#24 - Kalendarz zadań

| # | Przypadek | Wynik | Uwagi |
|---|-----------|-------|-------|
| 1 | Filtrowanie po dniu ("Other day") | PASS | Poprawne wyświetlanie |
| 2 | Filtr "Today" | PASS | Opcja dostępna w menu |

## US#25 - Zadania na dany dzień

| # | Przypadek | Wynik | Uwagi |
|---|-----------|-------|-------|
| 1 | Dzień z zadaniami | PASS | Wyświetla wszystkie zadania z tego dnia |
| 2 | Dzień bez zadań | PASS | "No tasks available" |

## US#26 - Konflikty czasowe

| # | Przypadek | Wynik | Uwagi |
|---|-----------|-------|-------|
| 1 | Nakładające się zadania | PASS | "WARNING: Task 'X' conflicts with task 'Y'" |
| 2 | Stykające się (end=start) | PASS | BRAK konfliktu, zadanie tworzone normalnie |
| 3 | Opcja anulowania po konflikcie | PASS | "Task has conflicts. Create anyway? (y/n)" |

## Podsumowanie

**Wszystkie przetestowane przypadki brzegowe przechodzą poprawnie.**

Aplikacja prawidłowo:
- Waliduje dane wejściowe i wyświetla czytelne komunikaty błędów
- Pozwala na ponowne wprowadzenie danych po błędzie
- Wykrywa konflikty czasowe i daje użytkownikowi wybór
- Obsługuje granicę stykających się zadań (brak fałszywego konfliktu)
- Obsługuje case-insensitive email w rejestracji i logowaniu
- Ogranicza liczbę prób logowania
