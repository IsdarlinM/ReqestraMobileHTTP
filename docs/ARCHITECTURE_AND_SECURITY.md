# Reqestra 3.3 — Arquitectura, datos y seguridad

## 1. Visión general

Reqestra consta de **dos clientes diferentes** que comparten funciones, convenciones visuales y sintaxis de variables, pero no una base de código ni un almacén de secretos común:

- **Android:** Kotlin + Jetpack Compose, OkHttp, AndroidSessionRepository, WorkspaceStore y Android Keystore.
- **Web:** HTML semántico, CSS adaptable, JavaScript ES modules, Fetch API, WebCrypto, Service Worker restringido y gateway local opcional en Node.js 20+.
- **PayPal:** enlace externo a una página de donación alojada por PayPal. No hay integración de órdenes/captura/webhooks.

Ningún cliente exige cuentas de Reqestra, backend centralizado, base de datos remota o telemetría.

## 2. Estructura del repositorio

    app/src/main/java/com/imr/reqestra/
      core/validation/RequestValidator.kt
      core/variables/RequestParameterizer.kt
      core/automation/CollectionRunner.kt
      core/discovery/EndpointExplorer.kt
      core/curl/CurlCodec.kt
      core/openapi/OpenApiImporter.kt
      core/donations/PayPalDonations.kt
      data/network/HttpEngine.kt
      data/session/AndroidSessionRepository.kt
      data/workspace/WorkspaceStore.kt
      presentation/MainViewModel.kt
      presentation/ReqestraApp.kt
      presentation/support/SupportScreen.kt
    web/
      index.html             UI accesible y navegación
      styles.css             Material Studio palette
      config.js              ID pública opcional de PayPal
      app.mjs                Estado, acciones y coordinación
      lib/core.mjs           Query, variables, OpenAPI, cURL, descubrimiento
      lib/ui.mjs             Renderizado DOM seguro con textContent
      lib/transport.mjs      Fetch directo y gateway
      lib/vault.mjs          AES-256-GCM de workspace
      server.mjs             Gateway local de origen restringido
      sw.js                  Cache única de interfaz
      package.json           scripts npm (sin dependencias externas)
      tests/                 pruebas Node
    docs/
      FEATURES_AND_USE_CASES.md
      WEB_SETUP.md
      PAYPAL_DONATIONS.md
      ARCHITECTURE_AND_SECURITY.md
      TEST_PLAN.md

## 3. Flujo de envío

### Android

Usuario → Compose WorkspaceScreen → MainViewModel → RequestParameterizer → RequestValidator → HttpEngine/OkHttp → ResponseSnapshot → Inspector/Historial.

Los headers y cookies de sesión se aplican por origen. Reqestra limita los tamaños y controla redirecciones, TLS y transporte mediante OkHttp.

### Web directa

Usuario → app.mjs → core.parametrizeDraft → core.validateDraft → Fetch del navegador → transport.readLimited → Inspector/Historial.

Browser Fetch respeta obligatoriamente CORS, políticas de cookies, certificados TLS, mixed content y headers prohibidos. Los errores se muestran como fallos de red/CORS sin inventar estados HTTP no disponibles.

### Web con gateway

Usuario → app.mjs → Web Fetch same-origin POST /api/execute → web/server.mjs → allowlist exacta + DNS filtrado y fijado → Node HTTPS/HTTP → respuesta JSON limitada → Inspector.

- La solicitud requiere interacción del usuario.
- El gateway no inicia exploraciones por sí solo ni hace cron jobs.
- Se mantiene desconectado si REQESTRA_ALLOWED_ORIGINS está vacío.
- Sólo escucha en loopback cuando tiene permitido ejecutar solicitudes.
- Se valida el origen completo (esquema, host, puerto) antes de realizar DNS.
- Se obtiene DNS previamente y se fija la dirección IP validada para la conexión; se rechazan rangos reservados por defecto.
- No se siguen redirects desde Node.
- El servidor evita imprimir URLs, headers, body y tokens en los logs.
- Sólo 60 solicitudes/minuto por cliente, dos conexiones simultáneas y limites de payload.

**Limitaciones:** no es un gateway de producción, no incluye autenticación a múltiples usuarios, protección avanzada contra proxies aguas arriba ni persistencia de cookies. No debe exponerse a Internet ni colocarse detrás de un reverse proxy público sin una revisión de arquitectura completa. El acceso de usuarios maliciosos con control local al proceso podría usar los orígenes permitidos.

## 4. Modelo de datos

### RequestDraft

URL, método, customMethod, queryParamsText, headersText, cookiesText, bodyMode, contentType, body, timeoutSeconds, allowCleartext, followRedirects, forceHttp1 y configuración de sesión.

### SavedRequest

ID, nombre, nombre de colección, RequestDraft original sin expandir las variables. Se usan IDs aleatorios.

### Environment

Nombre + mapa de variables. El mapa puede contener secretos y debe permanecer cifrado cuando se guarda.

### Workspace

Lista de solicitudes, entornos, entorno activo, headers persistentes por origen en web. En Android el repositorio de sesiones se almacena separadamente en Keystore.

### HistoryEntry

ID, método, URL redactada, status, duración y timestamp. **No** persiste headers ni body ni response completa.

### Response

Status, headers expuestos, durationMs, body limitado, indicador de truncamiento y solicitud saneada. En web se conserva solo en memoria.

## 5. Almacenamiento cifrado

### Android

AES-GCM y Android Keystore mediante SecurePreferences. No hay sincronización multi-dispositivo. Un desinstalado o pérdida de clave puede hacer inaccesibles los datos.

### Web

El usuario debe crear un espacio de trabajo cifrado con contraseña (mínimo diez caracteres). Se usa WebCrypto:

1. Generar salt aleatorio de 16 bytes.
2. PBKDF2-SHA256, 260 000 iteraciones.
3. Derivar clave AES-256-GCM no exportable.
4. Generar nonce de 12 bytes por guardado.
5. Cifrar JSON de borrador, colecciones, entornos, sesiones persistentes e historial.
6. Guardar salt, nonce y ciphertext en localStorage.

No se almacena contraseña ni clave de descifrado en disco deliberadamente. La app no implementa recuperación de passphrase. Bloquear/recargar obliga a desbloquear.

**No confundir cifrado en reposo con protección contra XSS o malware activo**: mientras el espacio esté desbloqueado, JavaScript legítimo tiene acceso a datos en memoria. Utiliza un navegador confiable y verifica extensiones.

## 6. Modelo de amenazas y medidas

| Riesgo | Medida | Riesgo residual |
|---|---|---|
| Secretos en historial | URL y headers redactados, sin body persistente | Body mostrado puede contener secretos |
| Lectura del almacenamiento web | AES-GCM con clave derivada de passphrase | Extensión/XSS con pestaña desbloqueada |
| SSRF desde gateway | Allowed origins exactos, DNS filtrado, IP fijada, loopback | Operador puede habilitar IP privada explícitamente |
| Abuso de gateway público | Sin publicación pública; bind loopback | Otros procesos locales pueden alcanzarlo |
| Lectura de recursos locales | No hay exploración de archivos ni file:// | Usuario del equipo conserva control local |
| Exceso de tráfico | Máx. 24/20, 1 solicitud/segundo; gateway 60/min | Usuario puede ejecutar varias sesiones repetidamente |
| Desbordamiento de memoria | Request 5 MiB, response 4 MiB | UI puede consumir memoria en dispositivos pequeños |
| Código externo en preview | iframe sandbox + CSP, WebView Android aislada | HTML complejo puede no renderizarse |
| Comandos cURL maliciosos | Interpretación como texto, sin shell, rechaza -k | Importador parcial |
| Fugas vía Referer | Referrer-Policy no-referrer en gateway | En hosting estático depende de cabeceras del proveedor |
| Manipulación de destino PayPal | URL https fija + ID alfanumérica | Debes comprobar manualmente el beneficiario real |
| CORS / cookies | Políticas estándar del navegador | Limitación funcional inherente |

### Cabeceras CSP del servidor local

En Node se sirven cabeceras Content-Security-Policy, X-Content-Type-Options, X-Frame-Options y Referrer-Policy. Si publicas estáticamente, configura cabeceras equivalentes en tu proveedor. No habilites scripts inline arbitrarios.

### Cache Service Worker

La PWA utiliza una lista cerrada de archivos de la interfaz y estrategia de actualización network-first; no cachea /api, URLs remotas, headers ni respuestas del cliente HTTP. El almacenamiento de secretos corresponde exclusivamente al Vault cifrado.

## 7. Descubrimiento acotado

- No hay listas internas de diccionario, recursión, escaneos de IP ni intentos de evasión de controles.
- Primero se hace un baseline GET y después las rutas/parámetros/métodos candidatos del mismo origen.
- Se consideran indicios de diferencia solo estado y tamaño comparados con baseline; es una heurística imperfecta.
- GET, HEAD, OPTIONS vienen seleccionados por defecto. Los métodos que pueden modificar recursos exigen habilitación y confirmación adicionales.
- La cancelación es best-effort: no puede garantizar que el servidor no haya procesado ya una mutación.

## 8. Donaciones y compliance

Un botón PayPal hosted_button_id no es un secret de API. La URL abre PayPal externamente y no es evidencia de una donación completada. No hay intercambio de información financiera entre Reqestra y PayPal salvo la navegación del donante por su propia decisión.

Google Play puede exigir Play Billing si una contribución permite acceder a bienes, funcionalidades o servicios digitales. Reqestra **no** otorga beneficios por donar. Los cambios futuros a los incentivos o a la política requieren revisión. Consulta docs/PAYPAL_DONATIONS.md.

## 9. Pruebas y release

### Android

    ./gradlew --no-daemon testDebugUnitTest assembleDebug --stacktrace

### Web

    cd web
    npm run check
    npm test
    npm start

Verificar CORS, TLS, restricciones del gateway, persistencia cifrada, cancelación, métodos con efectos y botones PayPal mediante pruebas manuales. Los tests no son una auditoría integral de seguridad.

## 10. Desarrollo futuro

La paridad real de red entre Android y Web en un servicio alojado exigiría backend multiusuario con autenticación, scopes ACL, DNS pinning, observabilidad saneada, cuotas por cuenta, protección CSRF, TLS y revisión de privacidad. Estos elementos no se deben confundir con el gateway local actual. La sincronización remota de secretos requeriría un diseño criptográfico independiente y consentimiento explícito.
