# ADR-0004: In-Memory H2 Database for Development

## Status
Accepted

## Date
2026-11-13

## Context
During early stages of development and prototyping, requiring every developer and automated CI runner to install, configure, migrate, and run an external database server (like PostgreSQL or MySQL) introduces friction, setup overhead, and maintenance complexity.

## Decision
We decided to adopt the **H2 In-Memory Relational Database** (`com.h2database:h2`) integrated with Spring Data JPA and Hibernate auto-DDL (`spring-boot-h2console`):
- Connection: In-memory JDBC URL (`jdbc:h2:mem:taskdb`).
- Schema Lifecycle: Generated automatically at startup based on JPA `@Entity` annotations.
- H2 Web Console: Available for interactive query debugging during development.

## Consequences

### Positive
- **Zero Configuration**: Developers can clone the repository and launch the backend immediately without Dockerizing or configuring database servers.
- **Fast Test Execution**: Automated tests (`TaskControllerTest`, `TaskApplicationTests`) execute rapidly in memory with fresh fixtures.
- **Self-Contained**: Simplifies CI/CD pipelines.

### Negative
- **Data Volatility**: Data does not persist across backend application restarts.
- **Feature Gap**: In-memory H2 does not support all production-grade features (e.g., advanced clustering, specific Postgres JSONB / full-text indexing).
- **Migration Path**: Switching to a persistent production database (e.g., PostgreSQL) will require adding Liquibase or Flyway schema migration tools.
