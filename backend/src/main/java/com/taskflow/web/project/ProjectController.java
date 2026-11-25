package com.taskflow.web.project;

import com.taskflow.application.project.ProjectService;
import com.taskflow.application.project.ProjectService.CreateProjectCommand;
import com.taskflow.application.project.ProjectService.UpdateProjectCommand;
import com.taskflow.web.CurrentActor;
import com.taskflow.web.project.ProjectDtos.AddMemberRequest;
import com.taskflow.web.project.ProjectDtos.MemberResponse;
import com.taskflow.web.project.ProjectDtos.ProjectRequest;
import com.taskflow.web.project.ProjectDtos.ProjectResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/projects")
class ProjectController {

    private final ProjectService projects;

    ProjectController(ProjectService projects) {
        this.projects = projects;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ProjectResponse create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ProjectRequest req) {
        return ProjectResponse.from(
                projects.create(CurrentActor.from(jwt), new CreateProjectCommand(req.name(), req.description())));
    }

    @GetMapping
    List<ProjectResponse> list(@AuthenticationPrincipal Jwt jwt) {
        return projects.list(CurrentActor.from(jwt)).stream().map(ProjectResponse::from).toList();
    }

    @GetMapping("/{projectId}")
    ProjectResponse get(@AuthenticationPrincipal Jwt jwt, @PathVariable Long projectId) {
        return ProjectResponse.from(projects.get(CurrentActor.from(jwt), projectId));
    }

    @PutMapping("/{projectId}")
    ProjectResponse update(@AuthenticationPrincipal Jwt jwt, @PathVariable Long projectId,
                           @Valid @RequestBody ProjectRequest req) {
        return ProjectResponse.from(projects.update(CurrentActor.from(jwt), projectId,
                new UpdateProjectCommand(req.name(), req.description())));
    }

    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long projectId) {
        projects.delete(CurrentActor.from(jwt), projectId);
    }

    @GetMapping("/{projectId}/members")
    List<MemberResponse> members(@AuthenticationPrincipal Jwt jwt, @PathVariable Long projectId) {
        return projects.listMembers(CurrentActor.from(jwt), projectId).stream().map(MemberResponse::from).toList();
    }

    @PostMapping("/{projectId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    MemberResponse addMember(@AuthenticationPrincipal Jwt jwt, @PathVariable Long projectId,
                             @Valid @RequestBody AddMemberRequest req) {
        return MemberResponse.from(
                projects.addMember(CurrentActor.from(jwt), projectId, req.email(), req.role()));
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeMember(@AuthenticationPrincipal Jwt jwt, @PathVariable Long projectId, @PathVariable Long userId) {
        projects.removeMember(CurrentActor.from(jwt), projectId, userId);
    }
}
