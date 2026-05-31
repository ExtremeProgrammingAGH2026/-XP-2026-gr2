package org.example;

import java.time.Instant;

import org.example.sorting.HasScheduledTime;

public class Task implements HasScheduledTime {
    private String id;
    private String title;
    private String description;
    private String owner;
    private Instant startDate;
    private Instant endDate;
    private TaskStatus status;

    public Task(String id, String title, String description, String owner, Instant startDate) {
        this(id, title, description, owner, startDate, startDate);
    }

    public Task(String id, String title, String description, String owner, Instant startDate, Instant endDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.owner = owner;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = TaskStatus.NEW;
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
        return getScheduledTime();
    }

    public Instant getEndDate() {
        return endDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public Instant getScheduledTime() {
        return startDate;
    }
}