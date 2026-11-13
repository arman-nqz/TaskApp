# 12. Glossary

This glossary defines ubiquitous domain terms and technical terminology used throughout the codebase and architecture documentation.

---

## 12.1 Domain Terminology

| Term | Definition |
| :--- | :--- |
| **Task** | The primary business entity representing a unit of work or todo item with a title, description, priority, status, and optional due date. |
| **Task Priority** | Urgency classification assigned to a task. Can be `LOW`, `MEDIUM`, or `HIGH`. |
| **Task Status** | Lifecycle phase of a task. Can be either `OPEN` (active/pending) or `COMPLETE` (done/resolved). |
| **Due Date** | The target completion date for a task (`LocalDate`). Must be present or in the future upon creation or editing. |
| **Task Count** | Real-time counter of existing tasks displayed in the UI header. |

---

## 12.2 Technical Terminology

| Term | Definition |
| :--- | :--- |
| **arc42** | A widely adopted open-source architecture template (ISO/IEC/IEEE 42010 compliant) created by Dr. Peter Hruschka and Dr. Gernot Starke. |
| **C4 Model** | An architectural visualization framework created by Simon Brown that decomposes systems across four hierarchical levels: Context, Containers, Components, and Code. |
| **ADR** | Architecture Decision Record: A structured markdown document capturing a software design decision, its context, consequences, and status (Michael Nygard format). |
| **DTO (Data Transfer Object)** | An object that carries data between processes (in this system, between the REST API client and the Spring controller) to avoid exposing internal entity models. |
| **Entity** | An object possessing a unique identity and lifecycle managed by an Object-Relational Mapping (ORM) framework (JPA / Hibernate). |
| **H2 Database** | A lightweight, fast, open-source relational database engine written in Java, embedded in-memory for testing and local development. |
| **Jakarta Validation** | The standard Java specification for declarative bean validation annotations (`@NotBlank`, `@Length`, `@FutureOrPresent`, `@NotNull`). |
| **Nginx** | High-performance HTTP server and reverse proxy used in the frontend Docker container to serve static assets and proxy `/api/*` calls. |
| **React Context** | A React mechanism for passing data through the component tree without manually passing props down at every level, used for centralized client state (`TaskContext`). |
| **Reverse Proxy** | An intermediate server that receives requests from clients and forwards them to backend servers, shielding backend topology and solving browser CORS restrictions. |
| **Vite** | Next-generation frontend build tooling offering fast local development server start via native ES modules and Rollup-based production builds. |
