# Ticket: Product search and category filters

## Summary

Add a search box and optional category filter on the storefront so visitors can find retro parts without scrolling the full catalog.

## Why this fits a demo

Touches UI (Thymeleaf + CSS), query layer (`ProductRepository` / `CatalogService`), and at least one test. Shows refactoring across layers with clear before/after behavior.

## Scope

- **In:** Home (or dedicated `/shop`) with text search over product name, SKU, and description; optional filter by category (dropdown or chips).
- **Out:** Full-text engine (Elasticsearch), autocomplete API, pagination beyond a simple “show first N results” if needed for large lists.

## Acceptance criteria

- [ ] User can enter a query and see a result list (or empty state) with existing card styling.
- [ ] User can restrict results to one category without losing the search string.
- [ ] Repository or service exposes a clear method (e.g. `search(String q, Long categoryId)`) suitable for unit testing.
- [ ] At least one automated test covers search behavior (repository slice or service test).

## Demo talking points

- “We’ll add a feature that spans the template, service, and persistence layer.”
- Show running tests after the change.

## Estimate

Small–medium (demo-sized: ~30–45 minutes with AI assistance).
