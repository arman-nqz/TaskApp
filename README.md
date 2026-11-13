# Task Management App

A modern, full-stack task management application featuring a **Spring Boot** REST API backend and a responsive **React + TypeScript** frontend.

---

## 🚀 Features

- **Full CRUD operations**: Create, view, update, and delete tasks.
- **Task Attributes**:
  - Title and detailed descriptions
  - Due dates with visual indicators
  - Status tracking (`OPEN`, `COMPLETE`)
  - Priority levels (`LOW`, `MEDIUM`, `HIGH`)
- **Modern User Experience**: Clean interface styled with Tailwind CSS and Radix UI components, with toast notifications for feedback.
- **RESTful Architecture**: Validation, global exception handling, and an in-memory H2 database.
- **Docker Support**: Containerized frontend with reverse proxying to backend out-of-the-box.

---

## 🛠️ Tech Stack

| Layer | Technologies |
| :--- | :--- |
| **Backend** | Java 25, Spring Boot 4, Spring Data JPA, Hibernate, H2 Database, Maven |
| **Frontend** | React 19, TypeScript, Vite, Tailwind CSS, Radix UI, Lucide Icons |
| **DevOps / Tooling** | Docker, Docker Compose, Nginx |

---

## 📋 Prerequisites

Ensure you have the following installed locally:

- **Java JDK 25** (or compatible JDK)
- **Node.js 20+** and **npm** *(optional if running frontend with Docker)*
- **Docker & Docker Compose** *(optional, for running the frontend container)*

---

## 🏁 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/taskManagementAppWIP.git
cd taskManagementAppWIP
```

---

### 2. Start the Backend

The backend runs on `http://localhost:8080`.

Using the Maven wrapper from the root of the project:

```bash
# On Linux / macOS:
./mvnw spring-boot:run

# On Windows:
mvnw.cmd spring-boot:run
```

To run the automated tests:
```bash
./mvnw test
```

---

### 3. Start the Frontend

You can run the frontend either with **Docker** or directly with **Node.js**:

#### Option A: Using Docker (Recommended)

From the project root:

```bash
docker compose up
```

The UI will be accessible at [http://localhost:3000](http://localhost:3000). The container automatically proxies `/api` calls to your local backend on port `8080`.

#### Option B: Using Node.js locally

```bash
cd frontend
npm install
npm run dev
```

The frontend will start with Vite at [http://localhost:3000](http://localhost:3000).

---

## 🔌 API Endpoints

All backend endpoints are prefixed under `/api/v1/tasks`:

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/tasks` | Retrieve all tasks |
| `POST` | `/api/v1/tasks` | Create a new task |
| `PUT` | `/api/v1/tasks/{taskId}` | Update an existing task |
| `DELETE` | `/api/v1/tasks/{taskId}` | Delete a task by ID |

### Example Task Payload

```json
{
  "title": "Finish Project Documentation",
  "description": "Write a clean README.md and prepare repository for GitHub",
  "dueDate": "2026-11-20",
  "priority": "HIGH",
  "status": "OPEN"
}
```

---

## 📁 Project Structure

```text
taskManagementAppWIP/
├── docker-compose.yml          # Docker Compose config for frontend container
├── pom.xml                     # Maven build configuration
├── src/                        # Spring Boot backend source code
│   ├── main/
│   │   ├── java/com/armannqz/task/
│   │   │   ├── controller/     # REST controllers & exception handlers
│   │   │   ├── domain/         # Entities, DTOs, and request objects
│   │   │   ├── mapper/         # Entity-DTO mapping
│   │   │   ├── repository/     # Spring Data JPA repositories
│   │   │   └── service/        # Business logic layer
│   │   └── resources/          # Application configuration
│   └── test/                   # Backend unit and integration tests
└── frontend/                   # React + TypeScript frontend
    ├── src/                    # Components, hooks, and providers
    ├── Dockerfile              # Container configuration
    ├── nginx.conf.template     # Nginx reverse proxy template
    └── package.json            # Frontend dependencies and scripts
```

---

## 📄 License

This project is licensed under the MIT License - feel free to customize for your own needs.
