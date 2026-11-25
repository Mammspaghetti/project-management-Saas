package com.taskflow.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.taskflow.application.common.Actor;
import com.taskflow.application.common.ProjectAccess;
import com.taskflow.application.task.TaskService;
import com.taskflow.application.task.TaskService.CreateTaskCommand;
import com.taskflow.domain.exception.ConflictException;
import com.taskflow.domain.exception.ForbiddenException;
import com.taskflow.domain.exception.NotFoundException;
import com.taskflow.domain.model.GlobalRole;
import com.taskflow.domain.model.Project;
import com.taskflow.domain.model.ProjectRole;
import com.taskflow.domain.model.Task;
import com.taskflow.domain.model.TaskPriority;
import com.taskflow.domain.repository.ProjectRepository;
import com.taskflow.domain.repository.TaskRepository;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskServiceRbacTest {

    private static final long PROJECT_ID = 1L;
    private static final Actor OWNER = new Actor(10L, GlobalRole.USER);
    private static final Actor MEMBER = new Actor(11L, GlobalRole.USER);
    private static final Actor VIEWER = new Actor(12L, GlobalRole.USER);
    private static final Actor OUTSIDER = new Actor(13L, GlobalRole.USER);
    private static final Actor ADMIN = new Actor(99L, GlobalRole.ADMIN);

    private TaskRepository tasks;
    private TaskService service;

    @BeforeEach
    void setUp() {
        ProjectRepository projects = mock(ProjectRepository.class);
        Project project = new Project(PROJECT_ID, "Demo", null, OWNER.userId(), Instant.now(), Map.of(
                OWNER.userId(), ProjectRole.OWNER,
                MEMBER.userId(), ProjectRole.MEMBER,
                VIEWER.userId(), ProjectRole.VIEWER));
        when(projects.findById(PROJECT_ID)).thenReturn(Optional.of(project));

        tasks = mock(TaskRepository.class);
        when(tasks.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));
        service = new TaskService(tasks, new ProjectAccess(projects));
    }

    private static CreateTaskCommand cmd(Long assignee) {
        return new CreateTaskCommand("Write docs", null, TaskPriority.HIGH, assignee, null);
    }

    @Test
    void owner_member_and_admin_can_create_tasks() {
        for (Actor actor : new Actor[] {OWNER, MEMBER, ADMIN}) {
            assertThat(service.create(actor, PROJECT_ID, cmd(null)).getTitle()).isEqualTo("Write docs");
        }
    }

    @Test
    void viewer_can_read_but_not_write() {
        service.list(VIEWER, PROJECT_ID, null);

        assertThatThrownBy(() -> service.create(VIEWER, PROJECT_ID, cmd(null)))
                .isInstanceOf(ForbiddenException.class);
        verify(tasks, never()).save(any());
    }

    @Test
    void non_member_gets_not_found_so_project_existence_is_not_leaked() {
        assertThatThrownBy(() -> service.list(OUTSIDER, PROJECT_ID, null)).isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> service.create(OUTSIDER, PROJECT_ID, cmd(null))).isInstanceOf(NotFoundException.class);
    }

    @Test
    void assignee_must_belong_to_the_project() {
        assertThatThrownBy(() -> service.create(OWNER, PROJECT_ID, cmd(OUTSIDER.userId())))
                .isInstanceOf(ConflictException.class);
        assertThat(service.create(OWNER, PROJECT_ID, cmd(MEMBER.userId())).getAssigneeId())
                .isEqualTo(MEMBER.userId());
    }
}
