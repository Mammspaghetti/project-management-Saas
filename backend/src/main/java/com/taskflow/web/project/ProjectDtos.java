package com.taskflow.web.project;

import com.taskflow.application.project.ProjectService.MemberView;
import com.taskflow.domain.model.Project;
import com.taskflow.domain.model.ProjectRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public final class ProjectDtos {

    private ProjectDtos() {}

    public record ProjectRequest(
            @NotBlank @Size(max = 120) String name,
            @Size(max = 2000) String description) {}

    public record ProjectResponse(Long id, String name, String description, Long ownerId,
                                  Instant createdAt, int memberCount) {
        public static ProjectResponse from(Project p) {
            return new ProjectResponse(p.getId(), p.getName(), p.getDescription(), p.getOwnerId(),
                    p.getCreatedAt(), p.getMembers().size());
        }
    }

    public record AddMemberRequest(@NotBlank @Email String email, @NotNull ProjectRole role) {}

    public record MemberResponse(Long userId, String email, String displayName, ProjectRole role) {
        public static MemberResponse from(MemberView m) {
            return new MemberResponse(m.user().getId(), m.user().getEmail(), m.user().getDisplayName(), m.role());
        }
    }
}
