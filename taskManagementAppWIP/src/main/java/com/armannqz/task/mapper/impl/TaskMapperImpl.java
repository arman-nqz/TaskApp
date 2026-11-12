package com.armannqz.task.mapper.impl;

import com.armannqz.task.domain.CreateTaskRequest;
import com.armannqz.task.domain.UpdateTaskRequest;
import com.armannqz.task.domain.dto.CreateTaskRequestDto;
import com.armannqz.task.domain.dto.TaskDto;
import com.armannqz.task.domain.dto.UpdateTaskRequestDto;
import com.armannqz.task.domain.entity.Task;
import com.armannqz.task.mapper.TaskMapper;
import org.springframework.stereotype.Component;

@Component
public class TaskMapperImpl implements TaskMapper {

    @Override
    public CreateTaskRequest fromDto(CreateTaskRequestDto dto) {
        return new  CreateTaskRequest(
                dto.title(),
                dto.description(),
                dto.dueDate(),
                dto.priority()
        );
    }

    @Override
    public TaskDto toDto(Task task) {
        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getPriority(),
                task.getStatus()
        );
    }

    @Override
    public UpdateTaskRequest fromDto(UpdateTaskRequestDto dto) {
        return new UpdateTaskRequest(
                dto.title(),
                dto.description(),
                dto.dueDate(),
                dto.status(),
                dto.priority()
        );
    }
}
