# Alcance de Reqestra 3.5.1

La versión **3.5.1** aplica correcciones de alto impacto visual,
accesibilidad y entrega. Las demás iniciativas permanecen como propuestas,
no se consideran implementadas automáticamente.

## Separación de controles y accesibilidad

- La barra superior Android distribuye **Idioma 🌐**, **Donar ♥** y
  **Apariencia ☀/🌙** en una fila con `Arrangement.spacedBy(10.dp)`.
- Añade `padding(end = 8.dp)`, evita botones pegados al borde y conserva
  objetivos táctiles de **44 dp** y el menú de seis idiomas.
- A la espera de verificación mediante compilación y prueba física;
  la comprobación estática se encuentra en `web/tests/layout.test.mjs`.

## Prioridades restantes y comprobación

| Prioridad | Mejora | Criterio de aceptación |
| --- | --- | --- |
| P0 | Validación visual de la barra superior | Idioma, Donar y Tema claramente separados a 320, 360, 390, 420 y 600 dp, en ambos temas y seis idiomas, sin tocarse ni recortarse |
| P0 | Restaurar CI Android y Web | Cada ejecución de GitHub Actions termina con pruebas ejecutadas, no fallos previos a la asignación del ejecutor; se obtiene APK de prueba |
| P0 | Actualización de APK documentada correctamente | Distinguir `applicationIdSuffix=".debug"` del APK release; confirmar firma, versionCode y el proceso para actualizar sin perder datos |
| P1 | Pruebas automáticas de UI Compose | Validar pulsación independiente, accesibilidad y apertura de Idioma, Donar y Apariencia, incluida pantalla estrecha |
| P1 | Pruebas web adaptativas | Ejecutar pruebas visuales en anchos 320–1440 px, menús abiertos, temas e idiomas; detectar solapamientos reales |
| P1 | Internacionalización | Probar etiquetas largas en ruso/francés/portugués, normalización de idioma del sistema y avisos de validación, sin traducir respuestas HTTP ni datos del usuario |
| P1 | Robustez de operaciones HTTP | Comprobar cancelación, timeout, errores TLS, redireccionamientos entre orígenes, respuestas grandes y preservación de datos cifrados |
| P1 | Donaciones seguras | Verificar que la URL pública de PayPal y el beneficiario son correctos, sin usar el enlace de administración como destino de pago |
| P2 | Accesibilidad | Asegurar lectura de etiquetas, orden de enfoque, contraste y tamaños de fuente 150 %/200 % |
| P2 | Rendimiento y experiencia | Optimizar respuestas JSON grandes, estados de carga y navegación sin perder el borrador |

## Fuera del alcance de esta versión de mantenimiento

- Soporte para extensiones remotas ejecutables.
- Sincronización en la nube entre Android y Web.
- Sistema de cuentas, pagos dentro de Reqestra o nuevas capacidades
  que modifiquen el modelo de datos.

Estos cambios deben analizarse como mejoras para 3.6 o superior,
con diseño y revisión de seguridad específicos.

## Validación reproducible en Termux

```bash
cd ~/ReqestraMobileHTTP
git pull --ff-only origin main
cd web && npm run check && npm test && cd ..
./gradlew --no-daemon --console=plain :app:testDebugUnitTest :app:assembleDebug
```

Comprueba el APK Android en 320, 360 y 420 dp con los seis idiomas,
ambos temas y tamaño de fuente aumentado. Las pruebas estáticas no
pueden demostrar la ausencia de superposición en todos los dispositivos.

**Estado:** se actualizó `versionName` a **3.5.1** y `versionCode`
a **10**. Para validar una instalación release se requiere firmar con la
misma clave que su versión anterior; el APK debug es otro paquete.

## Cambios ya incorporados en el código 3.5.1

- Donar web utiliza SVG para evitar distorsión; fondo y borde compatibles
  con los temas claro y oscuro, con diseño adaptado al ancho del dispositivo.
- La apariencia en Web ahora usa iconos SVG de sol y luna, y nombres de acción
  accesibles traducidos en los seis idiomas.
- Acciones de la barra superior Android claramente separadas,
  identificables por pruebas instrumentadas, con un estilo visual coherente.
- Script de compilación Termux que exige pruebas unitarias completadas,
  verifica el APK y lo copia a Descargas con su nombre de versión.
- Tests web para el diseño del botón de apoyo y el control de apariencia.

La causa de los fallos previos de GitHub Actions parece externa a los scripts,
porque los trabajos no obtuvieron un runner. No se declara CI reparado ni se
afirma haber construido un APK hasta que las pruebas reales terminen.
