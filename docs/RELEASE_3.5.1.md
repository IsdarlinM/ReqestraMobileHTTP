# Reqestra 3.5.1 — Diseño, accesibilidad y actualización

Versión de mantenimiento de **Reqestra HTTP Studio** en Android y Web.
No introduce sistemas de cuentas, nuevos pagos, ni ejecución de extensiones
remotas. La versión mantiene el trabajo local y el mismo flujo de donaciones
externas por PayPal.

## Mejoras implementadas

### Web

- Se reemplaza el carácter tipográfico `♥` del botón superior de
  donaciones por un SVG vectorial con `viewBox="0 0 24 24"` y tamaño
  **20 × 20 px** fijo. Esto evita variaciones en forma y proporción
  entre fuentes y dispositivos.
- En pantallas pequeñas el botón es cuadrado de **44 × 44 px**, sin
  deformar el icono ni invadir Idioma y Apariencia.
- El control de tema ahora utiliza iconos SVG de sol/luna, coherentes
  con los colores y bordes de Reqestra. Su nombre de acción accesible
  cambia con el tema y se traduce en los **seis idiomas**.
- La barra superior oculta etiquetas secundarias de estado en
  pantallas intermedias donde resultaban invasivas.
- Se actualiza la caché de la PWA para servir los estilos nuevos.

### Android

- La barra superior mantiene **10 dp** entre Idioma, Donar y Apariencia,
  conserva el objetivo táctil de **44 dp** e incorpora un fondo y
  una forma consistentes para los tres controles.
- Se añaden etiquetas de prueba a estos botones para detectar
  solapamientos con pruebas instrumentadas Compose.
- La identidad corporativa PNG y la navegación inferior existentes
  permanecen sin cambios de comportamiento.

### Entrega

- Android: `versionName 3.5.1`, `versionCode 10`.
- Web: versión `3.5.1`; se amplía `npm run check` para incluir
  el archivo de internacionalización.
- Script `scripts/build-termux.sh`: ejecuta pruebas unitarias,
  compila, comprueba que el APK no esté vacío y lo copia a
  `~/storage/downloads/Reqestra-3.5.1-debug.apk`.
- Pruebas web para verificar tamaño del SVG, flujo de donaciones,
  soporte de temas, etiquetas accesibles y traducciones.
- Pruebas instrumentadas Android para constatar que los controles
  Idioma, Donar y Apariencia están visibles y no se superponen.

## Cómo compilar en Termux

```bash
termux-setup-storage
cd ~/ReqestraMobileHTTP
git pull --ff-only origin main
cd web
npm run check && npm test
cd ..
bash scripts/build-termux.sh --open
```

**Advertencia importante sobre instalación:** el APK de depuración usa
`applicationId=com.imr.reqestra.debug`, mientras que la versión
de producción utiliza `com.imr.reqestra`. Por ello el debug
**no reemplaza una instalación release** ni recupera sus datos.
Actualizar release sin pérdida requiere el mismo application ID y
la misma clave de firma del APK existente; no desinstales la versión
anterior para «forzar» la instalación.

## Comprobaciones recomendadas antes de distribuir

1. Revisar las barras superiores y el menú de donaciones en Web a
   **320, 360, 390, 480, 750, 1024 y 1440 px**.
2. Repetir en modo claro y oscuro, en los seis idiomas.
3. Comprobar el contraste y el orden del foco por teclado y TalkBack.
4. Confirmar que el botón de donaciones lleva a la sección informativa y
   que el enlace final abre `www.paypal.com` con el beneficiario correcto.
5. Validar Android en pantallas reducidas, con fuentes aumentadas
   y ambas apariencias.
6. Ejecutar pruebas y compilación completas en Termux o en un runner de CI.

**Limitaciones conocidas:** GitHub Actions había fallado previamente
antes de asignar un runner. Las pruebas de código no sustituyen la
verificación visual en dispositivos físicos. La identidad del
beneficiario de PayPal debe comprobarla el propietario de la cuenta.
