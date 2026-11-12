package com.armannqz.task.service;

import com.armannqz.task.domain.CreateTaskRequest;
import com.armannqz.task.domain.UpdateTaskRequest;
import com.armannqz.task.domain.entity.Task;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    Task createTask(CreateTaskRequest request);

    List<Task> listTasks();

    Task updateTask(UUID taskId, UpdateTaskRequest request);
}
