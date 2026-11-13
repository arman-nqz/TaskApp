# 6. Runtime View

The Runtime View describes the dynamic behavior, interactions, and call sequences for key architectural scenarios.

---

## 6.1 Scenario 1: Task Creation Workflow

This scenario illustrates the complete end-to-end flow when a user creates a new task.

```mermaid
sequenceDiagram
    autonumber
    actor User as User
    participant Dialog as TaskCreateDialog
    participant Provider as TodoListProvider
    participant API as api.ts
    participant Nginx as Nginx Proxy (:3000)
    participant Controller as TaskController (:8080)
    participant Mapper as TaskMapperImpl
    participant Service as TaskServiceImpl
    participant Repo as TaskRepository
    participant DB as H2 Database

    User->>Dialog: Fills form (title, priority, date, desc) & clicks "Create"
    Dialog->>Provider: createTask(createTaskRequestDto)
    Provider->>API: createTask(dto)
    API->>Nginx: POST /api/v1/tasks { JSON }
    Nginx->>Controller: POST http://backend:8080/api/v1/tasks
    Note over Controller: Spring validates @Valid CreateTaskRequestDto
    Controller->>Mapper: fromDto(createTaskRequestDto)
    Mapper-->>Controller: CreateTaskRequest (domain record)
    Controller->>Service: createTask(request)
    Service->>Service: Instant.now(), default status=OPEN
    Service->>Repo: save(task)
    Repo->>DB: INSERT INTO tasks (...)
    DB-->>Repo: Saved row with generated UUID
    Repo-->>Service: Task (Entity)
    Service-->>Controller: Task (Entity)
    Controller->>Mapper: toDto(task)
    Mapper-->>Controller: TaskDto (id, title, status, etc.)
    Controller-->>Nginx: 201 Created { TaskDto JSON }
    Nginx-->>API: 201 Created { TaskDto JSON }
    API-->>Provider: Task object
    Provider->>Provider: refresh() (re-fetch full list)
    Provider-->>Dialog: Success
    Dialog-->>User: Closes dialog & displays task card
```

---

## 6.2 Scenario 2: Task Status Toggle (Quick Action)

When the user clicks the checkbox on a `TodoCard` to toggle a task from `OPEN` to `COMPLETE` or vice versa:

```mermaid
sequenceDiagram
    autonumber
    actor User as User
    participant Card as TodoCard
    participant Provider as TodoListProvider
    participant API as api.ts
    participant Controller as TaskController
    participant Service as TaskServiceImpl
    participant Repo as TaskRepository

    User->>Card: Clicks completion checkbox
    Card->>Provider: toggleTaskStatus(taskId)
    Provider->>Provider: Flips status (OPEN <-> COMPLETE)
    Provider->>API: updateTask(taskId, updateTaskRequestDto)
    API->>Controller: PUT /api/v1/tasks/{taskId}
    Controller->>Service: updateTask(taskId, request)
    Service->>Repo: findById(taskId)
    Repo-->>Service: Optional<Task>
    Service->>Service: Mutates status & updated timestamp
    Service->>Repo: save(task)
    Repo-->>Service: Updated Task
    Service-->>Controller: Updated Task
    Controller-->>API: 200 OK { TaskDto }
    API-->>Provider: Updated Task
    Provider->>Provider: refresh() (syncs UI state)
    Provider-->>Card: Re-renders with checked style & strike-through
```

---

## 6.3 Scenario 3: Task Retrieval on Application Startup

When the user opens the application in a browser:

```mermaid
sequenceDiagram
    autonumber
    actor Browser as Browser / User
    participant Nginx as Nginx Proxy (:3000)
    participant App as React App
    participant Provider as TodoListProvider
    participant API as api.ts
    participant Controller as TaskController
    participant Service as TaskServiceImpl
    participant Repo as TaskRepository

    Browser->>Nginx: GET http://localhost:3000/
    Nginx-->>Browser: index.html, JS, CSS bundles
    Browser->>App: Executes main.tsx & mounts TodoListProvider
    App->>Provider: useEffect() triggers refresh()
    Provider->>API: listTasks()
    API->>Nginx: GET /api/v1/tasks
    Nginx->>Controller: Forward to backend:8080
    Controller->>Service: listTasks()
    Service->>Repo: findAll(Sort.by(ASC, "created"))
    Repo-->>Service: List<Task> sorted by creation time
    Service-->>Controller: List<Task>
    Controller->>Controller: Stream map entity to TaskDto
    Controller-->>API: 200 OK [ TaskDto, ... ]
    API-->>Provider: Array of Task domain items
    Provider->>Provider: setTasks(tasks), setLoading(false)
    Provider-->>App: Re-renders TaskManagement view with task list
```

---

## 6.4 Scenario 4: Validation Failure and Exception Handling

When a client submits invalid data (e.g., an empty title or a due date in the past):

```mermaid
sequenceDiagram
    autonumber
    actor User as Client / User
    participant Controller as TaskController
    participant Handler as GlobalExceptionHandler
    participant DTO as ErrorDto

    User->>Controller: POST /api/v1/tasks {"title": "", "priority": "HIGH"}
    Note over Controller: Spring WebMVC intercepts @Valid.<br>Jakarta Validator detects @NotBlank failure on title.
    Controller--xHandler: Throws MethodArgumentNotValidException
    Handler->>Handler: handleValidationExceptions(ex)
    Handler->>Handler: Extracts first FieldError message ("Title must be between 1 and 255 characters")
    Handler->>DTO: new ErrorDto("Title must be between 1 and 255 characters")
    Handler-->>User: HTTP 400 Bad Request { "error": "Title must be between 1 and 255 characters" }
```
