# Reqestra

**Reqestra** es un banco de trabajo HTTP nativo para Android orientado a desarrollo, depuración de APIs y pruebas de seguridad autorizadas. Permite construir, modificar, enviar e inspeccionar peticiones completas sin limitar el editor al HTML.

```text
Reqestra
Mobile HTTP Workbench
imr :: v2.0.0
```

El nombre combina **request** y **orchestration**: una superficie única para componer y controlar el ciclo completo de una petición.

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
