package com.taskflow.domain.model;

import java.time.Instant;

public class User {

    private final Long id;
    private final String email;
    private final String passwordHash;
    private final String displayName;
    private final GlobalRole role;
    private final Instant createdAt;

    public User(Long id, String email, String passwordHash, String displayName, GlobalRole role, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.role = role;
        this.createdAt = createdAt;
    }

    public static User register(String email, String passwordHash, String displayName) {
        return new User(null, email.trim().toLowerCase(), passwordHash, displayName.trim(), GlobalRole.USER, Instant.now());
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getDisplayName() { return displayName; }
    public GlobalRole getRole() { return role; }
    public Instant getCreatedAt() { return createdAt; }
}
