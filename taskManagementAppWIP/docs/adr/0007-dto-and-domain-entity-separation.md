# ADR-0007: Separation of DTOs and Persistence Domain Entities

## Status
Accepted

## Date
2026-11-13

## Context
In REST API architectures backed by Object-Relational Mapping (ORM) frameworks like Hibernate/JPA, developers often face the choice between:
1. Reusing JPA entities directly as request and response bodies in REST controllers.
2. Introducing separate Data Transfer Object (DTO) contracts and mapping them to domain entities.

Reusing JPA entities directly often causes critical issues:
- Over-fetching or accidental exposure of internal database fields (e.g., version columns, password hashes).
- Mass-assignment vulnerabilities (clients sending arbitrary fields that get saved to the database).
- Lazy-initialization and recursive serialization errors during JSON conversion.
- Coupling API contract evolution directly to database schema evolution.

## Decision
We decided to strictly enforce the **Separation of DTOs and Persistence Entities**:
- Request/Response contracts are modeled as immutable Java records in `com.armannqz.task.domain.dto` (`CreateTaskRequestDto`, `UpdateTaskRequestDto`, `TaskDto`, `ErrorDto`).
- Persistence models are modeled as JPA entities in `com.armannqz.task.domain.entity` (`Task`).
- An explicit mapping component (`TaskMapper` / `TaskMapperImpl`) handles conversions between DTOs, internal domain requests (`CreateTaskRequest`, `UpdateTaskRequest`), and entities.

## Consequences

### Positive
- **Security & Integrity**: Prevents mass assignment and information leakage; incoming payloads can only set fields explicitly defined on request DTOs.
- **Contract Stability**: Database schema refactorings do not alter external API response structures.
- **Independent Validation**: Validation rules (`@NotBlank`, `@Length`, `@FutureOrPresent`) belong to DTOs without polluting database entity definitions.

### Negative
- Requires maintaining mapping code (`TaskMapperImpl`) and multiple classes representing similar data.
