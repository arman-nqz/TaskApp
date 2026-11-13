# ADR-0006: React Context API for Client-Side State Management

## Status
Accepted

## Date
2026-11-13

## Context
The frontend needs to manage shared client-side application state across multiple components:
- The list of active tasks (`Task[]`).
- The currently selected task (`selectedTask`) used by editing and deletion modals.
- Loading flags and network error messages.
- CRUD action handlers (`createTask`, `updateTask`, `deleteTask`, `toggleTaskStatus`, `refresh`).

We evaluated options including Redux Toolkit, Zustand, MobX, and the built-in React Context API.

## Decision
We decided to adopt the built-in **React Context API** coupled with custom hooks (`TodoListProvider` and `useTasks`):
- All task state and mutation actions are contained in `frontend/src/providers/task-provider.tsx`.
- Consumers subscribe via `const { tasks, selectedTask, selectTask, ... } = useTasks()`.

## Consequences

### Positive
- **Zero Third-Party State Dependencies**: No extra external state libraries required, reducing bundle size and maintenance overhead.
- **Simplicity**: Intuitive mental model using standard React hooks (`useState`, `useCallback`, `useEffect`).
- **Scoped**: The state boundary is cleanly scoped to the provider subtree.

### Negative
- As the application grows to encompass dozens of different domain entities or highly frequent partial re-renders, Context updates may cause broader component re-renders unless memoized or split into separate contexts.
