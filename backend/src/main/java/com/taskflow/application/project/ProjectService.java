package com.taskflow.application.project;

import com.taskflow.application.common.Actor;
import com.taskflow.application.common.ProjectAccess;
import com.taskflow.domain.exception.NotFoundException;
import com.taskflow.domain.model.Project;
import com.taskflow.domain.model.ProjectRole;
import com.taskflow.domain.model.User;
import com.taskflow.domain.repository.ProjectRepository;
import com.taskflow.domain.repository.UserRepository;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ProjectService {

    public record CreateProjectCommand(String name, String description) {}

    public record UpdateProjectCommand(String name, String description) {}

    public record MemberView(User user, ProjectRole role) {}

    private final ProjectRepository projects;
    private final UserRepository users;
    private final ProjectAccess access;

    public ProjectService(ProjectRepository projects, UserRepository users, ProjectAccess access) {
        this.projects = projects;
        this.users = users;
        this.access = access;
    }

    public Project create(Actor actor, CreateProjectCommand cmd) {
        return projects.save(Project.create(cmd.name(), cmd.description(), actor.userId()));
    }

    /** Projects the actor belongs to; an ADMIN sees every project. */
    @Transactional(readOnly = true)
    public List<Project> list(Actor actor) {
        return actor.isAdmin() ? projects.findAll() : projects.findAllByMember(actor.userId());
    }

    @Transactional(readOnly = true)
    public Project get(Actor actor, Long projectId) {
        return access.requireRead(actor, projectId);
    }

    public Project update(Actor actor, Long projectId, UpdateProjectCommand cmd) {
        Project project = access.requireManage(actor, projectId);
        project.update(cmd.name(), cmd.description());
        return projects.save(project);
    }

    public void delete(Actor actor, Long projectId) {
        access.requireManage(actor, projectId);
        projects.deleteById(projectId);
    }

    @Transactional(readOnly = true)
    public List<MemberView> listMembers(Actor actor, Long projectId) {
        Project project = access.requireRead(actor, projectId);
        return project.getMembers().entrySet().stream()
                .map(e -> new MemberView(
                        users.findById(e.getKey()).orElseThrow(() -> new NotFoundException("User not found")),
                        e.getValue()))
                .toList();
    }

    public MemberView addMember(Actor actor, Long projectId, String email, ProjectRole role) {
        Project project = access.requireManage(actor, projectId);
        User user = users.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new NotFoundException("No user with email " + email));
        project.addMember(user.getId(), role);
        projects.save(project);
        return new MemberView(user, role);
    }

    public void removeMember(Actor actor, Long projectId, Long userId) {
        Project project = access.requireManage(actor, projectId);
        project.removeMember(userId);
        projects.save(project);
    }
}
