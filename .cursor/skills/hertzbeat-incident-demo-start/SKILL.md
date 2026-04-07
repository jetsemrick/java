---
name: hertzbeat-incident-demo-start
description: >-
  Starts the Apache HertzBeat fork (Incident Command Center demo) locally: JDK 25
  Maven backend on port 1157 plus Angular dev server on 4200 with API proxy.
  Use when the user wants to run, start, or develop HertzBeat in this repo, open
  the incident demo UI, start backend and frontend together, or verify the stack
  after clone.
---

# Start HertzBeat Incident Demo

## Context

- **Repo layout**: HertzBeat lives under [`hertzbeat/`](hertzbeat/) (not the workspace root).
- **Backend** listens on **1157** (`server.port` in `hertzbeat-startup`).
- **Frontend** (`ng serve`) listens on **4200** by default; [`web-app/proxy.conf.json`](hertzbeat/web-app/proxy.conf.json) proxies `/api/*` → `http://localhost:1157`.
- **JDK 25+** is required (Maven enforcer). If the build fails, set `JAVA_HOME` to JDK 25 before `./mvnw`. On macOS with Homebrew: `export JAVA_HOME="$(brew --prefix openjdk)/libexec/openjdk.jdk/Contents/Home"`.
- **Node.js 18+** and **pnpm** (or npm) are required for the Angular app under `web-app/`.
- **Maven gotcha**: the root `hertzbeat` POM inherits `spring-boot-maven-plugin`. Running `spring-boot:run` together with `-am` can bind the goal to the **root** POM and fail with “Unable to find a suitable main class”. Build dependencies first, then run **only** `hertzbeat-startup`.

## Full stack (backend + frontend)

Use **two terminals**. Start the **backend first** so the proxy can reach the API.

**Terminal 1 — backend**

```bash
cd hertzbeat
export JAVA_HOME="$(brew --prefix openjdk)/libexec/openjdk.jdk/Contents/Home"   # macOS Homebrew; adjust if needed
./mvnw install -DskipTests -pl hertzbeat-startup -am
./mvnw spring-boot:run -pl hertzbeat-startup
```

Wait until logs show **Started HertzBeatApplication** and Tomcat on **http://localhost:1157**.

**Terminal 2 — frontend**

```bash
cd hertzbeat/web-app
pnpm install    # first time or after dependency changes
pnpm start
```

If `pnpm` is unavailable: `npm install` and `npm run start`.

- **App URL**: **http://localhost:4200/** (confirm in terminal; Angular prints the listening URL).
- **Incident Command**: **http://localhost:4200/alert/incidents** or menu **Alarm → Incident Command**.

After the first successful backend `install`, you can usually skip `install` and only run `spring-boot:run` unless you changed dependencies or ran `clean`.

## Backend only

Same as Terminal 1 in **Full stack** above.

## Frontend only

Use when the API is already running on 1157. Same as Terminal 2 in **Full stack** above. If API calls fail, verify the backend is up and `proxy.conf.json` targets `http://localhost:1157`.

## Seed demo data

After the backend is up and you are authenticated:

- **UI**: **Alarm → Incident Command** → **Seed demo**, or  
- **HTTP**: `POST /api/incidents/demo/seed` (same host as API; with Angular proxy, e.g. `POST http://localhost:4200/api/incidents/demo/seed` only works if the app serves that route through the proxy—prefer the UI button or `curl` to `http://localhost:1157/api/incidents/demo/seed` with auth headers if needed).

For more detail, see [`hertzbeat/DEMO_INCIDENT_COMMAND.md`](hertzbeat/DEMO_INCIDENT_COMMAND.md).

## Quick verification

- Backend: open `http://localhost:1157` (or actuator/health if configured).
- Full stack: open `http://localhost:4200`, sign in, then **Incident Command**. After **Seed demo**, incidents for `payment-service` (prod and staging) should appear.

## Troubleshooting

| Issue | Action |
|--------|--------|
| Enforcer error: JDK 25 | Install JDK 25+, set `JAVA_HOME`, re-run `./mvnw`. |
| UI shows API errors | Start backend before frontend; ensure port **1157** is listening and `pnpm start` uses [`proxy.conf.json`](hertzbeat/web-app/proxy.conf.json). |
| Port 1157 in use | Stop the other process or override `server.port` for local dev only. |
| Port 4200 in use | Stop the other dev server, or run `ng serve --port 4300` (and expect a different URL). |
| `pnpm` not found | Use `corepack enable` then `corepack prepare pnpm@latest --activate`, or use `npm install` / `npm run start`. |
