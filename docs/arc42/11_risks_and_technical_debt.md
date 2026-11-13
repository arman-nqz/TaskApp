# 11. Risks and Technical Debt

This section outlines identified architectural and implementation risks, along with recognized technical debt and recommended remediation strategies.

---

## 11.1 Identified Technical Risks

| Risk ID | Risk Description | Severity | Probability | Mitigation Strategy |
| :---: | :--- | :---: | :---: | :--- |
| **R-01** | **Data Volatility with In-Memory H2**: Because H2 is configured in-memory without file persistence, all task data is erased whenever the backend JVM restarts. | **High** | **High** | Introduce external database profiles (e.g., PostgreSQL / MySQL via Docker Compose) for production and persistent staging environments, while retaining H2 for unit/integration testing. |
| **R-02** | **Lack of Authentication & Multi-Tenancy**: The current API does not require authentication; all users share the same task repository, allowing anyone to modify or delete any task. | **High** | **High** | Implement Spring Security with JWT or OAuth2 bearer tokens, binding tasks to authenticated user identities. |
| **R-03** | **CORS Configuration for Local Vite Dev**: When developers run the frontend locally via `npm run dev` on port 3000 instead of Docker, direct requests to `http://localhost:8080` may trigger browser CORS blocks unless Vite proxy or Spring CORS filters are active. | **Medium** | **Medium** | Configure Vite development server proxy in `vite.config.ts` (`server.proxy: {'/api': 'http://localhost:8080'}`) or add `@CrossOrigin` in Spring Boot for dev profile. |
| **R-04** | **Unbounded Task List Retrieval**: `GET /api/v1/tasks` executes `findAll()` without pagination. As task volume grows, payload size and query latency will degrade performance. | **Medium** | **Medium** | Introduce Spring Data `Pageable` parameters (`page`, `size`) and paginated response wrappers. |

---

## 11.2 Recognized Technical Debt

### TD-01: Legacy Parameter Naming in Frontend API Layer
- **Description**: Several function parameters in `frontend/src/lib/api.ts` and `task-provider.tsx` use the term `todoId` instead of `taskId` (e.g., `deleteTask = async (todoId: string)`), reflecting evolutionary transition from a TodoList tutorial template to a formal Task Management domain.
- **Impact**: Minor cognitive dissonance for new developers reading code.
- **Remediation**: Refactor parameter names to `taskId` across frontend services and context definitions.

### TD-02: Missing Frontend Test Suite
- **Description**: While the backend includes integration tests (`TaskControllerTest` using `MockMvc`), the frontend currently lacks Vitest / React Testing Library automated component tests.
- **Impact**: Risk of UI regressions during frontend refactoring.
- **Remediation**: Set up Vitest and React Testing Library to test dialog submissions, status toggles, and context dispatchers.

### TD-03: Single Error Message Extraction
- **Description**: `GlobalExceptionHandler` extracts only the first field error from `MethodArgumentNotValidException` instead of returning a map or list of all validation errors.
- **Impact**: If a client submits a form with multiple invalid fields, they only discover the second error after fixing the first.
- **Remediation**: Update `ErrorDto` to optionally return `Map<String, String> errors` or `List<String> errors` when multiple validation constraints fail.
