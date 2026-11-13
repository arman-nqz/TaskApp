# C4 Model - Level 1: System Context Diagram

The System Context diagram provides the highest-level view of the software system, showing the boundary of the **Task Management System** and how human actors interact with it.

---

## 🖼️ System Context Diagram

```mermaid
C4Context
    title System Context Diagram for Task Management System

    Person(user, "User / Knowledge Worker", "A person managing personal tasks, priorities, and deadlines.")
    
    System(taskApp, "Task Management System", "Enables users to create, view, update, prioritize, and delete tasks via an intuitive web interface.")

    Rel(user, taskApp, "Manages tasks, tracks progress, and views deadlines", "HTTPS / HTTP")
```

---

## 🔍 Context Elements

### People / Actors

| Element | Role | Description |
| :--- | :--- | :--- |
| **User** | Knowledge Worker | Interacts with the Task Management System through modern web browsers (desktop and mobile) to organize daily activities. |

### Systems

| Element | Classification | Description |
| :--- | :--- | :--- |
| **Task Management System** | Software System | The system under design. Provides complete task lifecycle management (CRUD), due date validation, priority classification, and real-time client feedback. |

### External Dependencies
Currently, the Task Management System is intentionally self-contained:
- It does not depend on third-party SaaS APIs (such as external auth providers or notification services).
- All authentication, notifications, and persistence are self-hosted or embedded in the local environment.
