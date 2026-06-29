# AI Rumor Town

MVP for an AI-assisted horror rumor investigation game. The backend owns all fixed case truth, stages, NPC knowledge, clue unlock rules, reports, and scoring. The AI provider is only used through a bounded adapter and cannot mutate canonical case facts.

## Stack

- Backend: Java 17, Spring Boot 3, Maven, JPA, Flyway, MySQL 8, WebClient
- Frontend: Vue 3, Vite, TypeScript, Pinia, Vue Router, Axios, Vitest
- AI provider: SiliconFlow OpenAI-compatible chat completions with DeepSeek V4 Pro model setting

## Repository Layout

```text
backend/   Spring Boot REST API, Flyway migrations, tests
frontend/  Vue 3 app, API client, stores, views, tests
docs/      Superpowers specs and implementation plan
```

## Prerequisites

- JDK 17+
- Maven 3.9+
- Node.js 20+
- npm
- MySQL 8 for local runtime

## MySQL Setup

Create a database named `rumor_town` using UTF-8 settings:

```sql
CREATE DATABASE rumor_town CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Flyway migrations run automatically when the backend starts.

## Environment

Copy `.env.example` and set values for your local machine. The backend also works without `SILICONFLOW_API_KEY`; it returns deterministic mock AI responses for local development.

```text
RUMOR_TOWN_DB_URL=jdbc:mysql://localhost:3306/rumor_town?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
RUMOR_TOWN_DB_USER=root
RUMOR_TOWN_DB_PASSWORD=root
SILICONFLOW_API_KEY=
SILICONFLOW_BASE_URL=https://api.siliconflow.cn/v1
SILICONFLOW_MODEL=deepseek-ai/DeepSeek-V4-Pro
VITE_API_BASE_URL=http://localhost:8080/api
```

## Backend

```powershell
cd backend
mvn test
mvn spring-boot:run
```

Runtime defaults are read from environment variables in `backend/src/main/resources/application.yml`.

## Frontend

```powershell
cd frontend
npm install
npm run test
npm run build
npm run dev
```

The frontend API base URL defaults to `http://localhost:8080/api`.

## MVP Flow

1. Create a guest player.
2. Open the case archive.
3. Start one of the three fixed cases.
4. Visit an unlocked location.
5. Ask an NPC a question.
6. Review saved dialogue.
7. Inspect collected clues in the clue archive.
8. Advance the investigation stage after enough critical clues.
9. Submit a truth report at stage 4.
10. Review score, ending, summary, and missed points.

## Scope Notes

- Cases are fixed and seeded by Flyway.
- Canonical truth and rule meanings remain backend-owned.
- NPC dialogue may reveal only authorized, unlocked clue codes.
- Guest mode is implemented; the user table remains for future login support.
- Custom case generation, full login, multiplayer, voice, and clue-board graphing are out of MVP scope.