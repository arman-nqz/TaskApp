# ADR-0003: Layered Backend Architecture Pattern

## Status
Accepted

## Date
2026-11-13

## Context
A backend application handling HTTP endpoints, data validation, domain business rules, and relational database persistence needs a clean structural organization to avoid monolithic "fat controllers" or tight coupling between HTTP concerns and database persistence.

## Decision
We adopted the classic **Layered Architecture (N-Tier)** pattern with strict horizontal boundaries:

1. **Presentation Layer (`controller`)**:
   - `TaskController`: Handles incoming HTTP requests, maps URLs, initiates validation (`@Valid`), and formats HTTP responses (`ResponseEntity`).
   - `GlobalExceptionHandler`: Centralizes error interception (`@ControllerAdvice`) and constructs standardized error payloads.
2. **Mapping Layer (`mapper`)**:
   - `TaskMapper` / `TaskMapperImpl`: Transforms external DTOs to domain request objects and entities to response DTOs.
3. **Business Service Layer (`service`)**:
   - `TaskService` / `TaskServiceImpl`: Coordinates business operations, generates audit timestamps (`created`, `updated`), sets defaults (`TaskStatus.OPEN`), and throws business domain exceptions (`TaskNotFoundException`).
4. **Data Access Layer (`repository`)**:
   - `TaskRepository`: Spring Data JPA interface managing entity persistence and queries.
5. **Domain Layer (`domain`)**:
   - `Task` entity, domain requests, and DTO records.

## Consequences

### Positive
- **High Cohesion & Low Coupling**: Each layer has a single, well-defined responsibility.
- **Testability**: Service logic can be tested independently of HTTP mechanics; repositories can be tested independently of controllers.
- **Consistency**: Well-understood industry standard pattern familiar to Java and Spring developers.

### Negative
- Introducing a new property requires updating multiple layers (DTO, Mapper, Domain Request, Entity).
