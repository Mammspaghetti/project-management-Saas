package com.taskflow.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.taskflow.domain.exception.ConflictException;
import com.taskflow.domain.model.Project;
import com.taskflow.domain.model.ProjectRole;
import org.junit.jupiter.api.Test;

class ProjectDomainTest {

    @Test
    void creator_becomes_owner() {
        Project p = Project.create("  Roadmap ", null, 1L);

        assertThat(p.getName()).isEqualTo("Roadmap");
        assertThat(p.roleOf(1L)).contains(ProjectRole.OWNER);
    }

    @Test
    void owner_cannot_be_removed_nor_duplicated() {
        Project p = Project.create("Roadmap", null, 1L);

        assertThatThrownBy(() -> p.removeMember(1L)).isInstanceOf(ConflictException.class);
        assertThatThrownBy(() -> p.addMember(1L, ProjectRole.MEMBER)).isInstanceOf(ConflictException.class);
        assertThatThrownBy(() -> p.addMember(2L, ProjectRole.OWNER)).isInstanceOf(ConflictException.class);
    }

    @Test
    void role_permissions() {
        assertThat(ProjectRole.OWNER.canManageProject()).isTrue();
        assertThat(ProjectRole.MEMBER.canWriteTasks()).isTrue();
        assertThat(ProjectRole.MEMBER.canManageProject()).isFalse();
        assertThat(ProjectRole.VIEWER.canWriteTasks()).isFalse();
    }
}
