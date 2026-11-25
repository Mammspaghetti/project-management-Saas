package com.taskflow.application.common;

import com.taskflow.domain.model.GlobalRole;

/** The authenticated caller of a use case, as seen by the application layer. */
public record Actor(Long userId, GlobalRole role) {

    public boolean isAdmin() {
        return role == GlobalRole.ADMIN;
    }
}
