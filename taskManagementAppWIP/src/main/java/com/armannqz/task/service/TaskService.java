package com.armannqz.task.service;

import com.armannqz.task.domain.CreateTaskRequest;
import com.armannqz.task.domain.entity.Task;

import java.util.List;

public interface TaskService {
    Task createTask(CreateTaskRequest request);

    List<Task> listTasks();
}
