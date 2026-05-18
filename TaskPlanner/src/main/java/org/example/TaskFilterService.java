package org.example;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class TaskFilterService {

    public List<Task> filterByDateRange(List<Task> tasks, LocalDateTime from, LocalDateTime to) {
        return Collections.emptyList();
    }

    public List<Task> filterByOwner(List<Task> tasks, String owner) {
        return Collections.emptyList();
    }

    public List<Task> filterByMonth(List<Task> tasks, int year, int month) {
        return Collections.emptyList();
    }
}
