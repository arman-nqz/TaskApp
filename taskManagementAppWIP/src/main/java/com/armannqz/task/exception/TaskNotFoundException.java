package com.armannqz.task.exception;

import java.util.UUID;

/**
 * Thrown when attempting an operation (such as updating) on a task that does not exist.
 * Captures the missing task ID for centralized exception handling and error reporting.
 */
public class TaskNotFoundException extends RuntimeException {

    private final UUID id;

    public TaskNotFoundException(String message, UUID id) {
        super(String.format("Task with id '%s' does not exists.", id));
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
