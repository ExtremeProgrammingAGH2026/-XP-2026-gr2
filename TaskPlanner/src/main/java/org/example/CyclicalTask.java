package org.example;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CyclicalTask extends Task{
    private Instant endDate;
    private Cron cron;

    public CyclicalTask(String id, String title, String description, String owner, Instant startDate, Instant endDate, String cronExpression) {
        super(id, title, description, owner, startDate);
        this.endDate = endDate;
        CronParser parser = new CronParser(
                CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ));
        this.cron = parser.parse(cronExpression);
    }
    
    public Instant getEndDate() {
        return endDate;
    }

    @Override
    public Instant getScheduledTime() {
        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        ZonedDateTime now = ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        Optional<ZonedDateTime> nextExecution = executionTime.nextExecution(now);
        if (nextExecution.isEmpty()) {
            return super.getScheduledTime();
        }
        Instant nextInstant = nextExecution.get().toInstant();
        if (endDate != null && nextInstant.isAfter(endDate)) {
            return super.getScheduledTime();
        }
        return nextInstant;
    }

    public List<Instant> getAllOccurrences() {
        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime cursor = ZonedDateTime.ofInstant(super.getStartDate(), zone);
        List<Instant> occurrences = new ArrayList<>();
        Optional<ZonedDateTime> next = executionTime.nextExecution(cursor);
        while (next.isPresent()) {
            Instant inst = next.get().toInstant();
            if (endDate != null && inst.isAfter(endDate)) break;
            occurrences.add(inst);
            cursor = next.get();
            next = executionTime.nextExecution(cursor);
        }
        return occurrences;
    }
}
