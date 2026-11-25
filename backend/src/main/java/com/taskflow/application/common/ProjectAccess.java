package com.taskflow.application.common;

import com.taskflow.domain.exception.ForbiddenException;
import com.taskflow.domain.exception.NotFoundException;
import com.taskflow.domain.model.Project;
import com.taskflow.domain.model.ProjectRole;
import com.taskflow.domain.repository.ProjectRepository;

/**
 * Central place for project-level authorization (RBAC).
 *
 * <p>A global ADMIN is treated as OWNER of every project. A non-member gets a 404 (not 403) so that
 * the existence of a project is not leaked.
 */
public class ProjectAccess {

    private final ProjectRepository projects;

    public ProjectAccess(ProjectRepository projects) {
        this.projects = projects;
    }

    public Project requireRead(Actor actor, Long projectId) {
        Project project = load(projectId);
        effectiveRole(actor, project);
        return project;
    }

    public Project requireWriteTasks(Actor actor, Long projectId) {
        Project project = load(projectId);
        if (!effectiveRole(actor, project).canWriteTasks()) {
            throw new ForbiddenException("Your role does not allow modifying tasks in this project");
        }
        return project;
    }

    public Project requireManage(Actor actor, Long projectId) {
        Project project = load(projectId);
        if (!effectiveRole(actor, project).canManageProject()) {
            throw new ForbiddenException("Only the project owner can do this");
        }
        return project;
    }

    private Project load(Long projectId) {
        return projects.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project " + projectId + " not found"));
    }

    private ProjectRole effectiveRole(Actor actor, Project project) {
        if (actor.isAdmin()) {
            return ProjectRole.OWNER;
        }
        return project.roleOf(actor.userId())
                .orElseThrow(() -> new NotFoundException("Project " + project.getId() + " not found"));
    }
}
