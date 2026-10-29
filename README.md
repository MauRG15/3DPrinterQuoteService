# 3D Printer Quote Service

Do you have a 3D printing business? Do your customers ask for a quote and then
wait while you slice the piece just to give them a price? That's time wasted for
both of you.

This service automates the quoting process: the customer uploads their STL,
picks their settings, and gets a price back — no manual slicing required.

## How it works

1. The client submits an STL file along with print settings (filament, layer
   profile, infill, quantity, supports)
2. The service validates the request and stores the quote
3. It slices the model through a headless OrcaSlicer instance
4. It calculates the price from material consumed and machine time
5. The client gets back the quote with weight, print time and final price

If the slicer is unavailable, the quote is still saved and flagged for manual
review — the customer keeps their reference id either way.

## Tech stack

- Java 25 / Spring Boot 4.1.1
- Spring Data JDBC (SQL Server)
- Spring Web + Bean Validation
- WebClient for the OrcaSlicer integration
- JUnit 5 + Mockito for testing

## API

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/quote` | Submit an STL and settings, get a quote back |
| `GET` | `/quote/{id}` | Retrieve an existing quote |
| `GET` | `/filament/available` | List available filaments |
| `GET` | `/filament/colorsByMaterial?materialType=PLA` | List colors for a material |

Base path: `/3dPrinterQuoteService`

### Quote status

| Status | Meaning |
|---|---|
| `CREADO` | Quote registered, not yet priced |
| `COTIZADA` | Successfully sliced and priced |
| `REVISION_MANUAL` | Slicer unavailable — pending manual quote |

## Running locally

**Prerequisites**: JDK 25, SQL Server, and a headless OrcaSlicer instance
reachable at the configured URL.

1. Run `docs/schema.sql` to create the database and tables
2. Run `docs/seed.sql` to load sample filaments
3. Set the `DB_PASSWORD` environment variable
4. Adjust `slicer.base-url` and `slicer.machine-id` in `application.properties`
   for your printer
5. `./mvnw spring-boot:run`

Default printer profile is a Bambu Lab X1 Carbon with a 0.4mm nozzle (`GM001`).

## Pricing
materialCost = (grams × pricePerKg) / 1000
machineCost = minutes × machineCostPerMin
finalPrice = (materialCost + machineCost) × priceMultiplier


Both `pricing.machine-cost-per-min` and `pricing.profit-price-multiplier` are
configurable.

## Testing
./mvnw test
The suite runs without requiring the OrcaSlicer container — external
dependencies are mocked, including the slicer failure path.

## Design decisions

- **Spring Data JDBC over JPA** — the domain is simple enough that an ORM would
  add indirection without benefit
- **Explicit SQL in `@Query`** rather than derived query methods, for
  readability as conditions grow
- **Slicer failures don't fail the request** — the quote is persisted with
  `REVISION_MANUAL` and the client still receives their id, since the request
  itself was valid

## Roadmap

- Distinguish `copies_dont_fit` from infrastructure failures and return a
  useful error instead of flagging for manual review
- Asynchronous quoting (202 + polling) — slicing takes ~30s, too long to hold
  an HTTP connection
- Integration tests against a real SQL Server via Testcontainers

## Documentation

Ticket breakdowns and technical debt notes live in `docs/tickets/`.

## Slicer profiles

The `profiles/` folder contains a subset of the OrcaSlicer system profiles
(BBL and OrcaFilamentLibrary only), copied from the OrcaSlicer project. Other
vendors are excluded on purpose to avoid duplicate `setting_id` collisions.
See `profiles/NOTICE.md` for the origin and license.

## Third-party software

This project calls [orca-headless](https://github.com/leolobato/orcaslicer-headless)
over HTTP, run as a separate container. It is not bundled or modified here.
orca-headless and OrcaSlicer are licensed under AGPL-3.0.