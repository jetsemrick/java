# Ticket: Fix fulfillment ETA banner calculation

## Summary

The home page fulfillment banner is driven by `FulfillmentEtaService#homeBannerText()`, which should combine `PROCESSING_BUFFER_HOURS` (**8**) and `COURIER_WINDOW_HOURS` (**16**) into a single “ship within X hours” promise (**24** total).

### Current (buggy) behavior

- Code uses **`(long) Math.pow(PROCESSING_BUFFER_HOURS, COURIER_WINDOW_HOURS)`** — i.e. **8^16**, not a sum.
- Rendered banner text is: **`Fulfillment: orders ship within 281474976710656 hours`** (and the home “Fulfillment ETA” strip shows that value).
- `FulfillmentEtaServiceTest` fails because it asserts the **correct** string ending in **`24 hours`**.

### Expected behavior

- Total hours = **`PROCESSING_BUFFER_HOURS + COURIER_WINDOW_HOURS`** → **24**.
- Banner must read exactly: **`Fulfillment: orders ship within 24 hours`**.

## Why this fits a demo

- **Debugger-friendly:** Break on `totalHours`, inspect `Math.pow` vs adding the two constants, fix, re-run tests.
- **Cursor Debug mode:** Run `FulfillmentEtaServiceTest`, step through `homeBannerText()`, watch the absurd magnitude before the string is built.
- Touches one service + `StoreController` model attribute + `home.html` (`fulfillmentBanner`) — small, coherent slice.

## Scope

- **In:** Replace **`Math.pow`** with a **sum** of the two hour constants in [`FulfillmentEtaService`](src/main/java/com/cursor/retrostore/fulfillment/FulfillmentEtaService.java) so the banner reads `Fulfillment: orders ship within 24 hours`. Green [`FulfillmentEtaServiceTest`](src/test/java/com/cursor/retrostore/fulfillment/FulfillmentEtaServiceTest.java).
- **Out:** Real carrier APIs, business calendars, or timezone rules.

## Acceptance criteria

- [ ] `FulfillmentEtaServiceTest#homeBannerText_reflectsTwentyFourHourFulfillmentWindow` passes.
- [ ] Home page (`/`) displays the 24-hour promise (via `fulfillmentBanner`), not a pow-sized number.
- [ ] No change required to Thymeleaf beyond verifying `th:text="${fulfillmentBanner}"` still binds.

## Demo talking points

- “`pow` for ETA hours is absurd — obvious in code review or at a breakpoint.”
- Show failing test → fix line → green suite.

## Estimate

XS (demo-sized: ~5–15 minutes with AI or a breakpoint).
