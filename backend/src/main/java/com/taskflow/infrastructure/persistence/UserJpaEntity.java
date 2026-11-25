package com.taskflow.infrastructure.persistence;

import com.taskflow.domain.model.GlobalRole;
import com.taskflow.domain.model.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "users")
class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GlobalRole role;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected UserJpaEntity() {}

    static UserJpaEntity from(User u) {
        UserJpaEntity e = new UserJpaEntity();
        e.id = u.getId();
        e.email = u.getEmail();
        e.passwordHash = u.getPasswordHash();
        e.displayName = u.getDisplayName();
        e.role = u.getRole();
        e.createdAt = u.getCreatedAt();
        return e;
    }

    User toDomain() {
        return new User(id, email, passwordHash, displayName, role, createdAt);
    }
}
