package com.taskflow.infrastructure.persistence;

import com.taskflow.domain.model.Project;
import com.taskflow.domain.repository.ProjectRepository;
import com.taskflow.infrastructure.persistence.SpringDataRepositories.ProjectJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
class ProjectRepositoryAdapter implements ProjectRepository {

    private final ProjectJpaRepository jpa;

    ProjectRepositoryAdapter(ProjectJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Project save(Project project) {
        return jpa.save(ProjectJpaEntity.from(project)).toDomain();
    }

    @Override
    public Optional<Project> findById(Long id) {
        return jpa.findById(id).map(ProjectJpaEntity::toDomain);
    }

    @Override
    public List<Project> findAllByMember(Long userId) {
        return jpa.findAllByMember(userId).stream().map(ProjectJpaEntity::toDomain).toList();
    }

    @Override
    public List<Project> findAll() {
        return jpa.findAll().stream().map(ProjectJpaEntity::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}
