package org.example;

import java.time.Instant;

import org.example.sorting.HasScheduledTime;

public class Task implements HasScheduledTime {
    private String id;
    private String title;
    private String description;
    private String owner;
    private Instant startDate;
    private TaskStatus status;

    public Task(String id, String title, String description, String owner, Instant startDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.owner = owner;
        this.startDate = startDate;
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
        return startDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public Instant getScheduledTime() {
        return startDate;
    }
}
