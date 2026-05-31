// java
package org.example;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CyclicalTaskTest {

    @Test
    void dailyNoonQuartz_occurrencesHaveNoonLocalTime() {
        Instant start = Instant.parse("2024-01-01T00:00:00Z");
        Instant end = Instant.parse("2024-01-03T23:59:59Z");
        // quartz: second minute hour day month dayOfWeek
        String cron = "0 0 12 * * ?"; // codziennie o 12:00:00 lokalnie

        CyclicalTask task = new CyclicalTask("t1", "Daily noon", "desc", "owner", start, end, cron);
        List<Instant> occ = task.getAllOccurrences();

        assertEquals(3, occ.size(), "Powinny być 3 wystąpienia (3 dni)");
        ZoneId zone = ZoneId.systemDefault();
        for (Instant instant : occ) {
            ZonedDateTime z = ZonedDateTime.ofInstant(instant, zone);
            assertEquals(12, z.getHour(), "godzina powinna być 12");
            assertEquals(0, z.getMinute(), "minuta powinna być 0");
            assertEquals(0, z.getSecond(), "sekunda powinna być 0");
        }
    }

    @Test
    void everyHourAtMinute30_occurrencesHaveMinute30() {
        Instant start = Instant.parse("2024-01-01T10:00:00Z");
        Instant end = Instant.parse("2024-01-01T14:00:00Z");
        String cron = "0 30 * * * ?"; // co godzinę o :30 (sec 0, min 30)

        CyclicalTask task = new CyclicalTask("t2", "Hourly :30", "desc", "owner", start, end, cron);
        List<Instant> occ = task.getAllOccurrences();

        // oczekujemy wystąpień: 10:30, 11:30, 12:30, 13:30 (14:30 > end)
        assertEquals(4, occ.size(), "Powinny być 4 wystąpienia w przedziale");
        ZoneId zone = ZoneId.systemDefault();
        for (Instant instant : occ) {
            ZonedDateTime z = ZonedDateTime.ofInstant(instant, zone);
            assertEquals(30, z.getMinute(), "minuta powinna być 30");
            assertEquals(0, z.getSecond(), "sekunda powinna być 0");
        }
    }

    @Test
    void quartzWithSeconds_occurrencesHaveSpecifiedHMS() {
        Instant start = Instant.parse("2024-01-01T00:00:00Z");
        Instant end = Instant.parse("2024-01-02T23:59:59Z");
        String cron = "15 10 9 * * ?"; // codziennie o 09:10:15 lokalnie

        CyclicalTask task = new CyclicalTask("t3", "With seconds", "desc", "owner", start, end, cron);
        List<Instant> occ = task.getAllOccurrences();

        assertFalse(occ.isEmpty(), "Powinno być przynajmniej jedno wystąpienie");
        ZoneId zone = ZoneId.systemDefault();
        for (Instant instant : occ) {
            ZonedDateTime z = ZonedDateTime.ofInstant(instant, zone);
            assertEquals(9, z.getHour(), "godzina powinna być 9");
            assertEquals(10, z.getMinute(), "minuta powinna być 10");
            assertEquals(15, z.getSecond(), "sekunda powinna być 15");
        }
    }

    @Test
    void invalidCronExpression_throwsOnConstruction() {
        Instant start = Instant.parse("2024-01-01T00:00:00Z");
        Instant end = Instant.parse("2024-01-02T00:00:00Z");
        String bad = "this is not a cron";

        assertThrows(RuntimeException.class, () ->
                new CyclicalTask("t4", "Bad cron", "desc", "owner", start, end, bad));
    }
}
