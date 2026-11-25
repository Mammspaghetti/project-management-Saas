package com.taskflow.web.task;

import com.taskflow.domain.model.Task;
import com.taskflow.domain.model.TaskPriority;
import com.taskflow.domain.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;

public final class TaskDtos {

    private TaskDtos() {}

    public record TaskRequest(
            @NotBlank @Size(max = 200) String title,
            @Size(max = 4000) String description,
            TaskPriority priority,
            Long assigneeId,
            LocalDate dueDate) {}

    public record StatusRequest(@NotNull TaskStatus status) {}

    public record TaskResponse(Long id, Long projectId, String title, String description, TaskStatus status,
                               TaskPriority priority, Long assigneeId, LocalDate dueDate,
                               Instant createdAt, Instant updatedAt) {
        public static TaskResponse from(Task t) {
            return new TaskResponse(t.getId(), t.getProjectId(), t.getTitle(), t.getDescription(), t.getStatus(),
                    t.getPriority(), t.getAssigneeId(), t.getDueDate(), t.getCreatedAt(), t.getUpdatedAt());
        }
    }
}
