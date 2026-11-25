package com.taskflow.infrastructure.config;

import com.taskflow.application.auth.AuthService;
import com.taskflow.application.common.ProjectAccess;
import com.taskflow.application.port.PasswordHasher;
import com.taskflow.application.port.TokenIssuer;
import com.taskflow.application.project.ProjectService;
import com.taskflow.application.task.TaskService;
import com.taskflow.domain.repository.ProjectRepository;
import com.taskflow.domain.repository.TaskRepository;
import com.taskflow.domain.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Composition root: the application layer has no Spring stereotype annotations,
 * its classes are wired here.
 */
@Configuration
class UseCaseConfig {

    @Bean
    ProjectAccess projectAccess(ProjectRepository projects) {
        return new ProjectAccess(projects);
    }

    @Bean
    AuthService authService(UserRepository users, PasswordHasher hasher, TokenIssuer tokens) {
        return new AuthService(users, hasher, tokens);
    }

    @Bean
    ProjectService projectService(ProjectRepository projects, UserRepository users, ProjectAccess access) {
        return new ProjectService(projects, users, access);
    }

    @Bean
    TaskService taskService(TaskRepository tasks, ProjectAccess access) {
        return new TaskService(tasks, access);
    }
}
