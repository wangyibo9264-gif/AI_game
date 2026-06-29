# AI Rumor Town Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the MVP for《谣镇档案：怪谈调查局》with a Spring Boot backend, Vue frontend, MySQL persistence, fixed cases, bounded AI NPC dialogue, collected clue archive, and truth-report scoring.

**Architecture:** Use a two-app repository under `AI_game`: `backend/` for Spring Boot REST APIs and `frontend/` for Vue 3. The backend is the source of truth for cases, stages, NPC knowledge, clue unlocks, and reports; DeepSeek V4 Pro is called only through an AI adapter and cannot mutate canonical case facts.

**Tech Stack:** Java 17+, Spring Boot 3, Maven, MySQL 8, Flyway, JPA, WebClient, Vue 3, Vite, TypeScript, Pinia, Vue Router, Axios, Vitest.

---

## File Structure

Create this structure:

```text
AI_game/
  backend/
    pom.xml
    src/main/java/com/rumortown/
      RumorTownApplication.java
      common/
      user/
      casefile/
      game/
      npc/
      clue/
      report/
      ai/
    src/main/resources/
      application.yml
      db/migration/
    src/test/java/com/rumortown/
  frontend/
    package.json
    index.html
    vite.config.ts
    src/
      main.ts
      App.vue
      router/
      stores/
      api/
      views/
      components/
      styles/
  docs/superpowers/
    specs/
    plans/
```

Boundary rules:

- `casefile` owns static case metadata, rules, locations, NPC profiles, and canonical clue pools.
- `game` owns session state, stage progression, visits, and current investigation state.
- `npc` owns dialogue orchestration, but not direct model HTTP calls.
- `ai` owns provider-specific calls and response parsing.
- `clue` owns collected clues and player clue flags.
- `report` owns truth reports, scoring, endings, and AI-generated review text.

## Task 1: Backend Skeleton

**Files:**
- Create: `AI_game/backend/pom.xml`
- Create: `AI_game/backend/src/main/java/com/rumortown/RumorTownApplication.java`
- Create: `AI_game/backend/src/main/resources/application.yml`
- Create: `AI_game/backend/src/main/java/com/rumortown/common/ApiResponse.java`
- Create: `AI_game/backend/src/main/java/com/rumortown/common/GlobalExceptionHandler.java`
- Test: `AI_game/backend/src/test/java/com/rumortown/RumorTownApplicationTests.java`

- [ ] **Step 1: Create Spring Boot Maven project**

Create `backend/pom.xml` with Java 17, Spring Web, Validation, Data JPA, MySQL, Flyway, WebFlux, Lombok, and Test dependencies.

- [ ] **Step 2: Add application entrypoint**

```java
package com.rumortown;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RumorTownApplication {
    public static void main(String[] args) {
        SpringApplication.run(RumorTownApplication.class, args);
    }
}
```

- [ ] **Step 3: Add config**

Use env-driven config in `application.yml`:

```yaml
spring:
  datasource:
    url: ${RUMOR_TOWN_DB_URL:jdbc:mysql://localhost:3306/rumor_town?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai}
    username: ${RUMOR_TOWN_DB_USER:root}
    password: ${RUMOR_TOWN_DB_PASSWORD:root}
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
  flyway:
    enabled: true

siliconflow:
  api-key: ${SILICONFLOW_API_KEY:}
  base-url: ${SILICONFLOW_BASE_URL:https://api.siliconflow.cn/v1}
  model: ${SILICONFLOW_MODEL:deepseek-ai/DeepSeek-V4-Pro}
```

- [ ] **Step 4: Add common response and exception handler**

`ApiResponse<T>` should expose `success`, `data`, `message`. `GlobalExceptionHandler` should return `400` for validation errors and `500` for unhandled errors.

- [ ] **Step 5: Run backend tests**

Run: `mvn test` in `AI_game/backend`

Expected: application context test passes. If MySQL is not running, configure the test profile later in Task 2 before re-running.

## Task 2: Database Migrations and Domain Entities

**Files:**
- Create: `AI_game/backend/src/main/resources/db/migration/V1__init_schema.sql`
- Create: entity classes under `casefile`, `game`, `clue`, `report`, `user`
- Create: repository interfaces for each aggregate
- Test: `AI_game/backend/src/test/java/com/rumortown/casefile/CaseRepositoryTests.java`

- [ ] **Step 1: Write schema migration**

Create tables:

```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  display_name VARCHAR(80) NOT NULL,
  guest BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cases (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(64) NOT NULL UNIQUE,
  title VARCHAR(120) NOT NULL,
  summary TEXT NOT NULL,
  difficulty VARCHAR(32) NOT NULL,
  estimated_minutes INT NOT NULL,
  status VARCHAR(32) NOT NULL
);

CREATE TABLE case_rules (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  case_id BIGINT NOT NULL,
  rule_code VARCHAR(64) NOT NULL,
  rule_text TEXT NOT NULL,
  truth_meaning TEXT NOT NULL,
  display_order INT NOT NULL,
  FOREIGN KEY (case_id) REFERENCES cases(id)
);

CREATE TABLE case_locations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  case_id BIGINT NOT NULL,
  code VARCHAR(64) NOT NULL,
  name VARCHAR(120) NOT NULL,
  description TEXT NOT NULL,
  unlock_stage INT NOT NULL,
  FOREIGN KEY (case_id) REFERENCES cases(id)
);

CREATE TABLE case_npcs (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  case_id BIGINT NOT NULL,
  location_id BIGINT NOT NULL,
  code VARCHAR(64) NOT NULL,
  name VARCHAR(120) NOT NULL,
  role_name VARCHAR(120) NOT NULL,
  personality TEXT NOT NULL,
  speaking_style TEXT NOT NULL,
  unlock_stage INT NOT NULL,
  FOREIGN KEY (case_id) REFERENCES cases(id),
  FOREIGN KEY (location_id) REFERENCES case_locations(id)
);

CREATE TABLE npc_knowledge (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  npc_id BIGINT NOT NULL,
  known_facts TEXT NOT NULL,
  revealable_clue_codes TEXT NOT NULL,
  hidden_facts TEXT NOT NULL,
  forbidden_topics TEXT NOT NULL,
  FOREIGN KEY (npc_id) REFERENCES case_npcs(id)
);

CREATE TABLE case_clues (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  case_id BIGINT NOT NULL,
  clue_code VARCHAR(64) NOT NULL,
  title VARCHAR(160) NOT NULL,
  content TEXT NOT NULL,
  category VARCHAR(32) NOT NULL,
  unlock_stage INT NOT NULL,
  critical BOOLEAN NOT NULL DEFAULT FALSE,
  FOREIGN KEY (case_id) REFERENCES cases(id),
  UNIQUE(case_id, clue_code)
);

CREATE TABLE game_sessions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  case_id BIGINT NOT NULL,
  current_stage INT NOT NULL DEFAULT 1,
  status VARCHAR(32) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (case_id) REFERENCES cases(id)
);

CREATE TABLE location_visits (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  session_id BIGINT NOT NULL,
  location_id BIGINT NOT NULL,
  visited_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (session_id) REFERENCES game_sessions(id),
  FOREIGN KEY (location_id) REFERENCES case_locations(id)
);

CREATE TABLE dialogue_messages (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  session_id BIGINT NOT NULL,
  npc_id BIGINT NOT NULL,
  sender VARCHAR(32) NOT NULL,
  content TEXT NOT NULL,
  stage INT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (session_id) REFERENCES game_sessions(id),
  FOREIGN KEY (npc_id) REFERENCES case_npcs(id)
);

CREATE TABLE collected_clues (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  session_id BIGINT NOT NULL,
  clue_id BIGINT NOT NULL,
  importance VARCHAR(32) NOT NULL DEFAULT 'NORMAL',
  status VARCHAR(32) NOT NULL DEFAULT 'UNRESOLVED',
  collected_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (session_id) REFERENCES game_sessions(id),
  FOREIGN KEY (clue_id) REFERENCES case_clues(id),
  UNIQUE(session_id, clue_id)
);

CREATE TABLE truth_reports (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  session_id BIGINT NOT NULL,
  event_summary TEXT NOT NULL,
  responsible_person TEXT NOT NULL,
  key_evidence TEXT NOT NULL,
  rule_explanation TEXT NOT NULL,
  npc_lies TEXT NOT NULL,
  conclusion TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (session_id) REFERENCES game_sessions(id)
);

CREATE TABLE report_scores (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  report_id BIGINT NOT NULL,
  truth_score INT NOT NULL,
  clue_score INT NOT NULL,
  rule_score INT NOT NULL,
  ending VARCHAR(64) NOT NULL,
  summary TEXT NOT NULL,
  missed_points TEXT NOT NULL,
  FOREIGN KEY (report_id) REFERENCES truth_reports(id)
);
```

- [ ] **Step 2: Create JPA entities**

Use focused entity classes matching the table names. Use `LocalDateTime` for timestamps, `Long id`, and enums for `status`, `category`, clue importance, and clue status.

- [ ] **Step 3: Create repositories**

Create Spring Data repositories such as `CaseFileRepository`, `CaseRuleRepository`, `CaseLocationRepository`, `CaseNpcRepository`, `CaseClueRepository`, `GameSessionRepository`, `CollectedClueRepository`, and `TruthReportRepository`.

- [ ] **Step 4: Add repository smoke test**

Test that `CaseFileRepository.findAll()` starts with an empty list against a test database profile.

- [ ] **Step 5: Run tests**

Run: `mvn test`

Expected: schema loads and repository test passes.

## Task 3: Seed Three Fixed Cases

**Files:**
- Create: `AI_game/backend/src/main/resources/db/migration/V2__seed_cases.sql`
- Modify: repository tests to assert seeded cases

- [ ] **Step 1: Insert case rows**

Seed `CLOCK_317`, `ROSE_ROOM_8`, and `FOG_LAST_FERRY` with Chinese titles and summaries from the design doc.

- [ ] **Step 2: Insert 2-3 rules per case**

Seed exactly 3 rules per case. Include `truth_meaning` so report scoring can explain each rule.

- [ ] **Step 3: Insert MVP locations and NPCs**

For each case insert 4 locations and 5 NPCs. Use unlock stages 1-3 so stage progression can gate content.

- [ ] **Step 4: Insert clue pools**

For each case insert 10-12 clues across categories `PERSON`, `TIME`, `LOCATION`, `EVIDENCE`, `RULE`, `CONTRADICTION`. Mark 3-4 critical clues.

- [ ] **Step 5: Test seeded case count**

Test:

```java
@Test
void loadsSeededCases() {
    assertThat(caseFileRepository.findAll()).hasSize(3);
}
```

Run: `mvn test`

Expected: seeded case count is 3.

## Task 4: Case and Guest APIs

**Files:**
- Create: `AI_game/backend/src/main/java/com/rumortown/user/UserController.java`
- Create: `AI_game/backend/src/main/java/com/rumortown/user/UserService.java`
- Create: `AI_game/backend/src/main/java/com/rumortown/casefile/CaseController.java`
- Create: `AI_game/backend/src/main/java/com/rumortown/casefile/CaseService.java`
- Test: controller tests for guest and case APIs

- [ ] **Step 1: Write controller tests**

Test `POST /api/guest` returns a guest user id. Test `GET /api/cases` returns 3 cases. Test `GET /api/cases/{id}` returns rules and summary.

- [ ] **Step 2: Implement guest service**

`UserService.createGuest()` creates a `users` row with display name `guest-<timestamp>` and `guest=true`.

- [ ] **Step 3: Implement case DTOs and service**

Return only player-safe fields. Do not expose `truth_meaning`, NPC hidden facts, or canonical standard answers.

- [ ] **Step 4: Run controller tests**

Run: `mvn test`

Expected: guest and case endpoints pass.

## Task 5: Sessions, Visits, and Stage Progression

**Files:**
- Create: `AI_game/backend/src/main/java/com/rumortown/game/GameSessionController.java`
- Create: `AI_game/backend/src/main/java/com/rumortown/game/GameSessionService.java`
- Create: DTOs for session creation, session detail, and visit response
- Test: session lifecycle tests

- [ ] **Step 1: Write session tests**

Test creating a session starts stage 1. Test visiting a stage-locked location before unlock returns `400`. Test collecting critical clues later can advance stage.

- [ ] **Step 2: Implement session creation**

`POST /api/sessions` accepts `userId` and `caseId`, creates `game_sessions.status='ACTIVE'`, and returns current stage, case id, available locations, and available NPCs.

- [ ] **Step 3: Implement visit endpoint**

`POST /api/sessions/{sessionId}/locations/{locationId}/visit` records a visit only if `location.unlockStage <= currentStage`.

- [ ] **Step 4: Implement stage progression**

`POST /api/sessions/{sessionId}/advance` checks collected critical clues. Advance from stage 1 to 2 after at least 3 clues and 1 critical clue; stage 2 to 3 after at least 6 clues and 2 critical clues; stage 3 to 4 after at least 9 clues and 3 critical clues.

- [ ] **Step 5: Run tests**

Run: `mvn test`

Expected: lifecycle and stage tests pass.

## Task 6: AI Adapter and Prompt Guardrails

**Files:**
- Create: `AI_game/backend/src/main/java/com/rumortown/ai/AiNarrativeClient.java`
- Create: `AI_game/backend/src/main/java/com/rumortown/ai/SiliconFlowDeepSeekClient.java`
- Create: `AI_game/backend/src/main/java/com/rumortown/ai/NpcDialogueAiRequest.java`
- Create: `AI_game/backend/src/main/java/com/rumortown/ai/NpcDialogueAiResponse.java`
- Create: `AI_game/backend/src/main/java/com/rumortown/ai/ReportReviewAiRequest.java`
- Create: `AI_game/backend/src/main/java/com/rumortown/ai/ReportReviewAiResponse.java`
- Test: mock client parsing tests

- [ ] **Step 1: Define AI interfaces**

```java
public interface AiNarrativeClient {
    NpcDialogueAiResponse generateNpcReply(NpcDialogueAiRequest request);
    ReportReviewAiResponse reviewTruthReport(ReportReviewAiRequest request);
}
```

- [ ] **Step 2: Implement request and response records**

Use Java records. `NpcDialogueAiResponse` contains `reply`, `mood`, `revealedClueCodes`, `ruleHints`, and `suspicionDelta`.

- [ ] **Step 3: Implement SiliconFlow client**

Use Spring `WebClient`. Read `siliconflow.api-key`, `base-url`, and `model` from configuration. Send OpenAI-compatible chat completion requests to `/chat/completions`.

- [ ] **Step 4: Add mock fallback for missing API key**

If `SILICONFLOW_API_KEY` is blank, return a deterministic mock reply with empty `revealedClueCodes`. This keeps local development runnable.

- [ ] **Step 5: Test response parsing**

Test that a model JSON body is parsed into `NpcDialogueAiResponse`. Test invalid JSON falls back to safe reply with no clues.

Run: `mvn test`

Expected: AI adapter tests pass without a real API key.

## Task 7: NPC Dialogue and Clue Collection

**Files:**
- Create: `AI_game/backend/src/main/java/com/rumortown/npc/NpcDialogueController.java`
- Create: `AI_game/backend/src/main/java/com/rumortown/npc/NpcDialogueService.java`
- Create: `AI_game/backend/src/main/java/com/rumortown/clue/ClueService.java`
- Create: `AI_game/backend/src/main/java/com/rumortown/clue/ClueController.java`
- Test: NPC dialogue clue guardrail tests

- [ ] **Step 1: Write guardrail tests**

Test AI-revealed clue code is collected only if it exists, belongs to the current case, appears in NPC `revealable_clue_codes`, and `unlock_stage <= current_stage`.

- [ ] **Step 2: Implement dialogue prompt builder**

Build a prompt containing case title, stage, location, NPC profile, known facts, revealable clues, hidden facts, forbidden topics, collected clue summary, and player question. Do not include full case truth.

- [ ] **Step 3: Implement message endpoint**

`POST /api/sessions/{sessionId}/npcs/{npcId}/messages` saves the player message, calls `AiNarrativeClient`, validates clue codes, saves AI message, collects allowed clues, and returns reply plus newly collected clues.

- [ ] **Step 4: Implement clue archive endpoint**

`GET /api/sessions/{sessionId}/clues` returns collected clues grouped by category. `PATCH /api/sessions/{sessionId}/clues/{clueId}` updates importance and status.

- [ ] **Step 5: Run tests**

Run: `mvn test`

Expected: AI can never collect locked, nonexistent, or unauthorized clues.

## Task 8: Truth Report and Scoring

**Files:**
- Create: `AI_game/backend/src/main/java/com/rumortown/report/TruthReportController.java`
- Create: `AI_game/backend/src/main/java/com/rumortown/report/TruthReportService.java`
- Create: report DTOs
- Test: report submission tests

- [ ] **Step 1: Write report tests**

Test submitting a report at stage 4 creates `truth_reports` and `report_scores`. Test submitting before stage 4 returns `400`.

- [ ] **Step 2: Implement report submission**

`POST /api/sessions/{sessionId}/reports` accepts event summary, responsible person, key evidence, rule explanation, NPC lies, and conclusion.

- [ ] **Step 3: Implement deterministic pre-score**

Calculate baseline scores from collected critical clues and keyword matches from seeded truth meanings. Pass standard truth, collected clues, and player report to AI for explanatory summary.

- [ ] **Step 4: Persist score**

Save `truthScore`, `clueScore`, `ruleScore`, `ending`, `summary`, and `missedPoints`.

- [ ] **Step 5: Run tests**

Run: `mvn test`

Expected: report scoring works without a real API key through mock AI fallback.

## Task 9: Frontend Skeleton

**Files:**
- Create: `AI_game/frontend/package.json`
- Create: `AI_game/frontend/index.html`
- Create: `AI_game/frontend/vite.config.ts`
- Create: `AI_game/frontend/src/main.ts`
- Create: `AI_game/frontend/src/App.vue`
- Create: `AI_game/frontend/src/router/index.ts`
- Create: `AI_game/frontend/src/styles/theme.css`
- Test: `AI_game/frontend/src/App.test.ts`

- [ ] **Step 1: Create Vite Vue TypeScript app files**

Use dependencies: `vue`, `vue-router`, `pinia`, `axios`, `@vitejs/plugin-vue`, `typescript`, `vite`, `vitest`, `@vue/test-utils`, `jsdom`.

- [ ] **Step 2: Add base routes**

Routes: `/`, `/cases/:caseId`, `/sessions/:sessionId`, `/sessions/:sessionId/report`, `/reports/:reportId`.

- [ ] **Step 3: Add horror archive theme**

Define CSS variables for black green, fog gray, old paper, candle yellow, rust red, and readable text colors.

- [ ] **Step 4: Add smoke test**

Test App renders router view without crashing.

- [ ] **Step 5: Run frontend tests**

Run: `npm install`, then `npm run test` in `AI_game/frontend`.

Expected: smoke test passes.

## Task 10: Frontend API Layer and Stores

**Files:**
- Create: `AI_game/frontend/src/api/http.ts`
- Create: `AI_game/frontend/src/api/cases.ts`
- Create: `AI_game/frontend/src/api/sessions.ts`
- Create: `AI_game/frontend/src/api/clues.ts`
- Create: `AI_game/frontend/src/api/reports.ts`
- Create: stores under `AI_game/frontend/src/stores/`
- Test: store tests with mocked Axios

- [ ] **Step 1: Implement Axios client**

Base URL defaults to `import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'`.

- [ ] **Step 2: Implement API modules**

Mirror backend endpoints exactly from the spec.

- [ ] **Step 3: Implement Pinia stores**

Create `caseStore`, `sessionStore`, `dialogueStore`, `clueStore`, and `reportStore` with loading and error state.

- [ ] **Step 4: Test case store**

Mock `GET /cases` and verify store populates case list.

- [ ] **Step 5: Run tests**

Run: `npm run test`

Expected: store tests pass.

## Task 11: Case Archive and Detail UI

**Files:**
- Create: `AI_game/frontend/src/views/CaseArchiveView.vue`
- Create: `AI_game/frontend/src/views/CaseDetailView.vue`
- Create: `AI_game/frontend/src/components/CaseFileCard.vue`
- Create: `AI_game/frontend/src/components/RuleCard.vue`
- Test: view component tests

- [ ] **Step 1: Build archive view**

Render three case cards with title, summary, difficulty, estimated time, and progress placeholder.

- [ ] **Step 2: Build detail view**

Show opening summary, rule cards, known locations, and a start investigation button.

- [ ] **Step 3: Wire start button**

Create guest if none exists, create session for case, route to `/sessions/{sessionId}`.

- [ ] **Step 4: Test start flow**

Mock guest and session APIs, click start, assert route push.

- [ ] **Step 5: Run tests and build**

Run: `npm run test` and `npm run build`

Expected: tests and production build pass.

## Task 12: Investigation UI, Dialogue, and Clue Archive

**Files:**
- Create: `AI_game/frontend/src/views/InvestigationView.vue`
- Create: `AI_game/frontend/src/components/LocationPanel.vue`
- Create: `AI_game/frontend/src/components/NpcDialoguePanel.vue`
- Create: `AI_game/frontend/src/components/QuickQuestionBar.vue`
- Create: `AI_game/frontend/src/components/ClueArchivePanel.vue`
- Test: dialogue and clue UI tests

- [ ] **Step 1: Build three-column investigation layout**

Left locations, center dialogue, right collected clue archive. Use responsive CSS to turn clue archive into a drawer on narrow screens.

- [ ] **Step 2: Implement location visits**

Clicking a location calls visit endpoint and refreshes available NPCs.

- [ ] **Step 3: Implement NPC dialogue**

Free input sends player question. Quick buttons send predefined prompts for time, people, evidence, contradiction, and rule.

- [ ] **Step 4: Implement clue archive**

Show collected clues grouped by category. Allow toggling importance and status.

- [ ] **Step 5: Add stage update banner**

After dialogue or clue update, call advance endpoint and show “调查阶段更新” when stage changes.

- [ ] **Step 6: Run tests and build**

Run: `npm run test` and `npm run build`

Expected: investigation interactions pass component tests.

## Task 13: Report and Ending UI

**Files:**
- Create: `AI_game/frontend/src/views/TruthReportView.vue`
- Create: `AI_game/frontend/src/views/ReportResultView.vue`
- Create: `AI_game/frontend/src/components/ScorePanel.vue`
- Test: report submission tests

- [ ] **Step 1: Build report form**

Fields: event summary, responsible person, key evidence, rule explanation, NPC lies, conclusion.

- [ ] **Step 2: Require collected clues**

Show selected collected clues beside the report. Disable submit when session has no clues.

- [ ] **Step 3: Submit report**

Call `POST /sessions/{sessionId}/reports`, then route to report result.

- [ ] **Step 4: Build score page**

Render truth score, clue score, rule score, ending, summary, and missed points.

- [ ] **Step 5: Run tests and build**

Run: `npm run test` and `npm run build`

Expected: report flow passes.

## Task 14: End-to-End Verification and Documentation

**Files:**
- Create: `AI_game/README.md`
- Create: `AI_game/.env.example`
- Modify: backend and frontend docs as needed

- [ ] **Step 1: Write README**

Include prerequisites, MySQL setup, env vars, backend start, frontend start, and MVP flow.

- [ ] **Step 2: Add env example**

Include:

```text
RUMOR_TOWN_DB_URL=jdbc:mysql://localhost:3306/rumor_town?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
RUMOR_TOWN_DB_USER=root
RUMOR_TOWN_DB_PASSWORD=root
SILICONFLOW_API_KEY=
SILICONFLOW_BASE_URL=https://api.siliconflow.cn/v1
SILICONFLOW_MODEL=deepseek-ai/DeepSeek-V4-Pro
VITE_API_BASE_URL=http://localhost:8080/api
```

- [ ] **Step 3: Run backend verification**

Run: `mvn test` in `AI_game/backend`

Expected: all backend tests pass.

- [ ] **Step 4: Run frontend verification**

Run: `npm run test` and `npm run build` in `AI_game/frontend`

Expected: all frontend tests pass and build succeeds.

- [ ] **Step 5: Manual MVP smoke test**

Start backend and frontend, then verify:

1. Create guest player.
2. Open case archive.
3. Start《三点十七分的钟声》.
4. Visit a stage 1 location.
5. Ask an NPC a question.
6. See dialogue saved.
7. See any allowed clue appear in the clue archive.
8. Advance stage after enough clues.
9. Submit truth report at stage 4.
10. See score and ending page.

Expected: the entire MVP loop works without AI changing canonical truth.

## Self-Review

Spec coverage:

- Spring Boot backend, Vue frontend, MySQL, guest mode, DeepSeek adapter, three fixed cases, fixed truth, NPC dialogue, clue archive, stage progression, truth report, scoring, and horror UI are all covered by tasks.
- Custom case generation, full login, multiplayer, voice, and drag-line clue board remain out of scope.

Placeholder scan:

- No `TBD`, `TODO`, or “implement later” steps are present.
- Every task has concrete files, commands, and expected verification.

Type consistency:

- Backend package names and service names match the design doc.
- API endpoints match the confirmed interface design.
