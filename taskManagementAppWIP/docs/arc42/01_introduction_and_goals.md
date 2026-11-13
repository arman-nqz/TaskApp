# 1. Introduction and Goals

## 1.1 Requirements Overview

The Task Management Application is a productivity tool enabling individuals and teams to track personal and project tasks throughout their lifecycle. 

### Key Functional Capabilities

1. **Task Creation (`POST /api/v1/tasks`)**:
   - Create tasks with a mandatory `title` (1 to 255 characters).
   - Optional `description` (up to 1000 characters).
   - Optional `dueDate` (must be present or in the future: `@FutureOrPresent`).
   - Mandatory `priority` (`LOW`, `MEDIUM`, `HIGH`).
   - Defaults initial status to `OPEN` and records creation and update timestamps (`Instant.now()`).

2. **Task Retrieval (`GET /api/v1/tasks`)**:
   - List all persisted tasks sorted chronologically by creation timestamp in ascending order.
   - Return clean Data Transfer Objects (`TaskDto`) preventing database entity leak.

3. **Task Modification (`PUT /api/v1/tasks/{taskId}`)**:
   - Update `title`, `description`, `dueDate`, `priority`, and `status` (`OPEN` vs `COMPLETE`).
   - Enforce data validation on title length, description length, and future dates.
   - Automatically update the `updated` timestamp.
   - Return HTTP 400 with a descriptive error message if the task is not found.

4. **Task Deletion (`DELETE /api/v1/tasks/{taskId}`)**:
   - Delete task by UUID idempotently.
   - Return HTTP 204 No Content upon success.

5. **Client-Side Task Filtering & Quick Actions**:
   - One-click status toggle between `OPEN` and `COMPLETE`.
   - Priority badge visualizations (visual differentiation for Low, Medium, High).
   - Dynamic task count indicator.
   - Modal dialogs for creation, edition, and deletion confirmation.

---

## 1.2 Quality Goals

The architecture is designed to satisfy the following prioritized quality attributes (aligned with ISO/IEC 25010):

| Priority | Quality Attribute | Scenario / Goal | Architectural Strategy |
| :---: | :--- | :--- | :--- |
| **1** | **Maintainability & Modularity** | New features or entity attributes can be added without ripple effects across unrelated layers. | Strict Layered Architecture (`Controller` &rarr; `Service` &rarr; `Repository`), explicit DTO-to-Domain mappers (`TaskMapper`), and decoupling between presentation and business logic. |
| **2** | **Usability & Responsiveness** | End users experience instant UI feedback and clean error notifications when actions succeed or fail. | Client-side optimistic/reactive updates via React Context, accessible Radix UI primitives, toast feedback via Sonner, and structured JSON error responses. |
| **3** | **Reliability & Data Integrity** | Invalid inputs (empty titles, past due dates, strings over length) are rejected consistently with clear explanations. | Declarative bean validation (`jakarta.validation` annotations on DTOs) evaluated at the controller boundary and centralized in `GlobalExceptionHandler`. |
| **4** | **Deployability & Portability** | Developers and CI environments can launch the system reliably with minimal host configuration. | Containerized frontend with multi-stage build, dynamic Nginx proxy templating, Maven wrapper (`./mvnw`), and self-contained embedded H2 database. |

---

## 1.3 Stakeholders

| Role / Stakeholder | Description | Key Concerns & Expectations |
| :--- | :--- | :--- |
| **End Users** | People using the application to manage daily tasks. | Snappy UI, intuitive task creation/completion, error clarity, and zero data loss during active sessions. |
| **Software Engineers** | Developers maintaining, extending, or debugging the codebase. | Clean separation of concerns, strong type safety (TypeScript & Java), testability, idiomatic patterns, clear documentation. |
| **DevOps / Operations** | Engineers packaging and deploying the system. | Predictable container builds, minimal dependencies, environment-variable-driven configuration (`BACKEND_HOST`, `BACKEND_PORT`). |
| **QA / Test Engineers** | Engineers validating functional correctness and edge cases. | Deterministic test fixtures, easy mockability (`MockMvc`, `SpringBootTest`), reproducible environments. |
