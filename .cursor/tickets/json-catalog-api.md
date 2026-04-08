# Ticket: JSON catalog API for integrations

## Summary

Add a small REST API under `/api` (e.g. `GET /api/products`, `GET /api/products/{id}`) returning JSON for the same catalog shown in HTML, for “headless” or tooling demos.

## Why this fits a demo

Classic vertical slice: DTOs or projection interfaces, `@RestController`, content negotiation, and optional integration test with `MockMvc` or `WebTestClient`. Pairs well with showing the same domain in UI vs API.

## Scope

- **In:** Read-only JSON responses; stable field names; 404 for unknown product id; optional `GET /api/categories` listing categories with nested product ids or counts.
- **Out:** Auth, rate limiting, versioning, OpenAPI publish (unless you want an extra stretch goal).

## Acceptance criteria

- [ ] `GET /api/products` returns a JSON array with id, sku, name, price, category id/name for each product.
- [ ] `GET /api/products/{id}` returns one product or 404.
- [ ] Responses do not leak internal stack traces on error.
- [ ] At least one API test asserts status and JSON shape for a happy path.

## Demo talking points

- “Same `CatalogService`, second surface area—good for showing consistency.”
- Optional: curl or browser snippet hitting `/api/products` during the demo.

## Estimate

Small (demo-sized: ~20–35 minutes with AI assistance).
