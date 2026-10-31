# Ticket #3 — Suite de pruebas

## Objetivo
Cubrir con pruebas automatizadas la lógica de negocio del cotizador, de modo
que los dos caminos del flujo (cotización exitosa y degradación a revisión
manual) se verifiquen sin depender de que el contenedor de OrcaSlicer esté activo.

## Alcance
- Pruebas unitarias de `PricingService` y `QuoteService`
- Pruebas de integración de la capa web con `MockMvc`
- **Fuera de alcance**: pruebas contra el slicer real, pruebas de carga

## Dependencias agregadas
- `spring-boot-starter-test` (JUnit 5, Mockito, AssertJ)
- `com.h2database:h2` con `<scope>test</scope>`

## Configuración de pruebas
`src/test/resources/application.properties` apunta a H2 en memoria en modo de
compatibilidad con SQL Server, y repite las propiedades de `slicer` y `pricing`
que los beans necesitan para arrancar.

## Pruebas implementadas

### `PricingServiceTest` — unitarias sin mocks
| # | Caso | Verifica |
|---|---|---|
| 1 | Cálculo con valores reales de producción (13.608 g, 2576.55 s) | Da 106.08 |
| 2 | `pricePerKg` con muchos decimales | El resultado tiene escala 2 |
| 3 | Pieza de 0 g y 0 s | No truena, da 0.00 |

### `QuoteServiceTest` — unitarias con mocks
| # | Caso | Verifica |
|---|---|---|
| 4 | Filamento inexistente | `ResourceNotFoundException` |
| 5 | Filamento no disponible | `FilamentUnavailableException` |
| 6 | 51 piezas (límite actual: 50) | `InvalidQuantityException` |
| 7 | Relleno de 101% | `InvalidParametersException` |
| 8 | Correo sin `@` | `InvalidParametersException` |
| 9 | Slicer responde correctamente | Status `COTIZADA`, dos llamadas a `save` |
| 10 | Slicer lanza `SlicerException` | Status `REVISION_MANUAL`, precio en `null`, `PricingService` nunca invocado |

La prueba 10 reemplaza el procedimiento manual de apagar el contenedor Docker.

### Pruebas de integración — capa web con `MockMvc`
| # | Caso | Verifica |
|---|---|---|
| 11 | `GET /quote/99` con el servicio lanzando `ResourceNotFoundException` | 404 — el handler traduce correctamente |
| 12 | `POST /quote` sin campos | 400 — `BindException` manejada |
| 13 | `GET /filament/available` | 200 |

## Estado
- [x] Bloque 1 — `PricingService`
- [x] Bloque 2 — validaciones de `QuoteService`
- [x] Bloque 3 — los dos caminos de `processQuote`
- [x] Bloque 4 — capa web con `MockMvc`
- [x] La suite corre sin Docker encendido
- [x] Verificado que las pruebas fallan al romper el código a propósito

## Deuda técnica identificada

1. **Las validaciones solo son alcanzables a través de `processQuote`.** Cada
   prueba debe satisfacer las validaciones anteriores aunque no le interesen.
   Señal de que el método concentra demasiadas responsabilidades.

2. **H2 no es SQL Server.** Los `@Query` usan sintaxis específica (`[status]`,
   `VARBINARY(MAX)`, `BIT`) que podría comportarse distinto. El modo
   `MODE=MSSQLServer` mitiga pero no garantiza. Testcontainers sería el camino
   correcto a futuro.

3. **La base de pruebas no tiene esquema.** Ningún test ejercita los repositorios
   reales, así que las tablas nunca se crearon. Cuando se agreguen pruebas de
   repositorio hará falta un `schema.sql` en `src/test/resources`.

4. **`OrcaSlicerClientProxy` no tiene pruebas.** Verificar que arma bien los
   requests y mapea bien las respuestas requiere un servidor HTTP falso
   (WireMock o MockWebServer), fuera del alcance de este ticket.

5. **El límite de 50 piezas es arbitrario.** El slicer devuelve `copies_dont_fit`
   según la geometría real de la pieza. Convendría distinguir ese error de un
   fallo de infraestructura y devolver 400 con un mensaje útil en lugar de
   `REVISION_MANUAL`. Candidato a Ticket #4.