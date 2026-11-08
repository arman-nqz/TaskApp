package com.armannqz.task.domain.dto;

import com.armannqz.task.domain.entity.TaskPriority;
import com.armannqz.task.domain.entity.TaskStatus;

import java.time.LocalDate;
import java.util.UUID;

public record TaskDto(
        UUID id,
        String title,
        String description,
        LocalDate dueDate,
        TaskPriority priority,
        TaskStatus status
) {

}
