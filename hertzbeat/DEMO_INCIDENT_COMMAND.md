# Incident Command Center (Cursor demo)

This fork adds a thin **Incident Command Center** on top of Apache HertzBeat: it aggregates **firing group alarms** by `service` + `environment`, scores severity, suggests probable cause, and lists response steps. A **deterministic demo** seeds synthetic payment-service alarms for live walkthroughs.

## Prerequisites

- **JDK 25+** (enforced by the Maven enforcer in this repo).
- Maven wrapper: `./mvnw` in the repository root.
- For the UI: Node.js 18+ and `pnpm` (see upstream HertzBeat docs for `web-app`).

## Run backend

From the repo root (JDK 25+). Do **not** combine `spring-boot:run` with `-am` on the root reactor (the root POM can pick up `spring-boot:run` and fail with no main class). Build dependencies, then run only `hertzbeat-startup`:

```bash
./mvnw install -DskipTests -pl hertzbeat-startup -am
./mvnw spring-boot:run -pl hertzbeat-startup
```

## Run frontend

In a **second** terminal (with the backend on **1157**):

```bash
cd web-app
pnpm install
pnpm start
```

Opens **http://localhost:4200/**; `/api` is proxied to the backend via `web-app/proxy.conf.json`. Use `npm install` / `npm run start` if `pnpm` is unavailable.

## Full stack

1. Start backend (commands above).
2. Start frontend (`web-app` commands above).
3. Open **http://localhost:4200/alert/incidents** after login.

## Seed demo data

After login (default admin account per upstream docs), call:

```http
POST /api/incidents/demo/seed
```

Or use **Alarm > Incident Command** in the UI and click **Seed demo**.

## API

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/incidents` | List incident summaries |
| GET | `/api/incidents/{id}` | Detail, timeline, underlying group alarms |
| POST | `/api/incidents/demo/seed` | Replace demo rows with canonical scenario |

## Demo scenario (deterministic)

- **payment-service / prod**: `HighLatency`, `HighErrorRate`, `DeploymentRolledOut` (correlated narrative).
- **payment-service / staging**: `DiskSpaceLow` (separate incident; illustrates grouping by **environment**, not service alone).

Synthetic rows use fingerprint prefix `demo-incident-fp-` and group key prefix `demo-incident-` for safe cleanup on re-seed.

## 10–15 minute Cursor storyline (suggested)

1. **Navigate** the new code: `IncidentAggregationService`, `IncidentHeuristics`, `IncidentDemoSeedService`, `IncidentCommandController`, Angular `incident-command`.
2. **Live edit A**: Add a visible field (e.g. extend `IncidentSummaryDto` with `customerImpact` and show it in the UI).
3. **Live edit B**: Tweak severity or probable-cause rules in `IncidentHeuristics` and re-run unit tests `IncidentHeuristicsTest`.
4. **Live edit C**: Refactor grouping key (e.g. add `region` to the incident key) and adjust seed labels.
5. **Fallback**: If live coding runs long, rely on pre-seeded data and continue from the UI only.

## Verification

```bash
./mvnw -pl hertzbeat-alerter -am test -Dtest=IncidentHeuristicsTest,IncidentIdCodecTest
```

## Files touched (reference)

- Backend: `hertzbeat-alerter/.../incident/`, `.../controller/IncidentCommandController.java`, `IncidentDemoController.java`
- DAO: `GroupAlertDao`, `SingleAlertDao` (demo cleanup queries)
- Auth: `hertzbeat-startup/src/main/resources/sureness.yml`
- UI: `web-app/src/app/routes/alert/incident-command/`, `web-app/src/app/service/incident.service.ts`
