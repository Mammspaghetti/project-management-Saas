package com.taskflow.infrastructure.persistence;

import com.taskflow.domain.model.Project;
import com.taskflow.domain.model.ProjectRole;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "projects")
class ProjectJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "project_members", joinColumns = @JoinColumn(name = "project_id"))
    @MapKeyColumn(name = "user_id")
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Map<Long, ProjectRole> members = new HashMap<>();

    protected ProjectJpaEntity() {}

    static ProjectJpaEntity from(Project p) {
        ProjectJpaEntity e = new ProjectJpaEntity();
        e.id = p.getId();
        e.name = p.getName();
        e.description = p.getDescription();
        e.ownerId = p.getOwnerId();
        e.createdAt = p.getCreatedAt();
        e.members = new HashMap<>(p.getMembers());
        return e;
    }

    Project toDomain() {
        return new Project(id, name, description, ownerId, createdAt, members);
    }
}
