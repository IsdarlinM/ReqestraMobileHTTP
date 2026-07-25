# Security Policy — Reqestra

Reqestra es un cliente HTTP manual para desarrollo e investigación de seguridad autorizada.

## Sesiones y secretos

- El modo de sesión es explícito: desactivado, memoria o persistente cifrado.
- Los headers de sesión se aíslan por origen: esquema, host y puerto.
- Cookies se seleccionan por dominio, path, expiración y atributo `Secure`.
- Las cookies recibidas durante redirects se procesan antes del siguiente salto.
- El modo persistente cifra headers y cookies mediante AES-GCM.
- La clave AES se genera y protege mediante Android Keystore.
- Backups y device transfer de datos internos están deshabilitados.
- Los valores de cookies se ocultan en el gestor de sesión.
- El inspector redacta `Authorization`, `Proxy-Authorization`, `Cookie`, `Set-Cookie`, API keys y tokens comunes.
- El historial nunca guarda headers, cookies ni cuerpos.

## Transporte

- HTTPS es el comportamiento predeterminado.
- HTTP requiere activación explícita en la petición como opción de laboratorio.
- No existe `trust-all` ni bypass de certificate pinning.
- Release confía únicamente en CA del sistema.
- Debug puede confiar además en CA instaladas por el usuario para Burp o mitmproxy autorizados.
- Redirecciones están deshabilitadas por defecto.
- Los timeouts y límites de tamaño reducen consumo de recursos no controlado.

## Validación

- Se bloquean CR/LF en nombres y valores de headers/cookies.
- Las credenciales incrustadas en URL se rechazan.
- Fragmentos `#...` se rechazan porque no forman parte del request HTTP.
- Métodos personalizados deben cumplir la gramática de token HTTP.
- `Content-Length`, `Transfer-Encoding`, `Connection`, `Proxy-Connection`, `Upgrade` y `TE` no son editables.
- Cuerpo máximo: 5 MiB.
- Respuesta máxima leída: 4 MiB.

## Vista HTML

La vista previa local deshabilita:

- JavaScript.
- Acceso a red.
- Acceso a archivos.
- Acceso a content providers.
- DOM storage.
- Base de datos WebView.
- Navegación externa.

## Límites de propósito

Reqestra no controla bytes TCP, pseudoheaders HTTP/2 o framing manual; no intercepta tráfico de otras aplicaciones, no evade controles y no automatiza ataques.
