package com.armannqz.task.service.impl;

import com.armannqz.task.domain.CreateTaskRequest;
import com.armannqz.task.domain.entity.Task;
import com.armannqz.task.domain.entity.TaskStatus;
import com.armannqz.task.repository.TaskRepository;
import com.armannqz.task.service.TaskService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    @Override
    public Task createTask(CreateTaskRequest request) {

        Instant now = Instant.now();

        Task task = new Task(
                request.dueDate(),
                null,
                request.title(),
                request.description(),
                TaskStatus.OPEN,
                request.priority(),
                now,
                now
        );
        return taskRepository.save(task);
    }

    @Override
    public List<Task> listTasks() {
        return taskRepository.findAll(Sort.by(Sort.Direction.ASC, "created"));
    }
}
