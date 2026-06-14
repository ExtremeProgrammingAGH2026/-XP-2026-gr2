# Refaktoryzacja: Task 20 – Wyświetlanie tasków w konsoli

**User Story:** #25 – Jako użytkownik chcę widzieć zadania przypisane na dany dzień  
**Klasa:** `org.example.TaskPrintService`  
**Autor implementacji:** Dominik Mrozek

---

## Co było czytelne?

- Nazwy metod są opisowe i jednoznaczne (`printTasks`, `printTasksSortedByDate`, `printTasksByDateRange`, `printTasksByOwner`)
- Stałe zostały dobrze wydzielone (`TOP_BORDER`, `BOTTOM_BORDER`, `DATE_FORMATTER`, `ZONE`)
- Metoda `printTask(Task)` jest prosta i łatwa do zrozumienia
- Testy jednostkowe pokrywają wszystkie publiczne metody i są czytelne

## Co budziło wątpliwości?

1. **Tworzenie `TaskFilterService` wewnątrz metod** – `new TaskFilterService()` pojawia się w `printTasksByDateRange` i `printTasksByOwner`, co utrudnia testowanie i łamie zasadę Dependency Inversion (DIP)
2. **Duplikacja logiki sortowania** – `Comparator.comparing(Task::getStartDate)` powtarza się w trzech metodach (`printTasksSortedByDate`, `printTasksByDateRange`, `printTasksByOwner`)
3. **Klasa ma wiele odpowiedzialności** – filtrowanie, sortowanie i formatowanie/drukowanie w jednej klasie (naruszenie SRP)
4. **Bezpośrednie użycie `System.out`** – utrudnia testowanie (testy muszą przechwytywać `System.out`) i uniemożliwia zmianę sposobu wyjścia
5. **Hardcoded timezone `"Europe/Warsaw"`** – brak możliwości konfiguracji strefy czasowej

## Jaki widzisz potencjał do refaktoryzacji?

### SOLID:
- **SRP**: Wydzielenie odpowiedzialności formatowania tasków z klasy drukującej – klasa powinna tylko drukować, nie filtrować ani sortować
- **DIP**: Wstrzyknięcie `TaskFilterService` przez konstruktor zamiast tworzenia go wewnątrz metod
- **OCP**: Wprowadzenie interfejsu `TaskFormatter` pozwalającego na dodawanie nowych formatów bez modyfikacji `TaskPrintService`

### Code smells:
- Duplicate Code – powtarzający się wzorzec filter → sort → print
- Feature Envy – metody `printTasksByDateRange` i `printTasksByOwner` więcej robią z `TaskFilterService` niż z własnymi danymi
