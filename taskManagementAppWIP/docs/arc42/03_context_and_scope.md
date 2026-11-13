# 3. Context and Scope

The context boundary isolates the Task Management System from external actors, defining inbound and outbound data flows, protocols, and technical dependencies.

---

## 3.1 Business Context

The business context illustrates the interaction between external domain actors (users) and the system.

```mermaid
flowchart TD
    User([User / Knowledge Worker])

    subgraph SystemBoundary ["Task Management System"]
        App["Task Management Web Application"]
    end

    User -->|"Creates tasks, updates status, sets deadlines"| App
    App -->|"Provides task overview, deadlines, priority feedback"| User
```

### Business Actors and Use Cases

| Actor | Responsibility | System Interaction |
| :--- | :--- | :--- |
| **End User** | Manages daily workloads and project checklists. | - Views list of active tasks.<br>- Creates new task with title, priority, due date, description.<br>- Toggles task completion status.<br>- Edits existing task attributes.<br>- Deletes obsolete tasks. |

---

## 3.2 Technical Context

The technical context describes physical communication paths, transport protocols, network ports, and payloads traversing the system boundary.

```mermaid
flowchart LR
    Browser["User Browser (Client)"]
    
    subgraph HostEnvironment ["Host / Container Environment"]
        subgraph Container ["Docker Container (ui)"]
            Nginx["Nginx Reverse Proxy\n(Port 3000)"]
            StaticFiles["React SPA Build Assets\n(/usr/share/nginx/html)"]
        end

        subgraph BackendHost ["Spring Boot Runtime"]
            API["Task REST API\n(Port 8080)"]
            H2DB[("Embedded H2 Database\n(JDBC In-Memory)")]
        end
    end

    Browser -->|"HTTP / HTML, JS, CSS\nGET /:3000"| Nginx
    Browser -->|"HTTP / JSON REST\n/api/v1/tasks"| Nginx
    Nginx -->|"Internal File Read"| StaticFiles
    Nginx -->|"HTTP Proxy (Pass-through)\nhttp://${BACKEND_HOST}:${BACKEND_PORT}/api/"| API
    API -->|"JDBC Protocol"| H2DB
```

### Technical Interface Specifications

| Interface | Protocol | Port / Location | Data Format | Description |
| :--- | :--- | :--- | :--- | :--- |
| **User Interface** | HTTP 1.1 | `http://localhost:3000/` | HTML5, CSS3, JavaScript | Serves the single-page application bundle and visual UI components. |
| **Frontend Reverse Proxy** | HTTP 1.1 | `:3000/api/*` &rarr; `:8080/api/*` | JSON (UTF-8) | Forwards API requests from the browser to the backend without triggering browser CORS restrictions. |
| **Backend REST API** | HTTP 1.1 | `http://localhost:8080/api/v1/tasks` | JSON (UTF-8) | Exposes CRUD operations, accepts request payloads, and responds with status codes and DTOs. |
| **Persistence Link** | JDBC | `jdbc:h2:mem:taskdb` | SQL / Binary | Inter-process communication between Spring Data JPA (Hibernate) and the in-memory H2 database engine. |
