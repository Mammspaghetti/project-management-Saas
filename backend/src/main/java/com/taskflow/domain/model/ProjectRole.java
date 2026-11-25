package com.taskflow.domain.model;

/** Role of a user inside a given project. Ordered from most to least privileged. */
public enum ProjectRole {
    OWNER,
    MEMBER,
    VIEWER;

    /** Create, update and delete tasks. */
    public boolean canWriteTasks() {
        return this == OWNER || this == MEMBER;
    }

    /** Edit the project, manage members, delete the project. */
    public boolean canManageProject() {
        return this == OWNER;
    }
}
