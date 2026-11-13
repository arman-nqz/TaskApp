# ADR-0005: Nginx Reverse Proxy Container for Frontend

## Status
Accepted

## Date
2026-11-13

## Context
Deploying the Single-Page Application (SPA) in a containerized environment presents two key challenges:
1. Serving static assets (HTML, JS, CSS) securely and efficiently with proper SPA route fallback (`index.html`).
2. Browser Cross-Origin Resource Sharing (CORS) restrictions when the browser accesses frontend assets on port 3000 and attempts to invoke backend REST APIs running on port 8080.

## Decision
We decided to package the frontend into a lightweight **Nginx Alpine Docker container** that performs dual roles:
1. **Static Asset Web Server**: Serves compiled React bundles from `/usr/share/nginx/html` with `try_files $uri $uri/ /index.html`.
2. **Reverse Proxy**: Proxies `/api/` requests to the backend server via `http://${BACKEND_HOST}:${BACKEND_PORT}/api/`.
3. **Dynamic Configuration via `envsubst`**: Uses a startup script (`start.sh`) to substitute environment variables into `nginx.conf.template` at runtime.

## Consequences

### Positive
- **No CORS Issues**: The browser issues all requests (UI assets and `/api/v1/tasks` calls) to `http://localhost:3000`, maintaining same-origin semantics.
- **Portability**: Backend network target is fully configurable via `BACKEND_HOST` and `BACKEND_PORT` environment variables.
- **Production-Ready Foundation**: Nginx is battle-tested, lightweight (<25MB image), and highly performant.

### Negative
- Local developers wishing to run `npm run dev` directly without Docker must ensure proxying is configured in `vite.config.ts` or handle CORS.
- Adds an additional hop for API traffic within the development container network.
