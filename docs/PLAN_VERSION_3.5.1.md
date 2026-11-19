# Plan propuesto para Reqestra 3.5.1

Este documento describe una **versión de mantenimiento propuesta**, no una
versión publicada. El número actual de Android y Web sigue siendo **3.5.0**.
Las funcionalidades que requieran cambios mayores se trasladarán a 3.6.

## Corrección inmediata ya incluida en el código, sin subir la versión

- La barra superior Android distribuye **Idioma 🌐**, **Donar ♥** y
  **Apariencia ☀/🌙** en una fila con `Arrangement.spacedBy(10.dp)`.
- Añade `padding(end = 8.dp)`, evita botones pegados al borde y conserva
  objetivos táctiles de **44 dp** y el menú de seis idiomas.
- A la espera de verificación mediante compilación y prueba física;
  la comprobación estática se encuentra en `web/tests/layout.test.mjs`.

## Prioridades para cerrar 3.5.1

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

**Nota:** no se modifica `versionName` ni `versionCode` hasta que se
complete y verifique la versión 3.5.1.
