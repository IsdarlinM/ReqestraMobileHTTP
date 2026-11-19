# Corrección de superposiciones — Reqestra 3.5.0

## Objetivo

Evitar que los botones, títulos, controles de idioma, acciones de listas y
barras de navegación se monten entre sí en **Android (Jetpack Compose)** y
**Web/PWA**, conservando sus funciones y la versión 3.5.0.

## Cambios realizados

### Web

- La cabecera reserva espacio para **Donar**, **idioma** y **tema**. En
  pantallas de 480 px o menos, el logotipo permanece visible pero el texto
  de la marca se oculta; las tres acciones mantienen un área táctil de
  44 × 44 px. Los indicadores de estado se ocultan en pantallas pequeñas.
- Los botones en formularios y paneles se disponen en una o dos columnas,
  con salto de línea en etiquetas largas. Las listas permiten que sus
  acciones pasen a la siguiente fila y evitan anchos mínimos rígidos.
- Los encabezados de tarjetas se ajustan a más de una fila. Los campos
  quedan en una columna cuando la zona de contenido es estrecha.
- Los menús flotantes tienen altura máxima y desplazamiento propio; no
  deben invadir ni quedar bajo la navegación inferior.
- El contenido inferior respeta la barra fija y el área segura del sistema.
- La identidad, el selector 🌐 y las donaciones siguen operativos, en
  modo claro y oscuro.

### Android

- La cabecera pasa a mostrar solo el monograma cuando el ancho es menor
  de **390 dp**; a menos de **600 dp**, Donar usa el icono de corazón.
  Idioma y apariencia siguen accesibles. Los iconos tienen 44 dp.
- La navegación inferior reduce las bandas horizontales de sus iconos
  para evitar invasiones en teléfonos de 320–360 dp.
- En **Estudio**, las acciones Enviar, cargar respuesta, copiar/importar
  cURL y cancelar se colocan en una columna con espacios uniformes.
  El resumen HTTP y el botón del inspector pueden saltar a otra línea.
- En **Sesiones**, Guardar y Limpiar se muestran separados y ocupan
  toda la anchura.
- En **Historial**, el encabezado y Limpiar no compiten por la misma fila.
  Los metadatos HTTP se muestran en filas de lectura.
- En **Colecciones**, los botones Abrir y Eliminar están separados del
  contenido de la tarjeta; nombres y menús largos se truncan o saltan de
  línea sin reducir el área táctil.
- En **Inspector**, la información de la petición, los encabezados y
  los botones de copia tienen su propio espacio.
- En **Automatización**, los selectores de colecciones se ajustan
  al ancho disponible, incluso con nombres largos.
- En **Explorar**, las etiquetas extensas junto a las casillas
  disponen del ancho restante y pueden abarcar varias líneas.

## Pruebas de regresión

La prueba `web/tests/layout.test.mjs` comprueba las reglas
responsivas y los componentes Android que causaban conflictos de
diseño. Se ejecuta junto a los demás tests web:

```bash
cd web && npm run check && npm test
```

Para compilar Android:

```bash
./gradlew --no-daemon :app:testDebugUnitTest :app:assembleDebug
```

## Matriz visual para comprobar antes de distribuir

| Entorno | Anchos propuestos | Comprobación |
| --- | --- | --- |
| Web móvil | 280, 320, 360, 390, 420, 480 px | Cabecera, menús, tarjetas y barra inferior |
| Web tablet | 600, 750, 800, 1024 px | Paneles y listas que se reajustan |
| Web escritorio | 1280 y 1440 px | Navegación lateral y formularios |
| Android | 320, 360, 390, 420 y 600 dp | Cabecera, barra inferior y formularios |
| Accesibilidad | Escala de texto 100 %, 150 % y 200 % | Truncamientos y tamaño de controles |
| Temas e idiomas | Claro/oscuro, en/es/ru/zh/pt/fr | Etiquetas largas y contraste |

En cada combinación, verificar pulsando todas las acciones. Las
pruebas estáticas no sustituyen a un control visual mediante navegador
y dispositivo real. Conviene comprobar especialmente la orientación
horizontal, el teclado virtual y los tamaños de fuente aumentados.

**Sin cambio de versión:** Android y Web mantienen Reqestra **3.5.0**.
