package com.taskflow.web;

import com.taskflow.application.common.Actor;
import com.taskflow.domain.model.GlobalRole;
import org.springframework.security.oauth2.jwt.Jwt;

/** Translates the validated JWT into the application layer's {@link Actor}. */
public final class CurrentActor {

    private CurrentActor() {}

    public static Actor from(Jwt jwt) {
        return new Actor(Long.valueOf(jwt.getSubject()), GlobalRole.valueOf(jwt.getClaimAsString("role")));
    }
}
