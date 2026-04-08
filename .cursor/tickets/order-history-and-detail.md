# Ticket: Order history list and order detail page

## Summary

Expose persisted mock orders: a paginated or simple list of recent orders and a read-only detail page per order ID (reuse existing line-item model).

## Why this fits a demo

Builds on JPA entities already saved at checkout; adds new controller routes, Thymeleaf pages, and repository queries. Demonstrates navigation from confirmation → history.

## Scope

- **In:** `GET /orders` listing recent orders (newest first); `GET /orders/{id}` showing lines and totals; links from header or post-checkout message.
- **Out:** Auth, cancellation, refunds, email delivery.

## Acceptance criteria

- [ ] Order list shows order id, placed time, total, and optional email when present.
- [ ] Order detail matches data from `Order` / `OrderLine` (no duplicate business rules vs checkout).
- [ ] Invalid or missing order id returns 404 using existing error handling pattern.
- [ ] At least one test: repository query or controller smoke test.

## Demo talking points

- “We’re not adding payment—just surfacing data the app already stores.”
- Good moment to show “find references” on `Order` and `CheckoutService`.

## Estimate

Small–medium (demo-sized: ~30–45 minutes with AI assistance).
