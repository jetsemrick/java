---
name: retro-store-start
description: >-
  Starts the Cursor Retro Parts Spring Boot demo (Thymeleaf storefront on port 8080).
  Use when opening this repository, running the retro store locally, verifying the
  stack after clone, or when the user asks how to boot the Java demo app or Gradle bootRun.
---

# Retro Store — local start

## What this is

Greenfield Spring Boot app at the repo root: `retro-store` (Gradle). Server-rendered shop with H2, session cart, mock checkout.

## Start the app

From the repository root:

```bash
./gradlew bootRun
```

Wait for `Tomcat started on port 8080`, then open:

- [http://localhost:8080](http://localhost:8080)

## Stop

`Ctrl+C` in the terminal running `bootRun`.

## Quick checks

| Goal | Command or URL |
|------|----------------|
| Run tests | `./gradlew test` |
| H2 console (dev) | [http://localhost:8080/h2-console](http://localhost:8080/h2-console) — JDBC URL `jdbc:h2:mem:retrostore`, user `sa`, empty password |

## Prerequisites

- **Java 17+** (Gradle toolchain resolves 17 if installed)
- **Network** on first run (Gradle may download dependencies)

## If port 8080 is busy

Set another port for one run:

```bash
./gradlew bootRun --args='--server.port=8081'
```

Or add `server.port=8081` in `src/main/resources/application.properties` for a persistent change.
