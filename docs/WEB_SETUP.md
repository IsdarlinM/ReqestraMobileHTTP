# Reqestra Web 3.3 — Instalación, ejecución y publicación

## Requisitos

- Navegador actual con Fetch, módulos ES, WebCrypto y JavaScript habilitado.
- Para servir localmente: Node.js 20 o superior. Sin paquetes NPM de terceros.
- Para cifrar espacio persistente: localhost o sitio HTTPS (contexto seguro).
- Para publicar: servidor web estático HTTPS. El gateway no está disponible en hosting estático.

## 1. Iniciar en PC/Linux/macOS

    git clone https://github.com/IsdarlinM/ReqestraMobileHTTP.git
    cd ReqestraMobileHTTP/web
    node --version
    npm test
    npm start

Abre **http://127.0.0.1:5173**.

Para otro puerto:

    PORT=4173 node server.mjs

También puedes servir la web de manera puramente estática sin gateway, por ejemplo:

    cd web
    python3 -m http.server 5173 --bind 127.0.0.1

Abre http://127.0.0.1:5173. El motor Web directo requiere que la API permita peticiones CORS desde ese origen. La página no envía peticiones HTTP a APIs hasta que pulses los botones de envío o exploración.

## 2. Ejecutar desde Android/Termux

En el directorio de tu clon:

    pkg install nodejs
    cd ~/ReqestraMobileHTTP
    git pull --ff-only origin main
    cd web
    node server.mjs

En el navegador del mismo dispositivo abre http://127.0.0.1:5173. WebCrypto está disponible en localhost en navegadores compatibles. Para la app Android nativa compila con el Gradle wrapper y el SDK Android configurados.

## 3. Modo navegador

Es el modo predeterminado; no necesita servidor de API Reqestra. Envía solicitudes por Fetch directamente desde el navegador. Limitaciones obligatorias:

- CORS del servidor de destino.
- El navegador controla Cookie, Host, Content-Length, Connection, Origin y otros headers.
- Los certificados TLS deben ser válidos para el navegador.
- Las cookies HttpOnly y algunos headers de respuesta no son visibles a JavaScript.
- HTTP desde páginas HTTPS puede estar bloqueado por Mixed Content.
- Algunos status de redirecciones no seguidas se presentan como opaqueredirect.
- Force HTTP/1.1 no está disponible.
- Cada navegador puede imponer además políticas de privacidad, CSP y red.

Si la API está en tu control, permite explícitamente en CORS el origen de Reqestra Web y los headers/métodos que vas a utilizar. No uses * junto con credenciales y no deshabilites CORS del navegador por comodidad.

## 4. Gateway privado opcional

El gateway implementado en web/server.mjs sirve la interfaz y permite solicitudes HTTP del navegador hacia destinos declarados de antemano por ti.

**El gateway sólo debe ejecutarse en loopback, NO en Internet pública.** No implementa login, roles, control multiusuario ni un sistema de autenticación para exponerlo remotamente. Cuando existen orígenes permitidos, el servidor rechaza bind fuera de localhost.

Ejemplo para tu API de desarrollo:

    cd web
    REQESTRA_ALLOWED_ORIGINS=https://api.example.com node server.mjs

Para varios orígenes exactos (incluye esquema y puerto):

    REQESTRA_ALLOWED_ORIGINS=https://api1.example.com,https://api2.example.com:8443 node server.mjs

Abre http://127.0.0.1:5173 y selecciona **Motor de red → Gateway local autorizado** en Estudio.

Para laboratorio privado con IP de loopback o una IP interna se requiere una segunda activación explícita:

    REQESTRA_ALLOWED_ORIGINS=http://127.0.0.1:8080 REQESTRA_ALLOW_PRIVATE_TARGETS=1 node server.mjs

Sólo utilízalo en un laboratorio propio. El valor 1 desactiva el bloqueo de direcciones privadas, incluso cuando el host resuelve a ellas.

### Límites del gateway

- Requiere allowlist de **origen exacto** (esquema, host y puerto).
- Resuelve el destino y fija la IP validada durante el establecimiento de la conexión para limitar rebinding DNS.
- Bloquea por defecto loopback, redes privadas, link-local y rangos especiales.
- No sigue redirecciones HTTP.
- Sólo admite GET, HEAD, OPTIONS, POST, PUT, PATCH y DELETE.
- No permite redefinir Host ni headers de framing.
- Límite de cuerpo 5 MiB; límite de lectura de respuesta 4 MiB; timeout 1-120 segundos.
- Máximo 60 solicitudes por minuto por IP cliente y 2 simultáneas.
- No almacena tokens, cookies, request bodies ni resultados en disco.
- El usuario configura todos los orígenes autorizados. Nunca configurar un servicio genérico que acepte cualquier host.
- Mantiene datos confidenciales en la memoria del proceso durante una solicitud; para acceso remoto sería indispensable TLS, autenticación, auditoría y controles de autorización adicionales.

El gateway sólo sustituye algunas limitaciones del navegador para pruebas en equipo propio. No replica completamente las sesiones de OkHttp ni funciona como un servicio multiusuario.

## 5. Almacenamiento cifrado en la web

En Colecciones pulsa **Crear espacio cifrado** e introduce una contraseña larga. Se deriva la clave con PBKDF2-SHA256 (260000 iteraciones) y se cifran borrador, entornos, colecciones, headers persistentes e historial redactado con AES-256-GCM de WebCrypto. La contraseña no se transmite a servidor y no se incluye en el archivo cifrado.

- Si no creas un espacio, trabajas en memoria temporal.
- Después de recargar, pulsa Desbloquear con la misma contraseña.
- Puedes descargar e importar un respaldo .enc.json cifrado.
- Importar reemplaza el espacio del navegador actual; guarda un respaldo previo.
- Si pierdes la contraseña o se dañan los datos cifrados, no hay recuperación.
- Los datos Android cifrados con Keystore no pueden importarse automáticamente.
- El navegador, las extensiones y el equipo deben ser de confianza.

## 6. Donaciones PayPal en la web

El propietario crea un botón alojado desde la interfaz de PayPal:

https://www.paypal.com/donate/buttons

Elige la cuenta receptora correcta, completa la configuración y localiza el identificador público **hosted_button_id**. No inventes una ID ni copies un correo secreto o credenciales de PayPal.

Edita web/config.js y establece solamente la ID pública:

    window.REQESTRA_CONFIG = Object.freeze({
      paypalHostedButtonId: "TU_ID_PUBLICA",
    });

Actualiza/redistribuye el archivo web/config.js. La sección **Donar / Ayuda** activará un enlace externo que abre:

    https://www.paypal.com/donate/?hosted_button_id=TU_ID_PUBLICA

Si la ID no está presente o no supera validación alfanumérica, el botón queda inactivo. El dinero lo procesa PayPal, no el frontend Reqestra. Consulta también docs/PAYPAL_DONATIONS.md para requisitos de distribución.

## 7. Publicar como sitio HTTPS

Se puede publicar el contenido de **web/** en GitHub Pages, Netlify, Cloudflare Pages u otro alojamiento estático, apuntando la raíz de publicación a web/. Ajusta rutas según el proveedor; los recursos están escritos relativos a la carpeta. Utiliza HTTPS para habilitar almacenamiento cifrado.

En hosting estático sólo existe motor navegador. No añadas credenciales, llaves de PayPal ni tokens al archivo config.js o al repositorio. La ID pública de botón alojado sí puede ser visible. No hay API de pagos, base de datos remota ni webhook en el sitio.

**Importante:** GitHub Pages no ejecuta web/server.mjs; un sitio público no podrá utilizar el gateway privado a menos que lo despliegues por separado detrás de una arquitectura de autorización diseñada y auditada para ello (no incluida).

La PWA puede instalarse en navegadores compatibles. El service worker cachea únicamente la interfaz; evita interceptar las peticiones HTTP ni almacenar respuestas de APIs.

## 8. Comprobar que funciona

    cd web
    npm run check
    npm test
    npm start

Revisa manualmente con el navegador: Estudio, Query, variable faltante, validación de URL, Importación cURL, OpenAPI, colección, sesión por origen, historial, pruebas, exploración, cancelación, vista previa y donación configurada. Los tests de Node no reemplazan pruebas de navegador para CORS, Service Workers o accesibilidad.

## 9. Troubleshooting

- Error Fetch/CORS: revisa Access-Control-Allow-Origin de tu API. No significa automáticamente que la API esté caída.
- Error TLS: el navegador rechaza certificado incorrecto; no se implementa modo insecure.
- No puedo escribir Cookie manual: utiliza gateway local autorizado o cookies gestionadas por el navegador.
- Gateway desactivado: define REQESTRA_ALLOWED_ORIGINS, reinicia el servidor y vuelve a seleccionar Gateway.
- Origen no permitido: añade esquema/host/puerto exactos a la allowlist; no abras a todos los destinos.
- WebCrypto no disponible: usa HTTPS o localhost.
- No puedo desbloquear: verifica contraseña y archivo de backup; no existe recuperación.
- PayPal desactivado: configura hosted_button_id público y vuelve a publicar web/config.js.
- La PWA muestra versión antigua: asegúrate de publicar todos los archivos y recargar con conexión.
