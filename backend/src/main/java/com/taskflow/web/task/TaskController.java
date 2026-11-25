package com.taskflow.web.task;

import com.taskflow.application.task.TaskService;
import com.taskflow.application.task.TaskService.CreateTaskCommand;
import com.taskflow.application.task.TaskService.UpdateTaskCommand;
import com.taskflow.domain.model.TaskStatus;
import com.taskflow.web.CurrentActor;
import com.taskflow.web.task.TaskDtos.StatusRequest;
import com.taskflow.web.task.TaskDtos.TaskRequest;
import com.taskflow.web.task.TaskDtos.TaskResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/tasks")
class TaskController {

    private final TaskService tasks;

    TaskController(TaskService tasks) {
        this.tasks = tasks;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    TaskResponse create(@AuthenticationPrincipal Jwt jwt, @PathVariable Long projectId,
                        @Valid @RequestBody TaskRequest req) {
        return TaskResponse.from(tasks.create(CurrentActor.from(jwt), projectId,
                new CreateTaskCommand(req.title(), req.description(), req.priority(), req.assigneeId(), req.dueDate())));
    }

    @GetMapping
    List<TaskResponse> list(@AuthenticationPrincipal Jwt jwt, @PathVariable Long projectId,
                            @RequestParam(required = false) TaskStatus status) {
        return tasks.list(CurrentActor.from(jwt), projectId, status).stream().map(TaskResponse::from).toList();
    }

    @GetMapping("/{taskId}")
    TaskResponse get(@AuthenticationPrincipal Jwt jwt, @PathVariable Long projectId, @PathVariable Long taskId) {
        return TaskResponse.from(tasks.get(CurrentActor.from(jwt), projectId, taskId));
    }

    @PutMapping("/{taskId}")
    TaskResponse update(@AuthenticationPrincipal Jwt jwt, @PathVariable Long projectId, @PathVariable Long taskId,
                        @Valid @RequestBody TaskRequest req) {
        return TaskResponse.from(tasks.update(CurrentActor.from(jwt), projectId, taskId,
                new UpdateTaskCommand(req.title(), req.description(), req.priority(), req.assigneeId(), req.dueDate())));
    }

    @PatchMapping("/{taskId}/status")
    TaskResponse changeStatus(@AuthenticationPrincipal Jwt jwt, @PathVariable Long projectId,
                              @PathVariable Long taskId, @Valid @RequestBody StatusRequest req) {
        return TaskResponse.from(tasks.changeStatus(CurrentActor.from(jwt), projectId, taskId, req.status()));
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long projectId, @PathVariable Long taskId) {
        tasks.delete(CurrentActor.from(jwt), projectId, taskId);
    }
}
