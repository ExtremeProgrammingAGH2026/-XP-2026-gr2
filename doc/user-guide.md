# TaskPlanner - Dokumentacja użytkownika

## Spis treści

1. [Opis aplikacji](#opis-aplikacji)
2. [Wymagania](#wymagania)
3. [Instalacja i uruchomienie](#instalacja-i-uruchomienie)
4. [Ekran startowy](#ekran-startowy)
5. [Rejestracja](#rejestracja)
6. [Logowanie](#logowanie)
7. [Menu główne](#menu-główne)
8. [Tworzenie zadania](#tworzenie-zadania)
9. [Moje zadania](#moje-zadania)
10. [Zadania innych użytkowników](#zadania-innych-użytkowników)
11. [Konfiguracja](#konfiguracja)
12. [Struktura plików danych](#struktura-plików-danych)

---

## Opis aplikacji

TaskPlanner to konsolowa aplikacja do zarządzania zadaniami. Umożliwia tworzenie kont użytkowników, logowanie, tworzenie zadań z datą rozpoczęcia i zakończenia, przeglądanie własnych zadań oraz zadań innych użytkowników. Dane przechowywane są w plikach CSV.

## Wymagania

- **Java** 11 lub nowsza
- **Maven** 3.6+

## Instalacja i uruchomienie

### Kompilacja

```bash
cd TaskPlanner
mvn clean package -DskipTests
```

### Uruchomienie testów

```bash
cd TaskPlanner
mvn test
```

### Uruchomienie aplikacji

```bash
cd TaskPlanner
mvn exec:java -Dexec.mainClass="org.example.App"
```

Lub po kompilacji:

```bash
cd TaskPlanner
java -cp target/classes org.example.App
```

Przy pierwszym uruchomieniu aplikacja automatycznie tworzy katalog `data/` oraz plik `data/users.csv`.

## Ekran startowy

Po uruchomieniu wyświetla się ekran startowy z trzema opcjami:

```
=== Task Planner ===
1. Login
2. Register
3. Exit
Choice:
```

| Opcja | Opis |
|-------|------|
| **1** | Logowanie do istniejącego konta |
| **2** | Rejestracja nowego konta |
| **3** | Wyjście z aplikacji |

## Rejestracja

Po wybraniu opcji **2** aplikacja prosi o podanie danych:

```
=== Register ===
Name: Jan Kowalski
Email: jan@example.com
Password: mojehaslo123
Account created. Welcome, Jan Kowalski!
```

**Wymagania:**
- **Imię** - nie może być puste
- **Email** - poprawny format (np. `user@domain.com`), nie może być już zajęty
- **Hasło** - minimum 8 znaków

Po udanej rejestracji użytkownik jest automatycznie zalogowany i przechodzi do menu głównego.

## Logowanie

Po wybraniu opcji **1** aplikacja prosi o email i hasło:

```
=== Login ===
Email: jan@example.com
Password: mojehaslo123
Logged in as: Jan Kowalski
```

Użytkownik ma **3 próby** logowania. Po każdej nieudanej próbie wyświetlana jest informacja o pozostałej liczbie prób:

```
Invalid email or password. 2 attempt(s) remaining.
```

Po wyczerpaniu prób aplikacja wraca do ekranu startowego.

## Menu główne

Po zalogowaniu wyświetla się menu:

```
=== Menu ===
1. My tasks
2. Other users' tasks
3. Create task
4. Show config
5. Edit config
6. Save config
7. Exit
Choice:
```

| Opcja | Opis |
|-------|------|
| **1** | Wyświetla listę zadań zalogowanego użytkownika |
| **2** | Przeglądanie zadań innych użytkowników |
| **3** | Tworzenie nowego zadania |
| **4** | Wyświetlenie aktualnej konfiguracji |
| **5** | Edycja wybranego pola konfiguracji |
| **6** | Zapis konfiguracji do pliku |
| **7** | Wyjście z aplikacji |

## Tworzenie zadania

Po wybraniu opcji **3** aplikacja prosi o dane zadania:

```
=== New Task ===
Title: Spotkanie z klientem
Description: Omówienie wymagań projektu
Start date (dd.MM.yyyy HH:mm): 15.06.2026 10:00
End date (dd.MM.yyyy HH:mm): 15.06.2026 11:30
Task created.
```

**Format daty:** `dd.MM.yyyy HH:mm` (np. `15.06.2026 10:00`)

W przypadku błędnego formatu daty aplikacja prosi o ponowne wprowadzenie:

```
Start date (dd.MM.yyyy HH:mm): zly format
Invalid format. Use dd.MM.yyyy HH:mm
Start date (dd.MM.yyyy HH:mm):
```

Zadanie jest automatycznie przypisywane do zalogowanego użytkownika i zapisywane do pliku CSV.

## Moje zadania

Po wybraniu opcji **1** wyświetlane są zadania zalogowanego użytkownika, posortowane po dacie rozpoczęcia:

```
-------------------------------------------
TASK: Spotkanie z klientem status: NEW
Owned by: Jan Kowalski
Start date: 15.06.2026 10:00
Description: Omówienie wymagań projektu
-------------------------------------------
```

Jeśli nie ma żadnych zadań, wyświetla się komunikat:

```
No tasks available
```

## Zadania innych użytkowników

Po wybraniu opcji **2** wyświetlana jest lista innych zarejestrowanych użytkowników:

```
=== Other users ===
1. Anna Nowak
2. Piotr Wiśniewski
Select user (1-2):
```

Po wybraniu użytkownika wyświetlane są jego zadania w tym samym formacie co w sekcji "Moje zadania".

Jeśli w systemie nie ma innych użytkowników:

```
No other users in the system.
```

## Konfiguracja

### Wyświetlanie konfiguracji (opcja 4)

```
Current configuration:
1. usersFilePath: data/users.csv
2. tasksFilePath: data/tasks.csv
3. maxLoginAttempts: 3
4. minPasswordLength: 8
5. timeZoneName: Europe/Warsaw
6. dateTimeFormat: dd.MM.yyyy HH:mm
```

### Edycja konfiguracji (opcja 5)

Po wybraniu opcji **5** wyświetlana jest lista pól z numerami. Użytkownik wybiera numer pola do edycji i wpisuje nową wartość:

```
Current configuration:
1. usersFilePath: data/users.csv
2. tasksFilePath: data/tasks.csv
3. maxLoginAttempts: 3
4. minPasswordLength: 8
5. timeZoneName: Europe/Warsaw
6. dateTimeFormat: dd.MM.yyyy HH:mm
Select field to edit (1-6): 3
New value: 5
Configuration updated. Use 'Save config' to persist changes.
```

Zmiany obowiązują natychmiast w bieżącej sesji, ale **nie są automatycznie zapisywane** do pliku. Aby zachować zmiany na stałe, należy użyć opcji **6. Save config**.

### Zapis konfiguracji (opcja 6)

Konfiguracja zapisywana jest do pliku `data/config.json` w formacie JSON. Przy kolejnym uruchomieniu aplikacja automatycznie wczytuje konfigurację z tego pliku.

## Struktura plików danych

Aplikacja przechowuje dane w katalogu `data/`:

```
data/
├── users.csv        # Dane użytkowników
├── tasks.csv        # Dane zadań
└── config.json      # Konfiguracja aplikacji (opcjonalnie)
```

### users.csv

Format: `id;email;name;password`

```
550e8400-e29b-41d4-a716-446655440000;jan@example.com;Jan Kowalski;mojehaslo123
```

### tasks.csv

Format z nagłówkiem: `id;title;description;owner;startDate;status`

```
id;title;description;owner;startDate;status
a1b2c3d4;Spotkanie;Omówienie projektu;Jan Kowalski;15.06.2026 10:00;NEW
```

**Możliwe statusy zadania:** `NEW`, `IN_PROGRESS`, `DONE`
