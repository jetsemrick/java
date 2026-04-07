# Ticket: Incident blast radius map

| Field | Value |
|--------|--------|
| **ID** | HB-DEMO-002 |
| **Type** | Feature |
| **Area** | Incident Command Center (Angular + API) |
| **Priority** | P1 |

## Summary

Add a **visual blast-radius map**: a graph or diagram (nodes + edges) showing **services and dependencies**, with **affected nodes highlighted** when an incident is selected. The demo uses **seed-friendly** data (labels or a static graph) so the map is **always visually striking** without extra infra.

## Problem

Text-only “blast radius” strings do not communicate **scope** or **dependencies** to stakeholders. A map makes impact **obvious in screenshots and live demos**.

## Scope

### In scope

- New panel or route (e.g. `/alert/incidents/map` or a **tab** on incident detail) showing **nodes and links**.
- **Highlight** nodes affected by the **current incident** (from incident id or service label).
- Data source (pick one for v1, document in PR):
  - **Static graph** JSON shipped with the app for demo services (`payment-service`, deps), or
  - **Labels-driven** mapping from `service` / `region` / `environment` to graph positions, or
  - Simple **adjacency list** returned by a new `/api/incidents/.../graph` endpoint.
- **Demo mode**: one click loads the canonical payment-service scenario so the map **always** shows red/amber nodes.

### Out of scope

- Live service discovery from Kubernetes or real CMDB.
- Full graph layout engine (start with a **fixed layout** or a small library already in the bundle).

## Acceptance criteria

1. User can open the map view from **Incident Command** (link or tab) without manual URL typing.
2. **Visual**: at least **5 nodes** and **4 edges** visible in the default demo layout; affected nodes use **distinct color** (e.g. red) vs healthy (e.g. green/gray).
3. Selecting an incident **updates** the highlight set (or shows a clear message if no mapping).
4. **Performance**: initial render under 1s on a laptop with demo data (no blocking network for layout).
5. **Tests**: unit test for “resolve affected node ids from incident” or snapshot for graph DTO builder.

## Technical notes

- **Frontend**: `hertzbeat/web-app` — prefer SVG or Canvas, or a **single** lightweight dependency if not already present (justify in PR).
- **Backend** (optional for v1): if graph is static, ship JSON under `assets/`; if dynamic, add `IncidentGraphService` in `hertzbeat-alerter` next to incident DTOs.
- **Security**: if new API, add Sureness rules.

## Risks

- Bundle size if adding a heavy graph library — prefer minimal SVG first.
- Overlap with upstream HertzBeat UI — keep **namespace** and routes under `incident` demo.

## Demo script (1–2 min)

1. Seed demo → open map → **payment-service** and related nodes highlighted.
2. Switch incident (if two exist) → highlight set changes.
3. Screenshot slide: map + incident list side by side.
