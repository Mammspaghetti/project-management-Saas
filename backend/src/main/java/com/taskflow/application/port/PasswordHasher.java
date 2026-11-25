package com.taskflow.application.port;

/** Outbound port: password hashing. */
public interface PasswordHasher {
    String hash(String rawPassword);
    boolean matches(String rawPassword, String hash);
}
