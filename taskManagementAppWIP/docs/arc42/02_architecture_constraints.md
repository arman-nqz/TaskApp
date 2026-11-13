# 2. Architecture Constraints

## 2.1 Technical Constraints

| Constraint | Category | Description & Impact |
| :--- | :--- | :--- |
| **Java 25** | Runtime / Language | Backend source code utilizes Java 25 features (e.g., Records for immutable DTOs, modern pattern matching). Development and deployment environments must provide JDK 25 or higher. |
| **Spring Boot 4.0.9** | Framework | Spring Boot 4 manages dependency injection, auto-configuration, and web server lifecycle. Utilizes `spring-boot-starter-webmvc`, `spring-boot-starter-data-jpa`, and `spring-boot-starter-validation`. |
| **Node.js 22 & React 19** | Runtime / Language | Frontend is authored in TypeScript 5.9 and executed on React 19, leveraging modern concurrent rendering capabilities and functional hooks. |
| **Vite 7 Bundler** | Build Tooling | Frontend development server and production compilation use Vite (with `@vitejs/plugin-react`), outputting optimized static assets into `dist/`. |
| **In-Memory H2 Database** | Storage | Primary development database is embedded in-memory H2. Ephemeral by default; database schema is automatically generated and torn down on application restart (`spring.jpa.hibernate.ddl-auto`). |
| **Docker & Docker Compose** | Containerization | The frontend is containerized using Alpine Linux and Nginx. Orchestration relies on Docker Compose v2 specification. |
| **Port Allocations** | Networking | Port `3000` is dedicated to the frontend container / Nginx proxy. Port `8080` is dedicated to the Spring Boot REST API. |

---

## 2.2 Organizational Constraints

| Constraint | Description |
| :--- | :--- |
| **Independent Lifecycle** | The backend and frontend repositories are organized within a monorepo structure but maintain separate build and dependency definitions (`pom.xml` vs `frontend/package.json`). |
| **Open Source Licensing** | Third-party libraries must adhere to permissive licenses (MIT, Apache 2.0, BSD). |
| **Self-Contained Development** | A developer must be able to clone the repository and run the application with only a JDK and Docker installed, without needing an external RDBMS installation. |

---

## 2.3 Conventions and Standards

| Topic | Standard / Convention | Details |
| :--- | :--- | :--- |
| **API Architecture** | RESTful HTTP Conventions | Uniform resource URIs under `/api/v1/tasks`, semantic HTTP verbs (`GET`, `POST`, `PUT`, `DELETE`), standard status codes (`200 OK`, `201 Created`, `204 No Content`, `400 Bad Request`). |
| **Data Validation** | Jakarta / Hibernate Validator | Validation is expressed declaratively via annotations (`@NotBlank`, `@Length`, `@FutureOrPresent`, `@NotNull`) directly on DTOs. |
| **Code Formatting** | Prettier & EditorConfig | Frontend code is formatted using Prettier and TypeScript-ESLint. Backend adheres to standard Java Code Conventions. |
| **Architectural Documentation** | arc42 & C4 Model | Architecture documentation follows the arc42 structure and C4 visual diagrams to ensure standardized, vendor-neutral representation. |
