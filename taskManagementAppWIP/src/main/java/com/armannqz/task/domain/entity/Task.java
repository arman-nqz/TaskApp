package com.armannqz.task.domain.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private taskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private taskPriority priority;

    @Column(name = "created", updatable = false, nullable = false)
    private Instant created;

    @Column(name = "updated", nullable = false)
    private Instant updated;

    public Task() {
    }

    public Task(LocalDate dueDate, UUID id, String title, String description, taskStatus status, taskPriority priority, Instant created, Instant updated) {
        this.dueDate = dueDate;
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.created = created;
        this.updated = updated;
    }
}
