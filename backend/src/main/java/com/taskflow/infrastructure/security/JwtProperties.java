package com.taskflow.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @param secret    HMAC-SHA256 key, at least 32 characters. Provide it through the JWT_SECRET env variable.
 * @param ttlMinutes lifetime of an access token
 */
@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtProperties(String secret, long ttlMinutes) {}
