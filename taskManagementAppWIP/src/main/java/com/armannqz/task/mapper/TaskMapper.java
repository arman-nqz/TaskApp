package com.armannqz.task.mapper;

import com.armannqz.task.domain.CreateTaskRequest;
import com.armannqz.task.domain.UpdateTaskRequest;
import com.armannqz.task.domain.dto.CreateTaskRequestDto;
import com.armannqz.task.domain.dto.TaskDto;
import com.armannqz.task.domain.dto.UpdateTaskRequestDto;
import com.armannqz.task.domain.entity.Task;

public interface TaskMapper {

    CreateTaskRequest fromDto(CreateTaskRequestDto dto);

    TaskDto toDto(Task task);

    UpdateTaskRequest fromDto(UpdateTaskRequestDto dto);
}
