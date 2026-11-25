package com.taskflow.domain.repository;

import com.taskflow.domain.model.Task;
import com.taskflow.domain.model.TaskStatus;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Task save(Task task);
    Optional<Task> findByIdAndProjectId(Long id, Long projectId);
    /** @param statusFilter optional, {@code null} returns all tasks of the project. */
    List<Task> findByProjectId(Long projectId, TaskStatus statusFilter);
    void deleteById(Long id);
}
