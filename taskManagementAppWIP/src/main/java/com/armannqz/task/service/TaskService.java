package com.armannqz.task.service;

import com.armannqz.task.domain.CreateTaskRequest;
import com.armannqz.task.domain.entity.Task;

public interface TaskService {
    Task createTask(CreateTaskRequest request);
}
