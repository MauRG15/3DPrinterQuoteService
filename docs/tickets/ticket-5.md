# Ticket #5: Descarga del STL de una cotización

## Objetivo
Que el dueño del negocio pueda recuperar el archivo STL que un cliente subió,
a partir del id de su cotización. Antes el archivo entraba a la base de datos
(`stl_file`, VARBINARY) pero no había forma de sacarlo.

## Alcance
- `GET /quote/{id}/stl` devuelve el archivo como descarga
- 404 si la cotización no existe
- Pruebas unitarias y de capa web
- **Fuera de alcance**: autenticación, conservar el nombre original del archivo

## Decisiones tomadas

1. **El servicio devuelve solo los `byte[]`, sin DTO.** El controller ya conoce
   el id (llega en la URL), así que devolverlo de vuelta no aporta nada. Un
   DTO aparecería si el servicio tuviera que transportar más de un dato nuevo,
   por ejemplo bytes y nombre de archivo.
2. **`QuoteResponseDTO` no se modifica.** Es el contrato JSON de `POST /quote`
   y `GET /quote/{id}`. Agregarle los bytes los habría convertido en base64
   dentro del JSON de cada consulta.
3. **Nombre del archivo derivado del id** (`quote-{id}.stl`). La base solo
   guarda los bytes. Guardar el nombre original es mejor para el usuario,
   pero obliga a cambiar el esquema, la entidad y las pruebas del Ticket #3,
   y a manejar filas viejas sin nombre. Queda como deuda técnica.
4. **Content-Type `application/octet-stream`**: archivo binario genérico,
   sin inventar un tipo que otros clientes no entiendan.
5. **Se reutiliza `findQuote`.** Carga la fila completa, incluido el STL.
   Una consulta que seleccione solo `stl_file` sería más eficiente, pero no se
   investigó si Spring Data JDBC la mapea bien; se deja como deuda.
6. **El nombre del archivo no es un candado de seguridad.** Lo manda el
   cliente al subir el archivo, así que no verifica nada. Que se descargue el
   archivo correcto lo garantiza `WHERE id = :id`.

## Estado
- [x] Método `getStlById` en `QuoteService`
- [x] Pruebas unitarias: quote existente (mismos bytes) e id inexistente
- [x] `GET /quote/{id}/stl` con `Content-Type` y `Content-Disposition`
- [x] Pruebas MockMvc: 200 con cabeceras y cuerpo, y 404
- [x] Verificación real: el archivo descargado es idéntico al subido
- [x] README actualizado

## Evidencia
| Prueba | Resultado |
|---|---|
| SHA-256 del STL original | `6cea18d0ac183af8f5e110df2103a61a68ce184f5c0a7f7f144632dd6ec1982c` |
| SHA-256 del STL descargado | `6cea18d0ac183af8f5e110df2103a61a68ce184f5c0a7f7f144632dd6ec1982c` |

Los dos hashes son idénticos: el archivo no se corrompe al guardarse ni al
descargarse.

## Deuda técnica identificada

1. **Sin autenticación.** Los ids son consecutivos, así que cualquiera que
   conozca la URL puede descargar el STL de cualquier cliente probando
   números. Es el punto más delicado del ticket. El proyecto decidió no usar
   Spring Security por ahora, así que queda documentado como riesgo conocido
   y el endpoint se describe como de uso interno.
2. **Nombre original perdido.** Se podría guardar en una columna nueva con un
   `ALTER TABLE` protegido, y el servicio devolvería un record con nombre y
   bytes.
3. **`GET /quote/{id}` carga el STL en cada consulta** aunque no lo devuelve.
4. **El archivo completo viaja en memoria** (`byte[]`). Para archivos de hasta
   100 MB funciona, pero no escala. Streaming sería la alternativa.