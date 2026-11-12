package com.armannqz.task.domain;

import com.armannqz.task.domain.entity.TaskPriority;
import com.armannqz.task.domain.entity.TaskStatus;

import java.time.LocalDate;

public record UpdateTaskRequest(
        String title,
        String description,
        LocalDate dueDate,
        TaskStatus status,
        TaskPriority priority
) {
}
