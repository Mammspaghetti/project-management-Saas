package com.taskflow.application.port;

import com.taskflow.domain.model.User;

/** Outbound port: issues access tokens for authenticated users. */
public interface TokenIssuer {

    AccessToken issue(User user);

    record AccessToken(String value, long expiresInSeconds) {}
}
