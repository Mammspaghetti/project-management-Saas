package com.taskflow.infrastructure.persistence;

import com.taskflow.domain.model.Task;
import com.taskflow.domain.model.TaskPriority;
import com.taskflow.domain.model.TaskStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "tasks")
class TaskJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(nullable = false)
    private String title;

    @Column(length = 4000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority;

    @Column(name = "assignee_id")
    private Long assigneeId;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected TaskJpaEntity() {}

    static TaskJpaEntity from(Task t) {
        TaskJpaEntity e = new TaskJpaEntity();
        e.id = t.getId();
        e.projectId = t.getProjectId();
        e.title = t.getTitle();
        e.description = t.getDescription();
        e.status = t.getStatus();
        e.priority = t.getPriority();
        e.assigneeId = t.getAssigneeId();
        e.dueDate = t.getDueDate();
        e.createdAt = t.getCreatedAt();
        e.updatedAt = t.getUpdatedAt();
        return e;
    }

    Task toDomain() {
        return new Task(id, projectId, title, description, status, priority, assigneeId, dueDate, createdAt, updatedAt);
    }
}
