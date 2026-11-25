package com.taskflow.domain.model;

import com.taskflow.domain.exception.ConflictException;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** Aggregate root: a project and its membership. */
public class Project {

    private final Long id;
    private String name;
    private String description;
    private final Long ownerId;
    private final Instant createdAt;
    private final Map<Long, ProjectRole> members;

    public Project(Long id, String name, String description, Long ownerId, Instant createdAt,
                   Map<Long, ProjectRole> members) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.members = new LinkedHashMap<>(members);
    }

    /** Creates a new project whose creator is its OWNER. */
    public static Project create(String name, String description, Long ownerId) {
        Map<Long, ProjectRole> members = new LinkedHashMap<>();
        members.put(ownerId, ProjectRole.OWNER);
        return new Project(null, name.trim(), description, ownerId, Instant.now(), members);
    }

    public void update(String name, String description) {
        this.name = name.trim();
        this.description = description;
    }

    public Optional<ProjectRole> roleOf(Long userId) {
        return Optional.ofNullable(members.get(userId));
    }

    public void addMember(Long userId, ProjectRole role) {
        if (role == ProjectRole.OWNER) {
            throw new ConflictException("A project has a single owner");
        }
        if (members.containsKey(userId)) {
            throw new ConflictException("User is already a member of this project");
        }
        members.put(userId, role);
    }

    public void removeMember(Long userId) {
        if (userId.equals(ownerId)) {
            throw new ConflictException("The owner cannot be removed from the project");
        }
        members.remove(userId);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Long getOwnerId() { return ownerId; }
    public Instant getCreatedAt() { return createdAt; }
    public Map<Long, ProjectRole> getMembers() { return Collections.unmodifiableMap(members); }
}
