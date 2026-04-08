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

## Stack

- Java 17 (Gradle toolchain)
- Spring Boot 3.4, Spring Data JPA, H2, Thymeleaf
