# 8. Cross-Cutting Concepts

Cross-cutting concepts describe architectural patterns, standards, and rules applied consistently across multiple layers and components.

---

## 8.1 Domain Model & Entity Design

The core domain model centers around the `Task` aggregate entity:

```mermaid
classDiagram
    class Task {
        +UUID id
        +String title
        +String description
        +LocalDate dueDate
        +TaskStatus status
        +TaskPriority priority
        +Instant created
        +Instant updated
        +getId() UUID
        +getTitle() String
        +setTitle(String) void
        +getStatus() TaskStatus
        +setStatus(TaskStatus) void
        +getPriority() TaskPriority
        +setPriority(TaskPriority) void
        +equals(Object) boolean
        +hashCode() int
    }

    class TaskPriority {
        <<enumeration>>
        LOW
        MEDIUM
        HIGH
    }

    class TaskStatus {
        <<enumeration>>
        OPEN
        COMPLETE
    }

    Task --> TaskPriority : has
    Task --> TaskStatus : has
```

### Entity Persistence Constraints
- **Identity (`id`)**: Primary key of type `UUID`, automatically generated via JPA `@GeneratedValue(strategy = GenerationType.UUID)`. Entity equality (`equals` and `hashCode`) is strictly bound to the `id` property, ensuring consistent behavior across JPA persistence states.
- **Title (`title`)**: `nullable = false`, constrained to 255 characters.
- **Description (`description`)**: `length = 1000`, allows null values.
- **Timestamps (`created`, `updated`)**: `created` is marked `updatable = false, nullable = false`. Both utilize `java.time.Instant` representing UTC points in time.
- **Due Date (`dueDate`)**: `java.time.LocalDate` representing date-only values (without time zone bias).

---

## 8.2 Data Transfer Objects (DTO) vs Domain Requests

The system enforces a strict boundary between HTTP serialization contracts and internal domain representations:

```mermaid
graph LR
    subgraph External_Contracts ["External REST DTOs (Records)"]
        CreateDto["CreateTaskRequestDto"]
        UpdateDto["UpdateTaskRequestDto"]
        TaskDto["TaskDto"]
        ErrorDto["ErrorDto"]
    end

    subgraph Internal_Domain ["Internal Domain Representations"]
        CreateReq["CreateTaskRequest"]
        UpdateReq["UpdateTaskRequest"]
        TaskEntity["Task Entity"]
    end

    CreateDto -->|"TaskMapper.fromDto()"| CreateReq
    UpdateDto -->|"TaskMapper.fromDto()"| UpdateReq
    TaskEntity -->|"TaskMapper.toDto()"| TaskDto
```

- **DTOs (`CreateTaskRequestDto`, `UpdateTaskRequestDto`)**: Contain Jakarta validation annotations (`@NotBlank`, `@Length`, `@FutureOrPresent`, `@NotNull`) and validation error message constants.
- **Domain Requests (`CreateTaskRequest`, `UpdateTaskRequest`)**: Clean Java records containing sanitized data passed to `TaskService`. Free from validation annotation dependencies.

---

## 8.3 Centralized Validation Strategy

Data validation occurs at the system ingress boundary before executing business logic:

1. **Controller Binding**: Endpoints decorate input DTOs with `@Valid` (`@Valid @RequestBody CreateTaskRequestDto`).
2. **Constraint Enforcement**:
   - `title`: `@NotBlank` and `@Length(max = 255)` &rarr; *"Title must be between 1 and 255 characters"*
   - `description`: `@Length(max = 1000)` &rarr; *"Description must be less than 1000 characters"*
   - `dueDate`: `@FutureOrPresent` &rarr; *"Due date must be in the future"*
   - `priority`: `@NotNull` &rarr; *"task priority must be provided"*
   - `status`: `@NotNull` &rarr; *"Task status must be provided"* (update endpoint only)
3. **Short-Circuit Error Reporting**: If validation fails, Spring throws `MethodArgumentNotValidException`, bypassing controller method execution entirely.

---

## 8.4 Global Exception Handling & Error Response Contract

The `GlobalExceptionHandler` (`@ControllerAdvice`) provides deterministic error responses:

```json
{
  "error": "Title must be between 1 and 255 characters"
}
```

### Exception Mappings:

| Exception | HTTP Status Code | Response Body | Description |
| :--- | :--- | :--- | :--- |
| `MethodArgumentNotValidException` | `400 Bad Request` | `{"error": "<first validation failure message>"}` | Triggered on Jakarta validation failures. Extracts the first field error to provide actionable feedback. |
| `TaskNotFoundException` | `400 Bad Request` | `{"error": "Task with ID '<UUID>' not found"}` | Triggered when updating a non-existent task. Extracts the missing ID. |

---

## 8.5 Client-Side State Management

State management in the React frontend uses the React Context API with custom hooks:

```mermaid
flowchart TD
    Provider["TodoListProvider (task-provider.tsx)"]
    State["Internal State:\n- tasks: Task[]\n- selectedTask: Task | undefined\n- loading: boolean\n- error: string | undefined"]

    Actions["Action Dispatchers:\n- refresh()\n- createTask(request)\n- updateTask(id, request)\n- deleteTask(id)\n- selectTask(id)\n- toggleTaskStatus(id)"]

    Hook["useTasks() Custom Hook"]
    Components["Consuming Components:\n- TaskManagement\n- TodoCard\n- Dialogs"]

    Provider --> State
    Provider --> Actions
    State --> Hook
    Actions --> Hook
    Hook --> Components
```

### Design Principles:
- **Single Source of Truth**: All tasks are stored in a centralized array inside `TodoListProvider`.
- **Automatic Synchronization**: After mutating operations (`createTask`, `updateTask`, `deleteTask`), the provider executes `refresh()` to synchronize the UI with server state.
- **Fail-Safe Error State**: Any caught network or HTTP error sets the `error` state in context and bubbles to UI alerts or toast notifications via Sonner.
