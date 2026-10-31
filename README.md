# 3D Printer Quote Service

Do you have a 3D printing business? Do your customers ask for a quote and then
wait while you slice the piece just to give them a price? That's time wasted for
both of you.

This service automates the quoting process: the customer uploads their STL,
picks their settings, and gets a price back, with no manual slicing required.

## How it works

1. The client submits an STL file along with print settings (filament, layer
   profile, infill, quantity, supports)
2. The service validates the request and stores the quote
3. It slices the model through a headless OrcaSlicer instance
4. It calculates the price from material consumed and machine time
5. The client gets back the quote with weight, print time and final price

If the slicer is unavailable, the quote is still saved and flagged for manual
review. The customer keeps their reference id either way.

## Tech stack

- Java 25 / Spring Boot 4.1.1
- Spring Data JDBC (SQL Server)
- Spring Web + Bean Validation
- WebClient for the OrcaSlicer integration
- JUnit 5, Mockito and MockMvc for testing
- Docker and Docker Compose

## Quick start (Docker)

**Prerequisites**: Docker with Compose. Nothing else: Java, SQL Server and the
slicer all run in containers.

1. Copy the environment file and set a password for the database:

   ```
   cp .env.example .env
   ```

   Edit `.env` and set `DB_PASSWORD`. SQL Server rejects weak passwords: use at
   least 8 characters mixing upper case, lower case and numbers. Avoid single
   quotes (`'`), since the password is inserted into a SQL script.

2. Start everything:

   ```
   docker compose up -d --build
   ```

3. Wait a couple of minutes on the first run. SQL Server and the slicer take a
   while to initialize, and a slicer that has just started may close
   connections mid-request.

4. Check that it works:

   ```
   curl http://localhost:8080/3dPrinterQuoteService/filament/available
   ```

   You should get the four sample filaments back.

To stop the system, use `docker compose down`. To also wipe the database, use
`docker compose down -v` (this deletes all stored quotes).

### What `docker compose up` starts

| Service | Role |
|---|---|
| `sqlserver` | SQL Server 2022 (Express), data kept in a named volume |
| `init` | One-shot job: creates the database, the application login and the tables, then loads sample filaments. It exits when done |
| `orcaslicer-headless` | Headless OrcaSlicer, reached by the app over the internal network |
| `quote-service` | This application, exposed on port 8080 |

Startup order is enforced: the app waits for SQL Server to be healthy and for
`init` to finish successfully. If the schema step fails, the app does not start.

`docs/schema.sql` is written to be re-runnable (`IF NOT EXISTS` on every
object) and receives the application password as a `sqlcmd` variable, so the
login always matches the `DB_PASSWORD` the app uses. If you run it manually
outside Docker, replace `$(APP_PASSWORD)` yourself or use SQLCMD mode.

## API

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/quote` | Submit an STL and settings, get a quote back |
| `GET` | `/quote/{id}` | Retrieve an existing quote |
| `GET` | `/filament/available` | List available filaments |
| `GET` | `/filament/colorsByMaterial?materialType=PLA` | List colors for a material |
| `GET` | `/quote/{id}/stl` | Download the STL file stored for a quote (internal use) |
Base path: `/3dPrinterQuoteService`

### Security note

There is no authentication yet. Quote ids are sequential, so `GET /quote/{id}/stl`
would let anyone who can reach the service download any customer's file. It is
meant for the business owner only, and should not be exposed publicly until
access control is added.

### Quote status

| Status | Meaning |
|---|---|
| `CREADO` | Quote registered, not yet priced |
| `COTIZADA` | Successfully sliced and priced |
| `REVISION_MANUAL` | Slicer unavailable, pending manual quote |

## Running without Docker

You need JDK 25, a SQL Server instance and a reachable OrcaSlicer headless
instance.

1. Run `docs/schema.sql` and `docs/seed.sql` against your SQL Server
2. Set the `DB_PASSWORD` environment variable
3. Adjust `slicer.base-url` and `slicer.machine-id` in `application.properties`
   for your printer
4. `./mvnw spring-boot:run`

Default printer profile is a Bambu Lab X1 Carbon with a 0.4mm nozzle (`GM001`).

## Pricing

```
materialCost = (grams × pricePerKg) / 1000
machineCost  = minutes × machineCostPerMin
finalPrice   = (materialCost + machineCost) × priceMultiplier
```

Both `pricing.machine-cost-per-min` and `pricing.profit-price-multiplier` are
configurable. Money is handled with `BigDecimal` and explicit rounding.

## Testing

```
./mvnw test
```

19 automated tests: unit tests for the pricing formula and the quote service
(Mockito), and web-layer tests with MockMvc. The suite runs without Docker:
external dependencies are mocked, including the slicer failure path.

## Design decisions

- **Spring Data JDBC over JPA**: the domain is simple enough that an ORM would
  add indirection without benefit
- **Explicit SQL in `@Query`** rather than derived query methods, for
  readability as conditions grow
- **Slicer failures don't fail the request**: the quote is persisted with
  `REVISION_MANUAL` and the client still receives their id, since the request
  itself was valid
- **Repeatable database initialization**: a one-shot `init` service creates
  the schema and keeps the application login in sync with the configured
  password, so a clean clone works with a single command

## Slicer profiles

The `profiles/` folder contains a subset of the OrcaSlicer system profiles
(BBL and OrcaFilamentLibrary only), copied from the OrcaSlicer project. Other
vendors are excluded on purpose to avoid duplicate `setting_id` collisions.
See `profiles/NOTICE.md` for the origin and license.

## Third-party software

This project calls [orca-headless](https://github.com/leolobato/orcaslicer-headless)
over HTTP, run as a separate container. It is not bundled or modified here.
orca-headless and OrcaSlicer are licensed under AGPL-3.0.

## Roadmap
- Access control for owner-only endpoints such as the STL download
- Store the original filename of the uploaded STL
- Asynchronous quoting (202 + polling): slicing takes ~30s, too long to hold
  an HTTP connection
- Distinguish `copies_dont_fit` from infrastructure failures and return a
  useful error instead of flagging for manual review
- Integration tests against a real SQL Server via Testcontainers

## Documentation

Ticket breakdowns and technical debt notes live in `docs/tickets/`.