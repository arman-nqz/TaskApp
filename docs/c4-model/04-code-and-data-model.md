# C4 Model - Level 4: Code & Data Model

The Code and Data Model level zooms into the structural mechanics: object-oriented class hierarchies, immutable records, data contracts, and relational database schema definitions.

---

## 🗄️ 1. Relational Database Schema (Entity-Relationship)

```mermaid
erDiagram
    TASKS {
        UUID id PK "Primary Key (UUID)"
        VARCHAR(255) title "Task title, NOT NULL"
        VARCHAR(1000) description "Optional description"
        DATE due_date "Target completion date"
        VARCHAR(50) status "Status enum: OPEN, COMPLETE, NOT NULL"
        VARCHAR(50) priority "Priority enum: LOW, MEDIUM, HIGH, NOT NULL"
        TIMESTAMP created "Creation timestamp (UTC), NOT NULL, IMMUTABLE"
        TIMESTAMP updated "Last modification timestamp (UTC), NOT NULL"
    }
```

### Table Properties & Indexing:
- **Table Name**: `tasks`
- **Primary Key**: `id` (`UUID`)
- **Nullability Rules**:
  - `title`, `status`, `priority`, `created`, `updated` are `NOT NULL`.
  - `description` and `due_date` are nullable.
- **Sorting Default**: Indexed and ordered by `created ASC` during `TaskServiceImpl.listTasks()`.

---

## ☕ 2. Backend Class & Record Hierarchy

```mermaid
classDiagram
    direction TB

    class TaskController {
        -TaskService taskService
        -TaskMapper taskMapper
        +createTask(CreateTaskRequestDto) ResponseEntity~TaskDto~
        +listTasks() ResponseEntity~List~TaskDto~~
        +updateTask(UUID, UpdateTaskRequestDto) ResponseEntity~TaskDto~
        +deleteTask(UUID) ResponseEntity~Void~
    }

    class GlobalExceptionHandler {
        +handleValidationExceptions(MethodArgumentNotValidException) ResponseEntity~ErrorDto~
        +handleTaskNotFoundException(TaskNotFoundException) ResponseEntity~ErrorDto~
    }

    class TaskService {
        <<interface>>
        +createTask(CreateTaskRequest) Task
        +listTasks() List~Task~
        +updateTask(UUID, UpdateTaskRequest) Task
        +deleteTask(UUID) void
    }

    class TaskServiceImpl {
        -TaskRepository taskRepository
        +createTask(CreateTaskRequest) Task
        +listTasks() List~Task~
        +updateTask(UUID, UpdateTaskRequest) Task
        +deleteTask(UUID) void
    }

    class TaskMapper {
        <<interface>>
        +fromDto(CreateTaskRequestDto) CreateTaskRequest
        +fromDto(UpdateTaskRequestDto) UpdateTaskRequest
        +toDto(Task) TaskDto
    }

    class TaskMapperImpl {
        +fromDto(CreateTaskRequestDto) CreateTaskRequest
        +fromDto(UpdateTaskRequestDto) UpdateTaskRequest
        +toDto(Task) TaskDto
    }

    class TaskRepository {
        <<interface>>
    }

    class Task {
        -UUID id
        -String title
        -String description
        -LocalDate dueDate
        -TaskStatus status
        -TaskPriority priority
        -Instant created
        -Instant updated
        +equals(Object) boolean
        +hashCode() int
    }

    class CreateTaskRequestDto {
        <<record>>
        +String title
        +String description
        +LocalDate dueDate
        +TaskPriority priority
    }

    class UpdateTaskRequestDto {
        <<record>>
        +String title
        +String description
        +LocalDate dueDate
        +TaskPriority priority
        +TaskStatus status
    }

    class TaskDto {
        <<record>>
        +UUID id
        +String title
        +String description
        +LocalDate dueDate
        +TaskPriority priority
        +TaskStatus status
    }

    class ErrorDto {
        <<record>>
        +String error
    }

    TaskService <|.. TaskServiceImpl
    TaskMapper <|.. TaskMapperImpl
    TaskController --> TaskService : invokes
    TaskController --> TaskMapper : invokes
    TaskServiceImpl --> TaskRepository : delegates
    TaskRepository ..> Task : manages
    TaskMapperImpl ..> CreateTaskRequestDto : transforms
    TaskMapperImpl ..> UpdateTaskRequestDto : transforms
    TaskMapperImpl ..> TaskDto : outputs
    GlobalExceptionHandler ..> ErrorDto : outputs
```

---

## ⚛️ 3. Frontend TypeScript Domain Contracts

```typescript
// frontend/src/types/task.ts

export enum TaskPriority {
  LOW = "LOW",
  MEDIUM = "MEDIUM",
  HIGH = "HIGH",
}

export enum TaskStatus {
  OPEN = "OPEN",
  COMPLETE = "COMPLETE",
}

export interface Task {
  id: string;
  title: string;
  description?: string;
  dueDate?: string;
  priority: TaskPriority;
  status: TaskStatus;
}
```

```typescript
// frontend/src/lib/api.types.ts

export interface CreateTaskRequestDto {
  title: string;
  description?: string;
  dueDate?: string;
  priority: TaskPriority;
}

export interface UpdateTaskRequestDto {
  title: string;
  description?: string;
  dueDate?: string;
  priority: TaskPriority;
  status: TaskStatus;
}

export interface ApiErrorResponseDto {
  error: string;
}
```
