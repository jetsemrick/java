---
name: java-workspace-startup
description: Starts the correct stack for this repository by branch—Apache HertzBeat (main) or Apache OFBiz (demo/ofbiz-base). Use when opening this repo, running local demos, or when the user asks how to boot the backend, frontend, or Gradle/Maven for java workspace.
---

# Java workspace startup

This repo has **two different apps** on different branches. **Always confirm the branch** before running commands.

| Branch | Application | Primary JDK |
|--------|-------------|-------------|
| `main` | [Apache HertzBeat](https://github.com/apache/hertzbeat) fork + Incident Command demo under `hertzbeat/` | **25+** (Maven enforcer) |
| `demo/ofbiz-base` | [Apache OFBiz](https://ofbiz.apache.org/) at **repository root** | **17** (Gradle 7.6; not JDK 25) |

---

## `main` — HertzBeat + Incident Command

### Prerequisites

- **JDK 25+** (`JAVA_HOME` set).
- **Node 18+** and **pnpm** for the UI (`npm` works if documented alternatives are used).

### Backend (from repo root)

Run Maven **inside `hertzbeat/`** (that is where `mvnw` and the reactor live):

```bash
cd hertzbeat
./mvnw install -DskipTests -pl hertzbeat-startup -am
./mvnw spring-boot:run -pl hertzbeat-startup
```

Do **not** run `spring-boot:run` from the root reactor with `-am` in a way that binds the root POM to `spring-boot:run` with no main class—keep execution scoped to **`hertzbeat-startup`** only.

### Frontend (second terminal)

```bash
cd hertzbeat/web-app
pnpm install
pnpm start
```

- UI: **http://localhost:4200/** (proxies `/api` per `web-app/proxy.conf.json`).
- Backend default port aligns with HertzBeat docs (demo references **1157** for API proxy context).

### Demo entrypoints

- Full walkthrough: [hertzbeat/DEMO_INCIDENT_COMMAND.md](hertzbeat/DEMO_INCIDENT_COMMAND.md) (on `main`).
- Incident UI path: **http://localhost:4200/alert/incidents** after login; seed via UI or `POST /api/incidents/demo/seed`.

### Quick verification (optional)

```bash
cd hertzbeat
./mvnw -pl hertzbeat-alerter -am test -Dtest=IncidentHeuristicsTest,IncidentIdCodecTest
```

---

## `demo/ofbiz-base` — Apache OFBiz

### Prerequisites

- **JDK 17** full JDK. Set `JAVA_HOME` to that JDK.
- Gradle **7.6** in this tree does **not** support running `./gradlew` with **JDK 25**—you will see `Unsupported class file major version` if `JAVA_HOME` points at a too-new JDK.

### First-time data load + server

From **repository root** (OFBiz files are here, not in a subfolder):

```bash
./gradlew cleanAll loadAll
./gradlew ofbiz
```

- First run downloads dependencies; `cleanAll loadAll` can take a long time.
- `ofbiz` stays running (progress % may look odd; that is expected).

### URLs

- **https://localhost:8443/webtools**
- E-commerce demo: **https://localhost:8443/ecomseo**

### Reference docs in-tree

- [README.md](README.md) (branch-specific)
- [INSTALL](INSTALL), [README.adoc](README.adoc)

### Upstream sync (optional)

```bash
git remote add upstream https://github.com/apache/ofbiz-framework.git   # once
git fetch upstream
```

---

## Live-demo tips

- **Wrong branch / wrong JDK** is the most common failure—fix `JAVA_HOME` and `git branch` first.
- HertzBeat needs **two terminals** (backend + `web-app`).
- OFBiz needs **network patience** on first `./gradlew` and `loadAll`.
