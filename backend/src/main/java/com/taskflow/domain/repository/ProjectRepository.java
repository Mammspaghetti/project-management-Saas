package com.taskflow.domain.repository;

import com.taskflow.domain.model.Project;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    Project save(Project project);
    Optional<Project> findById(Long id);
    List<Project> findAllByMember(Long userId);
    List<Project> findAll();
    void deleteById(Long id);
}
