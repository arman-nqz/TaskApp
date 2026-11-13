# C4 Model - Level 3: Component Diagrams

The Component diagrams zoom inside individual containers to reveal their internal structural blocks, components, interfaces, and dependencies.

---

## 🖼️ 1. Backend API Components (`task`)

```mermaid
C4Component
    title Component Diagram for Backend REST API (Spring Boot)

    Container_Boundary(b1, "Backend REST API") {
        Component(ctrl, "TaskController", "Spring REST Controller", "Handles incoming HTTP requests under /api/v1/tasks, coordinates DTO mapping and calls TaskService.")
        Component(advisor, "GlobalExceptionHandler", "Spring Controller Advice", "Intercepts MethodArgumentNotValidException and TaskNotFoundException, returning standardized ErrorDto responses.")
        Component(mapper, "TaskMapper / TaskMapperImpl", "Spring Component", "Converts external DTOs (CreateTaskRequestDto, UpdateTaskRequestDto) to internal domain requests and entities to TaskDto.")
        Component(svc, "TaskService / TaskServiceImpl", "Spring Service", "Executes business logic: computes timestamps, applies status defaults, validates existence, and calls repository.")
        Component(repo, "TaskRepository", "Spring Data JPA Repository", "Executes database CRUD operations and sorting queries on Task entities.")
        Component(entity, "Task Entity", "JPA Entity", "Relational mapping for the 'tasks' table with UUID primary key.")
    }

    Container(nginxProxy, "Nginx Proxy", "Reverse Proxy", "Forwards /api/v1/tasks")
    ContainerDb(dbEngine, "H2 Database", "In-Memory Relational Engine", "Stores task records")

    Rel(nginxProxy, ctrl, "Dispatches HTTP Requests", "HTTP/REST")
    Rel(ctrl, mapper, "Transforms requests & responses", "Method Call")
    Rel(ctrl, svc, "Invokes task operations", "Method Call")
    Rel(ctrl, advisor, "Exceptions intercepted by", "Spring Context")
    Rel(svc, repo, "Persists / queries tasks", "Method Call")
    Rel(repo, entity, "Manages instance lifecycle", "ORM")
    Rel(repo, dbEngine, "Executes SQL statements", "JDBC")
```

### Backend Component Catalog

| Component | Type | Responsibility | Interface / Contract |
| :--- | :--- | :--- | :--- |
| **`TaskController`** | `@RestController` | Entry point for `/api/v1/tasks`. Enforces validation on input DTOs, returns `ResponseEntity<TaskDto>` or `ResponseEntity<List<TaskDto>>`. | `POST /`, `GET /`, `PUT /{taskId}`, `DELETE /{taskId}` |
| **`GlobalExceptionHandler`** | `@ControllerAdvice` | Intercepts bean validation failures and entity missing exceptions. Formats consistent error payloads. | `@ExceptionHandler(MethodArgumentNotValidException.class)`, `@ExceptionHandler(TaskNotFoundException.class)` |
| **`TaskMapper`** | `@Component` | Bidirectional conversion between API DTOs and internal domain representations. | `fromDto(CreateTaskRequestDto)`, `fromDto(UpdateTaskRequestDto)`, `toDto(Task)` |
| **`TaskService`** | `@Service` | Pure business logic: enforces chronological sort (`created ASC`), initializes `Instant.now()` timestamps, updates status. | `createTask()`, `listTasks()`, `updateTask()`, `deleteTask()` |
| **`TaskRepository`** | `@Repository` | JPA repository abstraction over `Task` entity. | Extends `JpaRepository<Task, UUID>` |

---

## 🖼️ 2. Frontend SPA Components (`frontend`)

```mermaid
C4Component
    title Component Diagram for Frontend SPA (React 19)

    Container_Boundary(f1, "React 19 Single-Page Application") {
        Component(app, "App", "React Component", "Top-level application root rendering TaskManagement.")
        Component(mgmt, "TaskManagement", "React Component", "Main dashboard view coordinating modals and task card grid.")
        Component(frame, "AppFrame", "Layout Component", "Provides application layout shell with sidebar and banner.")
        Component(card, "TodoCard", "React Component", "Renders task card: priority badge, due date, completion checkbox, action buttons.")
        Component(dialogs, "Modals / Dialogs", "TaskCreateDialog, TaskEditDialog, TaskDeleteDialog", "Interactive Radix UI dialogs for user input and confirmation.")
        Component(provider, "TodoListProvider (useTasks)", "React Context Provider", "Centralized state container managing tasks array, loading, error, and actions.")
        Component(client, "api.ts", "TypeScript Module", "Low-level HTTP fetch client communicating with /api/v1/tasks.")
    }

    Container(nginxServer, "Nginx Web Server", "Container Port 3000", "Proxies /api requests")

    Rel(app, mgmt, "Renders", "JSX")
    Rel(mgmt, frame, "Renders within", "JSX")
    Rel(mgmt, card, "Renders list of", "JSX")
    Rel(mgmt, dialogs, "Controls open state of", "React State")
    Rel(mgmt, provider, "Subscribes to tasks & selectedTask", "useTasks()")
    Rel(card, provider, "Dispatches toggleTaskStatus, selectTask", "useTasks()")
    Rel(dialogs, provider, "Dispatches createTask, updateTask, deleteTask", "useTasks()")
    Rel(provider, client, "Executes HTTP operations", "Async/Await")
    Rel(client, nginxServer, "Sends HTTP /api/v1/tasks requests", "Fetch API")
```

### Frontend Component Catalog

| Component | Responsibility |
| :--- | :--- |
| **`TodoListProvider`** | Implements React Context (`TaskContext`), holding `tasks`, `selectedTask`, `loading`, `error`, and exposing mutation functions. Automatically re-synchronizes via `refresh()`. |
| **`TaskManagement`** | Renders header counters (`data-testid="task-count-total"`), orchestrates visibility state for creation, editing, and deletion dialogs. |
| **`TodoCard`** | Renders an individual task with styling according to `TaskPriority` and `TaskStatus`. Fires `toggleTaskStatus(taskId)`. |
| **`TaskCreateDialog` / `TaskEditDialog` / `TaskDeleteDialog`** | Controlled dialog forms capturing title, description, priority, and date inputs, with client-side validation before dispatching to the provider. |
| **`api.ts`** | Encapsulates Fetch API requests, stringifies request bodies, parses JSON responses, and translates HTTP error statuses into thrown errors. |
