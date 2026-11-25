package com.taskflow.infrastructure.persistence;

import com.taskflow.domain.model.Task;
import com.taskflow.domain.model.TaskStatus;
import com.taskflow.domain.repository.TaskRepository;
import com.taskflow.infrastructure.persistence.SpringDataRepositories.TaskJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
class TaskRepositoryAdapter implements TaskRepository {

    private final TaskJpaRepository jpa;

    TaskRepositoryAdapter(TaskJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Task save(Task task) {
        return jpa.save(TaskJpaEntity.from(task)).toDomain();
    }

    @Override
    public Optional<Task> findByIdAndProjectId(Long id, Long projectId) {
        return jpa.findByIdAndProjectId(id, projectId).map(TaskJpaEntity::toDomain);
    }

    @Override
    public List<Task> findByProjectId(Long projectId, TaskStatus statusFilter) {
        return jpa.findByProject(projectId, statusFilter).stream().map(TaskJpaEntity::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}
