package com.taskflow.application.task;

import com.taskflow.application.common.Actor;
import com.taskflow.application.common.ProjectAccess;
import com.taskflow.domain.exception.ConflictException;
import com.taskflow.domain.exception.NotFoundException;
import com.taskflow.domain.model.Project;
import com.taskflow.domain.model.Task;
import com.taskflow.domain.model.TaskPriority;
import com.taskflow.domain.model.TaskStatus;
import com.taskflow.domain.repository.TaskRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TaskService {

    public record CreateTaskCommand(String title, String description, TaskPriority priority,
                                    Long assigneeId, LocalDate dueDate) {}

    public record UpdateTaskCommand(String title, String description, TaskPriority priority,
                                    Long assigneeId, LocalDate dueDate) {}

    private final TaskRepository tasks;
    private final ProjectAccess access;

    public TaskService(TaskRepository tasks, ProjectAccess access) {
        this.tasks = tasks;
        this.access = access;
    }

    public Task create(Actor actor, Long projectId, CreateTaskCommand cmd) {
        Project project = access.requireWriteTasks(actor, projectId);
        requireAssigneeIsMember(project, cmd.assigneeId());
        return tasks.save(Task.create(projectId, cmd.title(), cmd.description(), cmd.priority(),
                cmd.assigneeId(), cmd.dueDate()));
    }

    @Transactional(readOnly = true)
    public List<Task> list(Actor actor, Long projectId, TaskStatus statusFilter) {
        access.requireRead(actor, projectId);
        return tasks.findByProjectId(projectId, statusFilter);
    }

    @Transactional(readOnly = true)
    public Task get(Actor actor, Long projectId, Long taskId) {
        access.requireRead(actor, projectId);
        return find(projectId, taskId);
    }

    public Task update(Actor actor, Long projectId, Long taskId, UpdateTaskCommand cmd) {
        Project project = access.requireWriteTasks(actor, projectId);
        requireAssigneeIsMember(project, cmd.assigneeId());
        Task task = find(projectId, taskId);
        task.update(cmd.title(), cmd.description(), cmd.priority(), cmd.assigneeId(), cmd.dueDate());
        return tasks.save(task);
    }

    public Task changeStatus(Actor actor, Long projectId, Long taskId, TaskStatus status) {
        access.requireWriteTasks(actor, projectId);
        Task task = find(projectId, taskId);
        task.changeStatus(status);
        return tasks.save(task);
    }

    public void delete(Actor actor, Long projectId, Long taskId) {
        access.requireWriteTasks(actor, projectId);
        find(projectId, taskId);
        tasks.deleteById(taskId);
    }

    private Task find(Long projectId, Long taskId) {
        return tasks.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new NotFoundException("Task " + taskId + " not found"));
    }

    private void requireAssigneeIsMember(Project project, Long assigneeId) {
        if (assigneeId != null && project.roleOf(assigneeId).isEmpty()) {
            throw new ConflictException("Assignee must be a member of the project");
        }
    }
}
