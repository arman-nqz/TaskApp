# C4 Model - Level 2: Container Diagram

The Container diagram decomposes the Task Management System into its constituent deployable and runnable units (applications, data stores, and containers).

---

## 🖼️ Container Diagram

```mermaid
C4Container
    title Container Diagram for Task Management System

    Person(user, "User", "A user interacting via modern web browser")

    Container_Boundary(c1, "Task Management System") {
        Container(nginx, "Reverse Proxy & Web Server", "Nginx Alpine / Docker Container", "Serves compiled static SPA assets on port 3000 and reverse proxies API traffic to backend on port 8080.")
        Container(spa, "Single-Page Application", "React 19, TypeScript 5.9, Tailwind CSS", "Delivers interactive user interface in browser: task lists, creation/edit modals, priority badges, and status toggles.")
        Container(api, "Backend REST API", "Spring Boot 4, Java 25", "Exposes REST endpoints (/api/v1/tasks), executes domain logic, enforces input validation, and manages transactions.")
        ContainerDb(db, "In-Memory Database", "H2 Database Engine", "Stores tasks, priorities, statuses, and audit timestamps in memory.")
    }

    Rel(user, nginx, "Visits application and loads assets", "HTTP / :3000")
    Rel(nginx, spa, "Delivers static bundles (HTML, JS, CSS)", "Internal Filesystem")
    Rel(user, spa, "Executes actions within browser runtime", "DOM Events")
    Rel(spa, nginx, "Sends task CRUD API requests", "HTTP / JSON (/api/v1/tasks)")
    Rel(nginx, api, "Proxies REST requests", "HTTP / JSON (host.docker.internal:8080)")
    Rel(api, db, "Reads and writes task records", "JDBC")
```

---

## 🔍 Container Specifications

| Container | Technology | Responsibilities | Networking / Ports |
| :--- | :--- | :--- | :--- |
| **Nginx Reverse Proxy** | Nginx Alpine, Docker | - Serves pre-compiled static assets.<br>- Provides SPA routing fallback (`try_files $uri /index.html`).<br>- Proxies `/api/*` to the Spring Boot backend. | Listens on port `3000` (Docker host port mapped `3000:3000`). |
| **Single-Page Application (SPA)** | React 19, TypeScript, Tailwind CSS, Radix UI | - Executes within the user's browser runtime.<br>- Renders responsive UI components.<br>- Dispatches asynchronous API calls via `fetch`.<br>- Manages local state via React Context. | Browser memory / client execution. |
| **Backend REST API** | Java 25, Spring Boot 4.0.9, Spring MVC, Spring Data JPA | - Validates incoming request payloads (`@Valid`).<br>- Translates DTOs to domain models.<br>- Coordinates business logic and timestamp generation.<br>- Intercepts and formats errors via `@ControllerAdvice`. | Listens on host port `8080`. |
| **In-Memory Database** | H2 Database Engine | - Persists table `tasks` with UUID keys.<br>- Automatically created on startup via Hibernate DDL. | Embedded in-process inside Spring Boot JVM (`jdbc:h2:mem:taskdb`). |
