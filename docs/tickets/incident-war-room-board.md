# Ticket: Incident war room board (Kanban)

| Field | Value |
|--------|--------|
| **ID** | HB-DEMO-001 |
| **Type** | Feature |
| **Area** | Incident Command Center (Angular + API) |
| **Priority** | P1 |

## Summary

Add a **war room** view that shows active incidents as cards on a **Kanban-style board** with columns such as *Triage → Investigating → Mitigated → Closed*. Users can **drag cards** between columns to update workflow state. The view must be **visually obvious** in demos (columns, colors, counts).

## Problem

The current Incident Command list/detail flow does not surface **response workflow** or **team coordination**. A board makes status and progress legible at a glance.

## Scope

### In scope

- New route (e.g. `/alert/incidents/war-room` or `/alert/war-room`) and menu entry under **Alarm**.
- Board UI: columns, incident cards (reuse summary fields: service, environment, severity, headline).
- Persist **workflow state** per incident (new field or mapping from incident id → state).
- Backend: REST to **update state** on drag-drop (or explicit save); list incidents for board filtered by state as needed.
- **Deterministic demo**: seed or mock data can drive at least one card per column for screenshots.

### Out of scope

- Real multi-user locking, full RBAC beyond existing Sureness roles.
- Replacing the existing list/detail pages (board is additive).

## Acceptance criteria

1. With backend + frontend running, user can open the war room URL and see **four columns** with titles and **empty states** when no incidents exist.
2. After **Seed demo** (or equivalent), **at least one** incident appears as a card; user can **move** it to another column and **refresh** to see the same state (persisted).
3. **Visual**: column headers, card severity badge, and **column counts** (or WIP hint) are visible without opening devtools.
4. Automated tests (unit or integration) cover **state transition** logic or API contract for PATCH/update.
5. **Sureness**: new `/api/...` routes are registered in `hertzbeat-startup` `sureness.yml` (and `script/sureness.yml` if used for Docker).

## Technical notes

- **Frontend**: `hertzbeat/web-app` — ng-zorro drag/drop or CDK drag-drop; follow existing `alert` module patterns.
- **Backend**: extend incident slice in `hertzbeat-alerter` (e.g. store workflow on a synthetic entity or extend DTOs) — prefer minimal schema change; document choice in PR.
- **IDs**: incident IDs are already URL-safe (`IncidentIdCodec`); use them as keys for state.

## Risks

- Drag-drop accessibility on mobile: keep keyboard path or “Move to…” dropdown as fallback.
- Persistence: if only in-memory, document “demo only” or add H2/DB table for dev.

## Demo script (1–2 min)

1. Open war room → empty columns.
2. Seed demo → cards appear.
3. Drag one card → column updates → reload page → state holds.
