# 5. Building Block View

The Building Block View provides a static decomposition of the system into subsystems, components, and packages, descending through levels of abstraction (aligned with C4 Levels 2 and 3).

---

## 5.1 Level 1: Whitebox Overall System

The overall system comprises three primary runtime units:
1. **Frontend Container (`ui`)**: Serves single-page application assets and proxies API calls via Nginx.
2. **Backend Application (`task`)**: Spring Boot REST application executing business logic and data access.
3. **Database (`h2`)**: In-memory relational database storing task records.

```mermaid
graph TD
    subgraph SystemBoundary ["Task Management System"]
        subgraph Container_Frontend ["Frontend Container (Port 3000)"]
            Nginx["Nginx Reverse Proxy & Web Server"]
            SPA["React 19 SPA (Client-side bundle)"]
            Nginx -->|Serves| SPA
        end

        subgraph Host_Backend ["Backend Process (Port 8080)"]
            SpringAPI["Spring Boot Backend (WebMVC + JPA)"]
        end

        subgraph Embedded_DB ["Data Tier"]
            H2DB[("H2 Database Engine")]
        end
    end

    SPA -->|"HTTP Requests (/api/v1/tasks)"| Nginx
    Nginx -->|"Proxy Pass (HTTP)"| SpringAPI
    SpringAPI -->|"JDBC"| H2DB
```

---

## 5.2 Level 2: Subsystem Decomposition

### 5.2.1 Backend Subsystem (`com.armannqz.task`)

The backend is decomposed into distinct, cohesive packages adhering to a strict layered architecture:

```mermaid
graph TD
    subgraph PresentationLayer ["1. Presentation Layer"]
        TaskController["TaskController\n(@RestController)"]
        GlobalExceptionHandler["GlobalExceptionHandler\n(@ControllerAdvice)"]
    end

    subgraph MappingLayer ["2. Mapping & Contract Layer"]
        TaskMapper["TaskMapper\n(Interface)"]
        TaskMapperImpl["TaskMapperImpl\n(@Component)"]
        TaskMapper -.->|implements| TaskMapperImpl
        DTOs["DTO Records:\nCreateTaskRequestDto\nUpdateTaskRequestDto\nTaskDto\nErrorDto"]
        DomainRequests["Domain Request Records:\nCreateTaskRequest\nUpdateTaskRequest"]
    end

    subgraph ServiceLayer ["3. Business Service Layer"]
        TaskService["TaskService\n(Interface)"]
        TaskServiceImpl["TaskServiceImpl\n(@Service)"]
        TaskService -.->|implements| TaskServiceImpl
    end

    subgraph DataAccessLayer ["4. Data Access Layer"]
        TaskRepository["TaskRepository\n(Interface extends JpaRepository)"]
        TaskEntity["Task\n(@Entity)"]
    end

    TaskController -->|"Calls"| TaskService
    TaskController -->|"Uses"| TaskMapper
    TaskController -->|"Validates"| DTOs
    GlobalExceptionHandler -.->|"Intercepts Exceptions"| TaskController
    GlobalExceptionHandler -->|"Returns"| DTOs

    TaskMapperImpl -->|"Transforms"| DTOs
    TaskMapperImpl -->|"Produces"| DomainRequests
    TaskMapperImpl -->|"Maps Entity to"| DTOs

    TaskServiceImpl -->|"Executes logic with"| DomainRequests
    TaskServiceImpl -->|"Calls CRUD methods"| TaskRepository
    TaskServiceImpl -->|"Manipulates"| TaskEntity

    TaskRepository -->|"Persists"| TaskEntity
```

#### Detailed Responsibilities of Backend Blocks

| Component / Block | Package | Responsibilities | Collaborators |
| :--- | :--- | :--- | :--- |
| `TaskController` | `com.armannqz.task.controller` | Exposes REST endpoints (`/api/v1/tasks`), receives HTTP requests, invokes bean validation, and delegates business requests to `TaskService`. | `TaskService`, `TaskMapper`, `CreateTaskRequestDto`, `UpdateTaskRequestDto` |
| `GlobalExceptionHandler` | `com.armannqz.task.controller` | Centralized exception translation via `@ControllerAdvice`. Intercepts `MethodArgumentNotValidException` and `TaskNotFoundException`, returning standardized `ErrorDto` with HTTP 400. | `ErrorDto`, `TaskNotFoundException` |
| `TaskService` / `TaskServiceImpl` | `com.armannqz.task.service` | Contains business rules: calculates audit timestamps (`created`, `updated`), initializes default status (`OPEN`), enforces task presence, sorts tasks chronologically. | `TaskRepository`, `Task`, `CreateTaskRequest`, `UpdateTaskRequest` |
| `TaskMapper` / `TaskMapperImpl` | `com.armannqz.task.mapper` | Decouples external API DTO records from internal domain request records and JPA entities. | `TaskDto`, `CreateTaskRequestDto`, `UpdateTaskRequestDto`, `Task` |
| `TaskRepository` | `com.armannqz.task.repository` | Spring Data JPA interface extending `JpaRepository<Task, UUID>`. Provides standard CRUD operations and query derivation. | `Task`, Spring Data JPA runtime |
| `Task` | `com.armannqz.task.domain.entity` | JPA Entity mapped to table `tasks`. Enforces identity equality based on UUID. | Hibernate / JPA Engine |

---

### 5.2.2 Frontend Subsystem (`frontend/src`)

The frontend application follows a component-driven architecture organized around shared context state:

```mermaid
graph TD
    subgraph UI_Components ["Presentation Components"]
        App["App.tsx"]
        TaskManagement["TaskManagement.tsx"]
        AppFrame["AppFrame.tsx\n(Sidebar + BottomBanner)"]
        TodoCard["TodoCard.tsx\n(Task Item View)"]
        CreateDialog["TaskCreateDialog.tsx"]
        EditDialog["TaskEditDialog.tsx"]
        DeleteDialog["TaskDeleteDialog.tsx"]
    end

    subgraph State_Management ["State Management Layer"]
        TaskProvider["TodoListProvider / useTasks\n(TaskContext)"]
    end

    subgraph Network_Layer ["Infrastructure / Network Layer"]
        ApiClient["api.ts\n(Fetch API Client)"]
        ApiTypes["api.types.ts / task.ts"]
    end

    App --> TaskManagement
    TaskManagement --> AppFrame
    TaskManagement --> TodoCard
    TaskManagement --> CreateDialog
    TaskManagement --> EditDialog
    TaskManagement --> DeleteDialog

    TaskManagement -->|"Consumes state & actions"| TaskProvider
    TodoCard -->|"Triggers edit/delete/toggle"| TaskProvider
    CreateDialog -->|"Submits new task"| TaskProvider
    EditDialog -->|"Submits changes"| TaskProvider
    DeleteDialog -->|"Confirms delete"| TaskProvider

    TaskProvider -->|"Executes HTTP calls"| ApiClient
    ApiClient -->|"Transfers DTOs"| ApiTypes
```

#### Detailed Responsibilities of Frontend Blocks

| Component / Block | Directory | Responsibilities | Collaborators |
| :--- | :--- | :--- | :--- |
| `TaskManagement` | `components/` | Master view layout: header counter, task list iteration, modal state coordination. | `useTasks`, `AppFrame`, `TodoCard`, dialog components |
| `TodoListProvider` | `providers/` | Manages single source of truth for tasks, active task selection, loading state, error states, and asynchronous dispatchers. | `api.ts`, React Context |
| `TodoCard` | `components/` | Renders individual task attributes (title, due date, priority badges, completion checkbox) and triggers action modals. | `useTasks`, Radix UI Checkbox, Lucide icons |
| `TaskCreateDialog` / `TaskEditDialog` / `TaskDeleteDialog` | `components/` | Modal dialogues capturing form inputs, executing client validations, and dispatching mutations. | `useTasks`, Radix UI Dialog, Input/Select UI |
| `api.ts` | `lib/` | Low-level fetch wrapper communicating with `/api/v1/tasks`. Unpacks server errors into descriptive Error instances. | `api.types.ts`, Browser Fetch API |
