# Reqestra 3.5 — Android y Web

**Reqestra** es un banco de trabajo HTTP para **Android nativo y navegadores web** orientado a desarrollo, depuración de APIs y pruebas de seguridad autorizadas. Permite construir, modificar, enviar e inspeccionar peticiones completas sin limitar el editor al HTML.

```text
Reqestra
Mobile HTTP Workbench
imr :: v3.5.0
```

El nombre combina **request** y **orchestration**: una superficie única para componer y controlar el ciclo completo de una petición.

## Corrección de botones superpuestos — sin cambio de versión

Android y Web ajustan barras, tarjetas, acciones y selectores para que
los controles no se superpongan al cambiar de ancho de pantalla.
Las acciones de la cabecera se mantienen visibles, las filas con botones
largos se apilan y los menús se desplazan dentro de la zona disponible.

Ver la [guía de interfaz responsiva](docs/INTERFAZ_RESPONSIVA.md)
y su matriz de pruebas. La versión permanece **3.5.0**.

## Acerca de Reqestra y Ayuda

Android incluye **Acerca de y ayuda** dentro del menú inferior **Más**.
La Web incorpora **About & Help** en la navegación lateral y en **More**
cuando se usa desde un teléfono. Ambas pantallas muestran el logotipo,
la versión actual, una guía de primeros pasos, funciones disponibles,
recomendaciones de privacidad y enlaces a documentación y problemas.

La guía extensa está en [docs/GUIA_DE_USO.md](docs/GUIA_DE_USO.md).
La sección está traducida a los seis idiomas de la interfaz.
**No se ha cambiado el número de versión** por esta mejora.

## Selector de idioma y traducciones — 3.5

Android y Web incluyen un selector de idioma con la opción activa resaltada
por colores de marca y una marca ✓ tanto en modo claro como oscuro.
La web usa HTML inicial inglés y un menú accesible con navegación por
teclado. Android localiza títulos, indicaciones, menús, errores
y mensajes de resultado con catálogo local. Los seis idiomas
conservan el contenido original de las peticiones HTTP.

Ver [auditoría de localización](docs/INTERNACIONALIZACION_Y_EXTENSIONS.md).

## Navegación y apariencia móvil — 3.4.1

La cabecera de Android muestra los accesos **🌐 Idioma**, **♥ Donate**
y **☀️/🌙 Apariencia**. Se eliminó el botón de menú superior repetido:
el menú **Más** de la barra inferior es el único acceso a las herramientas
secundarias (Historial, Inspector, Vista previa, Sesiones y Extensiones).

El menú Más adapta los colores de fondo al tema oscuro/claro, usa tarjetas
con contraste claro y una respuesta de pulsación discreta sin ripple
expansivo. El tema sigue al sistema hasta una selección manual, que se
guarda en el dispositivo. El logotipo sigue siendo el **mismo PNG** en
Android y Web. Consulta las secciones 6 y 7 de
[la guía de interfaz](docs/INTERNACIONALIZACION_Y_EXTENSIONS.md).

## Identidad visual e idiomas — 3.4

Reqestra utiliza el mismo monograma **R con órbita turquesa**, aportado por el propietario,
en el icono Android, la barra superior de Compose, el favicon y la PWA web.

La interfaz ofrece **inglés (en), español (es), ruso (ru), chino mandarín simplificado
(zh), portugués (pt) y francés (fr)**. El idioma inicial corresponde al idioma
principal del dispositivo si está soportado y, en caso contrario, inglés.
El selector 🌐 de la barra superior permite cambiarlo manualmente; esta preferencia
se conserva localmente. Las solicitudes HTTP y sus respuestas no se traducen.

Las extensiones integradas ahora cuentan con una acción **Abrir/Open** que enlaza
con la herramienta real; GraphQL Schema Explorer, conectores proxy externos y
complementos remotos siguen claramente señalados como no implementados.
No se ejecuta código de terceros.

Consulta [la guía de idiomas, iconos y extensiones](docs/INTERNACIONALIZACION_Y_EXTENSIONS.md).
Los textos de interfaz se localizan sin enviar secretos a servicios externos;
algunos mensajes técnicos y cadenas heredadas todavía usan inglés de respaldo y
necesitan revisión lingüística por hablantes nativos antes de una publicación comercial.

## Nuevos clientes y donaciones — 3.3

**Android:** Kotlin, Jetpack Compose, OkHttp, sesiones por origen y colecciones cifradas con Android Keystore.

**Web:** aplicación responsive y PWA instalable en la carpeta web/, con Estudio, variables, Query, colecciones y entornos cifrados, pruebas de colecciones, exploración acotada, historial, inspector, vista previa aislada, importaciones cURL/OpenAPI y sesiones. No requiere framework ni servicios externos para funcionar en modo navegador.

**Donaciones PayPal opcionales:** pantalla Apoyar proyecto en Android y Donar/Ayuda en Web. Se configura un identificador público de botón PayPal alojado; **no hay cuenta receptora preconfigurada**, ni pagos dentro de Reqestra, ni beneficios digitales por donar.

### Ejecutar la web

    cd web
    npm run check
    npm test
    npm start

Abre http://127.0.0.1:5173. Requiere Node.js 20+. También puedes publicar la carpeta web/ como sitio estático HTTPS.

**Limitación real de paridad:** Fetch del navegador está sujeto a CORS, certificados TLS, cookies HttpOnly y restricciones de headers (Cookie, Host, etc.). Para APIs propias o entornos autorizados puedes iniciar el **gateway local restringido**, desactivado por defecto:

    cd web
    REQESTRA_ALLOWED_ORIGINS=https://api.example.com node server.mjs

Selecciona Gateway en Estudio. El gateway no se debe exponer en Internet: usa loopback, allowlist exacta de orígenes, DNS filtrado e IP fijada, límite de tasa y no sigue redirecciones.

### Donaciones PayPal

Crea tu propio botón desde https://www.paypal.com/donate/buttons. Configura el valor público hosted_button_id en web/config.js. Para Android configura paypalHostedButtonId en ~/.gradle/gradle.properties antes de compilar. Sin ID, el botón permanece desactivado.

### Documentación completa

- [Manual de funcionalidades y 20 casos de uso](docs/FEATURES_AND_USE_CASES.md)
- [Instalación de Web, PWA y gateway local](docs/WEB_SETUP.md)
- [Configuración PayPal y requisitos](docs/PAYPAL_DONATIONS.md)
- [Arquitectura, amenazas, límites y protección de secretos](docs/ARCHITECTURE_AND_SECURITY.md)
- [Plan de pruebas y checklist de publicación](docs/TEST_PLAN.md)
- [Sistema de diseño Material 3](docs/DESIGN_SYSTEM.md)
- [Descubrimiento y parametrización Android 3.2](docs/API_DISCOVERY_3_2.md)

**La web no se encuentra alojada automáticamente** al crear estos archivos: debes ejecutarla localmente o configurar tu hosting. GitHub Actions Android ha tenido fallos de ejecución antes de arrancar su runner; compila el APK en un entorno con SDK Android 36.

## Novedades 3.2 — API Discovery Lab

- Pestaña **Query** en Estudio: agrega parámetros estructurados `nombre=valor`, conservando la query existente y codificando los valores.
- Variables de entorno `{{baseUrl}}` y `{{token}}` en URL, query, headers, cookies y cuerpo, también en colecciones guardadas.
- **Explorar** desde la navegación principal para analizar rutas que introduzcas y candidatos a parámetros mediante comparaciones con una petición de referencia.
- Sugerencias pasivas de rutas del último response: JSON `href/url/path/endpoint`, HTML `href` y cabecera `Link`. No inician solicitudes por sí solas.
- Pruebas de **GET, HEAD y OPTIONS**; POST, PUT, PATCH y DELETE requieren activación adicional y confirmación explícita.
- Cada exploración está limitada a un mismo origen, **hasta 24 peticiones secuenciales, a 1 por segundo**, sin redirecciones automáticas. No se ejecuta enumeración masiva.
- Resultados con código HTTP, tamaño, duración, `Allow` y diferencias observadas para parámetros candidatos (heurística; no es evidencia de vulnerabilidad).

Consulta [Uso de parametrización y exploración](docs/API_DISCOVERY_3_2.md) antes de probar endpoints.

## Diseño visual 3.1 — Studio palette

La interfaz utiliza una paleta Material 3 consistente para modos claro y oscuro: verde esmeralda para acciones y éxito, azul para información, ámbar para advertencias y rojo para errores. Las tarjetas, campos de código, avisos, indicadores HTTP y navegación comparten tokens de color, tipografía y formas.

Consulta [la guía del sistema visual](docs/DESIGN_SYSTEM.md) para los códigos hexadecimales y los criterios de contraste y accesibilidad.

## Novedades 3.0

- Navegación compacta: Estudio, Colecciones, Pruebas e Historial.
- Respuesta resumida debajo del editor y acceso al inspector completo.
- Colecciones y entornos cifrados con Android Keystore, variables tipo `{{baseUrl}}`.
- Importación de especificaciones OpenAPI 3.x **JSON** con URL base absoluta.
- Ejecución manual de hasta 20 peticiones secuenciales, assertions de estado/latencia y resultados.
- Catálogo interno de extensiones con distinción clara entre funcionalidades integradas y planeadas.
- Formateador de JSON, pestañas horizontales desplazables y tamaños más compactos.

Consulta [ROADMAP 3.0](docs/ROADMAP_3_0.md) para requisitos, uso, limitaciones y funciones todavía no implementadas.

## Funcionalidades

### Constructor de requests

- Métodos `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `HEAD`, `OPTIONS` y método personalizado.
- URL completa editable: esquema, host, puerto, path y query string.
- Headers repetidos y headers específicos por petición.
- Header `Host` editable.
- Editor dedicado de cookies.
- Cuerpos:
  - Sin cuerpo.
  - Texto / Raw.
  - JSON.
  - HTML.
  - XML.
  - `application/x-www-form-urlencoded`.
  - GraphQL.
  - Binario suministrado como Base64.
- `Content-Type` editable.
- Seguimiento opcional de redirecciones.
- Cookies conservadas también durante saltos intermedios de redirección.
- Timeout configurable entre 1 y 120 segundos.
- Preferencia HTTP/1.1 para comparar comportamiento con HTTP/2.
- HTTP sin cifrar mediante activación explícita de laboratorio.
- Cancelación de la petición activa.

### Sesiones opcionales

Reqestra dispone de tres modos:

1. **Sin sesión:** no conserva headers ni cookies entre peticiones.
2. **Solo memoria:** mantiene la sesión hasta cerrar el proceso de la aplicación.
3. **Persistente cifrada:** mantiene la sesión entre reinicios.

Características de sesión:

- Headers de sesión guardados **por origen**: esquema, host y puerto.
- Un `Authorization` configurado para un origen no se aplica a otro dominio.
- Cookies procesadas según dominio, path, expiración y atributo `Secure`.
- Cookies de respuestas intermedias se reenvían al siguiente redirect cuando corresponde.
- Cookies manuales pueden iniciar o sobrescribir valores de sesión.
- Headers específicos de la petición sobrescriben los headers de sesión del mismo nombre.
- Persistencia cifrada con AES-GCM y clave administrada por Android Keystore.
- Backups y transferencia de datos internos deshabilitados.

### Inspector

- Request enviado: método, URL, headers y tamaño/cuerpo.
- Response: status, protocolo, duración, tipo de contenido, headers y cuerpo.
- Redacción visual de `Authorization`, `Cookie`, `Set-Cookie`, API keys y tokens comunes.
- Respuestas limitadas a 4 MiB para evitar consumo de memoria descontrolado.
- Cuerpos enviados limitados a 5 MiB.

### Vista previa

- Renderizado local de HTML en WebView aislada.
- JavaScript, red, archivos, content providers, DOM storage y base de datos deshabilitados.
- Vista monoespaciada para JSON, XML, GraphQL, Raw y otros cuerpos.

### Historial

El historial conserva un máximo de 50 registros y almacena únicamente:

- Método.
- URL redactada.
- Código HTTP.
- Duración.
- Fecha y hora.

No guarda headers, cookies, cuerpos ni credenciales.

## Precedencia de headers y cookies

### Headers

```text
headers de sesión del origen < headers específicos del request
```

### Cookies

```text
cookies de sesión < Cookie: escrito en headers < editor dedicado de cookies
```

## Límite técnico deliberado

Reqestra permite editar las partes semánticas de una petición compatibles con OkHttp. Los headers de framing `Content-Length`, `Transfer-Encoding`, `Connection`, `Proxy-Connection`, `Upgrade` y `TE` se calculan o controlan internamente para evitar mensajes incoherentes.

No es un editor de bytes TCP, un motor de request smuggling ni un interceptor de otras aplicaciones.

## Estructura

```text
app/src/main/java/com/imr/reqestra/
├── core/
│   ├── request/          # Composición y precedencia
│   ├── security/         # Redacción de secretos
│   └── validation/       # Validación defensiva
├── data/
│   ├── history/          # Historial minimizado
│   ├── network/          # Motor OkHttp y CookieJar
│   └── session/          # Sesión en memoria y persistente cifrada
├── domain/model/         # Modelos inmutables
└── presentation/         # Compose, ViewModel, pantallas y tema
```

## Stack

- Kotlin 2.3.21.
- Jetpack Compose + Material 3.
- OkHttp 4.12.0.
- Android Gradle Plugin 8.13.2.
- Gradle 8.13.
- JDK 17.
- compileSdk / targetSdk 36.
- minSdk 26.

## Compilación y pruebas

Requisitos:

- Android Studio compatible con AGP 8.13.
- JDK 17.
- Android SDK 36.

El repositorio no almacena binarios generados. En la primera ejecución, `gradlew` o `gradlew.bat` ejecuta `gradle/wrapper/WrapperBootstrap.java`, descarga el JAR oficial de Gradle 8.13 y exige este SHA-256 antes de usarlo:

```text
81a82aaea5abcc8ff68b3dfcb58b3c3c429378efd98e7433460610fecd7ae45f
```

Si el archivo ya existe y coincide, no se realiza ninguna conexión adicional para restaurarlo.

Linux/macOS:

```bash
chmod +x scripts/verify.sh
./scripts/verify.sh
```

Windows:

```cmd
scripts\verify.cmd
```

El script ejecuta:

```text
clean → testDebugUnitTest → lintDebug → assembleDebug
```

APK generado:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Consulta `TEST_REPORT.md` para distinguir las comprobaciones ejecutadas en el entorno de entrega de las pruebas Android incluidas para ejecutar con SDK.

## Uso autorizado

Usa Reqestra exclusivamente contra sistemas propios, laboratorios, CTF, auditorías autorizadas o activos incluidos en el alcance de un programa bug bounty.
