# 9. Architectural Decisions

This section provides an index and summary of the formal **Architecture Decision Records (ADRs)** documented for the Task Management Application. Detailed ADR files are maintained in the [`docs/adr/`](../adr/README.md) directory following the Michael Nygard format.

---

## 9.1 Architecture Decision Records (ADR) Summary

| ADR ID | Title | Status | Date | Summary of Decision |
| :--- | :--- | :---: | :---: | :--- |
| **[ADR-0001](../adr/0001-record-architecture-decisions.md)** | Record Architecture Decisions | Accepted | 2026-11 | Adopt Michael Nygard's Architecture Decision Record (ADR) format to systematically document significant architectural choices. |
| **[ADR-0002](../adr/0002-decoupled-spa-and-rest-api.md)** | Decoupled SPA Frontend and REST API | Accepted | 2026-11 | Separate frontend (React SPA) and backend (Spring Boot REST API) into independent runtime units communicating over HTTP/JSON. |
| **[ADR-0003](../adr/0003-spring-boot-layered-backend.md)** | Layered Backend Architecture | Accepted | 2026-11 | Structure backend into Presentation (Controller), Business Logic (Service), and Data Access (Repository) layers. |
| **[ADR-0004](../adr/0004-in-memory-h2-database.md)** | In-Memory H2 Database for Development | Accepted | 2026-11 | Utilize embedded in-memory H2 database to enable frictionless local development and automated testing without external infrastructure. |
| **[ADR-0005](../adr/0005-nginx-reverse-proxy-container.md)** | Nginx Reverse Proxy Container | Accepted | 2026-11 | Package frontend into an Nginx Alpine container acting as both static web server and reverse proxy for backend API calls. |
| **[ADR-0006](../adr/0006-react-context-state-management.md)** | React Context API for Client-Side State | Accepted | 2026-11 | Use React Context API (`TodoListProvider` / `useTasks`) for state management instead of heavyweight external state libraries (Redux). |
| **[ADR-0007](../adr/0007-dto-and-domain-entity-separation.md)** | Separation of DTOs and Domain Entities | Accepted | 2026-11 | Maintain strict boundary between REST request/response DTOs and JPA database entities, mediated by `TaskMapper`. |
