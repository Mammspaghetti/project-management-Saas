package com.taskflow.domain.exception;

/** Authentication failure (bad credentials). */
public class UnauthorizedException extends DomainException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
