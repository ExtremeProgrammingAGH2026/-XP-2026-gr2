package org.example;

import java.time.LocalDateTime;

public class Task {
    private String id;
    private String title;
    private String description;
    private String owner;
    private LocalDateTime startDate;

    public Task(String id, String title, String description, String owner, LocalDateTime startDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.owner = owner;
        this.startDate = startDate;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getOwner() {
        return owner;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }
}
