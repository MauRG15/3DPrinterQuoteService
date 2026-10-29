# Ticket #4: Containerización

## Objetivo
Que cualquiera pueda clonar el repo y levantar el sistema completo (app, base
de datos y slicer) con un solo comando, sin instalar Java, SQL Server ni
configurar nada a mano.

## Alcance
- Imagen Docker de la aplicación (multi-stage build)
- `docker-compose.yml` con cuatro servicios: SQL Server, init, OrcaSlicer y app
- Creación automática del esquema y los datos de ejemplo
- Contraseña de la base fuera del repositorio (`.env`)
- README actualizado
- **Fuera de alcance**: despliegue en la nube, CI/CD, optimización de imagen

## Arquitectura

```
sqlserver (healthy) ──> init (crea esquema y termina) ──> quote-service
orcaslicer-headless ───────────────────────────────────────┘
```

| Servicio | Rol |
|---|---|
| `sqlserver` | SQL Server 2022 Express, datos en volumen con nombre, con healthcheck |
| `init` | Servicio de un solo uso: ejecuta `schema.sql` y `seed.sql` con `sqlcmd` y termina |
| `orcaslicer-headless` | Slicer, alcanzado por nombre de servicio en la red interna |
| `quote-service` | La aplicación, expuesta en el puerto 8080 |

## Decisiones tomadas

1. **El esquema se crea con un servicio `init`, no desde la aplicación.**
   JDBC no entiende `GO` (es un separador de herramientas de SQL Server, no
   del motor) y la imagen de SQL Server no ejecuta scripts de inicio por sí
   sola. `sqlcmd` sí, y compose permite servicios que corren una vez.

2. **El script de esquema es repetible.** Cada objeto se crea con
   `IF NOT EXISTS`, y el seed solo inserta si la tabla está vacía. Sin esto,
   cada reinicio sin borrar el volumen habría fallado o duplicado datos.

3. **La contraseña del login de la aplicación llega por `sqlcmd -v`.** El
   script usa `$(APP_PASSWORD)` en lugar de un valor escrito. El login se
   crea o se actualiza con la misma contraseña que recibe la app, así que no
   pueden desincronizarse.

4. **La carpeta `profiles/` se versiona** (11 MB). Es una selección curada
   (solo BBL y OrcaFilamentLibrary, para evitar `setting_id` duplicados) y
   sin ella un clon limpio arranca un slicer sin máquinas. Se documentó su
   origen y licencia en `profiles/NOTICE.md`.

5. **Se usa `-b` en `sqlcmd`.** Sin esa bandera termina con código 0 aunque
   el script tenga errores SQL, y `service_completed_successfully` daría por
   bueno un esquema roto.

## Estado
- [x] Dockerfile multi-stage
- [x] SQL Server en contenedor con healthcheck
- [x] Contraseña en `.env`, con `.env.example` versionado
- [x] `.dockerignore`
- [x] Servicio `init` con esquema repetible
- [x] OrcaSlicer en el compose
- [x] App espera a `sqlserver` (healthy) e `init` (completado)
- [x] `docker compose down -v && docker compose up -d` desde cero funciona
- [x] `POST /quote` devuelve `COTIZADA` dentro de Docker
- [x] `init` es repetible: el conteo de filamentos se mantiene en 4
- [x] README con la sección de Docker

## Lo que costó trabajo: el `Login failed`

La app cayó al arrancar con `Login failed for user 'cotizador3dApp'`. El
diagnóstico fue por descarte, y mis primeras hipótesis estaban mal:

- **No era el orden de arranque.** SQL Server aparecía sano antes de que
  arrancara la app y el error seguía.
- **No faltaban la base ni el login.** Una consulta a `sys.sql_logins` y
  `sys.databases` mostró que ya existían.
- **Era la contraseña.** El login se había creado con el valor que tenía
  entonces `schema.sql`, distinto del `DB_PASSWORD` que recibe la app. Se
  confirmó entrando con `sqlcmd` como ese usuario, que daba el mismo error.

El arreglo manual (`ALTER LOGIN`) solo sirvió para el volumen existente. El
arreglo real fue el diseño de `init`: crear o actualizar el login con la
contraseña correcta cada vez que corre.

Aprendizaje: cuando un mensaje de error es genérico, conviene aislar la
variable con una prueba directa (aquí, conectarse con la misma credencial
desde fuera de la app) en lugar de seguir cambiando configuración.

## Deuda técnica identificada

1. **El slicer no tiene healthcheck.** La app solo espera a que su contenedor
   haya iniciado. Un slicer recién arrancado acepta conexiones pero las cierra
   a media subida, y las primeras cotizaciones pueden caer en
   `REVISION_MANUAL`.
2. **Imágenes con la etiqueta `latest`** para el slicer, y `2022-latest` para
   SQL Server. Un build futuro puede traer cambios inesperados; conviene fijar
   versiones.
3. **El login de la aplicación es `db_owner`.** Es más permiso del necesario.
   Un usuario con acceso de lectura y escritura a sus tablas bastaría.
4. **La contraseña no puede contener comillas simples**, porque se inserta
   dentro de un texto SQL.
5. **`schema.sql` está pensado para Docker.** Ejecutarlo a mano en SSMS exige
   sustituir `$(APP_PASSWORD)`.
6. **Variable `SA_PASSWORD` en desuso**; el nombre actual de la imagen es
   `MSSQL_SA_PASSWORD`.
7. **La contraseña de `sa` se pasa en el healthcheck.** Si se escribe como
   `${DB_PASSWORD}`, queda visible en `docker inspect`. Leerla con `$$SA_PASSWORD`
   desde dentro del contenedor evita eso.