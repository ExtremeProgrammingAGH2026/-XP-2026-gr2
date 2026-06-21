# Przypadki brzegowe — MUST User Stories

## US#1 - Rejestracja konta

| # | Przypadek | Oczekiwany rezultat |
|---|-----------|---------------------|
| 1 | Pusta nazwa (empty string) | Błąd walidacji |
| 2 | Nazwa = same spacje | Błąd walidacji |
| 3 | Email bez `@` (np. "userexample.com") | Błąd walidacji |
| 4 | Email bez domeny (np. "user@") | Błąd walidacji |
| 5 | Email już zarejestrowany | Błąd: email zajęty |
| 6 | Email duplikat case-insensitive (np. "User@X.com" vs "user@x.com") | Błąd: email zajęty |
| 7 | Hasło < 8 znaków (np. "abc1234") | Błąd walidacji |
| 8 | Hasło dokładnie 8 znaków | Rejestracja OK (granica) |
| 9 | Null w polu name/email/password | Błąd walidacji |

## US#2 - Logowanie

| # | Przypadek | Oczekiwany rezultat |
|---|-----------|---------------------|
| 1 | Nieprawidłowe hasło | Błąd logowania |
| 2 | Nieistniejący email | Błąd logowania |
| 3 | Przekroczenie max prób (3 nieudane) | Powrót do ekranu startowego |
| 4 | Poprawne logowanie po 2 nieudanych próbach | Logowanie OK |
| 5 | Email case-insensitive przy logowaniu | Logowanie OK (jeśli user istnieje) |

## US#22 - Tworzenie zadań jednorazowych

| # | Przypadek | Oczekiwany rezultat |
|---|-----------|---------------------|
| 1 | Pusta nazwa zadania | Błąd walidacji / ponowne pytanie |
| 2 | Nazwa = same spacje | Błąd walidacji |
| 3 | Data końca wcześniejsza niż data początku | Błąd walidacji |
| 4 | Data końca = data początku | Błąd walidacji (end musi być strictly after start) |
| 5 | Brak opisu (puste) | OK — opis jest opcjonalny |
| 6 | Nieprawidłowy format daty (np. "2024-13-45 99:99") | Błąd parsowania, ponowne pytanie |

## US#8 - Przypisywanie zadań do osoby

| # | Przypadek | Oczekiwany rezultat |
|---|-----------|---------------------|
| 1 | Przypisanie do siebie (zalogowany user) | OK |
| 2 | Przypisanie do innego zarejestrowanego użytkownika | OK |
| 3 | Jedyny użytkownik w systemie (brak innych do wyboru) | Zadanie przypisane do siebie |

## US#14 - Zmiana statusu zadania

| # | Przypadek | Oczekiwany rezultat |
|---|-----------|---------------------|
| 1 | NEW → IN_PROGRESS | OK |
| 2 | IN_PROGRESS → DONE | OK |
| 3 | DONE → NEW (cofnięcie) | OK (stan odwracalny) |
| 4 | Wielokrotna zmiana statusu tego samego zadania | OK |
| 5 | Zmiana statusu gdy brak zadań na liście | Komunikat "brak zadań" |

## US#15 - Lista swoich zadań

| # | Przypadek | Oczekiwany rezultat |
|---|-----------|---------------------|
| 1 | Użytkownik bez żadnych zadań | Pusta lista / komunikat |
| 2 | Zadania z różnymi statusami (NEW, IN_PROGRESS, DONE) | Wyświetlone wszystkie |
| 3 | Filtrowanie aktywnych (tylko NEW + IN_PROGRESS) | DONE wykluczone |
| 4 | Sortowanie po dacie rozpoczęcia | Chronologicznie rosnąco |

## US#16 - Zadania innych domowników

| # | Przypadek | Oczekiwany rezultat |
|---|-----------|---------------------|
| 1 | Jedyny użytkownik w systemie | Brak "innych" do wyświetlenia |
| 2 | Inny użytkownik nie ma zadań | Pusta lista |
| 3 | Wyświetlanie zadań wybranego użytkownika | Poprawne zadania danego usera |

## US#21 - Zadania cykliczne

| # | Przypadek | Oczekiwany rezultat |
|---|-----------|---------------------|
| 1 | DAILY — generowanie wystąpień w oknie | 1 wystąpienie/dzień |
| 2 | WEEKLY — generowanie w oknie | 1 wystąpienie/tydzień |
| 3 | MONTHLY — koniec miesiąca (28/29/30/31 dni) | Poprawna obsługa granic miesięcy |
| 4 | Recurrence end date wcześniejsza niż start | Brak wystąpień / błąd |
| 5 | Nieprawidłowe wyrażenie cron | RuntimeException |

## US#24 - Kalendarz zadań

| # | Przypadek | Oczekiwany rezultat |
|---|-----------|---------------------|
| 1 | Brak zadań w danym miesiącu | Pusta lista |
| 2 | Filtrowanie po zakresie dat (inclusive) | Zadania na granicach zakresu uwzględnione |
| 3 | Zadanie startujące dokładnie na początku zakresu | Uwzględnione |
| 4 | Zadanie kończące się dokładnie na końcu zakresu | Uwzględnione |

## US#25 - Zadania na dany dzień

| # | Przypadek | Oczekiwany rezultat |
|---|-----------|---------------------|
| 1 | Dzień bez żadnych zadań | Pusta lista |
| 2 | Wiele zadań tego samego dnia | Wszystkie wyświetlone |
| 3 | Zadanie wielodniowe (rozciąga się na kilka dni) | Wyświetlone w każdym dniu z zakresu |
| 4 | Nieprawidłowy format daty wejściowej | Błąd parsowania |

## US#26 - Konflikty czasowe

| # | Przypadek | Oczekiwany rezultat |
|---|-----------|---------------------|
| 1 | Dwa zadania nachodzące się czasowo | Wykryty konflikt |
| 2 | Zadania stykające się (end jednego = start drugiego) | BRAK konfliktu |
| 3 | Nowe zadanie w pełni zawarte w istniejącym | Wykryty konflikt |
| 4 | Brak istniejących zadań | Brak konfliktu |
| 5 | Wiele istniejących zadań, konflikt z jednym | Wykryty konflikt (wystarczy 1) |
| 6 | To samo zadanie (ten sam ID) | Ignorowane (nie jest konfliktem z samym sobą) |
