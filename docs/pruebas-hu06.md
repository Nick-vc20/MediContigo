# HU-06 · Pruebas de visualización pública y usabilidad (6.4)

Historia: *Como paciente, quiero consultar información completa del centro para tomar una decisión antes de acudir.*

## 1. Pruebas automáticas (backend)

```
cd backend
./mvnw test
```

Archivo: `PerfilCentroPublicoTest.java`

| Prueba | Qué comprueba |
|---|---|
| perfilConsolidadoTraeDatosEspecialidadesHorariosYPersonal | El perfil trae datos del centro, especialidades, horarios y personal |
| losHorariosVienenOrdenadosPorDia | Los horarios salen ordenados de lunes a domingo |
| noMuestraEspecialidadesInactivas | Una especialidad dada de baja desaparece del perfil |
| noMuestraMedicosDadosDeBaja | Un médico desactivado no aparece en el personal |
| centroInexistenteDevuelve404 | Un id que no existe responde 404 |
| elMapaDevuelveVariosCentrosOrdenadosPorDistancia | El mapa lista varios centros, del más cercano al más lejano |
| elRadioLimitaLosResultados | Con un radio menor salen menos centros |
| rechazaUbicacionInvalida | Latitud fuera de rango o radio 0 son rechazados |

## 2. Prueba del endpoint (Thunder Client o navegador)

| Petición | Resultado esperado |
|---|---|
| `GET http://localhost:8081/api/publico/centros/1` | 200 con centro, especialidades, horarios y médicos |
| `GET http://localhost:8081/api/publico/centros/9999` | 404 |
| `GET http://localhost:8081/api/centros?lat=-12.0464&lng=-77.0428&radio=10` | 200, lista de centros con `distanciaKm`, ordenada |
| `GET http://localhost:8081/api/centros?lat=500&lng=0` | 400 con mensaje |

## 3. Visualización y usabilidad (manual)

Abrir `http://localhost:5173/`. Marcar cada fila al probar.

| # | Prueba | Esperado | OK |
|---|---|---|---|
| 1 | Cargar el mapa con ubicación permitida | Se ven la lista lateral y el mapa con todos los centros del radio | ☐ |
| 2 | Bloquear la ubicación del navegador y recargar | Aviso amarillo y centros cerca de Lima Centro | ☐ |
| 3 | Clic en un centro de la lista | El panel muestra el perfil y el mapa se acerca al marcador | ☐ |
| 4 | Clic en un marcador del mapa | Mismo resultado que el punto 3 y el marcador se agranda | ☐ |
| 5 | Botón «← Volver a la lista» | Regresa a la lista sin perder los resultados | ☐ |
| 6 | Cambiar radio 5 km / 10 km | La lista y el mapa se actualizan | ☐ |
| 7 | Buscar otra zona (ej. «Miraflores, Lima») | El mapa se centra ahí y la lista cambia | ☐ |
| 8 | Abrir una especialidad en el perfil | Se ven descripción, horarios agrupados y médicos con CMP | ☐ |
| 9 | Especialidad que atiende hoy | Muestra la etiqueta «Atiende hoy» y el día actual resaltado | ☐ |
| 10 | Emergencias | El horario se muestra como «24 horas» | ☐ |
| 11 | «Cómo llegar» y «Llamar» | Abren Google Maps con destino y el marcador telefónico | ☐ |
| 12 | «Página completa» | Abre `/centro/:id`; el enlace se puede compartir | ☐ |
| 13 | Abrir `/centro/9999` | Mensaje «No encontramos este centro médico» y botón Reintentar | ☐ |
| 14 | Apagar el backend y abrir un perfil | Mensaje de error claro y botón Reintentar | ☐ |
| 15 | Ancho 360 px (celular) | El mapa va arriba y el panel debajo; al elegir un centro baja al perfil; sin scroll horizontal | ☐ |
| 16 | Ancho 768 px (tablet) | Misma disposición vertical, todo legible | ☐ |
| 17 | Ancho 1280 px (escritorio) | Panel a la izquierda y mapa a la derecha | ☐ |
| 18 | Solo teclado (Tab, Enter) | Se puede elegir un centro, abrir especialidades y volver a la lista | ☐ |

## 4. Datos de ejemplo

`data.sql` carga 11 centros de Lima con teléfono, costo, tiempo de espera, nivel de ocupación,
40 especialidades, sus horarios y 45 médicos. Los nombres, teléfonos y CMP son ficticios.
