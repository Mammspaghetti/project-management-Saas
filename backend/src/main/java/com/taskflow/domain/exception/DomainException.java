package com.taskflow.domain.exception;

/** Base type of all business errors. Mapped to HTTP statuses by the web layer. */
public abstract class DomainException extends RuntimeException {
    protected DomainException(String message) {
        super(message);
    }
}
