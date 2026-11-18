# Reqestra Web 3.3

Cliente web de Reqestra: Estudio HTTP, query/variables, colecciones cifradas, OpenAPI/cURL, sesiones por origen, pruebas, exploración acotada, historial, inspector, vista previa HTML aislada y donaciones PayPal configurables.

## Iniciar

Requiere Node 20 o superior:

    cd web
    npm run check
    npm test
    npm start

Abre http://127.0.0.1:5173.

## Modalidades de envío

**Navegador:** Fetch directo; está sujeto a CORS, cookies, políticas TLS y headers prohibidos.

**Gateway local privado:** habilita manualmente una lista de orígenes de API bajo tu control:

    REQESTRA_ALLOWED_ORIGINS=https://api.example.com node server.mjs

En la interfaz selecciona **Gateway local autorizado**. No se configura por defecto ni puede exponerse fuera de loopback cuando la allowlist está activa. Para APIs locales propias se requiere REQESTRA_ALLOW_PRIVATE_TARGETS=1 explícito; evita hacerlo en redes compartidas.

## Secretos y almacenamiento

La app funciona en modo temporal sin guardar secretos. Para persistir, crea un almacén cifrado AES-GCM con contraseña (WebCrypto/PBKDF2). Se puede descargar un backup cifrado. No hay recuperación de contraseña ni sincronización automática con Android.

## Donaciones

Crea tu botón de donación desde https://www.paypal.com/donate/buttons y escribe su **ID pública**, sin secretos, en web/config.js. La opción permanece desactivada hasta que exista una ID válida. Los pagos se completan fuera de Reqestra en PayPal.

## Documentación

- [Manual funcional y casos de uso](../docs/FEATURES_AND_USE_CASES.md)
- [Guía completa de instalación y despliegue](../docs/WEB_SETUP.md)
- [Integración PayPal](../docs/PAYPAL_DONATIONS.md)
- [Arquitectura y seguridad](../docs/ARCHITECTURE_AND_SECURITY.md)
- [Matriz de pruebas](../docs/TEST_PLAN.md)

No publiques el gateway en un dominio público ni confundas la ausencia de CORS con un problema del servidor de destino.
