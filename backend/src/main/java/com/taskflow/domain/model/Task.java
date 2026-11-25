package com.taskflow.domain.model;

import java.time.Instant;
import java.time.LocalDate;

public class Task {

    private final Long id;
    private final Long projectId;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private Long assigneeId;
    private LocalDate dueDate;
    private final Instant createdAt;
    private Instant updatedAt;

    public Task(Long id, Long projectId, String title, String description, TaskStatus status,
                TaskPriority priority, Long assigneeId, LocalDate dueDate, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.assigneeId = assigneeId;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Task create(Long projectId, String title, String description, TaskPriority priority,
                              Long assigneeId, LocalDate dueDate) {
        Instant now = Instant.now();
        return new Task(null, projectId, title.trim(), description, TaskStatus.TODO,
                priority == null ? TaskPriority.MEDIUM : priority, assigneeId, dueDate, now, now);
    }

    public void update(String title, String description, TaskPriority priority, Long assigneeId, LocalDate dueDate) {
        this.title = title.trim();
        this.description = description;
        this.priority = priority == null ? this.priority : priority;
        this.assigneeId = assigneeId;
        this.dueDate = dueDate;
        this.updatedAt = Instant.now();
    }

    public void changeStatus(TaskStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getProjectId() { return projectId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public TaskPriority getPriority() { return priority; }
    public Long getAssigneeId() { return assigneeId; }
    public LocalDate getDueDate() { return dueDate; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
