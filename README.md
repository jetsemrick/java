# Cursor Retro Parts (demo)

Spring Boot + Thymeleaf demo store for retro computer parts. UI uses Cursor-inspired light theme tokens (`static/css/cursor-theme.css`). Data is seeded on startup into an embedded H2 database.

## Run

```bash
./gradlew bootRun
```

Open [http://localhost:8080](http://localhost:8080).

## Useful routes

| Path | Description |
|------|-------------|
| `/` | Home — categories and product cards |
| `/products/{id}` | Product detail + add to cart |
| `/categories/{id}` | Products in one category |
| `/cart` | Cart lines, update/remove |
| `/checkout` | Mock checkout (optional email + terms) |
| `/order/confirmation/{id}` | Order summary after checkout |
| `/admin/catalog` | Read-only admin inventory table |

## Tests

```bash
./gradlew test
```

**Demo (intentional red test):** `FulfillmentEtaServiceTest#homeBannerText_reflectsTwentyFourHourFulfillmentWindow` fails because `FulfillmentEtaService` uses `Math.pow(buffer, courier)` instead of summing the two hour values (banner shows a huge number). Set a breakpoint in `FulfillmentEtaService#homeBannerText`, fix the math, re-run `./gradlew test`. The home page reads the same string from the model (`fulfillmentBanner`).

## Stack

- Java 17 (Gradle toolchain for compile/test)
- Gradle 9.3 (wrapper)
- Spring Boot 3.4, Spring Data JPA, H2, Thymeleaf

## IntelliJ / Gradle sync

The wrapper uses **Gradle 9.3.0**, which matches current IntelliJ expectations when the **Gradle JVM** is newer. If sync still warns about the JVM Gradle runs on, set **Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JVM** to **JDK 17, 21, or 23** (your app still compiles with toolchain 17). Using JDK 25 only for the IDE’s Gradle daemon can be unsupported depending on IDE + Gradle pairing.
