# ADR-0002: Decoupled Single-Page Application (SPA) and REST API

## Status
Accepted

## Date
2026-11-13

## Context
When architecting the task management application, we evaluated two primary architectural paradigms:
1. **Server-Side Rendered (SSR) Monolith**: Using Spring Boot MVC with Thymeleaf or JSP templates rendering HTML directly on the server.
2. **Decoupled Client-Server**: An independent Single-Page Application (SPA) built with modern frontend tools (React, TypeScript, Vite) communicating with a stateless Spring Boot REST API over HTTP/JSON.

The application requires rich, instantaneous interactivity (modal dialogs, inline status toggles, real-time counters, responsive layouts).

## Decision
We decided to adopt a **Decoupled Client-Server Architecture**:
- Frontend: Single-Page Application using React 19, TypeScript, and Tailwind CSS.
- Backend: RESTful Web Service using Spring Boot 4 and Java 25.
- Communication: Standard HTTP JSON endpoints under `/api/v1/tasks`.

## Consequences

### Positive
- **Rich User Experience**: Smooth client-side state transitions without full-page reloads.
- **Independent Evolution**: Frontend and backend codebases, tooling, and dependencies evolve independently.
- **API Reusability**: The REST API can serve multiple client interfaces in the future (mobile native applications, CLI utilities, third-party integrations).
- **Specialized Tooling**: Frontend developers utilize Vite HMR and modern UI component ecosystems; backend developers utilize JVM performance and Spring Boot tooling.

### Negative
- Requires maintaining two separate development and build pipelines (Maven vs npm/Vite).
- Requires network coordination and proxying to prevent Cross-Origin Resource Sharing (CORS) complications during development and deployment.
