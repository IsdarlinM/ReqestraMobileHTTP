# Reqestra 3.1 — sistema visual

## Objetivo

Presentación consistente en temas claro y oscuro sin alterar reglas HTTP, sesiones ni persistencia.

## Paleta

| Uso | Oscuro | Claro |
|---|---|---|
| Fondo | #07131F | #F3F7FA |
| Superficie | #102130 | #FFFFFF |
| Superficie secundaria | #203647 | #E5EDF2 |
| Acción principal | #34E8C2 | #006B5B |
| Texto principal | #EAF4F8 | #152B36 |
| Texto secundario | #B8C8D5 | #506472 |
| Información | #AED8FF | #225D85 |
| Advertencia | #FFD38E | #875000 |
| Error | #FFB4AE | #B3261E |

Los colores onPrimary, onSurface y onContainer se definen en presentation/theme/Theme.kt.

## Contraste y semántica

- Principal claro sobre oscuro: 15.12:1.
- Texto secundario oscuro sobre superficie secundaria oscura: 7.30:1.
- Acción principal oscura sobre blanco: 6.45:1.
- Texto secundario claro sobre superficie secundaria clara: 5.20:1.
- Texto sobre contenedor de éxito oscuro: 8.57:1.

Estos pares verificados superan 4.5:1, pero no sustituyen una auditoría integral de accesibilidad.

GET y HEAD usan azul; POST, verde; PUT/PATCH, ámbar; DELETE, rojo. Los códigos HTTP 2xx se muestran en verde, 3xx en ámbar y 4xx/5xx en rojo. El texto acompaña siempre al color.

## Componentes

- StudioScreenHeader: títulos, rótulos superiores y subtítulos consistentes.
- StudioSurfaceCard: tarjetas con borde sutil y esquinas de 22 dp.
- StudioTag y StudioMethodTag: indicadores con color y descripción textual.
- StatusBanner: información azul y error rojo, con controles accesibles.
- CodeEditor: fondo suave, monoespaciado legible y borde verde al enfocar.
- ReqestraApp: barra superior con marca, navegación con selección destacada.
- WorkspaceScreen: botón principal de enviar visible y resumen HTTP mejorado.

## Verificación

En Termux, ejecutar ./gradlew :app:assembleDebug :app:testDebugUnitTest.
Comprobar ambos temas, tamaño de texto aumentado, TalkBack, orientación, URLs largas y todos los estados de petición.
Las pruebas de compilación Android no se consideran aprobadas hasta que se ejecute el comando local y termine correctamente; GitHub Actions ha tenido errores de arranque de runner.
