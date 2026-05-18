package org.example;

import java.time.Instant;

public class Task {
    private String id;
    private String title;
    private String description;
    private String owner;
    private Instant startDate;

    public Task(String id, String title, String description, String owner, Instant startDate) {
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

    public Instant getStartDate() {
        return startDate;
    }
}
